package com.pheiffware.lib.simulation;

import com.pheiffware.lib.Utils;

/**
 * General class for managing a simulation (something which evolves through a
 * series of time-steps). Runs in another background thread. Can be set to
 * free-wheel in real time or be run step by step.
 * 
 * @author Steve
 *
 */
public class SimulationManager<SimState>
{
	private final Simulation<SimState> simulation;
	private double absoluteSimTime;
	private volatile BaseSimThread updateThread;

	public SimulationManager(Simulation<SimState> simulation)
	{
		absoluteSimTime = 0.0;
		this.simulation = simulation;
		// Just place holder to avoid tests for null
		updateThread = new NullSimThread();
		updateThread.start();
	}

	/**
	 * Simulates the given amount of time over the given number of steps in a
	 * background thread. This thread's execution is throttled such that the
	 * minimum given real/sim time ratio is maintained. This prevents the
	 * simulation from running too quickly if being rendered.
	 * 
	 * @param totalElapsedSimTime
	 * @param numSteps
	 * @param minRealSimTimeRatio
	 *            Set to 0 to turn off throttling (run full speed).
	 */
	public synchronized void runDeterministicSimulationInBackground(double totalElapsedSimTime, int numSteps, double minRealSimTimeRatio)
	{
		updateThread = new DeterministicSimThread(totalElapsedSimTime / numSteps, numSteps, minRealSimTimeRatio);
		updateThread.start();
	}

	/**
	 * Runs simulation such that it tracks real time (multiplied by a factor).  It will run it as many time steps as processor allows on a single thread. 
	 * @param maxTimeStep
	 * @param minTimeStep
	 * @param simToRealTimeRatio
	 */
	public synchronized void runRealtimeSimulationInBackground(double maxTimeStep, double minTimeStep, double simToRealTimeRatio)
	{
		updateThread = new RealtimeSimThread(maxTimeStep, minTimeStep, simToRealTimeRatio);
		updateThread.start();
	}

	public boolean isRunning()
	{
		return updateThread.isAlive();
	}

	public void awaitCompletion()
	{
		try
		{
			updateThread.join();
		}
		catch (InterruptedException e)
		{
		}
	}

	/**
	 * Ends the background simulation quickly.
	 */
	public synchronized void stopBackgroundSimulation()
	{
		updateThread.signalStop();
		try
		{
			updateThread.join();
		}
		catch (InterruptedException e)
		{
		}
	}

	public synchronized SimState getState()
	{
		return updateThread.requestState();
	}

	/**
	 * Updates one time step of given size.
	 * 
	 * @param elapsedSimTime
	 */
	private final void timeStep(double elapsedSimTime)
	{
		absoluteSimTime += elapsedSimTime;
		simulation.timeStep(absoluteSimTime, elapsedSimTime);
	}

	/**
	 * A base class for managing running a simulation in a background thread.
	 * @author Steve
	 */
	private abstract class BaseSimThread extends Thread
	{
		private Object lock = new Object();
		private volatile boolean signalFlag = false;
		private volatile boolean stopFlag = false;
		private long realStartTime;

		@Override
		public void run()
		{
			realStartTime = System.nanoTime();
			try
			{
				synchronized (lock)
				{
					runSimulation();
				}
			}
			catch (SimStoppedException e)
			{
				// Exits thread.
			}
		}

		protected final long getRealStartTime()
		{
			return realStartTime;
		}

		protected abstract void runSimulation() throws SimStoppedException;

		public final SimState requestState()
		{
			SimState state;
			signalFlag = true;
			synchronized (lock)
			{
				signalFlag = false;
				state = simulation.copyState();
				lock.notify();
			}
			return state;
		}

		public final void signalStop()
		{
			signalFlag = true;
			synchronized (lock)
			{
				signalFlag = false;
				stopFlag = true;
				lock.notify();
			}
		}

		/**
		 * Gets around 2 problems: 
		 * 1. Using a small wait (wait can't wait for less than 1ms in windows)
		 * 2. Waiting, spinning in tight loop doing nothing waiting for potential requests or to get access.
		 * SHOULD ONLY BE CALLED FROM WITHIN synchronized(this) BLOCKS.
		 * @throws SimStoppedException Will be thrown if a request to stop was made.
		 */
		protected final void handleSignals() throws SimStoppedException
		{
			if (signalFlag == true)
			{
				try
				{
					// Gives up lock until the requester calls notify explicitly
					lock.wait();
				}
				catch (InterruptedException e)
				{
				}
				if (stopFlag)
				{
					throw new SimStoppedException();
				}
			}
		}
	}

	private class NullSimThread extends BaseSimThread
	{
		@Override
		protected void runSimulation() throws SimStoppedException
		{
			// Do nothing and end immediately
		}
	}

	/**
	 * A class for running a simulation in a deterministic way (always same number of steps of the same size).  However, allows the state to be queried while running to render live updates.
	 * @author Steve
	 */
	private class DeterministicSimThread extends BaseSimThread
	{
		private final double timeStep;
		private final int numSteps;
		private final double minRealSimTimeRatio;

		public DeterministicSimThread(double timeStep, int numSteps, double minRealSimTimeRatio)
		{
			this.timeStep = timeStep;
			this.numSteps = numSteps;
			this.minRealSimTimeRatio = minRealSimTimeRatio;
		}

		protected void runSimulation() throws SimStoppedException
		{
			double simTimeElapsed = 0;
			for (int step = 0; step < numSteps; step++)
			{
				timeStep(timeStep);
				simTimeElapsed += timeStep;
				throttle(getRealStartTime(), simTimeElapsed);
			}
		}

		/**
		 * Enforce delay if simulation is running to fast.  This will handle any incoming requests to getState or stop.
		 * @param startTime
		 * @param simTimeElapsed
		 * @throws SimStoppedException 
		 */
		private final void throttle(long startTime, double simTimeElapsed) throws SimStoppedException
		{
			// Don't throttle at all if this is 0.
			if (minRealSimTimeRatio == 0)
			{
				handleSignals();
				return;
			}
			double realTimeElapsed;
			do
			{
				handleSignals();
				realTimeElapsed = Utils.getTimeElapsed(startTime);
			} while (realTimeElapsed / simTimeElapsed < minRealSimTimeRatio);
		}
	}

	private class RealtimeSimThread extends BaseSimThread
	{
		private final double maxTimeStep;
		private final double minTimeStep;
		private final double simToRealTimeRatio;

		public RealtimeSimThread(double maxTimeStep, double minTimeStep, double simToRealTimeRatio)
		{
			this.maxTimeStep = maxTimeStep;
			this.minTimeStep = minTimeStep;
			this.simToRealTimeRatio = simToRealTimeRatio;
		}

		protected void runSimulation() throws SimStoppedException
		{
			while (true)
			{
				double timeStep = Utils.getTimeElapsed(getRealStartTime()) * simToRealTimeRatio;

				if (timeStep > maxTimeStep)
				{
					timeStep = maxTimeStep;
				}
				else if (timeStep < minTimeStep)
				{
					timeStep = minTimeStep;
				}
				timeStep(timeStep);
				handleSignals();
			}
		}
	}

}

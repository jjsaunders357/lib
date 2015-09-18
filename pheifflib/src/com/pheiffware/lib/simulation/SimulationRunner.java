package com.pheiffware.lib.simulation;

/**
 * Manages a simulation by running it in a background thread.  This deals with the threading/synchronization issues related to this.  
 * How the simulation is actually run is done by the runSimulation() method calling this class' timeStep() method as appropriate.
 * @author Steve
 *
 * @param <SimState> The type of state returned from the simulation.
 */
public abstract class SimulationRunner<SimState> implements Runnable
{
	private Object lock = new Object();
	private volatile boolean signalFlag = false;
	private volatile boolean stopFlag = false;

	private final Thread updateThread;

	private long realStartTimeStamp;
	private double elapsedSimTime;
	private final Simulation<SimState> simulation;

	public SimulationRunner(Simulation<SimState> simulation)
	{
		this.simulation = simulation;
		updateThread = new Thread(this);
	}

	/**
	 * An extending class overrides this to perform one or more time steps in the desired manner in a background thread.
	 * This method should call timeStep() to update the time step.
	 * This method should periodically call handleSignals().  This method allows other threads to get the simulation state 
	 * or issue a stop.  handleSignals() with throw a SimStoppedException if/when a stop is requested.  It is expected that 
	 * this will simply be rethrown.
	 * 
	 * @throws SimStoppedException
	 */
	protected abstract void runSimulation() throws SimStoppedException;

	/**
	 * Performs a check to see if another thread is trying to do something.  It will block until the other thread is done and wakes this 
	 * back up.  There are only 2 such actions which other threads can take: getState and stop.  
	 * This method will throw a stop exception when finished if another thread initiated a stop.

	 * SHOULD ONLY BE CALLED FROM WITHIN synchronized(this) BLOCKS.
	 * @throws SimStoppedException Will be thrown if a request to stop was made.
	 */
	protected final void handleSignals() throws SimStoppedException
	{
		if (signalFlag == true)
		{
			try
			{
				// Gives up lock. The requester is expected to call notify()
				// explicitly when done.
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

	/**
	 * Updates one time step of given size.
	 * 
	 * @param timeStep
	 */
	protected final void performTimeStep(double timeStep)
	{
		elapsedSimTime += timeStep;
		simulation.performTimeStep(timeStep);
	}

	/**
	 * Wrapper for the runSimulation method which extending classes fill in.
	 */
	@Override
	public void run()
	{
		realStartTimeStamp = System.nanoTime();
		elapsedSimTime = 0.0;
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

	/**
	 * Gets a snapshot of the simulation.
	 * @return
	 */
	public final SimState getState()
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

	/**
	 * Causes the simulation to stop in an orderly manner.
	 */
	public final void start()
	{
		updateThread.start();
	}

	/**
	 * Wait for the simulation to end naturally.
	 */
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
	 * Causes the simulation to stop in an orderly manner.
	 */
	public final void stop()
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
	 * Stops the simulation and blocks until it ends.
	 */
	public final void stopAndWait()
	{
		stop();
		awaitCompletion();
	}

	/**
	 * Is the simulation running?
	 * @return
	 */
	public boolean isRunning()
	{
		return updateThread.isAlive();
	}

	public final long getRealStartTime()
	{
		return realStartTimeStamp;
	}

	public final double getElapsedSimTime()
	{
		return elapsedSimTime;
	}
}

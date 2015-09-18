package com.pheiffware.lib.simulation;

import com.pheiffware.lib.Utils;

public class DeterministicSimulationRunner<SimState> extends SimulationRunner<SimState>
{
	private final double timeStep;
	private final int numSteps;
	private final double minRealSimTimeRatio;

	public DeterministicSimulationRunner(Simulation<SimState> simulation, double timeStep, int numSteps, double minRealSimTimeRatio)
	{
		super(simulation);
		this.timeStep = timeStep;
		this.numSteps = numSteps;
		this.minRealSimTimeRatio = minRealSimTimeRatio;
	}

	protected void runSimulation() throws SimStoppedException
	{
		for (int step = 0; step < numSteps; step++)
		{
			performTimeStep(timeStep);
			throttle();
		}
	}

	/**
	 * Enforce delay if simulation is running to fast.  This will handle any incoming requests to getState or stop.
	 * @param startTime
	 * @param simTimeElapsed
	 * @throws SimStoppedException 
	 */
	private final void throttle() throws SimStoppedException
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
			realTimeElapsed = Utils.getTimeElapsed(getRealStartTime());
		} while (realTimeElapsed / getElapsedSimTime() < minRealSimTimeRatio);
	}
}

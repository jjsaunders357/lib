package com.pheiffware.lib.simulation;

public class DeterministicSimulationRunner<SimState> extends SimulationRunner<SimState>
{
	private final double maxSimTimePerSecond;
	private final double timeStep;
	private final int numSteps;

	public DeterministicSimulationRunner(Simulation<SimState> simulation, double maxSimTimePerSecond, double timeStep, int numSteps)
	{
		super(simulation);
		this.maxSimTimePerSecond = maxSimTimePerSecond;
		this.timeStep = timeStep;
		this.numSteps = numSteps;
	}

	protected void runSimulation() throws SimStoppedException
	{
		for (int step = 0; step < numSteps; step++)
		{
			performTimeStep(timeStep);
			throttleAndHandleSignals(maxSimTimePerSecond);
		}
	}
}

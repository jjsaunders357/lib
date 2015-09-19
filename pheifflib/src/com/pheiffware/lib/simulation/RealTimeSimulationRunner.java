package com.pheiffware.lib.simulation;


public class RealTimeSimulationRunner<SimState> extends SimulationRunner<SimState>
{
	private final double maxTimeStep;
	private final double minTimeStep;
	private final double simTimePerSecond;

	public RealTimeSimulationRunner(Simulation<SimState> simulation, double maxSimTimePerSecond, double maxTimeStep, double minTimeStep)
	{
		super(simulation);
		this.maxTimeStep = maxTimeStep;
		this.minTimeStep = minTimeStep;
		this.simTimePerSecond = maxSimTimePerSecond;
	}

	protected void runSimulation() throws SimStoppedException
	{
		long lastTimeStamp = System.nanoTime();
		while (true)
		{
			long nextTimeStamp = System.nanoTime();
			double timeStep = simTimePerSecond * (nextTimeStamp - lastTimeStamp) / 10000000000.0;
			lastTimeStamp = nextTimeStamp;

			if (timeStep > maxTimeStep)
			{
				timeStep = maxTimeStep;
			}
			else if (timeStep < minTimeStep)
			{
				timeStep = minTimeStep;
			}
			performTimeStep(timeStep);
			throttleAndHandleSignals(simTimePerSecond);
		}
	}
}

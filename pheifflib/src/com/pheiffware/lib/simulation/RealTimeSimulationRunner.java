package com.pheiffware.lib.simulation;

import com.pheiffware.lib.Utils;

public class RealTimeSimulationRunner<SimState> extends SimulationRunner<SimState>
{
	private final double maxTimeStep;
	private final double minTimeStep;
	private final double simToRealTimeRatio;

	public RealTimeSimulationRunner(Simulation<SimState> simulation, double maxTimeStep, double minTimeStep, double simToRealTimeRatio)
	{
		super(simulation);
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
			performTimeStep(timeStep);
			handleSignals();
		}
	}
}

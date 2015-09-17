package com.pheiffware.lib.simulation;

public interface Simulation<SimState>
{
	/**
	 * Called to updates the simulation by a give size time step.
	 * 
	 * @param absoluteTime
	 * @param elapsedTime
	 *            Time since last update.
	 */
	public void timeStep(double absoluteTime, double elapsedTime);

	/**
	 * Returns a snap shot of the simulation. The SimulationManager prevents this
	 * from being called simultaneously with timeStep(). However, the returned
	 * state must not reference anything touched by timeStep() as after this is
	 * called, update will continue to execute.
	 * 
	 * This is typically used for rendering the current state of the simulation.  
	 * 
	 * This can be less than complete as long as it contains all the information you need for display.
	 * 
	 * @return
	 */
	public SimState copyState();
}

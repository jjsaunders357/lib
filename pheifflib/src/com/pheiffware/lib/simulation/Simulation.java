package com.pheiffware.lib.simulation;

public interface Simulation<SimState>
{
	/**
	 * Called to updates the simulation by a give size time step.
	 * 
	 * @param elapsedTime
	 *            Time since last update.
	 */
	public void performTimeStep(double elapsedTime);

	/**
	 * Returns a snap shot of the simulation. SimulationManagers prevents this
	 * from being called simultaneously with timeStep(). However, the returned
	 * state must not reference anything touched by timeStep() (which is not static) 
	 * as after this is called, update will continue to execute.
	 * 
	 * This is typically used for rendering the current state of the simulation.  
	 * 
	 * This can be less than complete as long as it contains all the information you need for display.
	 * 
	 * @return
	 */
	public SimState copyState();

	/**
	 * Used to apply an external input, such as a key press, to the simulation state.  SimulationManagers prevent this
	 * from being called simultaneously with timeStep().  However, any object passed in should be copied unless it will 
	 * not be used externally while the simulation is running.
	 * @param key
	 * @param value
	 */
	public void applyExternalInput(String key, Object value);
}

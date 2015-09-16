/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics;

/**
 * Updates the physics system based on the passage of real time.
 */
public class RealtimePhysicsSystemManager extends PhysicsSystemManager
{
	// Prevents a time-step from ever being longer than this. If something
	// causes the thread to not be able to update for a while (OS stealing
	// resources) this prevents wild jumps in state.
	private final double maxTimeStep;

	// The minimum time allowed time-step (again in real computer time, not
	// simulated time).
	private final double minTimeStep;

	// Converts real passage of time to simulation time-steps. In units of
	// (simTimeUnit/second). Each time step will be
	// (real time passed since last update) * simulationRate.
	private final double simulationRate;

	// The last time the system was updated.
	private long lastUpdateTimeStamp;

	public RealtimePhysicsSystemManager(double maxTimeStep, double minTimeStep,
			double simulationRate)
	{
		super();
		this.maxTimeStep = maxTimeStep;
		this.minTimeStep = minTimeStep;
		this.simulationRate = simulationRate;
	}

	@Override
	public void start()
	{
		lastUpdateTimeStamp = System.nanoTime();
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * physics.managers.PhysicsSystemManager#updateImplement(physics.PhysicsSystem
	 * )
	 */
	@Override
	protected void updateImplement(PhysicsSystem physicsSystem)
	{
		double timeStep = elapsedTimeSinceLastUpdate() * simulationRate;

		if (timeStep > maxTimeStep)
		{
			timeStep = maxTimeStep;
		}
		else if (timeStep < minTimeStep)
		{
			timeStep = minTimeStep;
		}
		physicsSystem.update(timeStep);
	}

	/**
	 * Calculates the elapsed time since last update.
	 * 
	 * @return
	 */
	private double elapsedTimeSinceLastUpdate()
	{
		long currentTimeStamp = System.nanoTime();
		double elapsedTime = (currentTimeStamp - lastUpdateTimeStamp) / 1000000000.0f;
		lastUpdateTimeStamp = currentTimeStamp;
		return elapsedTime;
	}

}

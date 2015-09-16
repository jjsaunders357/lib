package com.pheiffware.lib.physics;

/**
 * Simulates a movement from an initial to final position based on an
 * acceleration, deceleration and maximum velocity curve.
 * 
 * Assumes constant acceleration to maximumVelocity and then constant
 * deceleration back to 0 at the end of the move. For short moves, maximum
 * velocity will never be attained.
 * 
 * This allows for infinite acceleration/deceleration
 * 
 * If moving in the negative direction, all distance quantities such as
 * totalDistance will still be reported as positive.
 * 
 * @author Stephen Pheiffer
 */
public class AccelerationSimulator
{
	private final double maxVelocity;
	private final double acceleration;

	private final double deceleration;
	private final double startPosition;
	private final double endPosition;
	// The time the motor spends accelerating
	private double accelerationTime;
	// The time the motor spends decelerating
	private double decelerationTime;
	// The distance the motor travels while accelerating
	private double accelerationDistance;
	// The distance the motor travels while decelerating
	private double decelerationDistance;
	// The time the motor spends at maximum velocity
	private final double maxVelocityTime;
	// The distance the motor travels while at maximum velocity
	private final double maxVelocityDistance;
	// The total distance traveled by the motor
	private final double totalDistance;

	// Is the motor moving positive or negative?
	private final double direction;

	/**
	 * Limitations: maxVelocity, acceleration, deceleration must all be > 0.
	 * 
	 * @param startPosition
	 * @param endPosition
	 * @param maxVelocity
	 * @param acceleration
	 *            Can properly handle acceleration = +inf
	 */
	public AccelerationSimulator(double startPosition, double endPosition,
			double maxVelocity, double acceleration)
	{
		this(startPosition, endPosition, maxVelocity, acceleration,
				acceleration);
	}

	/**
	 * Limitations: maxVelocity, acceleration, deceleration must all be > 0.
	 * 
	 * @param startPosition
	 * @param endPosition
	 * @param maxVelocity
	 * @param acceleration
	 *            Can properly handle acceleration = +inf
	 * @param deceleration
	 *            Can properly handle deceleration = +inf
	 */
	public AccelerationSimulator(double startPosition, double endPosition,
			double maxVelocity, double acceleration, double deceleration)
	{
		super();
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.maxVelocity = maxVelocity;
		this.acceleration = acceleration;
		this.deceleration = deceleration;

		if (acceleration != Double.POSITIVE_INFINITY)
		{
			accelerationTime = maxVelocity / acceleration;
			accelerationDistance = acceleration * accelerationTime
					* accelerationTime / 2.0;

		}
		else
		{
			accelerationTime = 0;
			accelerationDistance = 0;
		}
		if (deceleration != Double.POSITIVE_INFINITY)
		{
			decelerationTime = maxVelocity / deceleration;
			decelerationDistance = maxVelocity * decelerationTime
					- deceleration * decelerationTime * decelerationTime / 2.0;
		}
		else
		{
			decelerationTime = 0;
			decelerationDistance = 0;
		}
		totalDistance = Math.abs(endPosition - startPosition);
		direction = Math.signum(endPosition - startPosition);

		// Should never happen in infinite case
		if (accelerationDistance + decelerationDistance > totalDistance)
		{
			/**
			 * Special case, motor cannot reach top speed before it has to start
			 * decelerating.
			 */
			accelerationTime = Math
					.sqrt(2.0
							* totalDistance
							/ (acceleration * acceleration / deceleration + acceleration));
			accelerationDistance = acceleration * accelerationTime
					* accelerationTime / 2.0;

			decelerationDistance = totalDistance - accelerationDistance;
			decelerationTime = acceleration * accelerationTime / deceleration;
			maxVelocityTime = 0;
			maxVelocityDistance = 0;
		}
		else
		{
			maxVelocityDistance = totalDistance - accelerationDistance
					- decelerationDistance;
			maxVelocityTime = maxVelocityDistance / maxVelocity;
		}

	}

	/**
	 * Returns the current simulated position of the motor at the specified
	 * time. It is assumed to be at startPosition at elapsedTime = 0. The motor
	 * will accelerate, up to maximum velocity, and then decelerate back to 0
	 * such that it arrives exactly at endPosition, where it will remain for all
	 * future times.
	 * 
	 * There are no "units" used in the simulation. Distances are assumed to be
	 * in some consistent unit time is also in a consistent unit. Limitations:
	 * Negative time is undefined. Postconditions: None
	 * 
	 * @param time
	 *            The time to calculate the simulated position of the motor for
	 *            (starts at startPosition at time 0).
	 * @return The simulated position of the motor at the specified time.
	 */
	public double getPositionAtTime(double time)
	{

		if (time <= 0.0)
		{
			return startPosition;
		}
		else if (time <= accelerationTime)
		{
			// Still accelerating
			return startPosition + direction
					* (0.5 * acceleration * time * time);
		}
		else if (time <= accelerationTime + maxVelocityTime)
		{
			// Coasting at max speed
			return startPosition
					+ direction
					* (accelerationDistance + (time - accelerationTime)
							* maxVelocity);
		}
		else if (time <= accelerationTime + maxVelocityTime + decelerationTime)
		{
			// Decelerating
			double elapsedDecelerationTime = time
					- (accelerationTime + maxVelocityTime);

			// Max velocity may not have been achieved.
			double decelerationStartVelocity = accelerationTime
					* accelerationTime * acceleration / 2.0;

			return startPosition
					+ direction
					* (accelerationDistance + maxVelocityDistance
							+ decelerationStartVelocity
							* elapsedDecelerationTime - 0.5 * deceleration
							* elapsedDecelerationTime * elapsedDecelerationTime);
		}
		else
		{
			// Finished
			return endPosition;
		}
	}

	/**
	 * Checks if the motor will be finished moving by the specified elapsed time
	 * <dl>
	 * <dt>Limitations:</dt>
	 * <dd>None</dd>
	 * <dt>Postconditions:</dt>
	 * <dd>None
	 * 
	 * </dd>
	 * </dl>
	 */
	public boolean isFinished(double elapsedTime)
	{
		return elapsedTime >= accelerationTime + maxVelocityTime
				+ decelerationTime;
	}

	public double getMaxVelocity()
	{
		return maxVelocity;
	}

	public double getAcceleration()
	{
		return acceleration;
	}

	public double getDeceleration()
	{
		return deceleration;
	}

	public double getStartPosition()
	{
		return startPosition;
	}

	public double getEndPosition()
	{
		return endPosition;
	}

	public double getAccelerationTime()
	{
		return accelerationTime;
	}

	public double getDecelerationTime()
	{
		return decelerationTime;
	}

	public double getAccelerationDistance()
	{
		return accelerationDistance;
	}

	public double getDecelerationDistance()
	{
		return decelerationDistance;
	}

	public double getMaxVelocityTime()
	{
		return maxVelocityTime;
	}

	public double getMaxVelocityDistance()
	{
		return maxVelocityDistance;
	}

	public double getTotalDistance()
	{
		return totalDistance;
	}

	public double getTotalTime()
	{
		return accelerationTime + maxVelocityTime + decelerationTime;
	}

	public double getDirection()
	{
		return direction;
	}

}

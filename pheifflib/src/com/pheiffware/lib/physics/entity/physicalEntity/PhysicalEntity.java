/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.physics.InteractionException;
import com.pheiffware.lib.physics.entity.Entity;

/**
 * An entity with the concept of mass, force, acceleration, etc.
 */
// TODO: Remove rigid body package.
public abstract class PhysicalEntity extends Entity
{
	// Consider object stopped if its velocity falls below this
	private static final double STOPPED_VELOCITY_SQUARED = Math.pow(0.0000001,
			2);

	// The velocity of the entity
	public final Vec3D velocity;

	// How much relative velocity between objects is conserved. The coefficients
	// of the 2 objects are multiplied.
	public final double coefficientOfRestitution;

	// The mass
	public final double mass;

	// Pre-calculated inverse
	public final double inverseMass;

	// Pre-calculated root
	public final double sqrtMass;

	// Used to accumulate total force acting on entity during a time step
	private final Vec3D accumulatedForce;

	// (duh)
	private boolean ignoresGravity = false;

	public PhysicalEntity(Vec3D velocity, double mass,
			double coefficientOfRestitution)
	{
		this.velocity = new Vec3D(velocity);
		this.coefficientOfRestitution = coefficientOfRestitution;
		this.mass = mass;
		if (mass == Float.POSITIVE_INFINITY)
		{
			inverseMass = 0;
			sqrtMass = Float.POSITIVE_INFINITY;
		}
		else
		{
			inverseMass = 1.0f / mass;
			sqrtMass = (double) Math.sqrt(mass);
		}
		accumulatedForce = new Vec3D(0, 0, 0);
	}

	public void updateMotion(double elapsedTime)
	{
		double ax = accumulatedForce.x * inverseMass;
		double ay = accumulatedForce.y * inverseMass;
		double az = accumulatedForce.z * inverseMass;
		double atFactor = 0.5f * elapsedTime * elapsedTime;
		double tx = ax * atFactor + velocity.x * elapsedTime;
		double ty = ay * atFactor + velocity.y * elapsedTime;
		double tz = az * atFactor + velocity.z * elapsedTime;
		velocity.addTo(ax * elapsedTime, ay * elapsedTime, az * elapsedTime);
		move(tx, ty, tz);
		accumulatedForce.toZero();
	}

	public abstract void resolveCollision(PhysicalEntity physicalEntity,
			double elapsedTime) throws InteractionException;

	/**
	 * Move the entity's center and update all other related information such as
	 * bounding volume.
	 * 
	 * @param translation
	 */
	public abstract void move(final double tx, final double ty, final double tz);

	/**
	 * Convenience method for move (should not be used where efficiency matters)
	 * 
	 * @param translation
	 */
	public final void move(final Vec3D translation)
	{
		move(translation.x, translation.y, translation.z);
	}

	public final boolean hasMotionStopped()
	{
		return velocity.magnitudeSquared() < STOPPED_VELOCITY_SQUARED;
	}

	public final double getCoefficientOfRestitution()
	{
		return coefficientOfRestitution;
	}

	public void addForce(final Vec3D direction, final double magnitude)
	{
		accumulatedForce.x += direction.x * magnitude;
		accumulatedForce.y += direction.y * magnitude;
		accumulatedForce.z += direction.z * magnitude;
	}

	public boolean ignoresGravity()
	{
		return ignoresGravity;
	}

	public void setIgnoresGravity(boolean ignoreGravity)
	{
		this.ignoresGravity = ignoreGravity;
	}

	public void applyImpulse(final Vec3D impulse)
	{
		velocity.addTo(impulse);
	}

	public void applyImpulse(final Vec3D impulseNormal, final double magnitude)
	{
		velocity.addToScaledVector(impulseNormal, magnitude);
	}
}

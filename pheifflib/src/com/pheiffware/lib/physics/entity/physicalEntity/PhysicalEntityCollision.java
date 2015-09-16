package com.pheiffware.lib.physics.entity.physicalEntity;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.intersect.IntersectionInfo;

//TODO: If unitVelocity dot collisionTangent < cos (specialAngle) then rather than bounce, transfer all velocity so that it is along new tangent.  This will allow ramps to work without oddities.  If you are NOT under this special circumstance, then do the normal thing and completely ignore this rule.

/**
 * Describes information about a rigid body collision and provides calculation
 * utilities to get the results.
 */
public class PhysicalEntityCollision
{
	// TODO: Reexamine if this is necessary
	private static final double MAX_RELATIVE_STATIC_VELOCITIES = 5f;

	/**
	 * Calculates the impulse along a line of action (instantaneous change in
	 * momentum)
	 * 
	 * Note: this can accommodate the case that one entity's mass is infinite
	 * (an inverse mass of 0).
	 * 
	 * @param inverseMass1
	 * @param speed1
	 * @param inverseMass2
	 * @param speed2
	 * @param combinedCoefficientOfRestitution
	 * @return
	 */
	public static final double calcCollisionImpulse(final double inverseMass1,
			final double speed1, final double inverseMass2,
			final double speed2, final double combinedCoefficientOfRestitution)
	{
		return (speed2 - speed1) * (combinedCoefficientOfRestitution + 1)
				/ (inverseMass1 + inverseMass2);
	}

	// The 1st entity involved in the collision
	protected final PhysicalEntity entity1;

	// The 2nd entity involved in the collision
	protected final PhysicalEntity entity2;

	// Collision normal in direction from entity1 to entity2
	private final Vec3D collisionNormal;

	// How deep is the overlap along the line of the collisionNormal
	private final double penetration;

	// Amount of velocity1 along the normal of the collision
	private final double velocity1NormalComponent;

	// Amount of velocity2 along the normal of the collision (generally
	// negative)
	private final double velocity2NormalComponent;

	private final double relativeNormalVelocity;

	private double combinedCoefficientOfRestitution;

	public PhysicalEntityCollision(PhysicalEntity entity1,
			PhysicalEntity entity2, IntersectionInfo pointOfImpact)
	{
		this(entity1, entity2, pointOfImpact.intersectionNormal,
				pointOfImpact.penetration);
	}

	public PhysicalEntityCollision(PhysicalEntity entity1,
			PhysicalEntity entity2, Vec3D collisionNormal, double penetration)
	{
		this.entity1 = entity1;
		this.entity2 = entity2;
		this.collisionNormal = collisionNormal;
		this.penetration = penetration;
		velocity1NormalComponent = Vec3D.dot(entity1.velocity, collisionNormal);
		velocity2NormalComponent = Vec3D.dot(entity2.velocity, collisionNormal);
		relativeNormalVelocity = velocity1NormalComponent
				- velocity2NormalComponent;
		combinedCoefficientOfRestitution = entity1
				.getCoefficientOfRestitution()
				* entity2.getCoefficientOfRestitution();
		if (relativeNormalVelocity * combinedCoefficientOfRestitution < MAX_RELATIVE_STATIC_VELOCITIES)
		{
			combinedCoefficientOfRestitution = 0.0f;
		}
	}

	public final void resolve()
	{
		if (areApproaching())
		{
			addCollisionImpulses();
		}
		unEmbed();
	}

	private final boolean areApproaching()
	{
		return relativeNormalVelocity >= 0;
	}

	/**
	 * 
	 * Tangential collision: uJ gives the tangential impulse. Impulse represents
	 * the integral of force over time. uJ gives an approximation of what the
	 * bodies feel tangentially over the same time.
	 * 
	 * uJ is always along the tangential line of action which is vRelTangent
	 * (for this comment). 2 formulas: vRelTangent = normal X relativeVelocity X
	 * normal vRelTangent = relativeVelocity - normal * (normal dot relative
	 * velocity)
	 * 
	 * uJ can NEVER exceed |vRelTangent| / (1/m1 + 1/m2 + r1XunitVRelTangent/I1
	 * + r2XunitVRelTangent/I2) and should be clamped to this value. This max
	 * value is effective a coefficient of restitution of 0. In other words
	 * friction always acts as a collision with coefficient of restitution in
	 * the range [-1,0]. 0 = velocities are totally equalized by the collision
	 * -1 = velocities are unchanged.
	 * 
	 * Rolling friction: When rolling without slippage, |vRelTangent| == 0. This
	 * will result in the rolling continuing forever. To prevent this, all
	 * collisions come with an extra coefficient of rolling friction (crr). This
	 * works like u, but it ONLY affects relative velocity caused by rotation.
	 * 
	 * crrJ is applied to rotations. Like crrJ cannot exceed |vRelTangent| /
	 * (r1XunitVRelTangent/I1 + r2XunitVRelTangent/I2).
	 */

	// (speed2 - speed1) * (combinedCoefficientOfRestitution + 1) /
	// (inverseMass1 + inverseMass2);

	private void addCollisionImpulses()
	{
		// Calculate change in velocities along collision normal
		double impulse = calcCollisionImpulse(entity1.inverseMass,
				velocity1NormalComponent, entity2.inverseMass,
				velocity2NormalComponent, combinedCoefficientOfRestitution);
		if (entity1.inverseMass > 0)
		{
			entity1.applyImpulse(collisionNormal, impulse * entity1.inverseMass);
		}
		entity2.applyImpulse(collisionNormal, -impulse * entity2.inverseMass);

		// For tangential aspect of collision

	}

	/**
	 * Moves 2 entities' so that they are just touching given a collision.
	 * 
	 * Note: The first entity's mass may be infinity, but not the second.
	 * 
	 * @param collision
	 * @param entity1
	 * @param entity2
	 */
	private void unEmbed()
	{
		if (entity1.mass == Float.POSITIVE_INFINITY)
		{
			entity2.move(collisionNormal.x * penetration, collisionNormal.y
					* penetration, collisionNormal.z * penetration);
			return;
		}

		final double entitiy1SeparationFactor;
		if (entity1.sqrtMass == entity2.sqrtMass)
		{
			entitiy1SeparationFactor = -0.5f;
		}
		else
		{
			entitiy1SeparationFactor = -entity1.sqrtMass
					/ (entity1.sqrtMass + entity2.sqrtMass);
		}
		final double separationMagnitude1 = penetration
				* entitiy1SeparationFactor;
		final double separationMagnitude2 = penetration
				* (1 + entitiy1SeparationFactor);

		entity1.move(collisionNormal.x * separationMagnitude1,
				collisionNormal.y * separationMagnitude1, collisionNormal.z
						* separationMagnitude1);
		entity2.move(collisionNormal.x * separationMagnitude2,
				collisionNormal.y * separationMagnitude2, collisionNormal.z
						* separationMagnitude2);
	}

	// Vec3F tangentialVelocity = new Vec3F(entity1.velocity);
	// tangentialVelocity.subFrom(entity2.velocity);
	// tangentialVelocity.crossByLeft(collisionNormal);
	// tangentialVelocity.crossByRight(collisionNormal);
	//
	// // TODO: Only relevant for 2D
	// tangentialVelocity.z = 0;
	// tangentialVelocity.normalize();

}
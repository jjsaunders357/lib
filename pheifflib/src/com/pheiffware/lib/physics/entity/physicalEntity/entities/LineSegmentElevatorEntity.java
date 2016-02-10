/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity.entities;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;

//TODO: Make this movement algorithm more generally applied to any entity
/**
 * 
 */
public class LineSegmentElevatorEntity extends LineSegmentEntity
{
	private Vec3D maxVelocity;
	private double acceleration;
	private Vec3D direction;

	public LineSegmentElevatorEntity(Vec3D p1, Vec3D p2, int normalSide,
			double mass, double coefficientOfRestitution, Vec3D maxVelocity,
			double acceleration)
	{
		super(p1, p2, normalSide, new Vec3D(0, 0, 0), mass,
				coefficientOfRestitution);
		this.maxVelocity = maxVelocity;
		this.direction = Vec3D.normalize(maxVelocity);
		this.acceleration = acceleration;
		setIgnoresGravity(true);
	}

	@Override
	public void ai(double elapsedTime, PhysicsSystem physicsSystem)
	{
		// Figure out force required to get to full speed

		// Amount of speed in the direction of maxVelocity
		double requiredVelocity = Vec3D
				.subDot(maxVelocity, velocity, direction);

		// Required acceleration
		double requiredAcceleration;

		requiredAcceleration = requiredVelocity / elapsedTime;
		if (requiredAcceleration > acceleration)
		{
			requiredAcceleration = acceleration;
		}

		addForce(direction, requiredAcceleration * mass);
	}
}

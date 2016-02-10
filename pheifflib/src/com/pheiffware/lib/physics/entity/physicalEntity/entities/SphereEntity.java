package com.pheiffware.lib.physics.entity.physicalEntity.entities;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.shapes.Sphere;
import com.pheiffware.lib.physics.InteractionException;
import com.pheiffware.lib.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.PhysicalEntityCollision;

/**
 * Represents a spherical entity.
 */
public class SphereEntity extends PhysicalEntity
{
	public final Sphere sphere;

	public SphereEntity(final Vec3D center, final Vec3D velocity,
			final double mass, final double coefficientOfRestitution,
			final double radius)
	{
		super(velocity, mass, coefficientOfRestitution);
		sphere = new Sphere(center, radius);
	}

	// TODO: Optimize
	public final static void resolveSphereSphereCollision(
			final SphereEntity sphere1, final SphereEntity sphere2)
	{
		double xdiff = sphere2.sphere.center.x - sphere1.sphere.center.x;
		double ydiff = sphere2.sphere.center.y - sphere1.sphere.center.y;
		double zdiff = sphere2.sphere.center.z - sphere1.sphere.center.z;

		double distance = (double) Math.sqrt(xdiff * xdiff + ydiff * ydiff
				+ zdiff * zdiff);
		double penetration = sphere2.sphere.radius + sphere1.sphere.radius
				- distance;
		if (penetration > 0)
		{
			double invDistance = 1.0f / distance;
			PhysicalEntityCollision collision = new PhysicalEntityCollision(
					sphere1, sphere2, new Vec3D(xdiff * invDistance, ydiff
							* invDistance, zdiff * invDistance), penetration);
			collision.resolve();
		}
	}

	public void move(final double tx, final double ty, final double tz)
	{
		sphere.center.addTo(tx, ty, tz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pheiffware.sphereadventure.engine.physics.entity.physicalEntity.
	 * PhysicalEntity
	 * #resolveCollision(com.pheiffware.sphereadventure.engine.physics
	 * .entity.physicalEntity.PhysicalEntity, double)
	 */
	@Override
	public void resolveCollision(PhysicalEntity physicalEntity,
			double elapsedTime) throws InteractionException
	{
		// physicalEntity.resolveCollision(this, elapsedTime);
		if (physicalEntity instanceof SphereEntity)
		{
			SphereEntity.resolveSphereSphereCollision(this,
					(SphereEntity) physicalEntity);
		}
		else if (physicalEntity instanceof LineSegmentElevatorEntity)
		{
			LineSegmentEntity.resolveLineSphereCollision(
					(LineSegmentEntity) physicalEntity, this, elapsedTime);
		}
	}

	public final double getRadius()
	{
		return sphere.radius;
	}

	public final Vec3D getCenter()
	{
		return sphere.center;
	}
}

/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity.boundingVolume;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * A bounding sphere around an object.
 */
public class BoundingSphere
{
	public boolean overlapping(BoundingSphere otherSphere)
	{
		double distanceSquared = Vec3D.distanceSquared(center,
				otherSphere.center);
		return distanceSquared < (radius + otherSphere.radius)
				* (radius + otherSphere.radius);
	}

	private Vec3D center;
	private double radius;

	public BoundingSphere(Vec3D center, double radius)
	{
		this.center = center;
		this.radius = radius;
	}

	/**
	 * @param boundingCenter
	 * @param boundingRadius
	 */
	public final void modify(Vec3D center, double radius)
	{
		this.center = center;
		this.radius = radius;
	}

	public final void move(final double x, final double y, final double z)
	{
		center.addTo(x, y, z);
	}

	public final Vec3D getCenter()
	{
		return center;
	}

	public final double getRadius()
	{
		return radius;
	}

}

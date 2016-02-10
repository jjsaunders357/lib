/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity.entities;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * A static wall.
 */
public class WallEntity extends LineSegmentEntity
{
	public WallEntity(Vec3D p1, Vec3D p2, int normalSide, Vec3D velocity,
			double coefficientOfRestitution)
	{
		super(p1, p2, normalSide, velocity, Float.POSITIVE_INFINITY,
				coefficientOfRestitution);
	}

	@Override
	public void updateMotion(double elapsedTime)
	{
		// Never moves
	}
}

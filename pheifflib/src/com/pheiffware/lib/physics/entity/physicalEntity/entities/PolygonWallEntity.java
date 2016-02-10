/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity.entities;

import com.pheiffware.lib.geometry.Vec3D;

/**
 *
 */
public class PolygonWallEntity extends PolygonEntity
{
	public PolygonWallEntity(Vec3D velocity, double coefficientOfRestitution,
			Vec3D[] points)
	{
		super(velocity, Double.POSITIVE_INFINITY, coefficientOfRestitution,
				points);
	}

	@Override
	public void updateMotion(double elapsedTime)
	{
		// Never moves
	}
}

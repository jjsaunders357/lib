/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics.entity.physicalEntity.entities;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.intersect.IntersectCalc;
import com.pheiffware.lib.geometry.intersect.IntersectionInfo;
import com.pheiffware.lib.geometry.shapes.Sphere;
import com.pheiffware.lib.geometry.shapes.LineSegment;
import com.pheiffware.lib.physics.InteractionException;
import com.pheiffware.lib.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.PhysicalEntityCollision;
import com.pheiffware.lib.physics.entity.physicalEntity.boundingVolume.BoundingSphere;

/**
 * A polygon entity which can interact with circles.
 */
public class PolygonEntity extends PhysicalEntity
{
	public static void resolvePolygonSphereCollision(
			PolygonEntity polygonEntity, SphereEntity sphereEntity,
			double elapsedTime)
	{
		for (LineSegment lineSegment : polygonEntity.lineSegments)
		{
			IntersectionInfo pointOfImpact = IntersectCalc.calcIntersect2D(
					lineSegment, new Sphere(sphereEntity.getCenter(),
							sphereEntity.getRadius()));
			if (pointOfImpact != null)
			{
				PhysicalEntityCollision collision = new PhysicalEntityCollision(
						polygonEntity, sphereEntity, pointOfImpact);
				collision.resolve();
			}
		}
	}

	// All the points composing the polygon
	private final Vec3D[] points;

	// A set of lineSegment objects (used for collisions)
	private final LineSegment[] lineSegments;

	private BoundingSphere boundingSphere;

	public PolygonEntity(Vec3D velocity, double mass,
			double coefficientOfRestitution, Vec3D[] points)
	{
		super(velocity, mass, coefficientOfRestitution);
		this.points = copyPoints(points);
		lineSegments = new LineSegment[points.length];
		for (int i = 0; i < points.length - 1; i++)
		{
			lineSegments[i] = new LineSegment(this.points[i],
					this.points[i + 1], -1);
		}
		lineSegments[lineSegments.length - 1] = new LineSegment(
				this.points[points.length - 1], this.points[0], 1);
		calcBoundingVolume();
	}

	/**
	 * @param points2
	 * @return
	 */
	private final Vec3D[] copyPoints(Vec3D[] points)
	{
		Vec3D[] pointsCopy = new Vec3D[points.length];
		for (int i = 0; i < points.length; i++)
		{
			pointsCopy[i] = new Vec3D(points[i]);
		}
		return pointsCopy;
	}

	/**
	 * Calculates the optimal spherical bounding volume (slowly)
	 */
	private void calcBoundingVolume()
	{
		Vec3D p1 = null;
		Vec3D p2 = null;
		double longestDistanceSquared = 0.0f;

		for (int i = 0; i < points.length - 1; i++)
		{
			for (int j = i; j < points.length; j++)
			{
				double distanceSquared = Vec3D.distanceSquared(points[i],
						points[j]);
				if (distanceSquared > longestDistanceSquared)
				{
					longestDistanceSquared = distanceSquared;
					p1 = points[i];
					p2 = points[j];
				}
			}
		}
		boundingSphere = new BoundingSphere(
				Vec3D.scale(Vec3D.add(p1, p2), 0.5f),
				(double) Math.sqrt(longestDistanceSquared) / 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * physics.entity.rigidBody.RigidBodyEntity#calcCollision(physics.entity
	 * .rigidBody.RigidBodyEntity)
	 */
	@Override
	public void resolveCollision(PhysicalEntity physicalEntity,
			double elapsedTime) throws InteractionException
	{
		if (physicalEntity instanceof SphereEntity)
		{
			resolvePolygonSphereCollision(this, (SphereEntity) physicalEntity,
					elapsedTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physics.entity.Entity#move(vec3f.Vec3F)
	 */
	@Override
	public void move(final double tx, final double ty, final double tz)
	{
		for (Vec3D point : points)
		{
			point.addTo(tx, ty, tz);
		}
		boundingSphere.move(tx, ty, tz);
	}

	public final LineSegment[] getLineSegments()
	{
		return lineSegments;
	}

	public BoundingSphere getBoundingSphere()
	{
		return boundingSphere;
	}

}

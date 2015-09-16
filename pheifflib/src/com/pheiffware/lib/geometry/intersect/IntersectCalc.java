package com.pheiffware.lib.geometry.intersect;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.shapes.OrientedLineSegment;
import com.pheiffware.lib.geometry.shapes.Sphere;

public class IntersectCalc
{
	/**
	 * Calculates an intersection between 2 spheres. The intersection normal
	 * will face out from sphere1 towards sphere2.
	 * 
	 * @param sphere1
	 * @param sphere2
	 * @return
	 */
	public static IntersectionInfo calcIntersect3D(final Sphere sphere1,
			final Sphere sphere2)
	{
		double xdiff = sphere2.center.x - sphere1.center.x;
		double ydiff = sphere2.center.y - sphere1.center.y;
		double zdiff = sphere2.center.z - sphere1.center.z;

		double distance = (double) Math.sqrt(xdiff * xdiff + ydiff * ydiff
				+ zdiff * zdiff);
		double penetration = sphere2.radius + sphere1.radius - distance;
		if (penetration <= 0)
		{
			return null;
		}
		else
		{
			return new IntersectionInfo(new Vec3D(xdiff, ydiff, zdiff),
					penetration);
		}
	}

	/**
	 * Calculates intersections between line segment and sphere in 2D (assumes
	 * z's are equal). The normal of the intersection is facing toward the
	 * sphere and away from the line segment.
	 * 
	 * null is returned if there is no intersection.
	 * 
	 * @param lineSegment
	 * @param sphere
	 * @return
	 */
	public static IntersectionInfo calcIntersect2D(
			OrientedLineSegment lineSegment, Sphere sphere)
	{
		// All references to line refer to the infinite line as opposed to the
		// segment.
		double centerToLineDistance = Vec3D.subDot(sphere.center,
				lineSegment.p1, lineSegment.unitNormal);

		// The center of the sphere is actually past the line.
		if (centerToLineDistance < 0)
		{
			return null;
		}

		double linePenetration = sphere.radius - centerToLineDistance;

		// The sphere is penetrating the line
		if (linePenetration > 0)
		{
			Vec3D sphereCenter = sphere.center;
			// Distance, projected along line, from p1 (can be negative)
			double positionOnLine = lineSegment
					.getProjectedPositionOnLine(sphereCenter);

			// Quick check for no collision
			if (positionOnLine <= -sphere.radius
					|| positionOnLine >= lineSegment.length + sphere.radius)
			{
				return null;
			}
			else
			{
				Vec3D collisionNormal;
				double penetration;
				if (positionOnLine < 0)
				{
					collisionNormal = Vec3D.sub(sphereCenter, lineSegment.p1);
					double collisionNormalLength = collisionNormal.magnitude();
					penetration = sphere.radius - collisionNormalLength;
					if (penetration < 0)
					{
						return null;
					}
					collisionNormal.scaleBy(1.0f / collisionNormalLength);
				}
				else if (positionOnLine > lineSegment.length)
				{
					collisionNormal = Vec3D.sub(sphereCenter, lineSegment.p2);
					double collisionNormalLength = collisionNormal.magnitude();
					penetration = sphere.radius - collisionNormalLength;
					if (penetration < 0)
					{
						return null;
					}
					collisionNormal.scaleBy(1.0f / collisionNormalLength);
				}
				else
				{
					collisionNormal = lineSegment.unitNormal;
					penetration = linePenetration;
				}
				return new IntersectionInfo(collisionNormal, penetration);
			}
		}
		else
		{
			return null;
		}
	}
}

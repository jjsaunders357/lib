package com.pheiffware.lib.geometry;

import com.pheiffware.lib.geometry.shapes.LineSegment;
import com.pheiffware.lib.geometry.shapes.Sphere;

public class Geocalc
{
	/**
	 * Projects where a point would fall on a given ray's line.  The projection point may be behind the origin (not technically on the ray).
	 * @param point
	 * @param origin
	 * @param direction
	 * @return
	 */
	public static Vec3D projectPointOntoRay(Vec3D point, Vec3D origin, Vec3D direction)
	{
		// Project center of sphere onto line
		double distanceAlongRay = Vec3D.subDot(point, origin, direction);
		return Vec3D.add(origin, Vec3D.scale(direction, distanceAlongRay));
	}

	/**
	 * Calculates where a ray 1st intersects a sphere.  Assumes ray's origin is outside of sphere (will return negative distance otherwise).
	 * Returns null if no intersection occurs.
	 * @param sphere
	 * @param origin
	 * @param direction
	 * @return
	 */
	public static Vec3D rayIntersectSphere(Sphere sphere, Vec3D origin, Vec3D direction)
	{
		// Ray facing away from sphere
		if (Vec3D.dot(Vec3D.sub(sphere.center, origin), direction) < 0)
		{
			return null;
		}
		Vec3D centerProjectRay = projectPointOntoRay(sphere.center, origin, direction);
		double distanceToCenter = Vec3D.distance(centerProjectRay, sphere.center);
		if (distanceToCenter > sphere.radius)
		{
			return null;
		}

		// centerProjectRay is somewhere inside sphere (or right on the surface). Move along ray until we ge to the surface of the sphere.
		double offsetAlongRayToIntersection = Math.sqrt(sphere.radius * sphere.radius - distanceToCenter * distanceToCenter);
		centerProjectRay.addToScaledVector(direction, -offsetAlongRayToIntersection);
		return centerProjectRay;
	}

	/**
	 * Calculates the distance from the origin of a ray to a sphere.  Assumes origin is not in sphere (will return negative distance otherwise).
	 * Returns Double.Nan if no intersection happens.
	 * @param sphere
	 * @param origin
	 * @param angleInRadians
	 * @return
	 */
	public static double distanceRayToSphere(Sphere sphere, Vec3D origin, Vec3D direction)
	{
		Vec3D intersectionPoint = rayIntersectSphere(sphere, origin, direction);
		if (intersectionPoint == null)
		{
			return Double.NaN;
		}
		return Vec3D.distance(origin, intersectionPoint);
	}

	/**
	 * Calculates the distance from the origin of a ray to a line in 2 dimensions (considers segment an infinite line).
	 * Returns Double.Nan if ray faces away from line.  Returns Double.POSITIVE_INFINITY if ray is parallel to line.
	 * @param sphere
	 * @param origin
	 * @param angleInRadians
	 * @return
	 */
	public static double distanceRayToLine(LineSegment line, Vec3D origin, Vec3D direction)
	{

		Vec3D projection = projectPointOntoRay(origin, line.p1, line.direction);
		if (Vec3D.subDot(projection, origin, direction) < 0)
		{
			return Double.NaN;
		}
		else if (Vec3D.subDot(projection, origin, direction) == 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		return Math.abs(Vec3D.distance(projection, origin) / Vec3D.dot(line.unitNormal, direction));
	}

	/**
	 * Calculates the distance from the origin of a ray to a line segment in 2 dimensions.
	 * Returns Double.Nan if ray faces away from line.  Returns Double.POSITIVE_INFINITY if ray is parallel to line.
	 * @param sphere
	 * @param origin
	 * @param angleInRadians
	 * @return
	 */
	public static double distanceRayToLineSegment2D(LineSegment line, Vec3D origin, Vec3D direction)
	{
		double distanceRayToLine = distanceRayToLine(line, origin, direction);
		if (distanceRayToLine == Double.NaN)
		{
			return Double.NaN;
		}
		else if (distanceRayToLine == Double.POSITIVE_INFINITY)
		{
			return Double.POSITIVE_INFINITY;
		}
		Vec3D intersectionPoint = Vec3D.add(origin, Vec3D.scale(direction, distanceRayToLine));
		if (!line.isProjectedPointOnLineSegment(intersectionPoint))
		{
			return Double.POSITIVE_INFINITY;
		}
		return distanceRayToLine;
	}
}

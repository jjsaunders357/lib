/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.geometry.shapes;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * Represents a line segment between p1 and p2.
 */
public final class LineSegment extends SimpleLineSegment
{
	// Which side the line is "facing". Rotates normal this angle from the
	// tangent
	private int normalSide;

	// Unit vector in direction of the line
	public final Vec3D direction;

	// Unit vector perpendicular to line (and z-axis)
	public final Vec3D unitNormal;

	// Length
	public double length;

	public LineSegment(double x1, double y1, double x2, double y2)
	{
		this(new Vec3D(x1, y1, 0), new Vec3D(x2, y2, 0));
	}

	public LineSegment(Vec3D p1, Vec3D p2)
	{
		this(p1, p2, 1);
	}

	public LineSegment(Vec3D p1, Vec3D p2, int normalSide)
	{
		super(p1, p2);
		this.normalSide = normalSide;
		direction = new Vec3D(0, 0, 0);
		unitNormal = new Vec3D(0, 0, 0);
		reshape();
	}

	/**
	 * Call if the underlying vertices move relative to each other to redefine
	 * the line segment. If they both simply translate together, this is
	 * unnecessary.
	 */
	public final void reshape()
	{
		direction.x = p2.x - p1.x;
		direction.y = p2.y - p1.y;
		direction.z = p2.z - p1.z;
		length = direction.magnitude();
		direction.scaleBy(1.0f / length);
		unitNormal.set(direction);
		unitNormal.rotate2D(0, normalSide);
	}

	/**
	 * Gets the position of point projected onto the line. Will return 0 at p1
	 * and |p2| at p2
	 * 
	 * @param point
	 * @return
	 */
	public double getProjectedPositionOnLine(Vec3D point)
	{
		return Vec3D.subDot(point, p1, direction);
	}

	/**
	 * Given a point, it calculates its "parametric position" on the line.
	 * p1 == 0
	 * p2 == 1
	 * If the given point in not on the line, its projection on the line is used.
	 * @param point
	 * @return
	 */
	public double getParametricPosition(Vec3D point)
	{
		return Vec3D.subDot(point, p1, direction) / length;
	}

	/**
	 * Tests if the point's position, projected on the line, is within p1 and p2.
	 * @param point
	 * @return
	 */
	public boolean isProjectedPointOnLineSegment(Vec3D point)
	{
		double parametricPosition = getParametricPosition(point);
		return parametricPosition >= 0 && parametricPosition <= 1.0;
	}

	public final double getLength()
	{
		return length;
	}

	public final Vec3D getUnitTangent()
	{
		return direction;
	}

	public final Vec3D getUnitNormal()
	{
		return unitNormal;
	}

}

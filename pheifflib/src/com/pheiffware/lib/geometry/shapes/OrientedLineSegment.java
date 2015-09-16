/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.geometry.shapes;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * Represents a line segment between p1 and p2.
 */
public final class OrientedLineSegment extends LineSegment
{
	// Which side the line is "facing". Rotates normal this angle from the
	// tangent
	private int normalSide;

	// Unit vector in direction of the line
	public final Vec3D unitTangent;

	// Unit vector perpendicular to line (and z-axis)
	public final Vec3D unitNormal;

	// Length
	public double length;

	public OrientedLineSegment(Vec3D p1, Vec3D p2, int normalSide)
	{
		super(p1, p2);
		this.normalSide = normalSide;
		unitTangent = new Vec3D(0, 0, 0);
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
		unitTangent.x = p2.x - p1.x;
		unitTangent.y = p2.y - p1.y;
		unitTangent.z = p2.z - p1.z;
		length = unitTangent.magnitude();
		unitTangent.scaleBy(1.0f / length);
		unitNormal.set(unitTangent);
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
		return Vec3D.subDot(point, p1, unitTangent);
	}

	public final double getLength()
	{
		return length;
	}

	public final Vec3D getUnitTangent()
	{
		return unitTangent;
	}

	public final Vec3D getUnitNormal()
	{
		return unitNormal;
	}

}

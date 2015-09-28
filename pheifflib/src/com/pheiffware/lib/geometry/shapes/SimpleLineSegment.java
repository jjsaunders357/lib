package com.pheiffware.lib.geometry.shapes;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * A simple representation of a line segment between two points.  This does not pre-compute and useful values.
 * @author Steve
 *
 */
public class SimpleLineSegment
{
	// End point 1
	public final Vec3D p1;

	// End point 2
	public final Vec3D p2;

	public SimpleLineSegment(Vec3D p1, Vec3D p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	public SimpleLineSegment(double x1, double y1, double x2, double y2)
	{
		p1 = new Vec3D(x1, y1, 0);
		p2 = new Vec3D(x2, y2, 0);
	}

	public SimpleLineSegment(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		p1 = new Vec3D(x1, y1, z1);
		p2 = new Vec3D(x2, y2, z2);
	}
}

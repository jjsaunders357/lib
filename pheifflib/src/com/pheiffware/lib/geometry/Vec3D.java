package com.pheiffware.lib.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.Serializable;

/**
 * Represents a 3d vector/point. For efficiency (especially on Android), this
 * class is mutable.
 */
public class Vec3D implements Serializable
{
	private static final long serialVersionUID = 377896764022380713L;

	public static final Vec3D add(final Vec3D v1, final Vec3D v2)
	{
		return new Vec3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static final Vec3D sub(final Vec3D v1, final Vec3D v2)
	{
		return new Vec3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static final double dot(final Vec3D v1, final Vec3D v2)
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public static final Vec3D cross(final Vec3D v1, final Vec3D v2)
	{
		return new Vec3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
	}

	/**
	 * (vec1-vec2) * dotVec
	 * 
	 * @param vec1
	 * @param vec2
	 * @param dotVec
	 * @return
	 */
	public static final double subDot(Vec3D vec1, Vec3D vec2, Vec3D dotVec)
	{
		return (vec1.x - vec2.x) * dotVec.x + (vec1.y - vec2.y) * dotVec.y + (vec1.z - vec2.z) * dotVec.z;
	}

	public static final Vec3D scale(Vec3D vec, double scale)
	{
		return new Vec3D(vec.x * scale, vec.y * scale, vec.z * scale);
	}

	public static double distance(final Vec3D v1, final Vec3D v2)
	{
		return (double) Math.sqrt(distanceSquared(v1, v2));
	}

	public static double distanceSquared(final Vec3D v1, final Vec3D v2)
	{
		double xdiff = (v1.x - v2.x);
		double ydiff = (v1.y - v2.y);
		double zdiff = (v1.z - v2.z);
		return xdiff * xdiff + ydiff * ydiff + zdiff * zdiff;
	}

	public static Vec3D normalize(final Vec3D v1)
	{
		final double magnitude = v1.magnitude();
		final Vec3D result = new Vec3D(v1.x / magnitude, v1.y / magnitude, v1.z / magnitude);
		return result;
	}

	public static Vec3D average(Vec3D p1, Vec3D p2)
	{
		return lerp(p1, p2, 0.5);
	}

	/**
	 * Linear interpolation between 2 points. If weight == 0, then v1 equivalent
	 * is returned, weight == 1 then v2 equivalent is returned.
	 * 
	 * @param v1
	 * @param v2
	 * @param weight
	 * @return
	 */
	public static Vec3D lerp(final Vec3D v1, final Vec3D v2, double weight)
	{
		return add(scale(v1, 1 - weight), scale(v2, weight));
	}

	public static Vec3D calcCenter(Vec3D[] points)
	{
		Vec3D center = new Vec3D(0, 0, 0);
		for (Vec3D point : points)
		{
			center.addTo(point);
		}
		center.scaleBy(1.0f / points.length);
		return new Vec3D(center.x, center.y, center.z);
	}

	public static Vec3D rotate2D(Vec3D v1, double angleRadians)
	{
		v1 = new Vec3D(v1);
		v1.rotate2D(angleRadians);
		return v1;
	}

	public double x, y, z;

	public Vec3D(final double x, final double y, final double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3D(final Vec3D vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public final void toZero()
	{
		x = 0;
		y = 0;
		z = 0;
	}

	public void set(Vec3D vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public final double magnitudeSquared()
	{
		return x * x + y * y + z * z;
	}

	public final double magnitude()
	{
		return (double) Math.sqrt(magnitudeSquared());
	}

	public void normalize()
	{
		final double magnitude = magnitude();
		x = x / magnitude;
		y = y / magnitude;
		z = z / magnitude;
	}

	public final void addTo(final double x, final double y, final double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public final void addTo(final Vec3D vec)
	{
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	public final void addToScaledVector(final Vec3D vec, final double scaleVec)
	{
		x += vec.x * scaleVec;
		y += vec.y * scaleVec;
		z += vec.z * scaleVec;
	}

	public final void subFrom(final double x, final double y, final double z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	public final void subFromScaledVector(final Vec3D vec, final double scale)
	{
		x -= vec.x * scale;
		y -= vec.y * scale;
		z -= vec.z * scale;
	}

	public final void subFrom(final Vec3D vec)
	{
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}

	public final void scaleBy(final double scale)
	{
		x *= scale;
		y *= scale;
		z *= scale;
	}

	public double dotBy(Vec3D vec)
	{
		return x * vec.x + y * vec.y + z * vec.z;
	}

	/**
	 * Cross product: vec X this
	 * 
	 * @param collisionNormal
	 */
	public void crossByLeft(Vec3D vec)
	{
		double tempx = vec.y * z - vec.z * y;
		double tempy = vec.z * x - vec.x * z;
		z = vec.x * y - vec.y * x;
		x = tempx;
		y = tempy;
	}

	/**
	 * Cross product: this X vec
	 * 
	 * @param vec
	 */
	public void crossByRight(Vec3D vec)
	{
		double tempx = y * vec.z - z * vec.y;
		double tempy = z * vec.x - x * vec.z;
		z = x * vec.y - y * vec.x;
		x = tempx;
		y = tempy;
	}

	// **************************2D special functions**************************
	// These all operate on the vector's x,y components, treating them as though
	// they are in a plane.

	public final void rotate902D()
	{
		double temp = x;
		x = y;
		y = -temp;
	}

	public final double getAngle2D()
	{
		return (double) Math.atan2(y, x);
	}

	public final double getRelativeAngle2D(final Vec3D v1)
	{
		double angle;
		angle = v1.getAngle2D() - getAngle2D();
		if (angle < 0.0) angle += 2 * PI;
		return angle;
	}

	public final void rotate2D(final double cosAngle, final double sinAngle)
	{
		double temp = x * cosAngle - y * sinAngle;
		y = x * sinAngle + y * cosAngle;
		x = temp;
	}

	public final void rotate2D(final double angleRadians)
	{
		double c = cos(angleRadians);
		double s = sin(angleRadians);
		rotate2D(c, s);
	}

	public final void rotateAround2D(final double angleRadians, final Vec3D centerOfRotation)
	{
		x -= centerOfRotation.x;
		y -= centerOfRotation.y;
		rotate2D(angleRadians);
		x += centerOfRotation.x;
		y += centerOfRotation.y;
	}

	@Override
	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")";
	}

	@Override
	public boolean equals(Object obj)
	{
		Vec3D vec3F = (Vec3D) obj;
		if (vec3F.x == x && vec3F.y == y && vec3F.z == z)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return Double.hashCode(x) + Double.hashCode(y) * 37 + Double.hashCode(z) * 10001;
	}

}

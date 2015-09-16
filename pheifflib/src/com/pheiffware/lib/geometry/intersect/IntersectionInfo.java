package com.pheiffware.lib.geometry.intersect;

import com.pheiffware.lib.geometry.Vec3D;

public class IntersectionInfo
{
	public final Vec3D intersectionNormal;
	public final double penetration;

	public IntersectionInfo(Vec3D intersectionNormal, double penetration)
	{
		super();

		this.intersectionNormal = intersectionNormal;
		this.penetration = penetration;
	}
}

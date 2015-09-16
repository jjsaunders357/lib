package com.pheiffware.lib.physics.entity;

import com.pheiffware.lib.physics.PhysicsSystem;

public abstract class Entity implements Cloneable
{
	// Used for debugging
	public String name = "";

	public Entity()
	{

	}

	/**
	 * Performs anything the entity wants on itself and the rest of the world.
	 * 
	 * @param elapsedTime
	 * @param physicsSystem
	 */
	public void ai(double elapsedTime, PhysicsSystem physicsSystem)
	{

	}

	public Entity copyForRender()
	{
		try
		{
			return (Entity) super.clone();
		}
		catch (CloneNotSupportedException exception)
		{
			throw new RuntimeException("Not cloneable!");
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

}

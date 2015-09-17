package com.pheiffware.lib.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.pheiffware.lib.log.Log;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.lib.simulation.Simulation;

public class PhysicsSystem implements Simulation<List<Entity>>
{
	private static final int maxNumEntities = 1000;
	private int numEntities;
	private int numStaticEntities;
	private int numDynamicEntities;
	private final Entity[] entities;
	private final PhysicalEntity[] staticEntities;
	private final PhysicalEntity[] dynamicEntities;

	private double totalRunTime;

	public PhysicsSystem()
	{
		numEntities = 0;
		numStaticEntities = 0;
		numDynamicEntities = 0;
		entities = new Entity[maxNumEntities];
		dynamicEntities = new PhysicalEntity[maxNumEntities];
		staticEntities = new PhysicalEntity[maxNumEntities];
		reset();
	}

	/**
	 * Reset the physics system to a blank state.
	 */
	public void reset()
	{
		numEntities = 0;
		numStaticEntities = 0;
		numDynamicEntities = 0;
		totalRunTime = 0f;
	}

	@Override
	public void timeStep(double absoluteTime, double elapsedTime)
	{
		try
		{
			totalRunTime += elapsedTime;
			try
			{
				runAI(elapsedTime);
				updateMotion(elapsedTime);
				resolveCollisions(elapsedTime);
			}
			catch (InteractionException e)
			{
				Log.error("FAIL!", e);
				System.exit(0);
			}
		}
		catch (Exception e)
		{
			Log.error("FAIL!", e);
			System.exit(0);
		}
	}

	/**
	 * Copies the state of all entities, at least well enough that they can be
	 * drawn and are safe for access (may not be full serialization).
	 */
	@Override
	public List<Entity> copyState()
	{
		try
		{
			List<Entity> copyOfEntities = new ArrayList<Entity>(entities.length + staticEntities.length + dynamicEntities.length);
			for (int i = 0; i < numEntities; i++)
			{
				copyOfEntities.add(entities[i].copyForRender());
			}
			for (int i = 0; i < numStaticEntities; i++)
			{
				copyOfEntities.add(staticEntities[i].copyForRender());
			}
			for (int i = 0; i < numDynamicEntities; i++)
			{
				copyOfEntities.add(dynamicEntities[i].copyForRender());
			}
			return copyOfEntities;
		}
		catch (Exception e)
		{
			Log.error("FAIL!", e);
			System.exit(0);
			return new ArrayList<Entity>();
		}

	}

	private void runAI(double elapsedTime)
	{
		for (int i = 0; i < numEntities; i++)
		{
			entities[i].ai(elapsedTime, this);
		}
		for (int i = 0; i < numStaticEntities; i++)
		{
			staticEntities[i].ai(elapsedTime, this);
		}
		for (int i = 0; i < numDynamicEntities; i++)
		{
			dynamicEntities[i].ai(elapsedTime, this);
		}
	}

	private void updateMotion(double elapsedTime)
	{
		for (int i = 0; i < numStaticEntities; i++)
		{
			staticEntities[i].updateMotion(elapsedTime);
		}
		for (int i = 0; i < numDynamicEntities; i++)
		{
			dynamicEntities[i].updateMotion(elapsedTime);
		}
	}

	private void resolveCollisions(double elapsedTime) throws InteractionException
	{
		for (int i = 0; i < numStaticEntities; i++)
		{
			PhysicalEntity staticEntity = staticEntities[i];
			for (int j = 0; j < numDynamicEntities; j++)
			{
				PhysicalEntity entity2 = dynamicEntities[j];
				staticEntity.resolveCollision(entity2, elapsedTime);
			}
		}

		int innerSize = numDynamicEntities;
		int outerSize = numDynamicEntities - 1;
		for (int i = 0; i < outerSize; i++)
		{
			PhysicalEntity entity1 = dynamicEntities[i];

			for (int j = i + 1; j < innerSize; j++)
			{
				PhysicalEntity entity2 = dynamicEntities[j];
				entity1.resolveCollision(entity2, elapsedTime);
			}
		}
	}

	// TODO: Real,dynamic add/remove capability
	public void addEntity(Entity entity)
	{
		if (entity instanceof PhysicalEntity)
		{
			PhysicalEntity physicalEntity = (PhysicalEntity) entity;
			if (((PhysicalEntity) entity).mass == Float.POSITIVE_INFINITY)
			{
				staticEntities[numStaticEntities] = physicalEntity;
				numStaticEntities++;
			}
			else
			{
				dynamicEntities[numDynamicEntities] = physicalEntity;
				numDynamicEntities++;
			}
		}
		else
		{
			entities[numEntities] = entity;
			numEntities++;
		}
	}

	public void addDynamicEntity(PhysicalEntity physicalEntity)
	{
		dynamicEntities[numDynamicEntities] = physicalEntity;
		numDynamicEntities++;
	}

	public double getTotalRunTime()
	{
		return totalRunTime;
	}

	/**
	 * Randomizes the order physical entities are processed. This is for testing
	 * only.
	 * 
	 * @param random
	 */
	public void randomizeEntityProcessingOrder_TESTING_ONLY(Random random)
	{
		List<PhysicalEntity> physicalEntityList = new ArrayList<PhysicalEntity>(numDynamicEntities);
		for (int i = 0; i < numDynamicEntities; i++)
		{
			physicalEntityList.add(dynamicEntities[i]);
		}
		Collections.shuffle(physicalEntityList, random);
		for (int i = 0; i < numDynamicEntities; i++)
		{
			dynamicEntities[i] = physicalEntityList.get(i);
		}
	}

	public PhysicalEntity[] getPhysicalEntities()
	{
		return dynamicEntities;
	}

	public int getNumPhysicalEntities()
	{
		return numDynamicEntities;
	}
}

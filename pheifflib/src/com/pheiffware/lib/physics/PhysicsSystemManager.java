/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.physics;

import java.util.List;

import com.pheiffware.lib.physics.entity.Entity;

/**
 * Sets up a background thread to manage updating the physics system. It can be
 * queried for a cheap "render-copy" in a synchronized way.
 */
public abstract class PhysicsSystemManager
{

	// The main physics thread (executor service was overkill)
	private final Thread physicsThread;

	// The physics system object being managed
	private final PhysicsSystem physicsSystem;

	// This is set to make a request of the update thread. Without this, the
	// physics processing thread may hog the monitor lock.
	private volatile boolean request;

	// Keep running while true
	protected volatile boolean alive = true;

	public PhysicsSystemManager()
	{
		physicsSystem = new PhysicsSystem();

		// A thread which just runs the mainLoop() method.
		physicsThread = new Thread("Physics Update")
		{
			@Override
			public void run()
			{
				mainLoop();
			}
		};
	}

	/**
	 * Starts the simulator in a background thread.
	 */
	public void start()
	{
		physicsThread.start();
	}

	/**
	 * Kills the background thread and blocks until the simulation has ended.
	 */
	public void stop()
	{
		stopNonBlocking();
		try
		{
			physicsThread.join();
		}
		catch (InterruptedException exception)
		{
			throw new RuntimeException("Join should never be interrupted.",
					exception);
		}
	}

	/**
	 * Initiates the stop and returns immediately.
	 */
	protected void stopNonBlocking()
	{
		alive = false;
		physicsThread.interrupt();
	}

	/**
	 * This is run in the background thread.
	 */
	private void mainLoop()
	{
		synchronized (physicsSystem)
		{
			while (alive)
			{
				updateImplement(physicsSystem);
				if (request)
				{
					try
					{
						physicsSystem.wait(0, 1000);
					}
					catch (InterruptedException exception)
					{
						// TODO: This can happen when pause/stop occur
						throw new RuntimeException("Illegal interrupt",
								exception);
					}
				}
				// long start = System.nanoTime();
				// while (System.nanoTime() - start < 1000 && request == true)
				// {
				// // Just wait. A sleep here is unacceptable as this seems to
				// put the thread to sleep for at least 1ms.
				// }
			}
		}
	}

	/**
	 * Called regularly to actually perform the update in the extending class.
	 * Is guaranteed to be synchronized.
	 */
	protected abstract void updateImplement(PhysicsSystem physicsSystem);

	/**
	 * Allows outside systems to get a "cheap" copy for rendering purposes. Give
	 * a list of all entities in a state serialized enough to be rendered.
	 */
	public List<Entity> copyForRender()
	{
		request = true;
		synchronized (physicsSystem)
		{
			List<Entity> entityList = physicsSystem.copyForRender();
			request = false;
			return entityList;
		}
	}

	public void addEntity(Entity entity)
	{
		physicsSystem.addEntity(entity);
	}

	protected PhysicsSystem getPhysicsSystem()
	{
		return physicsSystem;
	}

}

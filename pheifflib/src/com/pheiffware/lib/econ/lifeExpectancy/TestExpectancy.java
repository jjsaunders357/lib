package com.pheiffware.lib.econ.lifeExpectancy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestExpectancy
{
	@Test
	public void test()
	{
		assertEquals(0.998349,
				LifeExpectancyCalculator.instance
						.chanceOfBeingAliveInOneYear(35), 0.0);
	}
}

/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.econ.lifeExpectancy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Used to figure out how long the average person will live.
 */
public class LifeExpectancyCalculator
{
	public static final LifeExpectancyCalculator instance = new LifeExpectancyCalculator();
	private static final int maxAge = 119;
	private final double[] chanceOfLivingOneYear;
	private final double[] lifeIntegralOneYear;

	// Chance of living one more year at a list of ages
	private LifeExpectancyCalculator()
	{
		chanceOfLivingOneYear = new double[maxAge + 1];
		lifeIntegralOneYear = new double[maxAge + 1];

		try (InputStream in = getClass().getResourceAsStream(
				"/com/pheiffware/lib/data/lifetable.txt");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));)
		{
			String readLine = reader.readLine();
			int i = 0;
			while (readLine != null)
			{
				// File stores chance of death, not life
				chanceOfLivingOneYear[i] = 1.0 - Double.valueOf(readLine);
				lifeIntegralOneYear[i] = calcLifeIntegral(chanceOfLivingOneYear[i]);
				readLine = reader.readLine();
				i++;
			}
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * @param age
	 * @return
	 */
	public final double chanceOfBeingAliveInOneYear(int age)
	{
		if (age > maxAge)
		{
			return 0.0;
		}
		else
		{
			return chanceOfLivingOneYear[age];
		}
	}

	/**
	 * The expected length of time a person of a given age is expected to live
	 * during a given year. This is much higher than the
	 * chanceOfBeingAliveInOneYear.
	 * 
	 * For example: If the chance of a person surviving one year, at the given
	 * age, is 1/12, the chance of them surviving through the first month is
	 * still ~80%. The result is that they are likely to live ~0.35 year more at
	 * this age, NOT one month.
	 * 
	 * @param age
	 * @return
	 */
	public final double getLifeIntegral(int age)
	{
		if (age > maxAge)
		{
			return 0.0;
		}
		else
		{
			return lifeIntegralOneYear[age];
		}
	}

	private static double calcLifeIntegral(double chanceOfLivingOneYear)
	{
		double n = 1000;
		return (chanceOfLivingOneYear - 1)
				/ (Math.pow(chanceOfLivingOneYear, 1 / n) - 1) / n;
	}

}

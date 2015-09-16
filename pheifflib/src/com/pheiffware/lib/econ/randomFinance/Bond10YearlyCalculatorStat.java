/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.econ.randomFinance;

import java.util.Random;

import com.pheiffware.lib.random.SkewedNormal;

/**
 * Takes stats from historical yearly 10-year-bond returns and uses them to
 * generate random numbers representing multiplier for each year. A multiplier
 * will always be 1 + percentage increase.
 */
@SuppressWarnings("serial")
public class Bond10YearlyCalculatorStat extends SkewedNormal
{

	public Bond10YearlyCalculatorStat()
	{
		this(new Random());
	}

	public Bond10YearlyCalculatorStat(Random random)
	{
		super(-0.152732269, 0.941718555, 0.152732269, 0.011424856, 0.078805455,
				1.05264, random);
	}
}

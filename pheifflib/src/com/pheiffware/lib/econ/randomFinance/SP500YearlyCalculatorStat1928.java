/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.econ.randomFinance;

import java.util.Random;

import com.pheiffware.lib.random.SkewedNormal;

/**
 * Takes stats from historical yearly sp500 returns (1928 - 2013) and uses them
 * to generated random numbers representing multiplier for each year. A
 * multiplier will always be 1 + percentage increase.
 */
@SuppressWarnings("serial")
public class SP500YearlyCalculatorStat1928 extends SkewedNormal
{
	public SP500YearlyCalculatorStat1928(Random random)
	{
		super(0.073147662, 1.022820363, -0.073147662, -0.009485621,
				0.199808202, 1.114673, random);
	}
}

package com.pheiffware.lib.econ.randomFinance;

import java.util.Random;

import org.junit.Test;

public class TestInvestments
{
	@Test
	public void test()
	{
		SP500YearlyCalculatorStat1928 sp500YearlyCalculatorStat = new SP500YearlyCalculatorStat1928(
				new Random());
		sp500YearlyCalculatorStat.testDistribution(3000000, -0.412956475,
				0.02160147, 0.003);

		Bond10YearlyCalculatorStat bond10YearlyCalculatorStat = new Bond10YearlyCalculatorStat(
				new Random());
		bond10YearlyCalculatorStat.testDistribution(3000000, 0.964887687,
				1.532824474, 0.01);
	}
}

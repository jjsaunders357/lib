package com.pheiffware.lib.math;

public class Probability
{
	public static final long npr(long n, long choose)
	{
		long product = 1;
		for (long i = n; i > (n - choose); i--)
		{
			product *= i;
		}
		return product;
	}

	public static final long ncr(long n, long choose)
	{
		long result = npr(n, choose);
		for (int i = 2; i <= choose; i++)
		{
			result /= i;
		}
		return result;
	}

	public static final double atLeastOnce(double prob, double numChances)
	{
		return 1 - Math.pow(1 - prob, numChances);
	}
}

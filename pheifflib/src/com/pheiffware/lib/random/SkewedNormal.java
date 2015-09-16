/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.random;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 * Normal distribution with skew/kurtosis. Believe this is referred to as a
 * Pearson IV distribution. How to create this kind of random generator:
 * 
 * Solve this system of equations
 * 
 * a + c == mean (easier to just set mean = 0)
 * 
 * b^2 + 6*b*d + 2*c^2 + 15*d^2 == std (easier to just calc with std = 1)
 * 
 * 2*c*(b^2 + 24*b*d + 105*d^2 + 2) == skew
 * 
 * 24*(b*d + c^2*(1 + b^2 + 28*b*d) + d^2*(12 + 48*b*d + 141*c^2 + 225*d^2)) ==
 * kurtosis
 * 
 * r = random.gaussian() x = a + bx + cx^2 + dx^3
 * 
 * Methodology From: http://www.talkstats.com/showthread.php/5853-Random-numbers
 * Solved system of equations at:
 * http://www.quickmath.com/webMathematica3/quickmath
 * /equations/solve/advanced.jsp
 */
@SuppressWarnings("serial")
public class SkewedNormal implements RandomGenerator, Serializable
{
	private final double a;
	private final double b;
	private final double c;
	private final double d;
	private final double dev;
	private final double mean;
	private final Random random;

	public SkewedNormal(double a, double b, double c, double d, double dev,
			double mean, Random random)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.dev = dev;
		this.mean = mean;
		this.random = random;
	}

	@Override
	public double getRandom()
	{
		final double n = random.nextGaussian();
		final double n2 = n * n;
		final double n3 = n2 * n;
		final double nWithSkewAndKurtosis = a + n * b + c * n2 + d * n3;
		return nWithSkewAndKurtosis * dev + mean;
	}

	public void testDistribution(int numTrials, double expectedSkew,
			double expectedKurtosis, double allowedErrorHighOrderTerms)
	{
		double data[] = new double[numTrials];

		double observedMean = 0;
		for (int i = 0; i < numTrials; i++)
		{
			data[i] = getRandom();
			observedMean += data[i];
		}
		observedMean /= numTrials;
		StandardDeviation devCalc = new StandardDeviation();
		Skewness skewCalc = new Skewness();
		Kurtosis kurtosisCalc = new Kurtosis();
		double observedDev = devCalc.evaluate(data);
		double observedSkew = skewCalc.evaluate(data);
		double observedKurtosis = kurtosisCalc.evaluate(data);
		assertEquals("", mean, observedMean, 0.001);
		assertEquals("", dev, observedDev, 0.001);
		assertEquals("", expectedSkew, observedSkew, allowedErrorHighOrderTerms);
		assertEquals("", expectedKurtosis, observedKurtosis, allowedErrorHighOrderTerms);
	}
}
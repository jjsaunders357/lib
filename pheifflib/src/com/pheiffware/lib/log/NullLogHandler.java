package com.pheiffware.lib.log;

/**
 * By default logging does nothing.
 * @author Steve
 *
 */
public class NullLogHandler implements LogHandler
{

	@Override
	public void error(String message, Exception e)
	{

	}

	@Override
	public void info(String message)
	{

	}

}

package com.pheiffware.lib.log;

public interface LogHandler
{
	public void error(String message, Exception e);

	public void info(String message);
}

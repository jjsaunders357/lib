package com.pheiffware.lib.log;

/**
 * Any code which logs goes through here.  The assumption is that if you are interested in logging the 1st line of your main() method will be:
 * Log.install(logHandler)
 * where logHandler is a handler for your specific platform (PC, android, etc).
 * @author Steve
 *
 */
public class PLog
{
	private static LogHandler instance = new NullLogHandler();

	public static void install(LogHandler instance)
	{
		PLog.instance = instance;
	}

	public static void error(String message, Exception e)
	{
		instance.error(message, e);
	}

	public static void info(String message)
	{
		instance.info(message);
	}
}

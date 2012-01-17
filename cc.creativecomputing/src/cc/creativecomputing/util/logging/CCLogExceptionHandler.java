package cc.creativecomputing.util.logging;

public class CCLogExceptionHandler
{
	public static void register()
	{
		System.setProperty("sun.awt.exception.handler", 
				CCLogExceptionHandler.class.getName());
	}

	// handle uncaught exceptions through the logger
	public void handle(Throwable ex)
	{
		CCLog.error(ex);
	}
}
package cc.creativecomputing.events;

/**
 * To listen to dispose events of the application implement this interface 
 * and add all instances as listeners to the main application. The dispose
 * event is called when an application ends and can be used to free up
 * hardware resources for example.
 * @author texone
 * @see CCApp#addDisposeListener(CCDisposeListener)
 * @example events.CCDisposeListenerTest
 */
public interface CCDisposeListener{
	
	/**
	 * Called once when the program is closed. Can be used to store relevant data to a file
	 * before the application ends. Implement this method to listen to dispose events.
	 * @shortdesc called on application end
	 * @example events.CCDisposeListenerTest
	 */
	public void dispose();
}

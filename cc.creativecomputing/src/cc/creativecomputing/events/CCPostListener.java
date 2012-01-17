package cc.creativecomputing.events;

import cc.creativecomputing.CCApp;

/**
 * <p>
 * Post events are generated for every frame of the application right
 * after the drawing. Classes that are interested in processing this 
 * event implement this interface (and its post method).The listener 
 * object created from that class is then registered using the application's 
 * <code>addPostListener</code> method.
 * </p>
 * @author texone
 * @see CCApp#addPostListener(CCPostListener)
 */
public interface CCPostListener{
	/**
	 * The post method is called directly after the draw call.
	 */
	public void post();
}

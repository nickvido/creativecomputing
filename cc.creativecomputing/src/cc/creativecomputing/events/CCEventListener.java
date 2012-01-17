package cc.creativecomputing.events;

import cc.creativecomputing.CCAbstractApp;


/**
 * Implement this interface to let your class listen to different events.
 * In this case your object adds itself as listener to all needed events.
 * @author texone
 * @example events.CCEventListenerTest
 * @see CCApp#addEventListener(CCEventListener)
 */
public interface CCEventListener {
	/**
	 * Implement this method to add your object as listener to different events.
	 * @param theApp
	 * @example events.CCEventListenerTest
	 */
	public void addAsListenerTo(final CCAbstractApp theApp);
}

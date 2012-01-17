package cc.creativecomputing.events;

import cc.creativecomputing.graphics.CCGraphics;

/**
 * To listen to draw events of the application implement this interface 
 * and add the all instances as listeners to the main application. All
 * draw listeners are called after the draw of the application is finished.
 * The listeners will be drawn in the order they have been added to the
 * application. The draw event can be used to let drawing automatically happen for
 * other object. This might be replaced by scene graph structure for
 * more precise control.
 * @author texone
 * @see CCApp#addDrawListener(CCDrawListener)
 * @example events.CCDrawListenerTest
 */
public interface CCDrawListener{

	/**
	 * Draw continuously executes the lines of code contained 
	 * inside its block until the program is stopped. The <code>draw()</code> function is called automatically 
	 * and should never be called explicitly. The number of times <code>draw()</code> executes in each second may be 
	 * controlled with the frameRate() function. Implement this interface to listen to draw events.
	 * @shortdesc automatically called on every frame for drawing
	 * @param g graphics object for drawing
	 * @example events.CCDrawListenerTest
	 */
	public void draw(CCGraphics g);
}

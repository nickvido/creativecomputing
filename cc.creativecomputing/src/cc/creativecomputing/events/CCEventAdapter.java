package cc.creativecomputing.events;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCMouseEvent.CCMouseButton;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABoundingRectangle;

public class CCEventAdapter extends CCAABoundingRectangle implements CCMouseListener, CCMouseMotionListener, CCKeyListener, CCSetupListener, CCUpdateListener, CCDrawListener, CCDisposeListener {
	boolean enabled; // set this to false to temporarily disable all events
	boolean verbose;

	private CCApp _myApp;

	// constructor
	public CCEventAdapter(final CCApp theApp) {
		_myApp = theApp;

		_mouseOver = false;
		_mouseDown = false;
		enabled = true;
		verbose = false;

		enableAppEvents();
		disableMouseEvents();
		disableKeyEvents();

	}

	/**
	 * enable all event callbacks
	 */
	public void enableAllEvents() {
		enableMouseEvents();
		enableKeyEvents();
		enableAppEvents();

	}

	/**
	 * disable all event callbacks
	 */
	public void disableAllEvents() {
		disableMouseEvents();
		disableKeyEvents();
		disableAppEvents();
	}

	/**
	 * call this if object should receive mouse events
	 */
	public void enableMouseEvents() {
		_myApp.addMouseListener(this);
		_myApp.addMouseMotionListener(this);
	}

	/**
	 * call this if object doesn't need to receive mouse events (default)
	 */
	public void disableMouseEvents() {
		_myApp.removeMouseListener(this);
		_myApp.removeMouseMotionListener(this);
	}

	/**
	 * call this if object should receive key events
	 */
	public void enableKeyEvents() {
		_myApp.addKeyListener(this);
	}

	/**
	 * call this if object doesn't need to receive key events (default)
	 */
	public void disableKeyEvents() {
		_myApp.removeKeyListener(this);
	}

	/**
	 * call this if object should update/draw automatically (default)
	 */
	public void enableAppEvents() {
		_myApp.addSetupListener(this);
		_myApp.addUpdateListener(this);
		_myApp.addDrawListener(this);
		_myApp.addDisposeListener(this);
	}

	/**
	 * call this if object doesn't need to update/draw automatically
	 */
	public void disableAppEvents() {
		_myApp.removeSetupListener(this);
		_myApp.removeUpdateListener(this);
		_myApp.removeDrawListener(this);
		_myApp.removeDisposeListener(this);
	}

	/**
	 * returns true if mouse is over object (based on position and size)
	 * @return
	 */
	public boolean isMouseOver() {
		return _mouseOver;
	}

	/**
	 * returns true if mouse button is down and over object (based on position and size)
	 * @return
	 */
	public boolean isMouseDown() {
		return _mouseDown;
	}

	/**
	 * returns mouse X (in screen coordinates)
	 * @return
	 */
	int getMouseX() {
		return _mouseX;
	}

	/**
	 * returns mouse Y (in screen coordinates)
	 * @return
	 */
	int getMouseY() {
		return _mouseY;
	}

	/**
	 * returns last mouse button to have activity
	 * @return
	 */
	public CCMouseButton getLastMouseButton() {
		return _mouseButton;
	}

	// extend ofxMSAInteractiveObject and override all of any of the following methods
	/**
	 * called when app starts
	 */
	public void setup() {}

	/**
	 * called every frame to update object
	 */
	public void update(final float theDeltaTime) {}

	/**
	 * called every frame to draw object
	 */
	public void draw(CCGraphics g) {}

	/**
	 * called when app quites
	 */
	public void dispose() {}

	// these behave very similar to those in flash
	/**
	 * called when mouse enters object x, y, width, height
	 */
	public void onRollOver(int x, int y) {}

	/**
	 * called when mouse leaves object x, y, width, height
	 */
	public void onRollOut() {}

	/**
	 * called when mouse moves while over object x, y, width, height
	 * @param x mouse x
	 * @param y mouse y
	 */
	public void onMouseMove(int x, int y) {}

	/**
	 * called when mouse moves while over object and button is down
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onDragOver(int x, int y, CCMouseButton button) {} 

	/**
	 * called when mouse moves while outside the object after being clicked on it
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onDragOutside(int x, int y, CCMouseButton button) {} 

	/**
	 * called when mouse presses while over object
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onPress(int x, int y, CCMouseButton button) {}

	/**
	 * called when mouse presses while outside object
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onPressOutside(int x, int y, CCMouseButton button) {} 

	/**
	 * called when mouse releases while over object
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onRelease(int x, int y, CCMouseButton button) {}

	/**
	 * called when mouse releases outside of object after being pressed on object
	 * @param x
	 * @param y
	 * @param button
	 */
	public void onReleaseOutside(int x, int y, CCMouseButton button) {}
	
	public void keyPressed(CCKeyEvent key) {}

	public void keyReleased(CCKeyEvent key) {}

	// you shouldn't need access to any of these unless you know what you are doing
	// (i.e. disable auto updates and call these manually)

	public void mouseMoved(CCMouseEvent theEvent) {
		int x = theEvent.x();
        int y = theEvent.y();
        CCMouseButton button = theEvent.button();
        
        _mouseX = x;
        _mouseY = y;
        
        if(isInside(x, y)) {                                             // if mouse is over the object
                if(!_mouseOver) {                                               // if wasn't over previous frame
                        onRollOver(x, y);                                               // call onRollOver
                        _mouseOver = true;                                              // update flag
                }
                onMouseMove(x, y);                                              // and trigger onMouseMove
        } else if(_mouseOver) {                                 // if mouse is not over the object, but the flag is true (From previous frame)
                onRollOut();                                                    // call onRollOut
                _mouseOver = false;                                             // update flag
        }

	}

	public void mousePressed(CCMouseEvent theEvent) {
		int x = theEvent.x();
        int y = theEvent.y();
        CCMouseButton button = theEvent.button();

	        _mouseX = x;
	        _mouseY = y;
	        _mouseButton = button;
	        
	        if(isInside(x, y)) {					// if mouse is over
	                if(!_mouseDown) {				// if wasn't down previous frame
	                        onPress(x, y, button);	// call onPress
	                        _mouseDown = true; 		// update flag
	                }
	        } else {                                                                // if mouse is not over
	                onPressOutside(x, y, button);
	        }

	}

	public void mouseDragged(CCMouseEvent theEvent) {
		int x = theEvent.x();
        int y = theEvent.y();
        CCMouseButton button = theEvent.button();

        _mouseX = x;
        _mouseY = y;
        _mouseButton = button;

        if(isInside(x, y)) {                                             // if mouse is over the object
                if(!_mouseOver) {                                               // if wasn't over previous frame
                        //                              onPress(x, y);                                                  // call onPress - maybe not
                        _mouseOver = true;                                              // update flag
                }
                onDragOver(x, y, button);                               // and trigger onDragOver
        } else {
                if(_mouseOver) {                                        // if mouse is not over the object, but the flag is true (From previous frame)
                        onRollOut();                                                    // call onRollOut
                        _mouseOver = false;                                             // update flag
                }
                if(_mouseDown) {
                        onDragOutside(x, y, button);
                }
        }

	}

	public void mouseReleased(CCMouseEvent theEvent) {
		int x = theEvent.x();
        int y = theEvent.y();
        CCMouseButton button = theEvent.button();

	        _mouseX = x;
	        _mouseY = y;
	        _mouseButton = button;
	        
	        if(isInside(x, y)) {
	                onRelease(x, y, button);
	        } else {
	                if(_mouseDown) onReleaseOutside(x, y, button);
	        }
	        _mouseDown = false;

	}

	public void _keyPressed(CCKeyEvent theEvent) {
		
	}

	public void _keyReleased(CCKeyEvent theEvent){
		
	}

	protected int _mouseX, _mouseY;
	protected CCMouseButton _mouseButton;
	boolean _mouseOver;
	boolean _mouseDown;

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseClicked(cc.creativecomputing.events.CCMouseEvent)
	 */
	public void mouseClicked(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseEntered(cc.creativecomputing.events.CCMouseEvent)
	 */
	public void mouseEntered(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseExited(cc.creativecomputing.events.CCMouseEvent)
	 */
	public void mouseExited(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCKeyListener#keyTyped(cc.creativecomputing.events.CCKeyEvent)
	 */
	public void keyTyped(CCKeyEvent theKeyEvent) {
		// TODO Auto-generated method stub
		
	}
};

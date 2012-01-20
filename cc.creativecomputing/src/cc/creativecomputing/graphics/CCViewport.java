package cc.creativecomputing.graphics;

import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;

public class CCViewport implements CCDrawListener{

	private int _myX = 0;
	private int _myY = 0;

	private int _myWidth = 640;
	private int _myHeight = 480;

	public CCViewport() {
	}

	public CCViewport(final CCViewport theViewport) {
		set(theViewport);
	}

	public CCViewport(final int theX, final int theY, final int theWidth, final int theHeight) {
		set(theX, theY, theWidth, theHeight);
	}

	public void set(final CCViewport theViewport) {
		_myX = theViewport._myX;
		_myY = theViewport._myY;
		_myWidth = theViewport._myWidth;
		_myHeight = theViewport._myHeight;
	}

	public void set(final int theX, final int theY, final int theWidth, final int theHeight) {
		_myX = theX;
		_myY = theY;
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public void position(final int theX, final int theY){
		_myX = theX;
		_myY = theY;
	}
	
	public CCVector2i position(){
		return new CCVector2i(_myX, _myY);
	}	
	
	public void dimension(final int theWidth, final int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public boolean pointInside(CCVector2i thePoint) {		
		return pointInside(new CCVector2f(thePoint.x, thePoint.y));
	}

	public boolean pointInside(CCVector2f thePoint) {		
		CCAABoundingRectangle myVPBounds = new CCAABoundingRectangle(
				_myX, _myY, _myX + _myWidth, _myY + _myHeight);
		boolean inside = myVPBounds.isInside(thePoint);
		
		return inside;
	}
		
	public int x(){
		return _myX;
	}
	
	public int y(){
		return _myY;
	}
	
	public int width(){
		return _myWidth;
	}
	
	public int height(){
		return _myHeight;
	}
	
	public float aspectRatio(){
		return (float)_myWidth/_myHeight;
	}

	public void draw(CCGraphics g) {
		g.gl.glViewport(_myX, _myY, _myWidth, _myHeight);
	}
}
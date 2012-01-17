package cc.creativecomputing.appmodes;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.xml.CCXMLElement;

public class CCMultiViewportMode extends CCAppMode{

	public static enum CCMultiViewportDirection{
		VERTICAL,HORIZONTAL;
	}
	
	public static CCMultiViewportDirection VERTICAL = CCMultiViewportDirection.VERTICAL;
	public static CCMultiViewportDirection HORIZONTAL = CCMultiViewportDirection.HORIZONTAL;
	
	private final CCMultiViewportDirection _myMultiViewportDirection;
	private final int _myNumberOfScreens;
	private final int _myGap;
	
	private List<CCCamera> _myCameras = new ArrayList<CCCamera>();
	
	public CCMultiViewportMode(final CCMultiViewportDirection theMultiViewportDirection, final int theNumberOfScreens, final int theGap){
		_myMultiViewportDirection = theMultiViewportDirection;
		_myNumberOfScreens = theNumberOfScreens;
		_myGap = theGap;
	}
	
	/**
	 * @invisible
	 * @param theSettingsXML
	 */
	public void setFromXML(final CCXMLElement theSettingsXML){
		
	}

	/**
	 * @invisible
	 */
	public void begin() {
		_myApp.g.beginDraw();
	}

	public void draw(){
		_myApp.g.clear();
		
		switch(_myMultiViewportDirection){
		case HORIZONTAL:
			for(CCCamera myCamera:_myCameras){
				myCamera.draw(_myApp.g);
				_myApp.g.frustum().update();
				_myApp.draw();
			}
			break;
		case VERTICAL:
			for(CCCamera myCamera:_myCameras){
				myCamera.draw(_myApp.g);
				_myApp.g.frustum().update();
				_myApp.g.pushMatrix();
				_myApp.g.rotateZ(270);
				_myApp.draw();
				_myApp.g.popMatrix();
			}
			break;
		}
	}
	
	/**
	 * @invisible
	 */
	public void end() {
		_myApp.g.endDraw();
	}
	
	/**
	 * @invisble
	 * @param theWidth
	 * @param theHeight
	 */
	public void setSize(final int theWidth, final int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	private void setCamerasHorizontal(final CCGraphics g, final int theWidth, final int theHeight, final float theGap){
		CCCamera myCamera1 = new CCCamera(g, theWidth, theHeight);
		myCamera1.viewport(new CCViewport(0, 0, theWidth, theHeight));
		myCamera1.frustumOffset().set(-(theWidth + theGap)*1.5f,0);
		_myCameras.add(myCamera1);
		
		CCCamera myCamera2 = new CCCamera(g, theWidth, theHeight);
		myCamera2.viewport(new CCViewport(theWidth, 0, theWidth, theHeight));
		myCamera2.frustumOffset().set(-(theWidth + theGap)*0.5f,0);
		_myCameras.add(myCamera2);
		
		CCCamera myCamera3 = new CCCamera(g, theWidth, theHeight);
		myCamera3.viewport(new CCViewport(theWidth * 2, 0, theWidth, theHeight));
		myCamera3.frustumOffset().set((theWidth + theGap)*0.5f,0);
		_myCameras.add(myCamera3);
		
		CCCamera myCamera4 = new CCCamera(g, theWidth, theHeight);
		myCamera4.viewport(new CCViewport(theWidth * 3, 0, theWidth, theHeight));
		myCamera4.frustumOffset().set((theWidth + theGap)*1.5f,0);
		_myCameras.add(myCamera4);
	}
	
	private void setCamerasVertical(final CCGraphics g, final int theWidth, final int theHeight, final float theGap){
		CCCamera myCamera1 = new CCCamera(g, theWidth, theHeight);
		myCamera1.viewport(new CCViewport(0, 0, theWidth, theHeight));
		myCamera1.frustumOffset().set(0, -(theWidth + theGap)*1.5f);
		_myCameras.add(myCamera1);
		
		CCCamera myCamera2 = new CCCamera(g, theWidth, theHeight);
		myCamera2.viewport(new CCViewport(0, theHeight, theWidth, theHeight));
		myCamera2.frustumOffset().set(0,-(theHeight + theGap)*0.5f);
		_myCameras.add(myCamera2);
		
		CCCamera myCamera3 = new CCCamera(g, theWidth, theHeight);
		myCamera3.viewport(new CCViewport(0, theHeight * 2, theWidth, theHeight));
		myCamera3.frustumOffset().set(0,(theHeight + theGap) * 0.5f);
		_myCameras.add(myCamera3);
		
		CCCamera myCamera4 = new CCCamera(g, theWidth, theHeight);
		myCamera4.viewport(new CCViewport(0, theHeight * 3, theWidth, theHeight));
		myCamera4.frustumOffset().set(0, (theHeight + theGap) * 1.5f);
		_myCameras.add(myCamera4);
	}
	
	/**
	 * @invisible
	 * @param glDrawable
	 * @param theWidth
	 * @param theHeight
	 */
	public void resize(GLAutoDrawable glDrawable, int theWidth, int theHeight){
		final GL gl = glDrawable.getGL();
		_myApp.g.gl = gl.getGL2();
		
		_myWidth = theWidth;
		_myHeight = theHeight;

		switch(_myMultiViewportDirection){
		case HORIZONTAL:
			_myApp.width = theWidth + (_myNumberOfScreens - 1) * _myGap;
			_myApp.height = theHeight;
			setCamerasHorizontal(_myApp.g, theWidth / _myNumberOfScreens, theHeight, _myGap);
			break;
		case VERTICAL:
			_myApp.width = theHeight + (_myNumberOfScreens - 1) * _myGap;
			_myApp.height = theWidth;
			setCamerasVertical(_myApp.g, theWidth, theHeight / _myNumberOfScreens, _myGap);
			break;
		}
		
		//these two could be added as Listener but are called separately 
		//here to make the initialization progress more obvious
		_myApp.g.resize(theWidth, theHeight);
	}
	
	/**
	 * Sets the size of the given frame dependent on the screen mode
	 * @invisible
	 * @param theFrame
	 */
	public void setupFrame(final JFrame theFrame){
		final Insets myInsets = theFrame.getInsets();
		theFrame.setSize(
			_myWidth + myInsets.left + myInsets.right, 
			_myHeight + myInsets.top + myInsets.bottom
		);
	}
}

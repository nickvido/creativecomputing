package cc.creativecomputing.appmodes;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.xml.CCXMLElement;


/**
 * An Application can run in different screen modes. This
 * is the basic mode, that simply opens a frame in one window.
 * 
 * @author texone
 *
 */
public class CCAppMode{
	protected CCApp _myApp;
	protected int _myWidth;
	protected int _myHeight;
	
	public CCAppMode(){
		
	}
	/**
	 * @invisible
	 * @param theSettingsXML
	 */
	public void setFromXML(final CCXMLElement theSettingsXML){
		
	}
	
	/**
	 * @invisible
	 * @param theApp
	 */
	public void setApp(final CCApp theApp){
		_myApp = theApp;
	}

	/**
	 * @invisible
	 */
	public void begin() {
		_myApp.g.beginDraw();
	}
	
	public void draw(){
		_myApp.draw();
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
	
	/**
	 * @invisible
	 * @param glDrawable
	 * @param theWidth
	 * @param theHeight
	 */
	public void resize(GLAutoDrawable glDrawable, int theWidth, int theHeight){
		final GL2 gl = glDrawable.getGL().getGL2();
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myApp.width = theWidth;
		_myApp.height = theHeight;
		
		_myApp.g.gl = gl;
		//these two could be added as Listener but are called separately 
		//here to make the initialization progress more obvious
		_myApp.g.resize(theWidth, theHeight);
	}
	
	/**
	 * Sets the size of the given frame dependent on the screen mode
	 * @invisible
	 * @param theFrame
	 */
	public void setupFrame(final Frame theFrame){
		final Insets myInsets = theFrame.getInsets();
		theFrame.setSize(
			_myWidth + myInsets.left + myInsets.right, 
			_myHeight + myInsets.top + myInsets.bottom
		);
	}
}

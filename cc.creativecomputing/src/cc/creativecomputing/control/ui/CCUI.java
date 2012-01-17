package cc.creativecomputing.control.ui;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphicsCore.CCShapeMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCGlutFont;
import cc.creativecomputing.xml.CCXMLElement;


public class CCUI extends CCUIComponent implements CCMouseListener, CCMouseMotionListener, CCPostListener, CCDisposeListener{
	
	public static CCGlutFont FONT = CCFontIO.createGlutFont(CCFontIO.BITMAP_HELVETICA_10);
	
	private final CCApp _myApp;
	
	private CCXMLElement _myXML;
	
	public CCUI(final CCApp theApp){
		super("",0,0,0,0);
		
		_myApp = theApp;
		_myApp.addMouseListener(this);
		_myApp.addMouseMotionListener(this);
		_myApp.addPostListener(this);
		_myApp.addDisposeListener(this);
	}
	
	public void show(){
		_myIsVisible = true;
	}
	
	public void hide(){
		_myIsVisible = false;
	}
	
	/*
	 * IMPLEMENT APPLICATION LISTENER
	 */
	
	
	public void draw(CCGraphics g){
		g.viewport(0, 0, g.width, g.height);

		CCColor myStoreColor = g.color();
		CCShapeMode myStoreShapeMode = g.rectMode();
		
		boolean myIsLighting = g.lighting;
		g.noLights();
		g.pushAttribute();
		g.noDepthTest();

		g.rectMode(CCShapeMode.CORNER);
		g.beginOrtho();
		
		g.color(255);
		super.draw(g);
		g.endOrtho();
		g.color(myStoreColor);
		g.rectMode(myStoreShapeMode);
		g.popAttribute();
		if(myIsLighting)
			g.lights();
	}
	
	public void post(){
		if(_myIsVisible)draw(_myApp.g);
	}
	public void dispose() {
	}
	
	/* 
	 * IMPLEMENT MOUSE LISTENERS
	 */

	public void mouseClicked(CCMouseEvent theEvent) {}

	public void mouseEntered(CCMouseEvent theEvent) {}

	public void mouseExited(CCMouseEvent theEvent) {}

	public void mousePressed(CCMouseEvent theEvent) {
		if(!_myIsVisible)return;
		onPress(theEvent);
	}

	public void mouseReleased(CCMouseEvent theEvent) {
		if(!_myIsVisible)return;
		onRelease(theEvent);
	}

	public void mouseDragged(CCMouseEvent theEvent) {
		if(!_myIsVisible)return;
		onDragg(theEvent);
	}

	public void mouseMoved(CCMouseEvent theEvent) {
		if(!_myIsVisible)return;
		onMove(theEvent);
	}

}

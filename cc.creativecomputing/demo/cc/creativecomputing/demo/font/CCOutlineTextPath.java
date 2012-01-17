package cc.creativecomputing.demo.font;


import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCVector3f;



public class CCOutlineTextPath extends CCApp{

	private List<CCVector3f> _myTextPath;

	public void setup(){
		CCOutlineFont font = CCFontIO.createOutlineFont("Arial",48, 30);
		g.bezierDetail(31);
		_myTextPath = font.getPath("TEXONE", CCTextAlign.CENTER, 192,0, 0, 0);
		g.clearColor(0.3f);
	}
	
	public void draw(){
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3f myPoint:_myTextPath){
			g.vertex(myPoint);
		}
		g.endShape();
		
		System.out.println(frameRate);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCOutlineTextPath.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1200, 400);
		myManager.start();
	}
}

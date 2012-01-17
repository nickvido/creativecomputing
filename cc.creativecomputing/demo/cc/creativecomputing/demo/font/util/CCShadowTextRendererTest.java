package cc.creativecomputing.demo.font.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCShadowTextRenderer;
import cc.creativecomputing.graphics.font.util.CCTextTexture;
import cc.creativecomputing.math.CCMath;


public class CCShadowTextRendererTest extends CCApp{

	private CCTextTexture _myShadowTextTexture;

	public void setup(){
		for(String myFont:CCFontIO.list()) {
			System.out.println(myFont);
		}
		_myShadowTextTexture = CCFontIO.createBitmapText(new CCShadowTextRenderer(20), "TEXONE", "Arial", 96);	
		g.clearColor(255);
	}
	
	public void draw(){
		g.clear();
		g.color(255);
		
		CCMath.randomSeed(0);
		for(int i = 0; i < 50;i++) {
			g.pushMatrix();
			g.translate(CCMath.random(-width/2, width/2), CCMath.random(-height/2, height/2));
			g.scale(CCMath.random(1));
			_myShadowTextTexture.draw(g,0,0);
			g.popMatrix();
		}
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCShadowTextRendererTest.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

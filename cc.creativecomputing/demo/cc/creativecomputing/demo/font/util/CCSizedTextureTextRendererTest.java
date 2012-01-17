package cc.creativecomputing.demo.font.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCSizedTextureTextRenderer;
import cc.creativecomputing.graphics.font.util.CCTextTexture;


public class CCSizedTextureTextRendererTest extends CCApp{

	private CCTextTexture _mySizedTextTexture;

	@Override
	public void setup(){
		_mySizedTextTexture = CCFontIO.createBitmapText(new CCSizedTextureTextRenderer(500,500),"TEXONE", "Verdana", 60);	
		g.clearColor(255);
	}
	
	@Override
	public void draw(){
		g.clear();
		g.color(255,0,0);
		g.image(_mySizedTextTexture, -200,-200);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCSizedTextureTextRendererTest.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

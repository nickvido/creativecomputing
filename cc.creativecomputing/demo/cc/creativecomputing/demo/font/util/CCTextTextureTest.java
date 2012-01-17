package cc.creativecomputing.demo.font.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCTextTexture;


public class CCTextTextureTest extends CCApp{

	private CCTextTexture _myTextTexture;

	public void setup(){
		for(String myFont:CCFontIO.list())System.out.println(myFont);;
		_myTextTexture = CCFontIO.createBitmapText("Texone", "Times", 60);
		g.clearColor(255);
	}
	
	public void draw(){
		g.clear();
		
		g.color(0);
		_myTextTexture.draw(g,-100,0);
		g.line(-100,-height/2,-100,height/2);
		g.line(-width/2,0,width/2,0);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCTextTextureTest.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCTextureMapFontTest extends CCApp {

	CCTextureMapFont _myFont;
	CCVectorFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	public void setup() {
		frameRate(30);
		
		String myFont = "arial";
		float mySize = 70;
		
		_myFont = CCFontIO.createTextureMapFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET);
		_myFont2 = CCFontIO.createVectorFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET);
		_myText = new CCText(_myFont);
		_myText.text("01234");
		_myText.position(-300,300);

		_myText2 = new CCText(_myFont2);
		_myText2.text("01234");
		_myText2.position(-300,230);
		
		g.clearColor(255,255,0);
		
		
	}

	public void draw() {
		g.clear();
		g.color(255);
		
		g.blend();
		g.image(_myFont.texture(), -400,-400);
		
		_myText.draw(g);
		_myText2.draw(g);
	}
	
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCTextureMapFontTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

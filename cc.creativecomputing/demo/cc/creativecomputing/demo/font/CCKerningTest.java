package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCKerningTest extends CCApp {

	CCVectorFont _myFont;
	CCVectorFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	public void setup() {
		frameRate(30);
		
		float mySize = 70;
		
		_myFont = CCFontIO.createVectorFont("arial", mySize, true, CCCharSet.EXTENDED_CHARSET);
		

		_myText = new CCText(_myFont);
		_myText.text("L L ATAW.F.");
		_myText.position(-300,200);
		
		_myFont2 = CCFontIO.createVectorFont("font/Arial.ttf", mySize, true, CCCharSet.EXTENDED_CHARSET);
		
		for(char myChar1:CCCharSet.EXTENDED_CHARSET.chars()) {
			for(char myChar2:CCCharSet.EXTENDED_CHARSET.chars()) {
				float myKerning = _myFont2.kerning(myChar1, myChar2) * mySize;
				if(myKerning < 0)System.out.println(myChar1 + ":" + myChar2 + ":" +myKerning);
			}
		}

		_myText2 = new CCText(_myFont2);
		_myText2.text("L L ATAW.F.");
		_myText2.position(-300,270);
		
		
	}

	public void draw() {
		g.clear();
		g.color(255);
		
		_myText.draw(g);
		_myText2.draw(g);
	}
	
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCKerningTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

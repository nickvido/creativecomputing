package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCUnicodeBlock;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCUnicodeBlockTest extends CCApp {
	
	private CCText _myText;

	@Override
	public void setup() {
		CCFont<?> myFont = CCFontIO.createTextureMapFont("Helvetica", 20, true, CCCharSet.EXTENDED_CHARSET);
		
		StringBuffer myBuffer = new StringBuffer("test");
		
		int myCounter = 1;
		
		for (char myChar : CCUnicodeBlock.LATIN_1_SUPPLEMENT.chars()) {
			if (!myFont.canDisplay(myChar)) {
				System.out.println("cannot display:" + (int) myChar + " " + myChar);
			} else {
				System.out.println("can display:" + (int) myChar + " " + myChar);
				myBuffer.append(myChar);
			}
			if(myCounter % 30 == 0) {
				myBuffer.append("\n");
			}
			myCounter++;
		}
		
		_myText = new CCText(myFont);
		_myText.text(myBuffer.toString());
		_myText.position(-width/2 + 80, height/2 - 80);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myText.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUnicodeBlockTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

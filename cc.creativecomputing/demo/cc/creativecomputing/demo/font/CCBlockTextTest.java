package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;

public class CCBlockTextTest extends CCApp {

	private CCFont<?> _myFont;
	private CCText _myBlockText;
	float _myTextBlockHeight;
	
	public void setup() {
		_myFont = CCFontIO.createTextureMapFont( "Arial", 20);
		
		_myBlockText = new CCText(_myFont);
		_myBlockText.lineBreak(CCLineBreakMode.BLOCK);
		_myBlockText.position(0,0);
		_myBlockText.dimension(100, 100);
		_myBlockText.align(CCTextAlign.JUSTIFY);
		_myBlockText.text("Use CCBlockText to display text in a defined block.");
		
		_myTextBlockHeight = _myBlockText.height();
		System.out.println(_myTextBlockHeight);
	}

	public void draw() {
		g.clearColor(0);
		g.clear();
		g.color(255);
		_myBlockText.draw(g);
		g.line(-width/2, -_myTextBlockHeight, width/2, -_myTextBlockHeight);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCBlockTextTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

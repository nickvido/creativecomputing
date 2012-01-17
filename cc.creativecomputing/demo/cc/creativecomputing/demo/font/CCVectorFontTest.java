package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;

public class CCVectorFontTest extends CCApp {
	
	private CCVectorFont _myFont;
	
	@CCControl(name = "font size", min = 0, max = 200)
	private float _cFontSize = 0;

	@Override
	public void setup() {
		_myFont = CCFontIO.createVectorFont("arial", 24);
		g.textFont(_myFont);
		
		addControls("app","app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.textSize(_cFontSize);
		g.text("TEX ONE",-width/2,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCVectorFontTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

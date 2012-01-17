package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCTextAlign;


public class CCOutlineFontTest extends CCApp{

	public void setup(){
		CCFont<?> font = CCFontIO.createOutlineFont("Arial",48, 30);
		g.textFont(font);
		g.textSize(192);
		g.textAlign(CCTextAlign.CENTER);
		g.clearColor(0.3f);
		g.bezierDetail(31);
	}
	
	public void draw(){
		g.clear();
		g.text("texone",0,0);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCOutlineFontTest.class);
		myManager.settings().size(1200, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

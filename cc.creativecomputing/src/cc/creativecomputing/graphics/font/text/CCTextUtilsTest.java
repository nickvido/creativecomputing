package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCTextUtils;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.util.CCStringUtil;

public class CCTextUtilsTest extends CCApp {

	@Override
	public void setup() {
		CCTextureMapFont font = CCFontIO.createTextureMapFont( "Verdana", 18);	
		
		String t = CCLoremIpsumGenerator.generate(60);
		System.out.println(t);
		System.out.println ("\n---------------------\n");
		System.out.println(  ".." + CCTextUtils.linebreakStringToFitInWidth( 300, font, 14, t ) + ".." );
		System.out.println ("\n---------------------");
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextUtilsTest.class);
		myManager.start();
	}
}


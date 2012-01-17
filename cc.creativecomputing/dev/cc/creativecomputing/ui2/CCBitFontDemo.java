/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui2;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.font.CCBitFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;

public class CCBitFontDemo extends CCApp {
	
	private CCBitFont _myBitfont;
	private CCTextureMapFont _myTextureMapFont;
	private CCText _myText;

	@Override
	public void setup() {
		_myBitfont = new CCBitFont(CCTextureIO.newTextureData(CCIOUtil.classPath(this, "standard56.png")), 2);
		_myTextureMapFont = CCFontIO.createTextureMapFont("arial", 24);
		_myText = new CCText(_myBitfont);
		_myText.size(_myBitfont.size() * 3);
		_myText.text("TEXONE \ntexone \ntex1");
		
		frameRate(10);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		_myText.draw(g);
		
		g.color(255,0,0);
		g.line(-width / 2, 0, width / 2, 0);
		
//		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBitFontDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


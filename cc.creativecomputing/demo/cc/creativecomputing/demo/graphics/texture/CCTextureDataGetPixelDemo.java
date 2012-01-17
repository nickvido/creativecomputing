/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.demo.graphics.texture;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCTextureDataGetPixelDemo extends CCApp {
	
	private CCTextureData _myData;
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myData = CCTextureIO.newTextureData("demo/textures/testAlpha.png");
		
		_myTexture = new CCTexture2D(_myData);
		System.out.println(_myData.getPixel(0, 0));
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.noBlend();
		g.clearColor(255,0,0);
		g.clear();
		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureDataGetPixelDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


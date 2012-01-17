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
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCTexture2DUpdateDemo extends CCApp {
	
	private CCTexture2D _myTexture;
	private CCTextureData _myData;

	@Override
	public void setup() {
		_myData = CCTextureIO.newTextureData("textures/sphere.png");
		_myTexture = new CCTexture2D(_myData);
	}

	@Override
	public void update(final float theDeltaTime) {

		_myTexture.updateData(_myData);
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myTexture, 0,0);
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture2DUpdateDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().vsync(false);
		myManager.start();
	}
}


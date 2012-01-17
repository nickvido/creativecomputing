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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.math.CCMath;

public class CCTexture1DTest extends CCApp {
	
	private CCTextureData _myTextureData;
	private CCTexture1D _myTexture;

	@Override
	public void setup() {
		frameRate(30);
		_myTextureData = CCTextureIO.newTextureData("textures/1d_texture.png");
		
		_myTexture = new CCTexture1D();
		_myTexture.data(_myTextureData);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.TRIANGLE_FAN);
		g.textureCoords(0);
		g.vertex(0,0);
		for(int i=0;i <= 360;i++) {
			
			float myRadius = (CCMath.sin(CCMath.radians(i)*7) + 1) * 50 + 50;
			
			float myX = CCMath.sin(CCMath.radians(i)) * myRadius;
			float myY = CCMath.cos(CCMath.radians(i)) * myRadius;
			
			
			g.textureCoords(2);
			g.vertex(myX, myY);
		}
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture1DTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


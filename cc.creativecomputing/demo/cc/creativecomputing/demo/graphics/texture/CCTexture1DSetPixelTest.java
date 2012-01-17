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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.math.CCMath;

public class CCTexture1DSetPixelTest extends CCApp {
	
	private CCTextureData _myTextureData1;
	private CCTextureData _myTextureData2;
	private CCTexture1D _myTexture;

	@Override
	public void setup() {
		frameRate(30);
		_myTextureData1 = CCTextureIO.newTextureData("textures/1d_texture_colors.png");
		
		_myTexture = new CCTexture1D();
		_myTexture.data(_myTextureData1);
		for(int i = 0; i < _myTexture.width();i++) {
			_myTexture.setPixel(i, new CCColor(CCMath.random(),CCMath.random(),CCMath.random()));
		}
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0);
		g.vertex(0,0);
		g.textureCoords(1);
		g.vertex(200,0);
		g.textureCoords(1);
		g.vertex(200,200);
		g.textureCoords(0);
		g.vertex(0,200);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture1DSetPixelTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


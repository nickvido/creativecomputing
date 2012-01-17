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
import cc.creativecomputing.graphics.CCGraphics.CCTextureGenMode;
import cc.creativecomputing.graphics.texture.CCTextureCubeMap;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.util.CCArcball;

public class CCTextureCubeMapTest extends CCApp {
	
	private CCTextureData _myTextureData;
	private CCTextureCubeMap _myCubeMap;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myTextureData = CCTextureIO.loadCubeMapData(
			"textures/cubemap/cube_posx.png",
			"textures/cubemap/cube_negx.png",
			"textures/cubemap/cube_posy.png",
			"textures/cubemap/cube_negy.png",
			"textures/cubemap/cube_posz.png",
			"textures/cubemap/cube_negz.png"	
		);
		_myCubeMap = new CCTextureCubeMap(_myTextureData);
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.texture(_myCubeMap);
		g.texGen(CCTextureGenMode.REFLECTION_MAP);
		g.sphere(200);
		g.noTexGen();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureCubeMapTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


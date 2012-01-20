/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;

public class CCShaderMipmapping extends CCApp {
	
	private CCGLSLShader _myShader;
	
	private CCVBOMesh _myMesh;
	
	private CCArcball _myArcball;
	
	private CCTexture2D _myHeightMap;
	
	@CCControl(name = "lod", min = 1, max = 10)
	private float _cLod = 1;

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/mipmap.vp"),
			CCIOUtil.classPath(this, "shader/mipmap.fp")
		);
		_myShader.load();
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, 200*200);
		for(int x = 0; x < 200;x++) {
			for(int y = 0; y < 200;y++) {
				_myMesh.addVertex(x * 2 - 200, y * 2 - 200);
				_myMesh.addTextureCoords(0, x / 200f, y / 200f);
			}
		}
		
		_myHeightMap = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/heightmap.png"));
		_myHeightMap.generateMipmaps(true);
		_myHeightMap.textureFilter(CCTextureFilter.LINEAR);
		_myHeightMap.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clearColor(100);
		g.clear();
		_myArcball.draw(g);
		g.noDepthTest();
		_myShader.start();
		g.texture(0, _myHeightMap);
		_myShader.uniform1i("texture", 0);
		_myShader.uniform1f("lod", _cLod);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderMipmapping.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

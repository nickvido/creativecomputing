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
package cc.creativecomputing.demo.graphics.shader.tutorial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCShaderTutorial02 extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			"demo/gpu/tutorial/tutorial02_vert.glsl",
			"demo/gpu/tutorial/tutorial02_frag.glsl"
		);
		_myShader.load();
		
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"));
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clearColor(200);
		g.clear();
		
		g.texture(0,_myTexture);
		_myShader.start();
		_myShader.uniform1i("colorMap", 0);
		g.beginShape();
		g.textureCoords(0f, 0f);
		g.vertex(-50, -50);
		g.textureCoords(1f, 0f);
		g.vertex( 50, -50);
		g.textureCoords(1f, 1f);
		g.vertex( 50,  50);
		g.textureCoords(0f, 1f);
		g.vertex(-50,  50);
		g.endShape();
		_myShader.end();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderTutorial02.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


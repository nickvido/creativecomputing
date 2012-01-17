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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCShader01 extends CCApp {
	
	private CCGLSLShader _myShader;
	
	private CCTexture2D _myTexture;
	private CCTexture2D _myAlphaTexture;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			"demo/gpu/tutorial/shader01_vert.glsl", 
			"demo/gpu/tutorial/shader01_frag.glsl"
		);
		_myShader.load();
		
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"));
		_myAlphaTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/texone.png"));
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		g.texture(0,_myTexture);
		g.texture(1,_myAlphaTexture);
		_myShader.start();
		_myShader.uniform1i("colorMap", 0);
		_myShader.uniform1i("alphaMap", 1);
		g.color(255,0,0);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0f,0f);
		g.vertex(-150, -150);
		g.textureCoords(1f,0f);
		g.vertex( 150, -150);
		g.textureCoords(1f,1f);
		g.vertex( 150,  150);
		g.textureCoords(0f,1f);
		g.vertex(-150,  150);
		g.endShape();
		_myShader.end();
		
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShader01.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


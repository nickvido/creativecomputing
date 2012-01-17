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
package cc.creativecomputing.demo.graphics.shader.raymarching;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;

public class CCRaymarchingMetaBallsDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCTexture1D _myTexture;
	
	private CCShaderTexture _myShaderTexture;
	private CCMatrix4f _myCameraTransformation = new CCMatrix4f();
	private CCVector3f _myCameraPosition = new CCVector3f(0.0f, 0.0f, 5.0f);
	private CCVector3f _myCameraTarget;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			null, 
			new String[] {
				CCIOUtil.classPath(this,"isocube.glsl"), 
				CCIOUtil.classPath(this,"raymarcher2.glsl")
			}
		);
		_myShader.load();
		
		CCTextureData myData = new CCTextureData(5,1);
		myData.setPixel(0,0,CCColor.parseFromInteger(0x0065356B));
		myData.setPixel(1,0,CCColor.parseFromInteger(0x00AB434F));
		myData.setPixel(2,0,CCColor.parseFromInteger(0x00C76347));
		myData.setPixel(3,0,CCColor.parseFromInteger(0x00FFA24C));
		myData.setPixel(4,0,CCColor.parseFromInteger(0x00519183));
		
		_myTexture = new CCTexture1D(myData);
		
		_myShaderTexture = new CCShaderTexture(width, height);
		
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		_myShaderTexture.beginDraw();
		g.clear();
		g.texture(0,_myTexture);
		
		_myShader.start();
		_myShader.uniform1f("time", frameCount / 100f);
		_myShader.uniform2f("resolution", width, height);
		_myShader.uniform1i("sampler0",0);
		_myShader.uniform("cameraTransformation", _myCameraTransformation);
		_myShader.uniform3f("cameraPosition", _myCameraPosition);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(0.0f, 0.0f);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(width, 0.0f);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(width, height);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(0.0f, height);
        g.endShape();
        
        _myShader.end();
        _myShaderTexture.endDraw();
        g.noTexture();
        
        g.clear();
        g.image(_myShaderTexture, -width/2, -height/2);
        
        System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRaymarchingMetaBallsDemo.class);
		myManager.settings().size(800, 600);
		myManager.settings().vsync(true);
		myManager.start();
	}
}


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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;

public class CCTextureWrapTest extends CCApp {
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@CCControlClass(name = "color")
	private static class CCControls{
		@CCControl(name = "red", min = 0, max = 1)
		private static float r = 0f;
		@CCControl(name = "green", min = 0, max = 1)
		private static float g = 0f;
		@CCControl(name = "blue", min = 0, max = 1)
		private static float b = 0f;
		
		@CCControl(name = "blend red", min = 0, max = 1)
		private static float br = 0f;
		@CCControl(name = "blend green", min = 0, max = 1)
		private static float bg = 0f;
		@CCControl(name = "blend blue", min = 0, max = 1)
		private static float bb = 0f;
		
		@CCControl(name = "wrap 1")
		private static CCTextureWrap wrap1 = CCTextureWrap.CLAMP;
		@CCControl(name = "wrap 2")
		private static CCTextureWrap wrap2 = CCTextureWrap.CLAMP;
	}

	@Override
	public void setup() {
		addControls(CCTextureWrapTest.class);
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.wrap(CCControls.wrap1);
		_myTexture2.wrap(CCControls.wrap2);
		
		_myTexture1.textureBorderColor(new CCColor(CCControls.br, CCControls.bg, CCControls.bb));
		_myTexture2.textureBorderColor(new CCColor(CCControls.br, CCControls.bg, CCControls.bb));
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		g.color(CCControls.r, CCControls.g, CCControls.b);
		g.texture(_myTexture1);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -100, -0.5f, -0.5f);
		g.vertex( 0, -100, 1.5f, -0.5f);
		g.vertex( 0,  100, 1.5f, 1.5f);
		g.vertex(-200,  100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();

		g.texture(_myTexture2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, -100, -0.5f, -0.5f);
		g.vertex(200, -100, 1.5f, -0.5f);
		g.vertex(200,  100, 1.5f, 1.5f);
		g.vertex(0,  100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureWrapTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


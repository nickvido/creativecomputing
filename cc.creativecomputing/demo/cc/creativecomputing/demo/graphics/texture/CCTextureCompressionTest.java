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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;

public class CCTextureCompressionTest extends CCApp {
	
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
		
		@CCControl(name = "filter")
		private static CCTextureFilter filter = CCTextureFilter.NEAREST;
		@CCControl(name = "mipmap filter")
		private static CCTextureMipmapFilter mipmap_filter = CCTextureMipmapFilter.NEAREST;
	}

	@Override
	public void setup() {
		frameRate(30);
		addControls(CCTextureCompressionTest.class);

		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myTexture1 = new CCTexture2D(myAttributes);
		_myTexture1.compressData(CCTextureIO.newTextureData("textures/waltz.jpg"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/waltz.dds"), myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.textureFilter(CCControls.filter);
		_myTexture1.textureMipmapFilter(CCControls.mipmap_filter);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		
		g.scale(mouseX / (float)width);
		
		g.color(CCControls.r, CCControls.g, CCControls.b);
		g.texture(_myTexture1);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-_myTexture1.width()/2, -_myTexture1.height()/2, 0f, 0f);
		g.vertex( 0, -_myTexture1.height()/2, 0.5f, 0f);
		g.vertex( 0,  _myTexture1.height()/2, 0.5f, 1f);
		g.vertex(-_myTexture1.width()/2,  _myTexture1.height()/2, 0, 1);
		g.endShape();
		g.noTexture();

		g.texture(_myTexture2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, -_myTexture2.height()/2, 0.5f, 0);
		g.vertex(_myTexture2.width()/2, -_myTexture2.height()/2, 1f, 0f);
		g.vertex(_myTexture2.width()/2,  _myTexture2.height()/2, 1f, 1f);
		g.vertex(0,  _myTexture2.height()/2, 0.5f, 1f);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureCompressionTest.class);
		myManager.settings().size(1000, 667);
		myManager.start();
	}
}


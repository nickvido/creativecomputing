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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;

public class CCTextureMipmapTest extends CCApp {
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	
	@CCControl(name = "red", min = 0, max = 1)
	private float red = 0f;
	
	@CCControl(name = "green", min = 0, max = 1)
	private float green = 0f;
	
	@CCControl(name = "blue", min = 0, max = 1)
	private float blue = 0f;
		
	@CCControl(name = "filter")
	private CCTextureFilter filter = CCTextureFilter.NEAREST;
	
	@CCControl(name = "mipmap filter")
	private CCTextureMipmapFilter mipmap_filter = CCTextureMipmapFilter.NEAREST;
	

	@Override
	public void setup() {
		frameRate(30);
		addControls("mipmap", "mipmap", this);
		
		final CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("textures/dice.jpg"));
		_myTexture1.generateMipmaps(true);
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/dice.jpg"));
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.textureFilter(filter);
		_myTexture1.textureMipmapFilter(mipmap_filter);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
		
		g.scale(mouseX / (float)width);
		
		g.color(red, green, blue);
		g.image(_myTexture1,-500, -333,500,667);
		g.image(_myTexture2, 0, -333,500,667);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureMipmapTest.class);
		myManager.settings().size(1000, 667);
		myManager.start();
	}
}


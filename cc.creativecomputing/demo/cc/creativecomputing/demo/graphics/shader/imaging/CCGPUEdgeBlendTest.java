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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.CCGPUEdgeBlender;
import cc.creativecomputing.graphics.shader.imaging.CCGPUEdgeBlender.CCGPUEdgeBlendDirection;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;

public class CCGPUEdgeBlendTest extends CCApp {
	
	
	private CCGPUEdgeBlender _myEdgeBlender;
	private CCTexture2D _myBlendTexture;

	@Override
	public void setup() {
		frameRate(30);
		
		_myBlendTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"),CCTextureTarget.TEXTURE_2D);
		_myEdgeBlender = new CCGPUEdgeBlender(g, _myBlendTexture,CCGPUEdgeBlendDirection.VERTICAL);
		addControls("settings", "edgeblender", _myEdgeBlender);
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.clear();
//		g.polygonMode(CCPolygonMode.LINE);
		
		_myEdgeBlender.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUEdgeBlendTest.class);
		myManager.settings().size(1200, 600);
		myManager.start();
	}
}


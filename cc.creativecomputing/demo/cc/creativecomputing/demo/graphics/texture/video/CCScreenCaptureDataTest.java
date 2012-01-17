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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.video.CCScreenCaptureData;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;

public class CCScreenCaptureDataTest extends CCApp {
	
	private CCScreenCaptureData _myData;
	private CCVideoTexture _myVideoTexture;

	@Override
	public void setup() {
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myData = new CCScreenCaptureData(this, 0,0,1200, 800, 30);
		_myVideoTexture = new CCVideoTexture(_myData, CCTextureTarget.TEXTURE_2D, myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.image(_myVideoTexture,-width/2, -height/2);
		
//		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCScreenCaptureDataTest.class);
		myManager.settings().size(640, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


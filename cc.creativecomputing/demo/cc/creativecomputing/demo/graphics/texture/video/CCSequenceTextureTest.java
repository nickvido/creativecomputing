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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.video.CCSequenceTexture;
import cc.creativecomputing.io.CCIOUtil;

public class CCSequenceTextureTest extends CCApp {
	
	private CCSequenceTexture _mySequenceTexture;
	
	@CCControlClass(name = "color")
	private static class CCControls{
		@CCControl(name = "rate", min = -2, max = 2)
		private static float rate = 1;
	}
	private String _myFolder = "videos/kaki/";

	@Override
	public void setup() {
//		frameRate(30);
		addControls(CCSequenceTextureTest.class);
		String[] myFiles = CCIOUtil.list(_myFolder,"png");
		for(int i = 0; i < myFiles.length;i++) {
			myFiles[i] = _myFolder + myFiles[i];
		}
		
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_mySequenceTexture = new CCSequenceTexture(this, CCTextureTarget.TEXTURE_2D, myAttributes, myFiles);
		_mySequenceTexture.loop();
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {
		_mySequenceTexture.rate(CCControls.rate);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
		g.texture(_mySequenceTexture);
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -100, -0.5f, -0.5f);
		g.vertex( 0, -100, 1.5f, -0.5f);
		g.vertex( 0,  100, 1.5f, 1.5f);
		g.vertex(-200,  100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSequenceTextureTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


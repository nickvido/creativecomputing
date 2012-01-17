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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.video.CCQuicktimeMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCVideoTextureTest extends CCApp {
	
	private CCQuicktimeMovie _myData;
	private CCVideoTexture _myTexture;

	@Override
	public void setup() {
		frameRate(20);
		
		_myData = new CCQuicktimeMovie(this, CCIOUtil.dataPath("videos/kaki.mov"));
		_myData.loop();
		
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_myTexture = new CCVideoTexture(_myData, CCTextureTarget.TEXTURE_2D, myAttributes);
	}
	
	float _myTime = 0;
	float _myNoise = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime;
		_myNoise = CCMath.noise(_myTime * 0.1f);
	}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
		
//		System.out.println(_myTexture.width() + ":"+_myTexture.height());
		g.texture(_myTexture);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-width/2, -height/2, -0.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2, -height/2, 1.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2,  height/2, 1.5f * _myNoise, 1.5f *_myNoise);
		g.vertex(-width/2,  height/2, -0.5f * _myNoise, 1.5f *_myNoise);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCVideoTextureTest.class);
		myManager.settings().size(320 * 2, 212 * 2);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


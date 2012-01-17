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
package cc.creativecomputing.texture.video.kinect;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.texture.video.kinect.CCKinect.CCKinectLedStatus;

public class CCKinectDemo extends CCApp {
	
	@CCControl(name = "tilt", min = -31, max = 31)
	private float _cTilt = 0;
	
	@CCControl(name = "average")
	private boolean _cAverage = false;
	
	private CCKinect _myKinect;
	private CCVideoTexture _myColorTexture;
	private CCVideoTexture _myDepthTexture;

	@Override
	public void setup() {
		
		_myKinect = new CCKinect(this);
		_myKinect.start();
		_myKinect.isDepthActive(true);
		_myKinect.isColorActive(true);
		
		addControls("app", "app", this);
		
		_myKinect.ledStatus(CCKinectLedStatus.YELLOW);
		
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_myColorTexture = new CCVideoTexture(_myKinect.colorData(), CCTextureTarget.TEXTURE_2D, myAttributes);
		_myDepthTexture = new CCVideoTexture(_myKinect.depthData(), CCTextureTarget.TEXTURE_2D, myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myKinect.tilt(_cTilt);
		_myKinect.depthData().averageData(_cAverage);
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myDepthTexture, -width/2,-height/2);
		g.image(_myColorTexture, 0,-height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCKinectDemo.class);
		myManager.settings().size(1280, 480);
		myManager.start();
	}
}


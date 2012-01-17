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
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.CCGraphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.texture.video.kinect.CCKinect.CCKinectLedStatus;

public class CCKinectPointCloudShaderDemo extends CCApp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	
	@CCControl(name = "tilt", min = -31, max = 31)
	private float _cTilt = 0;
	
	@CCControl(name = "average")
	private boolean _cAverage = false;
	
	private CCKinect _myKinect;
	private CCVideoTexture _myColorTexture;
	private CCVideoTexture _myDepthTexture;
	
	private CCVBOMesh _myMesh;
	private CCCGShader _myShader;

	private CCArcball _myArcball;
	
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
		
		_myColorTexture = new CCVideoTexture(_myKinect.colorData(), CCTextureTarget.TEXTURE_RECT, myAttributes);
		_myDepthTexture = new CCVideoTexture(_myKinect.depthData(), CCTextureTarget.TEXTURE_RECT, myAttributes);
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, 640 * 480);
		
		for(int x = 0; x < 640;x++) {
			for(int y = 0; y < 480;y++) {
				_myMesh.addVertex(x - 320, y - 240, 0);
				_myMesh.addTextureCoords(x, y);
			}
		}
		
		_myShader = new CCCGShader("demo/gpu/kinect/pointcloud.vp", "demo/gpu/kinect/pointcloud.fp");
		_myShader.load();
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myKinect.tilt(_cTilt);
		_myKinect.depthData().averageData(_cAverage);
	}

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.texture(0,_myDepthTexture);
		g.texture(1,_myColorTexture);
		_myShader.start();
		_myMesh.draw(g);
		_myShader.end();
		g.noTexture();
		g.popMatrix();
		
		g.clearDepthBuffer();
		g.image(_myDepthTexture, 0,-height/2, 320,240);
		
//		System.out.println(_myKinect.acceleration());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCKinectPointCloudShaderDemo.class);
		myManager.settings().size(1280, 480);
		myManager.start();
	}
}


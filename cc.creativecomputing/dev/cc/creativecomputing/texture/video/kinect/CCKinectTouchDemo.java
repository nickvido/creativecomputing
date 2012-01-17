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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.CCPixelRaster;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.texture.video.kinect.CCKinect.CCKinectLedStatus;
import cc.creativecomputing.texture.video.kinect.touch.CCConnectedPixelArea;
import cc.creativecomputing.texture.video.kinect.touch.CCConnectedPixelAreas;

public class CCKinectTouchDemo extends CCApp {
	
	@CCControl(name = "tilt", min = -31, max = 31)
	private float _cTilt = 0;
	
	@CCControl(name = "average")
	private boolean _cAverage = false;
	
	@CCControl(name = "min depth", min = 0, max = 10)
	private float _cMinDepth = 0;
	
	@CCControl(name = "max depth", min = 0, max = 10)
	private float _cMaxDepth = 0;
	
	@CCControl(name = "height range", min = 0, max = 30)
	private int _cHeightRange = 20;
	
	@CCControl(name = "start height", min = -20, max = 20)
	private int _cStartHeight = 0;
	
	@CCControl(name = "threshold", min = 0, max = 1)
	private float _cThreshold = 0;
	
	private CCKinect _myKinect;
	private CCVideoTexture _myDepthTexture;
	
	private CCConnectedPixelAreas _myPixelAreas;

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
		
		_myDepthTexture = new CCVideoTexture(_myKinect.depthData(), CCTextureTarget.TEXTURE_2D, myAttributes);
		
		_myPixelAreas = new CCConnectedPixelAreas();
	}
	
	private CCPixelRaster touchRaster() {
		CCPixelRaster myRaster = new CCPixelRaster("kinect", CCKinect.DEVICE_WIDTH, _cHeightRange);
		for (int x = 0; x < CCKinect.DEVICE_WIDTH; x ++) {
			for (int y =  0; y < _cHeightRange; y ++) {
				int myY = y - _cHeightRange;
				myY -= _cStartHeight;
				myY += CCKinect.DEVICE_HEIGHT / 2;
				int offset = x + myY * CCKinect.DEVICE_WIDTH;

				// Convert kinect data to world xyz coordinate
				int rawDepth = _myKinect.depthData().depth(x, myY);
				
				CCVector3f myWorldCoords = CCKinectUtil.depthToWorld(x, myY, rawDepth);
				if(myWorldCoords.z < _cMinDepth || myWorldCoords.z > _cMaxDepth)continue;
				myRaster.set(x, y, CCMath.norm(myWorldCoords.z, _cMinDepth, _cMaxDepth));
			}
		}
		return myRaster;
	}
	
	List<CCConnectedPixelArea> _myAreas = new ArrayList<CCConnectedPixelArea>();
	CCPixelRaster _myTouchRaster = new CCPixelRaster("kinect", CCKinect.DEVICE_WIDTH, _cHeightRange);

	@Override
	public void update(final float theDeltaTime) {
		_myKinect.tilt(_cTilt);
		_myKinect.depthData().averageData(_cAverage);
		
		_myTouchRaster = touchRaster();
		_myAreas = _myPixelAreas.connectedPixelAreas(touchRaster(), _cThreshold);
	}
	
	public void drawDepthTexture(CCGraphics g) {
		g.pushMatrix();
		g.translate(-width/2,0);
		g.scale(0.5f);
		
		g.image(_myDepthTexture, 0, 0);
		
		g.color(255,0,0);
		g.line(0, _cStartHeight + CCKinect.DEVICE_HEIGHT / 2, CCKinect.DEVICE_WIDTH, _cStartHeight + CCKinect.DEVICE_HEIGHT / 2);
		
		g.color(0,255,0);
		g.line(0, _cStartHeight + CCKinect.DEVICE_HEIGHT / 2 + _cHeightRange, CCKinect.DEVICE_WIDTH, _cStartHeight + CCKinect.DEVICE_HEIGHT / 2 + _cHeightRange);
		g.popMatrix();
	}
	
	public void drawPointCloud(CCGraphics g) {
		g.pushMatrix();
		g.translate(-width/2,0);
		g.scale(0.5f);
		g.translate(width/4,0);
		g.beginShape(CCDrawMode.POINTS);
		g.color(255);
		
		float myZ = 0;
		float minZ = 3000000;
		
		for (int x = 0; x < CCKinect.DEVICE_WIDTH; x ++) {
			for (int y2 =  0; y2 < _cHeightRange; y2 ++) {
				int myY = y2 - _cHeightRange;
				myY -= _cStartHeight;
				myY += CCKinect.DEVICE_HEIGHT / 2;
				int offset = x + myY * CCKinect.DEVICE_WIDTH;

				// Convert kinect data to world xyz coordinate
				int rawDepth = _myKinect.depthData().depth(x, myY);
				CCVector3f myWorldCoords = CCKinectUtil.depthToWorld(x, myY, rawDepth);
				if(myWorldCoords.z < 0)continue;
				// Scale up by 200
				myZ = CCMath.max(myWorldCoords.z, myZ);
				minZ = CCMath.min(myWorldCoords.z, minZ);
				float factor = 200;
				g.vertex(
					CCMath.map(myWorldCoords.x, 0f, 7, 0, -height),
					CCMath.map(myWorldCoords.z, 0f, 7, 0, -height)
				);
			}
		}
		g.endShape();
		
		g.color(0,255,255);
		float myMinDepth = CCMath.map(_cMinDepth, 0f, 7, 0, -height);
		float myMaxDepth = CCMath.map(_cMaxDepth, 0f, 7, 0, -height);
		
		g.line(-width/2, myMinDepth, width/2, myMinDepth);
		g.line(-width/2, myMaxDepth, width/2, myMaxDepth);
		
		float x1 = CCMath.cos(CCMath.radians(-90 + 57.8f/2)) * 500;
		float y1 = CCMath.sin(CCMath.radians(-90 + 57.8f/2)) * 500;
		float x2 = CCMath.cos(CCMath.radians(-90 - 57.8f/2)) * 500;
		float y2 = CCMath.sin(CCMath.radians(-90 - 57.8f/2)) * 500;
		g.color(255,255,0);
		g.line(0,0, x1,y1);
		g.line(0,0, x2,y2);
		
		System.out.println(myZ+":"+minZ);
		g.popMatrix();
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		
		drawDepthTexture(g);
		drawPointCloud(g);
		
		g.beginShape(CCDrawMode.POINTS);
		g.color(0,0,255);
		
		for (int x = 0; x < CCKinect.DEVICE_WIDTH; x ++) {
			for (int y2 =  0; y2 < _cHeightRange; y2 ++) {
				int myY = y2 - _cHeightRange;
				myY -= _cStartHeight;
				myY += CCKinect.DEVICE_HEIGHT / 2;
				int offset = x + myY * CCKinect.DEVICE_WIDTH;

				// Convert kinect data to world xyz coordinate
				int rawDepth = _myKinect.depthData().depth(x, myY);
				CCVector3f myWorldCoords = CCKinectUtil.depthToWorld(x, myY, rawDepth);
				if(myWorldCoords.z < _cMinDepth || myWorldCoords.z > _cMaxDepth)continue;

				// Scale up by 200
				float factor = 200;
				g.color(CCMath.norm(myWorldCoords.z, _cMinDepth, _cMaxDepth),1f,0);
				g.vertex(x,myY - 200);
				
				g.color(255,0,0);
				g.vertex(CCMath.map(myWorldCoords.x, _cMinDepth, _cMaxDepth, 0, height) + width/2, CCMath.map(myWorldCoords.z, _cMinDepth, _cMaxDepth, 0, height) - height/2);
			}
		}
		g.endShape();
		
		for(CCConnectedPixelArea myArea:_myAreas) {
			myArea.draw(g);
			
			CCVector2i myMaxPos = new CCVector2i();
//			myArea.max(_myTouchRaster, myMaxPos);
//			
//			myMaxPos.y += CCKinect.DEVICE_HEIGHT / 2;
//			myMaxPos.y -= _cStartHeight;
//			myMaxPos.y -= _cHeightRange;
			
			myMaxPos = new CCVector2i(myArea.center());
			
			int rawDepth = _myKinect.depthData().depth(myMaxPos.x, myMaxPos.y);
			CCVector3f myWorldCoords = CCKinectUtil.depthToWorld(myMaxPos.x, myMaxPos.y, rawDepth);
			
			g.color(255,0,0);
			g.ellipse(CCMath.map(myWorldCoords.x, _cMinDepth, _cMaxDepth, 0, height) + width/2, CCMath.map(myWorldCoords.z, _cMinDepth, _cMaxDepth, 0, height) - height/2, 20);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCKinectTouchDemo.class);
		myManager.settings().size(1280, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


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
package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTextureForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCPathFollowTest extends CCApp {
	
	private class CCGPUPath{
		
		private List<CCVector2f> _myPoints = new ArrayList<CCVector2f>();
		private List<CCVector2f> _myDirections = new ArrayList<CCVector2f>(); 
		
		private float _myContourForce = 0.1f;
		private float _myAreaForce = 1f;
		
		private boolean _myHasChanged = true;
		
		public CCGPUPath() {
		}
		
		public void clear() {
			_myPoints.clear();
			_myDirections.clear();
			
			_myHasChanged = true;
		}
		
		public void contourForce(final float theContourForce) {
			_myContourForce = theContourForce;
		}
		
		public void areaForce(final float theAreaForce) {
			_myAreaForce = theAreaForce;
		}
		
		public void addPoint(final CCVector2f thePoint) {
			_myPoints.add(thePoint);
		}
		
		public void addPoint(final float theX, final float theY) {
			_myPoints.add(new CCVector2f(theX, theY));
		}
		
		public void draw(CCTesselator theTesselator) {
			if(_myHasChanged) {
				for(int i = 0; i < _myPoints.size() - 1;i++) {
					CCVector2f myPoint1 = _myPoints.get(i);
					CCVector2f myPoint2 = _myPoints.get(i + 1);
					CCVector2f myDirection  = CCVecMath.subtract(myPoint2, myPoint1).normalize();
					
					_myDirections.add(myDirection);
				}
				_myDirections.add(_myDirections.get(_myDirections.size()-1).clone());
			}
			
			
			theTesselator.beginContour();
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				_myTesselator.normal(myDirection.y * _myAreaForce, -myDirection.x * _myAreaForce, 0);
				_myTesselator.vertex(myPoint.x - myDirection.y, myPoint.y + myDirection.x);
			}
			for(int i = _myPoints.size() - 1; i >= 0;i--) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				_myTesselator.normal(-myDirection.y * _myAreaForce, myDirection.x * _myAreaForce, 0);
				_myTesselator.vertex(myPoint.x + myDirection.y, myPoint.y - myDirection.x);
			}
			theTesselator.endContour();
		}
		
		public void drawContour(CCGraphics g, final float theWeight) {
			g.color(255,0,0);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				g.normal(myDirection.x * _myContourForce, myDirection.y * _myContourForce, 0);
				g.vertex(myPoint.x - myDirection.y * theWeight, myPoint.y + myDirection.x * theWeight);
				g.vertex(myPoint.x + myDirection.y * theWeight, myPoint.y - myDirection.x * theWeight);
			}
			g.endShape();
		}
	}

	@CCControl(name = "texture alpha", min = 0, max = 1)
	private float _cTextureAlpha = 0;

	@CCControl(name = "force scale", min = 0, max = 20)
	private float _cForceScale = 0;

	@CCControl(name = "area force scale", min = 0, max = 1)
	private float _cAreaForceScale = 0;

	@CCControl(name = "contour force scale", min = 0, max = 1)
	private float _cContourForceScale = 0;
	
	@CCControl(name = "contourWeight", min = 0, max = 20)
	private float _cContourWeight = 0;
	
	@CCControl(name = "noiseSpeed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;

	private CCShaderTexture _myPathForceFieldTexture;

	private CCTesselator _myTesselator;

	private CCCGShader _myContourShader;

	private CCSignal _myNoise;

	private float _myTime = 0;

	private CCGPUQueueParticles _myParticles;
	private CCGPUTextureForceField _myForceField;

	private CCArcball _myArcball;
	
	private CCGPUPath _myPath1;
	private CCGPUPath _myPath2;

	@Override
	public void setup() {
		addControls("app", "app", this);
		// frameRate(30);
		_myPathForceFieldTexture = new CCShaderTexture(400, 400);

		_myContourShader = new CCCGShader("gpu/demo/pathfollow/contour.vp", "gpu/demo/pathfollow/contour.fp");
		_myContourShader.load();

		_myNoise = new CCSimplexNoise();
		g.strokeWeight(10);

		_myTesselator = new CCTesselator();

		_myForceField = new CCGPUTextureForceField(_myPathForceFieldTexture, new CCVector2f(2, 2), new CCVector2f(200, 200));
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myForceField);
		myForces.add(new CCGPUViscousDrag(0.3f));
		// myForces.add(new CCGPUGravity(new CCVector3f(50,0,0)));
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700, 700);
		// _myParticles.make2D(true);

		_myArcball = new CCArcball(this);
		
		_myPath1 = new CCGPUPath();
		_myPath2 = new CCGPUPath();
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * _cNoiseSpeed;
		for (int i = 0; i < 300; i++) {
			_myParticles.allocateParticle(
				new CCVector3f(-300, CCMath.random(-200,200), 0), 
				new CCVector3f(CCMath.random(10,20), CCMath.random(-10,10), 0), 30);
		}
//		for (int i = 0; i < 150; i++) {
//			_myParticles.allocateParticle(
//				new CCVector3f(-400, (_myNoise.noise(1000, _myTime) - 0.5f) * 400 - 200 + CCMath.random(-10,10), 0), 
//				new CCVector3f(CCMath.random(10,20), CCMath.random(-10,10), 0), 30);
//		}
		_myForceField.strength(_cForceScale);

		_myParticles.update(theDeltaTime);
		
		_myPath1.clear();
		for (int i = 0; i <= 400; i += 4) {
			float myNoise = 300 + (_myNoise.value(i * 0.005f, _myTime)-0.5f) * 100;
			
			_myPath1.addPoint(new CCVector2f(i, myNoise));
		}
//		for(int i = 0; i < 360;i+=10) {
//			float x = CCMath.sin(CCMath.radians(i)) * 100 + 200;
//			float y = CCMath.cos(CCMath.radians(i)) * 100 + 200;
//			_myPath1.addPoint(x,y);
//		}
		
		_myPath1.contourForce(_cContourForceScale);
		_myPath1.areaForce(_cAreaForceScale);
		
		_myPath2.clear();
		for (int i = 0; i <= 400; i += 4) {
			float myNoise = 100 + (_myNoise.value(i * 0.005f + 1000, _myTime) - 0.5f) * 200;
			
			_myPath2.addPoint(new CCVector2f(i, myNoise));
		}
		
		_myPath2.contourForce(_cContourForceScale);
		_myPath2.areaForce(_cAreaForceScale);
	}

	@Override
	public void draw() {
//		g.polygonMode(CCPolygonMode.LINE);
		_myPathForceFieldTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		_myContourShader.start();
		_myTesselator.beginPolygon();
		_myTesselator.beginContour();
		for (int i = 400; i >= 0; i -= 5) {
			_myTesselator.normal(0, 1, 0);
			_myTesselator.vertex(i, 0);
		}
//		for (int i = 0; i <= 400; i += 50) {
//			_myTesselator.normal(0, -1, 0);
//			_myTesselator.vertex(0, i);
//		}
		for (int i = 0; i <= 400; i += 5) {
			_myTesselator.normal(0, -1, 0);
			_myTesselator.vertex(i, 400);
		}
//		for (int i = 400; i >= 0; i -= 5) {
//			_myTesselator.normal(0, 1, 0);
//			_myTesselator.vertex(0, i);
//		}
		_myTesselator.endContour();
		_myTesselator.beginContour();
		_myPath1.draw(_myTesselator);
		_myTesselator.endContour();
		_myTesselator.beginContour();
		_myPath2.draw(_myTesselator);
		_myTesselator.endPolygon();
		_myPath1.drawContour(g, _cContourWeight);
		_myPath2.drawContour(g, _cContourWeight);
		_myContourShader.end();
		_myPathForceFieldTexture.endDraw();
		//
		g.strokeWeight(1f);
		_myArcball.draw(g);
		g.clearColor(0);
		g.clear();
		g.blend();
		g.color(0, 25);
		// g.clearDepthBuffer();
		// g.rect(-1000,-1000,2000,2000);
		g.color(1f, _cTextureAlpha);
		g.polygonMode(CCPolygonMode.FILL);
		g.image(_myPathForceFieldTexture, -400, -400,800,800);
		g.color(255, 50);
		// g.noBlend();
		
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
		
		

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPathFollowTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

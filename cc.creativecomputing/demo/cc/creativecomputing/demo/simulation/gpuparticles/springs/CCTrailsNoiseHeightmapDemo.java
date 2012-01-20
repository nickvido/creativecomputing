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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUDampedSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUNoiseHeightMapForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;

import com.jogamp.opengl.cg.CGparameter;

public class CCTrailsNoiseHeightmapDemo extends CCApp {
	
	private class LeadingPosition{
		CCGPUParticle _myLeadingParticle;
		float _myY;
		float _myX;
		
		public LeadingPosition(final CCGPUParticle theLeader, final float theX, final float theY){
			_myLeadingParticle = theLeader;
			_myX = theX;
			_myY = theY;
		}
		
		public void update(final float theDeltaTime){
			_myX += theDeltaTime * 5;
//			_myAnchoredSprings.setSpringPos(_myLeadingParticle, new CCVector3f(_myX, 0, _myY));
		}
	}
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speedx", min = 0, max = 0.01f)
	private float _cNoiseSpeedX = 0;
	
	@CCControl(name = "noise speedz", min = 0, max = 0.01f)
	private float _cNoiseSpeedZ = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1f)
	private float _cNoiseStrength = 0;
	
	@CCControl(name = "gravity", min = 0, max = 1f)
	private float _cGravity = 0;
	
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUQueueParticles _myParticles;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	private CCGPUSprings _mySprings;
	
	private CCGPUNoiseHeightMapForce _myHeightField = new CCGPUNoiseHeightMapForce(0.005f,1,100,new CCVector3f(100,20,30));
	
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(1,0,0));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = false;
	
	private CCTileSaver _myTileSaver;
	
	private int _myNumberOfTrails = 400;
	private int _myParticlesPerTrail = 20;
	private int _myRows = 20;
	
	private List<LeadingPosition> _myLeadingParticles = new ArrayList<LeadingPosition>();
	
	private CCCGShader _myTrialsShader;
	private CGparameter _myInfoTextureParameter;
	
	private CCShaderTexture _myForceBlendTexture;
	


	@Override
	public void setup() {
		_myTileSaver = new CCTileSaver(g);
		
		addUpdateListener(_myTileSaver);
		addPostListener(_myTileSaver);
//		frameRate(30);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myGravity);
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myHeightField);
		
		_myForceBlendTexture = new CCShaderTexture(_myNumberOfTrails,_myParticlesPerTrail * _myRows);
//		_myIDTextureForceBlend = new CCGPUIDTextureForceBlend(_myForceBlendTexture, _myForceField, new CCGPUGravity(new CCVector3f()));
//		myForces.add(_myIDTextureForceBlend);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myAnchoredSprings = new CCGPUAnchoredSprings(g,0.5f, 0.2f,1);
		_myAnchoredSprings.strength(2f);
		myForces.add(_myAnchoredSprings);
		
		_mySprings = new CCGPUDampedSprings(g,2,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCGPUQueueParticles(g,_myRenderer,myForces, myConstraints, _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myRenderer.mesh().drawMode(CCDrawMode.LINES);
		_myArcball = new CCArcball(this);
		
		List<Integer> _myIDs = new ArrayList<Integer>();
		List<CCColor> _myColors = new ArrayList<CCColor>();
		List<CCVector2f> myTextureCoords = new ArrayList<CCVector2f>();
		
		_myForceBlendTexture.beginDraw();
		g.beginShape(CCDrawMode.POINTS);
		
		int counter = 0;
		
		for(int myRow = 0; myRow < _myRows;myRow++) {
			for(int x = 0; x < _myNumberOfTrails; x++){
				_myColors.add(new CCColor(0f,0.75f));
				
				// setup trail
				for(int y = 1; y < _myParticlesPerTrail; y++){
					_myIDs.add(counter);
					myTextureCoords.add(new CCVector2f(counter % _myNumberOfTrails, counter / _myNumberOfTrails));
					counter++;
					_myIDs.add(counter);
					_myColors.add(new CCColor(0f,(1f - (float)y / _myParticlesPerTrail) * 0.75f));
				}
				counter++;
				myTextureCoords.add(new CCVector2f(counter % _myNumberOfTrails, counter / _myNumberOfTrails));
			}
		}
		
		g.endShape();
		_myForceBlendTexture.endDraw();
		
		_myRenderer.mesh().indices(_myIDs);
		_myRenderer.mesh().textureCoords(0, myTextureCoords);
		_myRenderer.mesh().colors(_myColors);
		_myParticles.update(0);
		
		g.clearColor(255);

		addControls("app", "app", this);
		
		_myTrialsShader = new CCCGShader(
				CCIOUtil.classPath(this, "shader/trails.vp"),
				CCIOUtil.classPath(this, "shader/trails.fp")
			);
		_myTrialsShader.load();
		_myInfoTextureParameter = _myTrialsShader.fragmentParameter("infoTexture");
		CGparameter myStartZParameter = _myTrialsShader.vertexParameter("startZ");
		CGparameter myEndZParameter = _myTrialsShader.vertexParameter("endZ");
		
		_myTrialsShader.parameter(myStartZParameter, -height/2);
		_myTrialsShader.parameter(myEndZParameter, height/2);
		_myTrialsShader.texture(_myInfoTextureParameter, _myParticles.positions().id(1));
		
	}
	
	private float _myTime = 0;
	int i = 0;

	@Override
	public void update(final float theDeltaTime) {
		if (_myPause)
			return;
		
	

		float myX = CCMath.random(-width/2, width/2);
		float myY = CCMath.random(-height/2, height/2);

		// setup leading particle pulled by the anchored spring

		CCGPUParticle myParticle = _myParticles.allocateParticle(new CCVector3f(myX, 0, myY), new CCVector3f(), 60, false);
		if(myParticle!= null){
			_myLeadingParticles.add(new LeadingPosition(myParticle, myX, myY));
//			_myAnchoredSprings.addSpring(myParticle, new CCVector3f(myX, 0, myY));
	
			// setup trail
			for (int y = 1; y < _myParticlesPerTrail; y++) {
				CCGPUParticle myNewParticle = _myParticles.allocateParticle(new CCVector3f(myX - y * 10f, 0, myY), new CCVector3f(), 60, false);
				_mySprings.addSpring(myNewParticle, myParticle, 10f, true);
				myParticle = myNewParticle;
			}
			if(myParticle != null)i++;
			i%=_myNumberOfTrails;
		}
		

//		for (int i = 0; i < _myLeadingParticles.size(); i++) {
//			_myAnchoredSprings.setSpringPos(_myLeadingParticles.get(i), new CCVector3f(myMouseX, myMouseY, 0));
//		}

		_myTime += theDeltaTime;

		_myHeightField.noiseOffset(new CCVector3f(_myTime * _cNoiseSpeedX, 0, _myTime * _cNoiseSpeedZ));
		_myHeightField.noiseScale(_cNoiseScale);
		_myHeightField.strength(_cNoiseStrength);
		
		_myGravity.direction().x = _cGravity;

		_myParticles.update(theDeltaTime);
		
		List<LeadingPosition> myNewLeadingParticles = new ArrayList<LeadingPosition>();
		
		for (LeadingPosition myLeadingParticle:_myLeadingParticles) {
			if(myLeadingParticle._myLeadingParticle.timeOfDeath() > _myTime){
				myNewLeadingParticles.add(myLeadingParticle);
				myLeadingParticle.update(theDeltaTime);
			}
		}
		
		_myLeadingParticles = myNewLeadingParticles;
		
//		FloatBuffer myData = _myParticles.positions().getData(1);
//		while(myData.hasRemaining()){
//			for(int i = 0; i < _myNumberOfTrails;i++){
//				System.out.print("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
//			}
//			System.out.println();
//		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		g.blend();
		g.noDepthTest();
		g.color(0,75);
		
		_myTrialsShader.start();
		_myRenderer.mesh().draw(g);
		_myTrialsShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTrailsNoiseHeightmapDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

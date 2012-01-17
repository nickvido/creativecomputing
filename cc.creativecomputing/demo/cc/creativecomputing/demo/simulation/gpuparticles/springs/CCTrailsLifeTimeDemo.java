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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUDampedSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;

import com.jogamp.opengl.cg.CGparameter;

public class CCTrailsLifeTimeDemo extends CCApp {
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise spped", min = 0, max = 0.01f)
	private float _cNoiseSpeed = 0;
	
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUQueueParticles _myParticles;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	private CCGPUSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = false;
	
	private CCTileSaver _myTileSaver;
	
	private int _myNumberOfTrails = 600;
	private int _myParticlesPerTrail = 30;
	private int _myRows = 20;
	
	private List<CCGPUParticle> _myLeadingParticles = new ArrayList<CCGPUParticle>();
	
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
//		myForces.add(new CCGPUGravity(new CCVector3f(0,-0.1f,0)));
		_myForceField.strength(1f);
		myForces.add(_myForceField);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myForceBlendTexture = new CCShaderTexture(_myNumberOfTrails,_myParticlesPerTrail * _myRows);
//		myForces.add(_myIDTextureForceBlend);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myAnchoredSprings = new CCGPUAnchoredSprings(g,0.5f, 0.2f,1);
		_myAnchoredSprings.strength(2f);
		myForces.add(_myAnchoredSprings);
		
		_mySprings = new CCGPUDampedSprings(g,2,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCGPUQueueParticles(g, _myRenderer, myForces, myConstraints, _myNumberOfTrails,_myParticlesPerTrail * _myRows);
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
				_myColors.add(new CCColor(0f,0.1f));
				
				// setup trail
				for(int y = 1; y < _myParticlesPerTrail; y++){
					_myIDs.add(counter);
					myTextureCoords.add(new CCVector2f(counter % _myNumberOfTrails, counter / _myNumberOfTrails));
					counter++;
					_myIDs.add(counter);
					_myColors.add(new CCColor(0f,(1f - (float)y / _myParticlesPerTrail)*0.1f));
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
		
		_myTrialsShader.texture(_myInfoTextureParameter, _myParticles.positions().id(1));
		
		
	}
	
	private float _myTime = 0;
	
	private boolean _myDoEmit = true;
	
	private boolean _myDoDebug = false;
	int i = 0;

	@Override
	public void update(final float theDeltaTime) {
		if (_myPause)
			return;
		
	

		float myMouseX = mouseX - width / 2;
		float myMouseY = height / 2 - mouseY;

		// setup leading particle pulled by the anchored spring

		if(_myDoEmit) {
			CCGPUParticle myParticle = _myParticles.allocateParticle(new CCVector3f(myMouseX, myMouseY, 0), new CCVector3f(), 10, false);
			if(myParticle!= null){
				_myLeadingParticles.add(myParticle);
				_myAnchoredSprings.addSpring(myParticle, new CCVector3f(0, i * 20, 0));
		
				// setup trail
				for (int y = 1; y < _myParticlesPerTrail; y++) {
					CCGPUParticle myNewParticle = _myParticles.allocateParticle(new CCVector3f(myMouseX + y * 10f, myMouseY, 0), new CCVector3f(), 10, false);
					_mySprings.addSpring(myNewParticle, myParticle, 10f, true);
					myParticle = myNewParticle;
				}
				if(myParticle != null)i++;
				i%=_myNumberOfTrails;
			}
		}
		

		for (int i = 0; i < _myLeadingParticles.size(); i++) {
			_myAnchoredSprings.setSpringPos(_myLeadingParticles.get(i), new CCVector3f(myMouseX, myMouseY, 0));
		}

		_myTime += theDeltaTime;

		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime * _cNoiseSpeed));
		_myForceField.noiseScale(_cNoiseScale);

		_myParticles.update(theDeltaTime);
		
		List<CCGPUParticle> myNewLeadingParticles = new ArrayList<CCGPUParticle>();
		
		for (CCGPUParticle myLeadingParticle:_myLeadingParticles) {
			if(myLeadingParticle.timeOfDeath() > _myTime){
				myNewLeadingParticles.add(myLeadingParticle);
			}
		}
		
		_myLeadingParticles = myNewLeadingParticles;
		
		System.out.println("trial");
		if(_myDoDebug) {
			FloatBuffer myData = _myParticles.positions().getData(1);
			while(myData.hasRemaining()){
				for(int i = 0; i < _myNumberOfTrails;i++){
					if(i % _myParticlesPerTrail == 0)System.out.println("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
					else {
						myData.get();
						myData.get();
						myData.get();
						myData.get();
						
					}
				}
			}
				_myDoDebug = false;
		}
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
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case CCKeyEvent.VK_S:

//			_myParticles.pointSizeClamp(15, 15);
			_myTileSaver.init("export_tile/"+frameCount+".png");
//			CCScreenCapture.capture("export/"+frameCount+".png", width, height);
			break;
		case CCKeyEvent.VK_P:
//			_myParticles.pointSizeClamp(2, 2);
			_myPause = !_myPause;
			break;
		case CCKeyEvent.VK_E:
			_myDoEmit = !_myDoEmit;
//			if(!_myDoEmit) {
//				for(CCGPUParticle myLeadingParticle:_myLeadingParticles) {
//					_myParticles.minAge(myLeadingParticle.index(),myLeadingParticle.index()+_myParticlesPerTrail, 18);
//				}
//			}
			break;
		case CCKeyEvent.VK_D:
			_myDoDebug = !_myDoDebug;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTrailsLifeTimeDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


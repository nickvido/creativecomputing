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
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.math.CCMath;
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

public class CCTrailsDemo extends CCApp {
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 0.01f)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1f)
	private float _cNoiseStrength = 0;
	

	
	@CCControl(name = "anchored spring strength", min = 0, max = 1f)
	private float _cAnchorStrength = 0;
	
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUQueueParticles _myParticles;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	private CCGPUSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = false;
	
	private CCTileSaver _myTileSaver;
	
	private int _myNumberOfTrails = 100;
	private int _myParticlesPerTrail = 70;
	private int _myRows = 10;
	
	private List<CCGPUParticle> _myLeadingParticles = new ArrayList<CCGPUParticle>();
	
	private CCShaderTexture _myForceBlendTexture;

	@Override
	public void setup() {
		_myTileSaver = new CCTileSaver(g);
		
		addUpdateListener(_myTileSaver);
		addPostListener(_myTileSaver);
//		frameRate(30);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
//		myForces.add(new CCGPUGravity(new CCVector3f(0,0,-1f)));
		_myForceField.strength(2f);
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
		
		_myForceBlendTexture.beginDraw();
		g.beginShape(CCDrawMode.POINTS);
		
		for(int myRow = 0; myRow < _myRows;myRow++) {
			for(int x = 0; x < _myNumberOfTrails; x++){
				// setup leading particle pulled by the anchored spring
				int myX = x % 500;
				int myY = x / 500 + myRow * 2;
				myX *= 5;
				myY *= 5;
				
				CCGPUParticle myParticle = _myParticles.allocateParticle(
					new CCVector3f(myX,myY, 0),
					new CCVector3f(),
					3000, false
				);
				g.color(1f);
				g.vertex(myParticle.x(),myParticle.y(),0);
				_myLeadingParticles.add(myParticle);
				_myAnchoredSprings.addSpring(
					myParticle,new CCVector3f(myX,myY,0)
				);
				_myColors.add(new CCColor(0f,0.1f));
				
				// setup trail
				for(int y = 1; y < _myParticlesPerTrail; y++){
					CCGPUParticle myNewParticle = _myParticles.allocateParticle(
						new CCVector3f(myX,myY, y * 10f),
						new CCVector3f(),
						3000, false
					);
					_mySprings.addSpring(myNewParticle, myParticle, 10f, true);
					_myIDs.add(myParticle.index());
					_myIDs.add(myNewParticle.index());
					_myColors.add(new CCColor(0f,(1f - (float)y / _myParticlesPerTrail) * 0.1f));
					myParticle = myNewParticle;
					
					g.color(CCMath.pow(1f - (float)y / _myParticlesPerTrail,1f));
					g.vertex(myParticle.x(),myParticle.y(),0);
				}
			}
		}
		
		g.endShape();
		_myForceBlendTexture.endDraw();
		
		_myRenderer.mesh().indices(_myIDs);
		_myRenderer.mesh().colors(_myColors);
		_myParticles.update(0);
		g.clearColor(255);

		addControls("app", "app", this);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myPause)return;
		
		_myTime += theDeltaTime * 100;
		
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime * _cNoiseSpeed));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		
		_myAnchoredSprings.strength(_cAnchorStrength);
		
		_myParticles.update(theDeltaTime);
//		_myMesh.vertices(_myParticles.positions());
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
//		g.noBlend();
		g.blend();
		g.noDepthTest();
		_myParticles.draw();
		g.color(0,144);
//		_myMesh.draw(g);
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTrailsDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

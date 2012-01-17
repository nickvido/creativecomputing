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
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUSprings;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;

public class CCParticlesSpringsAttractorTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private CCGPUAttractor _myAttractor;
	
	private CCGPUParticlePointRenderer _myRenderer;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		_myForceField.strength(1f);
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUSprings(g,4,2f,2.5f);
		myForces.add(_mySprings);
		
		_myAttractor = new CCGPUAttractor(new CCVector3f(-1000,2000,0), -5f, 300);
		myForces.add(_myAttractor);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCGPUQueueParticles(g,_myRenderer,myForces,myConstraints,600,600);
		
		CCGPUParticle[] myLeftParticles = new CCGPUParticle[600];
		
		for(int x = 0; x < 600; x++){
			CCGPUParticle myParticleAbove = null;
			for(int y = 0; y < 600; y++){
				CCGPUParticle myParticle = _myParticles.allocateParticle(
					new CCVector3f(x * 2.5f - 750, y * 2.5f - 750,100),
					new CCVector3f(),
					3000, false
				);
				
				if(myParticleAbove != null) {
					_mySprings.addSpring(myParticleAbove, myParticle, 2.5f);
				}
				
				if(myLeftParticles[y] != null) {
					_mySprings.addSpring(myLeftParticles[y], myParticle, 2.5f);
				}

				myParticleAbove = myParticle;
				myLeftParticles[y] = myParticle;
			}
		}
		
		List<Integer> _myIndices = new ArrayList<Integer>();
		int counter = 0;
		for(int x = 0; x < 600; x++){
			for(int y = 0; y < 600; y++){
				if(y > 0)_myIndices.add(counter - 1);
				_myIndices.add(counter);
				if(y < 300 - 1)_myIndices.add(counter + 1);
				counter++;
				if(x < 599) {
					_myIndices.add(counter);
					_myIndices.add(counter + 600);
				}
				if(y < 599) {
					_myIndices.add(counter);
					_myIndices.add(counter + 1);
				}
			}
		}
		_myArcball = new CCArcball(this);
		
		_myRenderer.mesh().indices(_myIndices);
		
		g.strokeWeight(0.5f);
		g.clearColor(255);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 10;
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		
//		if(mousePressed)_myAttractor.strength(1f);
//		else _myAttractor.strength(0);
		_myAttractor.position().x(mouseX - width/2);
		_myAttractor.position().y(-mouseY + height/2);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		g.scale(0.5f);
		_myArcball.draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0,0.25f);
		_myParticles.draw();
//		g.noTexture();
		
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSpringsAttractorTest.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


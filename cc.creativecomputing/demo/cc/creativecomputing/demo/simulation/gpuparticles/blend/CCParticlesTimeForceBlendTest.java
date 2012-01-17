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
package cc.creativecomputing.demo.simulation.gpuparticles.blend;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesTimeForceBlendTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUForceField _myNoiseForceField;
	
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private CCGPUTimeForceBlend _myTimeBlendForce;

	@Override
	public void setup() {
		_myNoiseForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
		
		_myTimeBlendForce = new CCGPUTimeForceBlend(0,10, _myNoiseForceField, _myTargetForce);
		_myTimeBlendForce.blend(0.002f, 1f);
		_myTimeBlendForce.power(6);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myTimeBlendForce);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),600,600);
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("demo/particles/texone2.png"),4);
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		addControls(getClass());
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 1000; i++){
			int myIndex = _myParticles.nextFreeId();
			if(myIndex < 0)break;
			
			CCVector3f myTarget = _myTargetMaskSetup.target(myIndex);
			
			if(_myIsEmittingLine) {
				_myParticles.allocateParticle(
					new CCVector3f(myTarget.x,height/2,0),
					CCVecMath.random3f(10).subtract(-10, -10, 0),
					15, false
				);
			}else {
				_myParticles.allocateParticle(
					new CCVector3f(-500,200),
					CCVecMath.random3f(10).subtract(-10, -10, 0),
					15, false
				);
			}
		}
		
		_myTime += theDeltaTime;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myParticles.update(theDeltaTime);

//		FloatBuffer myData = _myParticles.positions().getData(1);
//		for(int i = 0; i < 10;i++) {
//			System.out.print(myData.get());
//			System.out.print(" _ ");
//			System.out.print(myData.get());
//			System.out.print(" _ ");
//			System.out.print(myData.get());
//			System.out.print(" _ ");
//			System.out.print(myData.get());
//			System.out.print(" | ");
//		}
//		System.out.println();
	}

	@Override
	public void draw() {
		g.clear();
		g.blend(CCBlendMode.BLEND);
		_myParticles.draw();
		
	}
	
	private boolean _myIsEmittingLine = true;
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 'l':
			_myIsEmittingLine =! _myIsEmittingLine;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTimeForceBlendTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


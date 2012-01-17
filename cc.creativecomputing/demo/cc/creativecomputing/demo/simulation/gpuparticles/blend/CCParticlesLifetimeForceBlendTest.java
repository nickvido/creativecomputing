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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimeForceBlend.CCGPUPlayMode;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimeForceBlend.CCGPUTimeMode;

public class CCParticlesLifetimeForceBlendTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUForceField _myNoiseForceField;
	private CCGPUGravity _myGravity;
	
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	private CCTexture _mySetupTexture;
	
	private List<Integer> _myCloseIndices = new ArrayList<Integer>();
	private int _myCloseIndex = 0;
	private float _myNumberToClose = 1;
	
	private CCGPUTimeForceBlend _myTimeBlendForce;
	
	private CCArcball _myArcball;
	
	private boolean _myIsOpening = true;

	@Override
	public void setup() {
		_myNoiseForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
		_myTimeBlendForce = new CCGPUTimeForceBlend(0,4, _myNoiseForceField, _myTargetForce);
		_myTimeBlendForce.blend(0.005f, 1f);
		_myTimeBlendForce.power(6);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myTimeBlendForce);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myGravity = new CCGPUGravity(new CCVector3f());
		myForces.add(_myGravity);
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),500,500);
		
		CCTextureData mySetupTextureData = CCTextureIO.newTextureData("demo/particles/texone2.png");
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(mySetupTextureData,4);
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		for(int i = 0; i < mySetupTextureData.width();i++) {
			List<Integer> myIntgers = _myTargetMaskSetup.indicesForArea(i, 0, i+1, mySetupTextureData.height());
			_myCloseIndices.addAll(myIntgers);
		}
		
		_myArcball = new CCArcball(this);
		
		addControls(getClass());
		
		g.clearColor(255);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		if(_myIsOpening) {
			for(int i = 0; i < 5000; i++){
				int myIndex = _myParticles.nextFreeId();
				if(myIndex < 0)break;
				
				CCVector3f myTarget = _myTargetMaskSetup.target(myIndex);
				
				if(_myIsEmittingLine) {
					_myParticles.allocateParticle(
						new CCVector3f(myTarget.x(),height/2,0),
						CCVecMath.random3f(10).subtract(-10, -10, 0),
						5, true
					);
				}else {
					_myParticles.allocateParticle(
						new CCVector3f(-500,200),
						CCVecMath.random3f(10).subtract(-10, -10, 0),
						15, true
					);
				}
			}
		}else {
			int myStartIndex = _myCloseIndex;
			int myEndIndex = CCMath.min(_myCloseIndex + 2500, _myCloseIndices.size());
			_myParticles.isPermanent(_myCloseIndices.subList(myStartIndex, myEndIndex),false);
			_myCloseIndex = myEndIndex;
			if(_myCloseIndex <= _myCloseIndices.size()) {
				_myNumberToClose *=1.1f;
				_myNumberToClose = CCMath.min(_myNumberToClose, 5000);
			}
		}
		
		_myTime += theDeltaTime;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		_myArcball.draw(g);
		g.clear();
		g.blend(CCBlendMode.BLEND);
		g.color(0);
		_myParticles.draw();
	}
	
	private boolean _myIsEmittingLine = true;
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 'l':
			_myIsEmittingLine =! _myIsEmittingLine;
			break;
		case 'k':
			_myCloseIndex = 0;
			_myNumberToClose = 1;
			_myIsOpening = false;
//			_myParticles.isPermanent(false);
			_myTimeBlendForce.playMode(CCGPUPlayMode.BACKWARD);
			_myTimeBlendForce.timeMode(CCGPUTimeMode.LIFE_TIME);
			_myTimeBlendForce.startTime(0);
			_myTimeBlendForce.endTime(0.02f);
			
			_myNoiseForceField.strength(1f);
			_myGravity.direction().set(0, 0, -1);
			break;
		case 'o':
			_myIsOpening = true;
			_myTimeBlendForce.playMode(CCGPUPlayMode.FORWARD);
			_myTimeBlendForce.timeMode(CCGPUTimeMode.TIME);
			_myTimeBlendForce.startTime(0);
			_myTimeBlendForce.endTime(4);
			
			_myNoiseForceField.strength(1);
			_myGravity.direction().set(0,0,0);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesLifetimeForceBlendTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


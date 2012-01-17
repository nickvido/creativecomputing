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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimedTextureForceBlend;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesTimedTextureBlendTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUForceField _myNoiseForceField;
	
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private CCGPUTimedTextureForceBlend _myTimeBlendForce;
	
	private CCRenderTexture _myFadeTexture;
	
	@CCControl(name = "blend back", min = 0, max = 1f)
	private float _cBlendBack = 0.5f;
	
	@CCControl(name = "blend", min = 0, max = 1f)
	private float _cBlend = 0.5f;

	@Override
	public void setup() {
		frameRate(20);

		addControls("app","app", this);
		_myFadeTexture = new CCRenderTexture(g, CCTextureTarget.TEXTURE_RECT, 600, 300);
		_myNoiseForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));

		_myTimeBlendForce = new CCGPUTimedTextureForceBlend(
			_myFadeTexture,
			new CCVector2f(2,2),
			new CCVector2f(300,150), 
			_myNoiseForceField, 
			_myTargetForce
		);
		addControls("forces","time texture blend", _myTimeBlendForce);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myTimeBlendForce);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),600,600);
		_myParticles.make2D(true);
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("demo/particles/texone2.png"),4);
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		_myUI.drawBackground(false);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		_myFadeTexture.beginDraw();
		g.clearColor(_cBlendBack);
		g.clear();
		g.color(_cBlend);
		g.ellipse(0, 0, 200);
		_myFadeTexture.endDraw();
		
		for(int i = 0; i < 1000; i++){
			int myIndex = _myParticles.nextFreeId();
			if(myIndex < 0)break;
			
			
				_myParticles.allocateParticle(
					CCVecMath.random(-width/2, width/2, -height/2, height/2, 0, 0),
					CCVecMath.random3f(10).subtract(-10, -10, 0),
					5, false
				);
		}
		
		_myTime += theDeltaTime;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		g.color(255);
		g.blend(CCBlendMode.BLEND);
		_myParticles.draw();
//		g.image(_myTimeBlendForce.blendTexture(), 0,0);
//		g.image(_myFadeTexture, 0,-300);
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
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTimedTextureBlendTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


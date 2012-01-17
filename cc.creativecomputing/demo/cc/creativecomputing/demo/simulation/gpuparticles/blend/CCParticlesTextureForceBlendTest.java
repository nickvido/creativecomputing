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
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTextureForceBlend;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTextureForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesTextureForceBlendTest extends CCApp {
	
	@CCControlClass(name = "texture force field")
	public static class ForceFieldSettings{
		
		@CCControl(name = "scale x", min = 0.1f, max = 10)
		public static float scaleX = 0;
		
		@CCControl(name = "scale y", min = 0.1f, max = 10)
		public static float scaleY = 0;
		
		@CCControl(name = "offset x", min = -1000, max = 1000)
		public static float offsetX = 0;

		@CCControl(name = "offset y", min = -1000, max = 1000)
		public static float offsetY = 0;
		
		@CCControl(name = "force scale", min = 0, max = 20)
		public static float forceScale = 0;
	}
	
	@CCControlClass(name = "texture force blend")
	public static class ForceBlendSettings{
		
		@CCControl(name = "scale x", min = 0.1f, max = 10)
		public static float scaleX = 0;
		
		@CCControl(name = "scale y", min = 0.1f, max = 10)
		public static float scaleY = 0;
		
		@CCControl(name = "offset x", min = -1000, max = 1000)
		public static float offsetX = 0;

		@CCControl(name = "offset y", min = -1000, max = 1000)
		public static float offsetY = 0;
		
		@CCControl(name = "force scale", min = 0, max = 20)
		public static float forceScale = 0;
	}
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUTextureForceField _myTextureForceField;
	private CCGPUForceField _myNoiseForceField;
	private CCGPUGravity _myGravity;
	
	private CCGPUTextureForceBlend _myForceBlend1;
	private CCGPUTextureForceBlend _myForceBlend2;

	@Override
	public void setup() {
		CCTexture2D myForceFieldTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/forcefield.png"), CCTextureTarget.TEXTURE_RECT);
		_myTextureForceField = new CCGPUTextureForceField(
			myForceFieldTexture,
			new CCVector2f(1,1),
			new CCVector2f(0,0)
		);
		_myNoiseForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
		
		CCTexture2D myBlendTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/texone.png"), CCTextureTarget.TEXTURE_RECT);
		_myForceBlend1 = new CCGPUTextureForceBlend(myBlendTexture, _myTextureForceField, _myNoiseForceField);
		
		_myGravity = new CCGPUGravity(new CCVector3f(0,-20,0));
		_myForceBlend2 = new CCGPUTextureForceBlend(myBlendTexture, _myForceBlend1, _myGravity);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
//		myForces.add(_myForceBlend1);
		myForces.add(_myForceBlend2);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),300,300);
		_myParticles.make2D(true);
		
		addControls(getClass());
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 100;i++) {
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),0),
				new CCVector3f(), 
				5
			);
		}
		_myTextureForceField.textureScale().set(ForceFieldSettings.scaleX, ForceFieldSettings.scaleY);
		_myTextureForceField.textureOffset().set(ForceFieldSettings.offsetX, ForceFieldSettings.offsetY);
		_myTextureForceField.strength(ForceFieldSettings.forceScale);
		
		_myTime += theDeltaTime * 0.1f;
		_myNoiseForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myNoiseForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		_myForceBlend1.textureScale().set(ForceBlendSettings.scaleX, ForceBlendSettings.scaleY);
		_myForceBlend1.textureOffset().set(ForceBlendSettings.offsetX, ForceBlendSettings.offsetY);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.image(_myForceFieldTexture, -width/2, -height/2);
		_myParticles.draw();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTextureForceBlendTest.class);
		myManager.settings().size(600, 300);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


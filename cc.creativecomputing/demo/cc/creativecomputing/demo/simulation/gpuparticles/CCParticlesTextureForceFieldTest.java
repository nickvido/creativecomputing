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
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTextureForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesTextureForceFieldTest extends CCApp {
	
	@CCControlClass(name = "force")
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
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUTextureForceField _myForceField;
	private CCTexture2D _myForceFieldTexture;

	@Override
	public void setup() {
		_myForceFieldTexture = new CCTexture2D(CCTextureIO.newTextureData("textures/world_stream_map.png"), CCTextureTarget.TEXTURE_RECT);
		_myForceField = new CCGPUTextureForceField(
			_myForceFieldTexture,
			new CCVector2f(1,1),
			new CCVector2f(450,225)
		);
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myForceField);
		myForces.add(new CCGPUViscousDrag(0.3f));
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),300,300);
		_myParticles.make2D(true);
		
		addControls(getClass());
	}
	
	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 100;i++) {
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),0),
				new CCVector3f(), 
				5
			);
		}
//		_myForceField.textureScale().set(ForceFieldSettings.scaleX, ForceFieldSettings.scaleY);
//		_myForceField.textureOffset().set(ForceFieldSettings.offsetX, ForceFieldSettings.offsetY);
		_myForceField.strength(ForceFieldSettings.forceScale);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.clearDepthBuffer();
		g.color(1,1f);
//		g.rect(-width/2, -height/2, width, height);
//		g.image(_myForceFieldTexture, -width/2, -height/2);
		g.color(255);
		_myParticles.draw();
		g.blend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTextureForceFieldTest.class);
		myManager.settings().size(900, 450);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


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
package cc.creativecomputing.simulation.gpuparticles.render;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCGPUParticleQuadsRenderer extends CCGPUParticleRenderer{
	
	private CCShaderTexture _myQuadsPositionTexture;
	
	private CCCGShader _myUpdateQuadsShader;
	private CGparameter _myPositionTextureParameter;
	private CGparameter _myForwardTextureParameter;
	private CGparameter _mySideTextureParameter;
	private CGparameter _myUpTextureParameter;
	private CGparameter _myScaleParameter;

	/* (non-Javadoc)
	 * @see cc.creativecomputing.gpu.particles.render.CCGPUParticleRenderer#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public void draw(CCGraphics theG) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.gpu.particles.render.CCGPUParticleRenderer#setup(cc.creativecomputing.gpu.particles.CCGPUAbstractParticles)
	 */
	@Override
	public void setup(CCGPUParticles theParticles) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.gpu.particles.render.CCGPUParticleRenderer#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		// TODO Auto-generated method stub
		
	}

}

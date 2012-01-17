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
package cc.creativecomputing.simulation.gpuparticles.fluidfield;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Divergence of velocity: This computes how divergent the velocity field is
 * (how much in/out flow there is at every point).  Used as input to the 
 * Poisson solver, below.
 * @author info
 * RENDER TO DIVERGENCE TEXTURE
 * Render without border
 */
public class CCGPUFluidDivergenceShader extends CCCGShader{

	private CGparameter _myWTextureParameter;
	private CGparameter _myHalfRdxParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidDivergenceShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/divergence.fp"));
		
		_myWTextureParameter = fragmentParameter("w");
		_myHalfRdxParameter = fragmentParameter("halfrdx");
		
		load();
	}

	/**
	 * Set to velocity texture
	 * @param theWTexture
	 */
	public void velocityTexture(final CCTexture2D theWTexture) {
		texture(_myWTextureParameter, theWTexture.id());
	}
	
	public void halfRdx(final float theHalfRdx) {
		parameter(_myHalfRdxParameter, theHalfRdx);
	}
}

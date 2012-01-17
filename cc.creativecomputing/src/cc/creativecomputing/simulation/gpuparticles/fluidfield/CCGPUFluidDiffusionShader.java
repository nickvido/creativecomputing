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
 * Poisson-pressure solver: By running this Jacobi Relaxation solver for 
 * multiple iterations, this solves for the pressure disturbance in the 
 * fluid given the divergence of the velocity.
 * 
 * render to pressure texture
 * render without border
 * @author info
 *
 */
public class CCGPUFluidDiffusionShader extends CCCGShader{

	private CGparameter _myTextureXParameter;
	private CGparameter _myTextureBParameter;
	
	private CGparameter _myAlphaParameter;
	private CGparameter _myRBetaParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidDiffusionShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/diffusion.fp"));
		_myTextureXParameter = fragmentParameter("x");
		_myTextureBParameter = fragmentParameter("b");
		_myAlphaParameter = fragmentParameter("alpha");
		_myRBetaParameter = fragmentParameter("rBeta"); 
		load();
	}
	
	public void textureX(CCTexture2D theTextureX) {
		texture(_myTextureXParameter,theTextureX.id());
	}
	
	public void textureB(CCTexture2D theTextureB) {
		texture(_myTextureBParameter,theTextureB.id());
	}
	
	public void alpha(final float theAlpha) {
		parameter(_myAlphaParameter, theAlpha);
	}

	public void rBeta(final float theRBeta) {
		parameter(_myRBetaParameter, theRBeta);
	}
}

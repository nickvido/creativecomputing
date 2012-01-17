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
 * This applies pure neumann boundary conditions (see floPoissonBC.cg) to 
 * the pressure field once per iteration of the poisson-pressure jacobi 
 * solver.  Also no-slip BCs to velocity once per time step.
 * @author info
 *
 */
public class CCGPUFluidBoundaryShader extends CCCGShader{
	
	private CGparameter _myScaleParameter;
	private CGparameter _myTextureParameter;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidBoundaryShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/boundary.fp"));
		_myScaleParameter = fragmentParameter("scale");
		_myTextureParameter = fragmentParameter("x");
		load();
	}
	
	public void scale(final float theScale) {
		parameter(_myScaleParameter, theScale);
	}

	public void texture(final CCTexture2D theTexture) {
		texture(_myTextureParameter,theTexture.id());
	}
}

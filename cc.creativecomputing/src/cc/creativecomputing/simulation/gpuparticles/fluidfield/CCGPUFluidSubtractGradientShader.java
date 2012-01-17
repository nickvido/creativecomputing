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
 * Subtract Gradient.  After solving for the pressure disturbance, this 
 * subtracts the pressure gradient from the divergent velocity field to 
 * give a divergence-free field.
 * @author info
 * RENDER TO Velocity TEXTURE
 * Render without border
 */
public class CCGPUFluidSubtractGradientShader extends CCCGShader{

	private CGparameter _myPressureTextureParameter;
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myHalfRdxParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidSubtractGradientShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/gradient.fp"));
		
		_myPressureTextureParameter = fragmentParameter("pressureTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myHalfRdxParameter = fragmentParameter("halfrdx");
		
		load();
	}

	/**
	 * Set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final CCTexture2D theVelocityTexture) {
		texture(_myVelocityTextureParameter,theVelocityTexture.id());
	}
	

	public void pressureTexture(final CCTexture2D thePressureTextureTexture) {
		texture(_myPressureTextureParameter,thePressureTextureTexture.id());
	}
	
	public void halfRdx(final float theHalfRdx) {
		parameter(_myHalfRdxParameter, theHalfRdx);
	}
}

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
 * vorticity confinement force computation.
 * @author info
 * RENDER TO velocity TEXTURE
 * Render without border
 */
public class CCGPUFluidVorticityForceShader extends CCCGShader{

	private CGparameter _myVorticityTextureParameter;
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myHalfRdxParameter;
	private CGparameter _myDXScaleParameter;
	private CGparameter _myDeltaTimeParameter;
	
	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidVorticityForceShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/vorticityforce.fp"));
		
		_myVorticityTextureParameter = fragmentParameter("vorticityTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myHalfRdxParameter = fragmentParameter("halfrdx");
		_myDXScaleParameter = fragmentParameter("dxscale");
		_myDeltaTimeParameter = fragmentParameter("deltaTime");
		load();
	}
	
	public void vorticityTexture(final CCTexture2D theVorticityTexture) {
		texture(_myVorticityTextureParameter,theVorticityTexture.id());
	}

	/**
	 * Set to velocity texture
	 * @param theVelocityTexture
	 */
	public void velocityTexture(final CCTexture2D theVelocityTexture) {
		texture(_myVelocityTextureParameter,theVelocityTexture.id());
	}
	
	public void gridScale(final float theGridScale) {
		parameter(_myHalfRdxParameter, 0.5f / theGridScale);
		parameter(_myDXScaleParameter, theGridScale, theGridScale);
	}
	
	public void deltaTime(final float theDeltaTime) {
		parameter(_myDeltaTimeParameter, theDeltaTime);
	}
}

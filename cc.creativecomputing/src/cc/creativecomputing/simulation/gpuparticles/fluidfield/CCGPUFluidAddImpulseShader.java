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
import cc.creativecomputing.math.CCVector2f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 *
 */
public class CCGPUFluidAddImpulseShader extends CCCGShader{
	

	private CGparameter _myWindowDimensionParameter;
	private CGparameter _myPositionParameter;
	private CGparameter _myColorParameter;
	
	private CGparameter _myRadiusParameter;
	
	private CGparameter _myBaseTextureParameter;

	/**
	 * @param theG
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGPUFluidAddImpulseShader(CCGraphics theG) {
		super(null, CCIOUtil.classPath(CCGPUFluid.class,"shader/addimpulse.fp"));
		_myWindowDimensionParameter = fragmentParameter("windowDimension");
		_myPositionParameter = fragmentParameter("position");
		_myColorParameter = fragmentParameter("color");
		_myRadiusParameter = fragmentParameter("radius");
		_myBaseTextureParameter = fragmentParameter("baseTexture");
		load();
	}

	public void position(final CCVector2f thePosition) {
		parameter(_myPositionParameter, thePosition);
	}
	
	public void windowDimension(final float theWidth, final float theHeight) {
		parameter(_myWindowDimensionParameter, theWidth, theHeight);
	}
	
	public void color(final float theRed, final float theGreen, final float theBlue) {
		parameter(_myColorParameter, theRed, theGreen, theBlue, 1f);
	}
	
	public void radius(final float theRadius) {
		parameter(_myRadiusParameter, theRadius);
	}
	
	public void baseTexture(final CCTexture2D theBaseTexture) {
		texture(_myBaseTextureParameter, theBaseTexture.id());
	}
}

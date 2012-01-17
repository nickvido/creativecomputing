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
package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christian riekoff
 *
 */
public class CCGPUPathFollow extends CCGPUTextureForceField{
	
	private CCShaderTexture _myPathForceFieldTexture;
	
	private CCCGShader _myContourShader;
	private CCTesselator _myTesselator;

	/**
	 * @param theTexture
	 * @param theTextureScale
	 * @param theTextureOffset
	 */
	public CCGPUPathFollow(CCGraphics g, CCVector2f theTextureScale, CCVector2f theTextureOffset) {
		super(null, theTextureScale, theTextureOffset);
		_myPathForceFieldTexture = new CCShaderTexture(400, 400);
		_myTexture = _myPathForceFieldTexture;
		
		_myContourShader = new CCCGShader(
			CCIOUtil.classPath(this,"shader/contour.vp"), 
			CCIOUtil.classPath(this,"shader/contour.fp")
		);
		_myContourShader.load();
		
		_myTesselator = new CCTesselator();
	}

}

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
package cc.creativecomputing.graphics.shader.util;

import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCGPUUtil;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCGPUTextureNoise {

private static CCGPUTextureNoise noise;
	
	public static void attachFragmentNoise(final CCCGShader theShader){
	    CGparameter mNoiseTextureParam = theShader.fragmentParameter("noiseTexture");
	    
	    if(noise == null) noise = new CCGPUTextureNoise();
	    
	    theShader.texture(mNoiseTextureParam, noise._myNoiseTexture.id());
	}
	
	public static void attachVertexNoise(final CCCGShader theShader){
	    CGparameter myNoiseTextureParam = theShader.vertexParameter("noiseTexture");
	    
	    if(noise == null) noise = new CCGPUTextureNoise();
	    
	    theShader.texture(myNoiseTextureParam, noise._myNoiseTexture.id());
	}
	
	private CCTexture2D _myNoiseTexture;
	
	private CCGPUTextureNoise() {
		_myNoiseTexture = new CCTexture2D(CCTextureIO.newTextureData(CCGPUUtil.shaderPath("util/noise.png")));
		_myNoiseTexture.wrap(CCTextureWrap.REPEAT);
		_myNoiseTexture.textureFilter(CCTextureFilter.LINEAR);
		
	}
}

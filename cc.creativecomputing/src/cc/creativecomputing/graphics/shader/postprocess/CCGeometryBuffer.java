/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.graphics.shader.postprocess;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCGeometryBuffer {

	public static final int POSITIONS = 0;
	public static final int NORMALS = 1;
	public static final int COLORS = 2;
	
	protected CCRenderTexture _myRenderTexture;
	protected CCGLSLShader _myShader;
	
	private CCGraphics _myGraphics;
	
	public CCGeometryBuffer(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(3);
		myAttributes.enableDepthBuffer(true);
		myAttributes.internalFormat(CCPixelInternalFormat.RGBA32F);
		myAttributes.filter(CCTextureFilter.NEAREST);
		myAttributes.wrap(CCTextureWrap.CLAMP);
		
		_myRenderTexture = new CCRenderTexture(g, myAttributes, theWidth, theHeight);
		
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "geometrybuffer_vertex.glsl"),
			CCIOUtil.classPath(this, "geometrybuffer_fragment.glsl")
		);
		_myShader.load();
	}
	
	public void beginDraw() {
		_myRenderTexture.beginDraw();
		_myShader.start();
		_myShader.uniform1f( "near", _myGraphics.camera().near());
		_myShader.uniform1f( "far", _myGraphics.camera().far() );
	}
	
	public void endDraw() {
		_myShader.end();
		_myRenderTexture.endDraw();
	}

	public CCRenderTexture data() {
		return _myRenderTexture;
	}
}

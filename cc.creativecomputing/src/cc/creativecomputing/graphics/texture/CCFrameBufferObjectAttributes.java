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
package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;

/**
 * @author christianriekoff
 * 
 */
public class CCFrameBufferObjectAttributes extends CCTextureAttributes{
	protected CCPixelInternalFormat _myDepthInternalFormat;
	
	protected int _myNumberOfSamples;
	protected int _myCoverageSamples;
	
	protected boolean _myDepthBuffer;
	protected boolean _myStencilBuffer;
	
	protected int _myNumberOfColorBuffers;

	/**
	 * Default constructor, sets the target to \c GL_TEXTURE_2D with an 8-bit color+alpha, 
	 * a 24-bit depth texture, and no multisampling or mipmapping
	 */
	public CCFrameBufferObjectAttributes(final int theNumberOfAttachements) {
		super();
		_myDepthInternalFormat = CCPixelInternalFormat.DEPTH_COMPONENT24;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA8;
		_myDepthInternalFormat = CCPixelInternalFormat.DEPTH_COMPONENT24;
		
		_myNumberOfSamples = 0;
		_myCoverageSamples = 0;
		_myNumberOfColorBuffers = theNumberOfAttachements;
		_myDepthBuffer = true;
		_myStencilBuffer = false;
		
		_myGenerateMipmaps = false;
		_myTextureWrapS = CCTextureWrap.CLAMP_TO_EDGE;
		_myTextureWrapT = CCTextureWrap.CLAMP_TO_EDGE;
		_myFilter = CCTextureFilter.LINEAR;
	}
	
	public CCFrameBufferObjectAttributes() {
		this(1);
	}

	/**
	 * Activates the given number of color buffers for the FBO.
	 * @param theNumberOfColorBuffers
	 */
	public void numberOfColorBuffers(final int theNumberOfColorBuffers) {
		_myNumberOfColorBuffers = theNumberOfColorBuffers;
	}

	/**
	 * Returns whether the FBO contains color buffers
	 * @return
	 */
	public int numberOfColorBuffers() {
		return _myNumberOfColorBuffers;
	}

	/**
	 * Enables or disables the creation of a depth buffer for the FBO.
	 * @param depthBuffer
	 */
	public void enableDepthBuffer(boolean depthBuffer) {
		_myDepthBuffer = depthBuffer;
	}

	/**
	 * Returns whether the FBO contains a depth buffer
	 * @return
	 */
	public boolean hasDepthBuffer() {
		return _myDepthBuffer;
	}

	/**
	 * Sets the GL internal format for the depth buffer. Defaults to {@link CCPixelInternalFormat#DEPTH_COMPONENT24}. 
	 * Common options also include {@link CCPixelInternalFormat#DEPTH_COMPONENT16} and {@link CCPixelInternalFormat#DEPTH_COMPONENT32}
	 * @param theDepthInternalFormat
	 */
	public void depthInternalFormat(CCPixelInternalFormat theDepthInternalFormat) {
		_myDepthInternalFormat = theDepthInternalFormat;
	}

	/**
	 * Returns the GL internal format for the depth buffer. Defaults to {@link CCPixelInternalFormat#DEPTH_COMPONENT24}
	 * @return
	 */
	public CCPixelInternalFormat depthInternalFormat() {
		return _myDepthInternalFormat;
	}

	/**
	 * Sets the number of samples used in MSAA-style antialiasing. 
	 * Defaults to none, disabling multisampling. Note that not all implementations support multisampling.
	 * @param theNumberOfSamples
	 */
	public void samples(int theNumberOfSamples) {
		_myNumberOfSamples = theNumberOfSamples;
	}

	/**
	 * Returns the number of samples used in MSAA-style antialiasing. Defaults to none, disabling multisampling.
	 * @return
	 */
	public int samples() {
		return _myNumberOfSamples;
	}

	/**
	 * Sets the number of coverage samples used in CSAA-style antialiasing. 
	 * Defaults to none. Note that not all implementations support CSAA, and is 
	 * currenlty Windows-only Nvidia.
	 * @param theCoverageSamples
	 */
	public void coverageSamples(int theCoverageSamples) {
		_myCoverageSamples = theCoverageSamples;
	}

	/**
	 * Returns the number of coverage samples used in CSAA-style antialiasing. Defaults to none.
	 * @return
	 */
	public int coverageSamples() {
		return _myCoverageSamples;
	}

}

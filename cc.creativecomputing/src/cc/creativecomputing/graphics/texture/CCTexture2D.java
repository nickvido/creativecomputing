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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCBufferObject;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageFrequency;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageTYPE;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCBufferObject.CCBufferTarget;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;


/**
 * This class represents a 2d texture. This is probably the
 * most common kind of texture used. You can create textures
 * by loading all kind of images as texture data and than pass the content
 * to a texture object. 
 * @author christian riekoff
 *
 */
public class CCTexture2D extends CCTexture{

	/**
	 * Creates a new 2d texture.
	 */
	public CCTexture2D() {
		super(CCTextureTarget.TEXTURE_2D, new CCTextureAttributes());
	}
	
	public CCTexture2D(CCTextureTarget theTarget) {
		super(theTarget, new CCTextureAttributes());
	}

	/**
	 * Creates a new 2D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public CCTexture2D(final CCTextureAttributes theTextureAttributes) {
		super(CCTextureTarget.TEXTURE_2D, theTextureAttributes);
	}

	/**
	 * Creates a new 2D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public CCTexture2D(final CCTextureAttributes theTextureAttributes, int theWidth, int theHeight) {
		super(CCTextureTarget.TEXTURE_2D, theTextureAttributes);
		allocateData(theWidth, theHeight);
	}
	
	public CCTexture2D(CCTextureTarget theTarget, final CCTextureAttributes theTextureAttributes) {
		super(theTarget, theTextureAttributes);
	}
	
	/**
	 * This is used internally for texture sequences and multitexturing
	 * @param theTarget
	 * @param theGenerateMipmaps
	 */
	protected CCTexture2D(CCTextureTarget theTarget, final CCTextureAttributes theTextureAttributes, final int theNumberOfTextures) {
		super(theTarget, theTextureAttributes, theNumberOfTextures);
	}
	
	/**
	 * Creates a new 2d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theTextureData
	 */
	public CCTexture2D(final CCTextureData theTextureData) {
		this();
		data(theTextureData);
	}
	
	public CCTexture2D(final CCTextureData theTextureData, CCTextureTarget theTarget) {
		this(theTarget);
		data(theTextureData);
	}
	
	/**
	 * Creates a new 2d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theTextureData
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public CCTexture2D(final CCTextureData theTextureData, final CCTextureAttributes theTextureAttributes) {
		this(theTextureAttributes);
		data(theTextureData);
	}
	
	public CCTexture2D(final int theWidth, final int theHeight) {
		this();
		allocateData(theWidth, theHeight);
	}
	
	public CCTexture2D(final int theWidth, final int theHeight, CCTextureTarget theTarget) {
		this(theTarget);
		allocateData(theWidth, theHeight);
	}
	
	public CCTexture2D(final CCTextureTarget theTarget, CCTextureAttributes theAttributes, int theNumberOfTextures, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theNumberOfTextures);
		allocateData(theWidth, theHeight);
	}
	
	private void allocateData(final int theWidth, final int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		for(int i = 0; i < _myTextureIDs.length;i++) {
		
		bind(i);
		_myStorageModes.unpackStorage();
		GL gl = CCGraphics.currentGL();
		gl.glTexImage2D(
			_myTarget.glID, 0, _myInternalFormat.glID, 
			_myWidth, _myHeight, 0, 
			_myFormat.glID, 
			_myPixelType.glID, 
			null
		); 
		_myStorageModes.defaultUnpackStorage();
		}
	}
	
	public void dataImplementation(final CCTextureData theData) {
		if(theData.isDataCompressed()) {
			CCGraphics.currentGL().glCompressedTexImage2D(
				_myTarget.glID, 0, theData.internalFormat().glID, 
				theData.width(), theData.height(), 0, 
				theData.buffer().capacity(), theData.buffer()
			);
		}else {
			try {
				CCGraphics.currentGL().glTexImage2D(
					_myTarget.glID, 0, theData.internalFormat().glID, 
					theData.width(), theData.height(), 0, 
					theData.pixelFormat().glID, 
					theData.pixelType().glID, 
					theData.buffer()
				);
			}catch(Exception e) {
				CCLog.info(_myTarget);
				CCLog.info(theData.internalFormat());
				CCLog.info(theData.pixelFormat());
				CCLog.info(theData.pixelType());
				CCLog.info(theData.width());
				CCLog.info(theData.height());
			}
		}
	}
	
	/**
	 * Replaces the content of the texture with pixels from the frame buffer. You can use this method
	 * to copy pixels from the frame buffer to a texture.
	 * @param g reference to the graphics object to copy the pixels from the frame buffer
	 * @param theDestX target x position for the copied data
	 * @param theDestY target y position for the copied data
	 * @param theSrcX source x position o the copied data
	 * @param theSrcY source y position o the copied data
	 * @param theWidth the with of the region to copy
	 * @param theHeight the height of the region to copy
	 */
	public void updateData(
		final CCGraphics g, 
		int theDestX, int theDestY,
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight
	) {
		_myPixelMap = null;
		
		theSrcX = CCMath.constrain(theSrcX, 0, g.width);
		theSrcY = CCMath.constrain(theSrcY, 0, g.height);
		
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theDestY = CCMath.constrain(theDestY, 0, _myHeight);
		
		theWidth = CCMath.min(theWidth, g.width - theSrcX, _myWidth - theDestX);
		theWidth = CCMath.min(theWidth, g.width - theSrcX, _myWidth - theDestX);
		
		bind();
		g.gl.glCopyTexSubImage2D(
			_myTarget.glID, 0, 
			theDestX, theDestY,
			theSrcX, theSrcY, 
			theWidth, theHeight
		);

		if(_myGenerateMipmaps)g.gl.glGenerateMipmap(_myTarget.glID);
	}
	
	private boolean _myUsePBO = false;
	
	/**
	 * If this is set true calls to update data will use a pixel buffer object
	 * to get direct access to the pixel buffer. This should give better performance
	 * and allow asynchronous texture updates. But needs far more testing and development. 
	 * @param theUsePBO
	 */
	public void usePBO(boolean theUsePBO) {
		_myUsePBO = theUsePBO;
	}
	
	public boolean usePBO() {
		return _myUsePBO;
	}
	
	private CCBufferObject _myBufferObject;
	
	public ByteBuffer buffer() {
		int mySize = _myPixelType.bytesPerChannel * _myFormat.numberOfChannels * _myWidth * _myHeight;
		if(_myBufferObject == null) {
			_myBufferObject = new CCBufferObject();
			_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
	        _myBufferObject.bufferData(mySize, null, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
		}
		_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
		return _myBufferObject.mapBuffer();
	}
	
	private void updateData(
		CCTextureData theData, 
		int theDestX, int theDestY, 
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight,
		boolean theDoResize
	) {
		if(theDoResize) {
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theDestY = CCMath.constrain(theDestY, 0, _myHeight);
		
		theSrcX = CCMath.constrain(theSrcX, 0, theData.width());
		theSrcY = CCMath.constrain(theSrcY, 0, theData.height());
		
		theWidth = CCMath.min(theWidth, theData.width() - theSrcX, _myWidth - theDestX);
		theHeight = CCMath.min(theHeight, theData.height() - theSrcY, _myHeight - theDestY);

		theData.pixelStorageModes().unpackStorage();

		GL2 gl = CCGraphics.currentGL();
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, theSrcX);
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_ROWS, theSrcY);

        // update the texture image:
        gl.glEnable(_myTarget.glID);
		gl.glBindTexture(_myTarget.glID, _myTextureIDs[_myTextureID]);
		
		int mySize = _myPixelType.bytesPerChannel * _myFormat.numberOfChannels * _myWidth * _myHeight;
		
		if(_myUsePBO) {
			if(_myBufferObject == null) {
				_myBufferObject = new CCBufferObject();
				_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
		        _myBufferObject.bufferData(mySize, null, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
			}
			_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);

	        // map the buffer object into client's memory
	        // Note that glMapBufferARB() causes sync issue.
	        // If GPU is working with this buffer, glMapBufferARB() will wait(stall)
	        // for GPU to finish its job. To avoid waiting (stall), you can call
	        // first glBufferDataARB() with NULL pointer before glMapBufferARB().
	        // If you do that, the previous data in PBO will be discarded and
	        // glMapBufferARB() returns a new allocated pointer immediately
	        // even if GPU is still working with the previous data.
	        _myBufferObject.bufferData(mySize, null, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
	        
	        ByteBuffer ptr = _myBufferObject.mapBuffer();
	        if(ptr != null)
	        {
	            // update data directly on the mapped buffer
	            if(theData.buffer() instanceof FloatBuffer){
	            	FloatBuffer myFloatBuffer = (FloatBuffer)theData.buffer();
	            	myFloatBuffer.rewind();
	            	ptr.asFloatBuffer().put(myFloatBuffer);
	            	ptr.rewind();
	            }
	            if(theData.buffer() instanceof ByteBuffer){
//	            	ByteBuffer myByteBuffer = (ByteBuffer)theData.buffer();
//	            	myByteBuffer.rewind();
//	            	ptr.put(myByteBuffer);
//	            	ptr.rewind();
	            	while(ptr.hasRemaining()) {
	            		ptr.put((byte)CCMath.random(255));
	            	}
	            }
	            _myBufferObject.unmapBuffer();
	        } 
	        gl.glTexSubImage2D(
		    	_myTarget.glID, 0, 
		    	theDestX, theDestY, 
		    	theWidth, theHeight, 
		    	theData.pixelFormat().glID, theData.pixelType().glID, 0
		    );

	        _myBufferObject.unbind();
		}else {
			gl.glTexSubImage2D(
				_myTarget.glID, 0, 
				theDestX, theDestY, 
				theData.width(), theData.height(), 
				theData.pixelFormat().glID, theData.pixelType().glID, theData.buffer()
			);
		}
		
		theData.pixelStorageModes().defaultUnpackStorage();
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture. This method makes it also possible to just copy
	 * a region of the texture data into the texture
	 * 
	 * @param theData the source data to copy to the texture
	 * 
	 * @param theDestX the target x position for the copied data
	 * @param theDestY the target y position for the copied data
	 * 
	 * @param theSrcX the source x position of the copied data
	 * @param theSrcY the source y position of the copied data
	 *  
	 * @param theWidth the width of the copied region
	 * @param theHeight the height of the copied region
	 */
	public void updateData(
		CCTextureData theData, 
		int theDestX, int theDestY, 
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight
	) {
		updateData(theData, theDestX, theDestY, theSrcX, theSrcY, theWidth, theHeight, false);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture. This method makes it also possible to just copy
	 * a region of the texture data into the texture dependent on the given
	 * width and height.
	 * 
	 * @param theData the source data to copy to the texture
	 * 
	 * @param theDestX the target x position for the copied data
	 * @param theDestY the target y position for the copied data
	 *  
	 * @param theWidth the width of the copied region
	 * @param theHeight the height of the copied region
	 */
	public void updateData(
		CCTextureData theData, 
		int theDestX, int theDestY, 
		int theWidth, int theHeight
	) {
		updateData(theData, theDestX, theDestY, 0, 0, theWidth, theHeight);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture.
	 * @param theData the new texture data 
	 * @param theDestX left side of the texture where to place the data
	 * @param theDestY top side of the texture where to place the data
	 */
	public void updateData(
		CCTextureData theData, 
		int theDestX, int theDestY
	) {
		updateData(theData, theDestX, theDestY, theData.width(), theData.height());
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture.
	 * @param theData the new texture data 
	 */
	public void updateData(CCTextureData theData) {
		if(theData.width() != _myWidth || theData.height() != _myHeight) {
			data(theData);
		}else {
			updateData(theData,0,0,0,0,theData.width(), theData.height(), true);
		}
	}
	
	public CCTextureData data() {
		GL2 gl = CCGraphics.currentGL();
		
		if(_myIsCompressed) {
			
			int size = parameter(GL2.GL_TEXTURE_COMPRESSED_IMAGE_SIZE);
			ByteBuffer res = ByteBuffer.allocate(size);
			gl.glGetCompressedTexImage(_myTarget.glID, 0, res);
			
			return new CCTextureData(
				_myWidth, _myHeight, 0, 
				_myInternalFormat, _myFormat, 
				CCPixelType.UNSIGNED_BYTE, 
				_myIsCompressed, _myMustFlipVertically, 
				res, 
				null
			);
		}else {
			CCPixelFormat fetchedFormat = null;
			int myBytesPerPixel;
			switch (_myInternalFormat) {
			case RGB:
			case BGR:
			case RGB8:
				myBytesPerPixel = 3;
				fetchedFormat = CCPixelFormat.RGB;
				break;
			case RGBA:
			case BGRA:
			case ABGR:
			case RGBA8:
				myBytesPerPixel = 4;
				fetchedFormat = CCPixelFormat.RGBA;
				break;
			case DEPTH_COMPONENT24:
				fetchedFormat = CCPixelFormat.DEPTH_COMPONENT;
				myBytesPerPixel = 3;
				break;
			default:
				throw new CCTextureException("The data method does not support the internal format of the texture:" + _myInternalFormat);
			}
			
			ByteBuffer res = ByteBuffer.allocate((_myWidth + (2 * border())) * (_myHeight + (2 * border())) * myBytesPerPixel);
			
			_myStorageModes.packStorage();
			gl.glGetTexImage(_myTarget.glID, 0, fetchedFormat.glID, GL.GL_UNSIGNED_BYTE, res);
			_myStorageModes.defaultPackStorage();
			
			return new CCTextureData(
				_myWidth, _myHeight, 0, 
				_myInternalFormat, _myFormat, 
				CCPixelType.UNSIGNED_BYTE, 
				_myIsCompressed, _myMustFlipVertically, 
				res, 
				null
			);
		}
	}
	
	/**
	 * Sets the pixel at the given index to the given color.
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top side
	 * @param theColor the new color of the pixel
	 */
	public void setPixel(final int theX, final int theY, final CCColor theColor) {
		GL gl = CCGraphics.currentGL();
		
		FloatBuffer myBuffer = FloatBuffer.allocate(4);
		myBuffer.put(theColor.red());
		myBuffer.put(theColor.green());
		myBuffer.put(theColor.blue());
		myBuffer.put(theColor.alpha());
		myBuffer.rewind();
		
		gl.glTexSubImage2D(
			_myTarget.glID, 0, 
			theX, theY, 1, 1,
			CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer
		);
	}
	
	private CCPixelMap _myPixelMap;
	
	/**
	 * Returns the color for the pixel at the given index
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top
	 * @return the color of the pixel
	 */
	public CCColor getPixel(final int theX, final int theY) {
		if(_myPixelMap == null) {
			FloatBuffer myPixelData = FloatBuffer.allocate(4 * _myWidth * _myHeight);
			GL2 gl = CCGraphics.currentGL();
			gl.glGetTexImage(_myTarget.glID, 0, CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myPixelData);
			myPixelData.rewind();
			
			_myPixelMap = new CCPixelMap(myPixelData, _myWidth, _myHeight, _myMustFlipVertically);
		}
		
		return _myPixelMap.getPixel(theX, theY);
	}
	
	/**
	 * Returns all pixels of the texture as pixelmap
	 * @return
	 */
	public CCPixelMap getPixels() {
		FloatBuffer myBuffer = FloatBuffer.allocate(4 * _myWidth * _myHeight);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetTexImage(_myTarget.glID, 0, CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new CCPixelMap(myBuffer, _myWidth, _myHeight, _myMustFlipVertically);
	}
	

}

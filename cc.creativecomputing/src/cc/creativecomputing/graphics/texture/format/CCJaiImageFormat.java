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
package cc.creativecomputing.graphics.texture.format;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTextureUtil;
import cc.creativecomputing.graphics.texture.CCTextureIO.CCFileFormat;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;

/**
 * @author christianriekoff
 *
 */
public class CCJaiImageFormat implements CCTextureFormat{
	
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		return createTextureData(CCIOUtil.openStream(theFile), theInternalFormat, thePixelFormat, theFileSuffix);
	}
	
	public CCTextureData createTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		return createTextureData(CCIOUtil.openStream(theFile), theInternalFormat, thePixelFormat, theFileSuffix);
	}
	
	private static Buffer createNIOBufferFromImage(final RenderedImage theImage) {
		//
		// Note: Grabbing the DataBuffer will defeat Java2D's image
		// management mechanism (as of JDK 5/6, at least).  This shouldn't
		// be a problem for most JOGL apps, but those that try to upload
		// the image into an OpenGL texture and then use the same image in
		// Java2D rendering might find the 2D rendering is not as fast as
		// it could be.
		//
		
		DataBuffer data = theImage.getData().getDataBuffer();
		if (data instanceof DataBufferByte) {
			return Buffers.newDirectByteBuffer(((DataBufferByte) data).getData());
		} else if (data instanceof DataBufferDouble) {
			throw new CCTextureException("DataBufferDouble rasters not supported by OpenGL");
		} else if (data instanceof DataBufferFloat) {
			return Buffers.newDirectFloatBuffer(((DataBufferFloat) data).getData());
		} else if (data instanceof DataBufferInt) {
			return Buffers.newDirectIntBuffer(((DataBufferInt) data).getData());
		} else if (data instanceof DataBufferShort) {
			return Buffers.newDirectShortBuffer(((DataBufferShort) data).getData());
		} else if (data instanceof DataBufferUShort) {
			return Buffers.newDirectShortBuffer(((DataBufferUShort) data).getData());
		} else {
			throw new RuntimeException("Unexpected DataBuffer type?");
		}
	}

	public CCTextureData createTextureData(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if(!(theFileSuffix.equals("tif") || theFileSuffix.equals("tiff")))return null;
		try {
			ImageDecoder myDecoder = ImageCodec.createImageDecoder("tiff", theStream, null);
			if (myDecoder == null) {
				return null;
			}
			
			RenderedImage myImage = myDecoder.decodeAsRenderedImage();
			ColorModel myColorModel = myImage.getColorModel();
			SampleModel mySampleMode = myImage.getSampleModel();
			
			CCTextureData myResult = new CCTextureData(myImage.getWidth(), myImage.getHeight());
			switch(mySampleMode.getDataType()) {
			case DataBuffer.TYPE_BYTE:
				myResult.pixelType(CCPixelType.BYTE);
				break;
			case DataBuffer.TYPE_SHORT:
				myResult.pixelType(CCPixelType.SHORT);
				break;
			case DataBuffer.TYPE_USHORT:
				myResult.pixelType(CCPixelType.UNSIGNED_SHORT);
				break;
			case DataBuffer.TYPE_INT:
				myResult.pixelType(CCPixelType.UNSIGNED_INT);
				break;
			case DataBuffer.TYPE_FLOAT:
				myResult.pixelType(CCPixelType.FLOAT);
				break;
			default:
				return null;
			}
			
			switch(myColorModel.getColorSpace().getType()) {
			case ColorSpace.TYPE_GRAY:
				myResult.pixelFormat(CCPixelFormat.LUMINANCE);
				myResult.internalFormat(CCPixelInternalFormat.LUMINANCE);
				break;
			default:
				return null;
			}
			
			myResult.buffer(createNIOBufferFromImage(myImage));
			
			return myResult;
		} catch (Exception e) {
			return null;
		}
	}

	public CCTextureData createTextureData(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		
		try {
			final InputStream myStream = theUrl.openStream();
			try {
				return createTextureData(myStream, theInternalFormat, thePixelFormat, theFileSuffix);
			} finally {
				myStream.close();
			}
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
	
	public boolean write(final File theFile, final CCTextureData theData) throws CCTextureException {
		
		
		CCPixelFormat pixelFormat = theData.pixelFormat();
		CCPixelType pixelType = theData.pixelType();
		
		int myBits;
		int myDataType;
		
		switch(theData.pixelType()) {
		case FLOAT:
			myBits = 32;
			myDataType = DataBuffer.TYPE_FLOAT;
			break;
		case INT:
		case UNSIGNED_INT:
			myBits = 32;
			myDataType = DataBuffer.TYPE_INT;
			break;
		case SHORT:
		case UNSIGNED_SHORT:
			myBits = 16;
			myDataType = DataBuffer.TYPE_SHORT;
			break;
		case BYTE:
		case UNSIGNED_BYTE:
			myBits = 8;
			myDataType = DataBuffer.TYPE_BYTE;
			break;
		default:
			return false;
		}

		int[] myChannelBits;
		ColorSpace myColorSpace;
		
		switch(theData.pixelFormat()) {
		case DEPTH_COMPONENT:
		case LUMINANCE:
		case LUMINANCE_ALPHA:
			myColorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			myChannelBits = new int[] {myBits};
			break;
		default:
			myColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			myChannelBits = new int[] {myBits, myBits, myBits};
			break;
		}
		
		boolean myAlpha = false;
		int myTransparency = Transparency.OPAQUE;
		switch(theData.pixelFormat()) {
		case LUMINANCE_ALPHA:
		case ABGR:
		case BGRA:
		case RGBA:
			myAlpha = true;
			myTransparency = Transparency.TRANSLUCENT;
			break;
		}
		
		
	     ColorModel myColorModel = new ComponentColorModel(
	    		myColorSpace,
	    		myChannelBits, 
	    		myAlpha, 
	    		myAlpha,
	    		myTransparency,
	            myDataType
	        );
	     
	     WritableRaster myRaster = myColorModel.createCompatibleWritableRaster(theData.width(), theData.height());
	     DataBuffer myBuffer = myRaster.getDataBuffer();
	     
	     if(myBuffer.getDataType() == DataBuffer.TYPE_FLOAT) {
	    	 DataBufferFloat myFloatBuffer = (DataBufferFloat)myBuffer;
	    	 myFloatBuffer.getData();
	    	 
	    	 FloatBuffer buf = (FloatBuffer) theData.buffer();
				if (buf == null) {
					buf = (FloatBuffer) theData.mipmapData()[0];
				}
				buf.rewind();
				buf.get(myFloatBuffer.getData());
				buf.rewind();
	     }
			
		
		BufferedImage myImage = new BufferedImage(myColorModel, myRaster, false, new Hashtable<Object, Object>());
		CCIOUtil.createPath(theFile.getAbsolutePath());
		try {
			ImageEncoder myEncoder = ImageCodec.createImageEncoder("tiff", new FileOutputStream(theFile), null);
			myEncoder.encode(myImage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

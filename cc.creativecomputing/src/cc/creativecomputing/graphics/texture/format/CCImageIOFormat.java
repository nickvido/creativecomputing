package cc.creativecomputing.graphics.texture.format;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.util.awt.ImageUtil;

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

public class CCImageIOFormat implements CCTextureFormat {
	
	private CCTextureData createTextureData(
		BufferedImage theImage, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat,
		final String theFileSuffix	
	) {
		if (theImage == null) {
			return null;
		}
		
		if(theImage.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
			theImage = CCTextureUtil.convert(theImage, BufferedImage.TYPE_4BYTE_ABGR);
		}
		
		if (CCTextureIO.DEBUG) {
			CCLog.info("TextureIO.newTextureData(): BufferedImage type for stream = " + theImage.getType());
		}
		
		CCTextureData myTextureData = new CCTextureData();
		if (theInternalFormat == null) {
			myTextureData.internalFormat(theImage.getColorModel().hasAlpha() ? CCPixelInternalFormat.RGBA : CCPixelInternalFormat.RGB);
		} else {
			myTextureData.internalFormat(theInternalFormat);
		}
		myTextureData.pixelFormat(thePixelFormat);
		
		return CCTextureUtil.toTextureData(theImage, myTextureData);
	}
	
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			return createTextureData(ImageIO.read(CCIOUtil.openStream(theFile)), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
	
	public CCTextureData createTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			return createTextureData(ImageIO.read(theFile), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}

	public CCTextureData createTextureData(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){

		try {
			return createTextureData(ImageIO.read(theStream), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
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
		
		if (
			(pixelFormat == CCPixelFormat.RGB || pixelFormat == CCPixelFormat.RGBA) && 
			(pixelType == CCPixelType.BYTE || pixelType == CCPixelType.UNSIGNED_BYTE)
		) {
			
			// Convert TextureData to appropriate BufferedImage
			BufferedImage image = new BufferedImage(
				theData.width(), 
				theData.height(), 
				(pixelFormat == CCPixelFormat.RGB) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR
			);
			
			byte[] imageData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			ByteBuffer buf = (ByteBuffer) theData.buffer();
			if (buf == null) {
				buf = (ByteBuffer) theData.mipmapData()[0];
			}
			buf.rewind();
			buf.get(imageData);
			buf.rewind();

			// Swizzle image components to be correct
			if (pixelFormat == CCPixelFormat.RGB) {
				for (int i = 0; i < imageData.length; i += 3) {
					byte red = imageData[i + 0];
					byte blue = imageData[i + 2];
					imageData[i + 0] = blue;
					imageData[i + 2] = red;
				}
			} else {
				for (int i = 0; i < imageData.length; i += 4) {
					byte red = imageData[i + 0];
					byte green = imageData[i + 1];
					byte blue = imageData[i + 2];
					byte alpha = imageData[i + 3];
					imageData[i + 0] = alpha;
					imageData[i + 1] = blue;
					imageData[i + 2] = green;
					imageData[i + 3] = red;
				}
			}

			// Flip image vertically for the user's convenience
			ImageUtil.flipImageVertically(image);

			// Happened to notice that writing RGBA images to JPEGS is broken
			if (CCFileFormat.JPG.fileExtension.equals(CCIOUtil.fileExtension(theFile)) && image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
				BufferedImage tmpImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				Graphics g = tmpImage.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				image = tmpImage;
			}
			try {
				return ImageIO.write(image, CCIOUtil.fileExtension(theFile), theFile);
			} catch (IOException e) {
				throw new CCTextureException(e);
			}
		}

		throw new CCTextureException("ImageIO writer doesn't support this pixel format / type (only GL_RGB/A + bytes)");
	}
}

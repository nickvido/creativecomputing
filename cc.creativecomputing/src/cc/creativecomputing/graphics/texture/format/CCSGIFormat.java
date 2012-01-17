package cc.creativecomputing.graphics.texture.format;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.graphics.texture.CCTextureIO.CCFileFormat;
import cc.creativecomputing.io.CCIOUtil;

public class CCSGIFormat extends CCStreamBasedTextureFormat {
	
	public CCTextureData createTextureData(
		final InputStream theStream, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	) throws CCTextureException {
		
		if (
			CCFileFormat.SGI.fileExtension.equals(theFileSuffix) || 
			CCFileFormat.SGI_RGB.fileExtension.equals(theFileSuffix) || 
			CCSGIImage.isSGIImage(theStream)
		) {
			CCSGIImage image = CCSGIImage.read(theStream);
			if (thePixelFormat == null) {
				thePixelFormat = image.getFormat();
			}
			if (theInternalFormat == null) {
				theInternalFormat = image.getInternalFormat();
			}
			return new CCTextureData(
				image.width(), image.getHeight(), 0,
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				false, false, 
				ByteBuffer.wrap(image.getData()), null
			);
		}

		return null;
	}
	
	public boolean write(final File theFile, final CCTextureData theData) throws CCTextureException {
		String fileSuffix = CCIOUtil.fileExtension(theFile);
		if (
			CCFileFormat.SGI.fileExtension.equals(fileSuffix) || 
			CCFileFormat.SGI_RGB.fileExtension.equals(fileSuffix)
		) {
			// See whether the SGI writer can handle this TextureData
			CCPixelFormat pixelFormat = theData.pixelFormat();
			CCPixelType pixelType = theData.pixelType();
			if (
				(pixelFormat == CCPixelFormat.RGB || pixelFormat == CCPixelFormat.RGBA) && 
				(pixelType == CCPixelType.BYTE || pixelType == CCPixelType.UNSIGNED_BYTE)
			) {
				ByteBuffer buf = ((theData.buffer() != null) ? (ByteBuffer) theData.buffer() : (ByteBuffer) theData.mipmapData()[0]);
				byte[] bytes;
				if (buf.hasArray()) {
					bytes = buf.array();
				} else {
					buf.rewind();
					bytes = new byte[buf.remaining()];
					buf.get(bytes);
					buf.rewind();
				}

				CCSGIImage image = CCSGIImage.createFromData(
					theData.width(), theData.height(), 
					(pixelFormat == CCPixelFormat.RGBA),
					bytes
				);
				image.write(theFile, false);
				return true;
			}

			throw new CCTextureException("SGI writer doesn't support this pixel format / type (only RGB/A + bytes)");
		}

		return false;
	}
}

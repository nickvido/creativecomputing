package cc.creativecomputing.graphics.texture.format;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.io.CCIOUtil;

public abstract class CCStreamBasedTextureFormat implements CCTextureFormat {
	
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		// The SGIImage and TGAImage implementations use InputStreams
		// anyway so there isn't much point in having a separate code
		// path for files
		return createTextureData(CCIOUtil.openStream(theFile), theInternalFormat, thePixelFormat, ((theFileSuffix != null) ? theFileSuffix : CCIOUtil.fileExtension(theFile)));
		
	}
	
	public CCTextureData createTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	) throws CCTextureException {
		try {
			InputStream myInputStream = new BufferedInputStream(new FileInputStream(theFile));
			// The SGIImage and TGAImage implementations use InputStreams
			// anyway so there isn't much point in having a separate code
			// path for files
			return createTextureData(myInputStream, theInternalFormat, thePixelFormat, ((theFileSuffix != null) ? theFileSuffix : CCIOUtil.fileExtension(theFile)));
		}catch (IOException myE) {
			throw new CCTextureException(myE);
		}
	}

	public CCTextureData createTextureData(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	) throws CCTextureException {
		try {
			InputStream myInputStream = new BufferedInputStream(theUrl.openStream());
			return createTextureData(myInputStream, theInternalFormat, thePixelFormat, theFileSuffix);
		}catch (IOException myE) {
			throw new CCTextureException(myE);
		}
	}
}

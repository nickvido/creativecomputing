package cc.creativecomputing.graphics.texture.format;

import java.nio.ByteBuffer;

/**
 * Simple class describing images and data; does not encapsulate image format information. User is responsible for
 * transmitting that information in another way.
 */

public class CCTextureInfo {
	private ByteBuffer data;
	private int width;
	private int height;
	private boolean isCompressed;
	private int compressionFormat;

	public CCTextureInfo(ByteBuffer data, int width, int height, boolean compressed, int compressionFormat) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.isCompressed = compressed;
		this.compressionFormat = compressionFormat;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getData() {
		return data;
	}

	public boolean isCompressed() {
		return isCompressed;
	}

	public int getCompressionFormat() {
		if (!isCompressed())
			throw new RuntimeException("Should not call unless compressed");
		return compressionFormat;
	}
}

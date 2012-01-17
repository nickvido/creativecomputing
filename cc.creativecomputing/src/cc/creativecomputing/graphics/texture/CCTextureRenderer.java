package cc.creativecomputing.graphics.texture;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCVector2f;

public abstract class CCTextureRenderer {
	
	protected int _myAddXSize;
	protected int _myAddYSize;
	
	protected CCVector2f _myOffset;
	
	protected Object _myIsAntialiased;
	
	public CCTextureRenderer(final int theAddXSize, final int theAddYSize, final int theOffsetX, final int theOffsetY){
		_myAddXSize = theAddXSize;
		_myAddYSize = theAddYSize;
		_myOffset = new CCVector2f(theOffsetX, theOffsetY);
	}
	
	public CCRenderedTexture createTexture(final BufferedImage theImage, final boolean theIsAntialias){
		
		_myIsAntialiased = theIsAntialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
			
		BufferedImage myTargetImage = new BufferedImage(
			theImage.getWidth() + _myAddXSize, 
			theImage.getHeight() + _myAddYSize, 
			BufferedImage.TYPE_INT_ARGB
		);
		
		return new CCRenderedTexture(_myOffset,CCTextureIO.newTextureData(createTexture(theImage, myTargetImage)));
		
	}
	
	protected abstract BufferedImage createTexture(final BufferedImage theImage, final BufferedImage theTargetImage);
}

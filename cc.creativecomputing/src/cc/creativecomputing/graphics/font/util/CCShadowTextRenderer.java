package cc.creativecomputing.graphics.font.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.filter.CCGaussianBlur;
import cc.creativecomputing.math.CCMath;

public class CCShadowTextRenderer extends CCTextRenderer{
	private float _mySize;
	private float _myTransparency;
	
	private Color _myBlack;
	
	public CCShadowTextRenderer(){
		this(5);
	}
	
	public CCShadowTextRenderer(final float theSize){
		this(theSize,1f);
	}
	
	public CCShadowTextRenderer(final float theSize, final float theTransparency){
		_mySize = theSize;
		_myBlack = new Color(0,0,0,theTransparency);
	}
	
	@Override
	protected void createTexture(final CCTextData theTextData, final String theText, final Font theFont, final Object theIsAntialias){
		theTextData.width += _mySize * 2;
		theTextData.height += _mySize * 2;
		theTextData.offset.x = -_mySize;
		theTextData.offset.y = _mySize;
		
		BufferedImage myTextImage = new BufferedImage(
			CCMath.ceil(theTextData.width), 
			CCMath.ceil(theTextData.height), 
			BufferedImage.TYPE_INT_ARGB
		);
		
		Graphics2D myGraphics = (Graphics2D) myTextImage.getGraphics();
		myGraphics.setFont(theFont);
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, theIsAntialias);
		myGraphics.setBackground(TRANSPARENT);
		myGraphics.setColor(_myBlack);
		myGraphics.drawString(theText, _mySize, theTextData.fontMetrics.getAscent() + _mySize);
			
		CCGaussianBlur myBlur = new CCGaussianBlur(_mySize);
		myTextImage = myBlur.filter(myTextImage);
			
		myGraphics = (Graphics2D) myTextImage.getGraphics();
		myGraphics.setFont(theFont);
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, theIsAntialias);
		myGraphics.setBackground(TRANSPARENT);
		myGraphics.setColor(Color.WHITE);
		myGraphics.drawString(theText, _mySize, theTextData.fontMetrics.getAscent() + _mySize);
			
		theTextData.textureData = CCTextureIO.newTextureData(myTextImage);
	}
}

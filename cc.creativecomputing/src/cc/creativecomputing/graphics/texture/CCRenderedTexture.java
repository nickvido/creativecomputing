package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;


public class CCRenderedTexture extends CCPlacedTexture{
	
	private final CCVector2f _myOffset;

	public CCRenderedTexture(final CCVector2f theOffset, final CCTextureData theData){
		super();
		_myOffset = theOffset;
		
		updateData(theData);
		theData.flush();
	}
	
	public void draw(final CCGraphics g, final float theX, final float theY){
		draw(g,theX,theY,0);
	}
	
	public void draw(
		final CCGraphics g, final float theX, final float theY, final float theZ
	){
		final float myWidth = width();
		final float myHeight = height();
			
		g.texture(this);
		g.beginShape(CCGraphics.QUADS);
		g.vertex(theX + _myOffset.x,			theY + _myOffset.y ,theZ, 0, 0);
		g.vertex(theX + _myOffset.x + myWidth,theY + _myOffset.y, theZ, 1, 0);
		g.vertex(theX + _myOffset.x + myWidth,theY + _myOffset.y - myHeight, theZ, 1, 1);
		g.vertex(theX + _myOffset.x,			theY + _myOffset.y - myHeight, theZ, 0, 1);
		g.endShape();
		g.noTexture();
	}
	
	public CCVector2f offset(){
		return _myOffset;
	}
}

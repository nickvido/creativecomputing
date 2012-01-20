package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphicsCore.CCShapeMode;
import cc.creativecomputing.math.CCVector2f;

public class CCEllipse extends CCAbstractShape{
	
	protected CCVector2f _myCenter;
	protected float _myRadius;
	
	public CCEllipse(final CCVector2f theCenter, final CCColor theColor, final float theRadius){
		_myCenter = theCenter;
		_myRadius = theRadius;
		_myColor = theColor;
	}
	
	public CCEllipse(final float theCenterX, final float theCenterY, final CCColor theColor, final float theRadius){
		_myCenter = new CCVector2f(theCenterX, theCenterY);
		_myColor = theColor;
		_myRadius = theRadius;
	}

	@Override
	public void draw(CCGraphics g) {
		g.color(_myColor);
		g.pushMatrix();
		g.translate(_myCenter);
		CCShapeMode myShapeMode = g.ellipseMode();
		g.ellipseMode(CCShapeMode.CENTER);
		g.ellipse(_myCenter, _myRadius);
		g.ellipseMode(myShapeMode);
		g.popMatrix();
	}

	@Override
	public void translate(float theX, float theY) {
		_myCenter.add(theX, theY);
	}

	@Override
	public void position(float theX, float theY) {
		_myCenter.set(theX, theY);
	}

}
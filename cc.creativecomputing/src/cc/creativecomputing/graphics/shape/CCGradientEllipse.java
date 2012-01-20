package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphicsCore.CCShapeMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCGradientEllipse extends CCEllipse{
	
	private float _myInnerRadius;
	private CCColor _myGradientColor;

	public CCGradientEllipse(CCVector2f theCenter, CCColor theColor, CCColor theGradientColor, float theInnerRadius, float theRadius) {
		super(theCenter, theColor, theRadius);
		_myShapeMode = CCShapeMode.CENTER;
		_myGradientColor = theGradientColor;
		_myInnerRadius = theInnerRadius;
	}

	public CCGradientEllipse(float theCenterX, float theCenterY, CCColor theColor, CCColor theGradientColor, float theInnerRadius, float theRadius) {
		this(new CCVector2f(theCenterX, theCenterY), theColor, theGradientColor,theInnerRadius,theRadius);
	}
	
	@Override
	public void draw(CCGraphics g) {
		if(!_myIsVisible)return;
		if(_myScale == 0)return;
		

	    int accuracy = (int)(4+Math.sqrt((_myRadius +_myRadius) * _myScale)*3);
	    
	    float inc = CCMath.TWO_PI / accuracy;

	    float val = 0;

		g.color(_myColor);
	    g.beginShape(CCDrawMode.TRIANGLE_FAN);
		g.normal(0, 0, 1);
		g.vertex(_myCenter);
		for (int i = 0; i < accuracy; i++) {
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myInnerRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myInnerRadius * _myScale
			);
			val += inc;
		}
		// back to the beginning
		g.vertex(_myCenter.x + _myInnerRadius * _myScale, _myCenter.y);
		g.endShape();
		
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		g.normal(0, 0, 1);
		for (int i = 0; i < accuracy; i++) {
			g.color(_myColor);
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myInnerRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myInnerRadius * _myScale
			);
			g.color(_myGradientColor);
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myRadius * _myScale
			);
			val += inc;
		}
		// back to the beginning
		g.color(_myColor);
		g.vertex(_myCenter.x + _myInnerRadius * _myScale, _myCenter.y);
		g.color(_myGradientColor);
		g.vertex(_myCenter.x + _myRadius * _myScale, _myCenter.y);
		g.endShape();
	}
}
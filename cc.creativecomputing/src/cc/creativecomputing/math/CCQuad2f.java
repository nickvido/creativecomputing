package cc.creativecomputing.math;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;

public class CCQuad2f {
	private CCVector2f _myLeftUpper;
	private CCVector2f _myLeftBottom;
	private CCVector2f _myRightBottom;
	private CCVector2f _myRightUpper;

	public CCQuad2f(final CCVector2f theLeftUpper, final CCVector2f theLeftBottom, final CCVector2f theRightBottom, final CCVector2f theRightUpper) {
		_myLeftUpper = theLeftUpper;
		_myLeftBottom = theLeftBottom;
		_myRightBottom = theRightBottom;
		_myRightUpper = theRightUpper;
	}

	public CCVector2f gridVector(final float theX, final float theY) {
		CCVector2f myX1 = CCVecMath.blend(theX, _myLeftUpper, _myRightUpper);
		CCVector2f myX2 = CCVecMath.blend(theX, _myLeftBottom, _myRightBottom);
		return CCVecMath.blend(theY, myX1, myX2);
	}
	
	public CCVector2f leftUpper(){
		return _myLeftUpper;
	}

	public CCVector2f leftBottom() {
		return _myLeftBottom;
	}

	public void leftBottom(CCVector2f theLeftBottom) {
		_myLeftBottom = theLeftBottom;
	}

	public CCVector2f rightBottom() {
		return _myRightBottom;
	}

	public void rightBottom(CCVector2f theRightBottom) {
		_myRightBottom = theRightBottom;
	}

	public CCVector2f rightUpper() {
		return _myRightUpper;
	}

	public void rightUpper(CCVector2f theRightUpper) {
		_myRightUpper = theRightUpper;
	}
	
	public void draw(CCGraphics g){
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myLeftUpper);
		g.vertex(_myLeftBottom);
		g.vertex(_myRightBottom);
		g.vertex(_myRightUpper);
		g.endShape();
	}
}
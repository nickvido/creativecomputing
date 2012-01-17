package cc.creativecomputing.math.util;

import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCQuad {
	private CCVector3f _myLeftUpper;
	private CCVector3f _myLeftBottom;
	private CCVector3f _myRightBottom;
	private CCVector3f _myRightUpper;

	public CCQuad(final CCVector3f theLeftUpper, final CCVector3f theLeftBottom, final CCVector3f theRightBottom, final CCVector3f theRightUpper) {
		_myLeftUpper = theLeftUpper;
		_myLeftBottom = theLeftBottom;
		_myRightBottom = theRightBottom;
		_myRightUpper = theRightUpper;
	}

	public CCVector3f gridVector(final float theX, final float theY) {
		CCVector3f myX1 = CCVecMath.blend(theX, _myLeftUpper, _myRightUpper);
		CCVector3f myX2 = CCVecMath.blend(theX, _myLeftBottom, _myRightBottom);
		return CCVecMath.blend(theY, myX1, myX2);
	}
	
	public CCVector3f leftUpper(){
		return _myLeftUpper;
	}

	public CCVector3f leftBottom() {
		return _myLeftBottom;
	}

	public void leftBottom(CCVector3f theLeftBottom) {
		_myLeftBottom = theLeftBottom;
	}

	public CCVector3f rightBottom() {
		return _myRightBottom;
	}

	public void rightBottom(CCVector3f theRightBottom) {
		_myRightBottom = theRightBottom;
	}

	public CCVector3f rightUpper() {
		return _myRightUpper;
	}

	public void rightUpper(CCVector3f theRightUpper) {
		_myRightUpper = theRightUpper;
	}
}
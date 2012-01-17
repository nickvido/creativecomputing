package cc.creativecomputing.cv.filter;

import cc.creativecomputing.cv.CCPixelRaster;
import cc.creativecomputing.math.CCMath;

public class CCNormalizationFilter implements CCIRasterFilter {
	
	CCPixelRaster _myBackground = null;
	
	public void setBackgroundRaster(CCPixelRaster theBackground) {
		_myBackground = theBackground;
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBackground == null) {
			_myBackground = theRaster.clone();
		}
		
		CCPixelRaster myResult = new CCPixelRaster("", theRaster.width(), theRaster.height());
		int myLength = theRaster.width() * theRaster.height();
		for (int i = 0; i < myLength; i++) {
			float myBackgroundSubpression = CCMath.abs(theRaster.data()[i] - _myBackground.data()[i]) / 255f;
			
			myResult.data()[i] = (255f - theRaster.data()[i]) * myBackgroundSubpression;
		}
		return myResult;
	}

}

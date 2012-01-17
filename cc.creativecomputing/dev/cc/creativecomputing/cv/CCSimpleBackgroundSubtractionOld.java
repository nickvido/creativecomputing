package cc.creativecomputing.cv;

import cc.creativecomputing.cv.filter.CCIRasterFilter;

public class CCSimpleBackgroundSubtractionOld implements CCIRasterFilter {
	
	CCPixelRaster _myBackground = null;
	
	public void setBackgroundRaster(CCPixelRaster theBackground) {
		_myBackground = theBackground;
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBackground == null) {
			return theRaster.clone();
		}
		CCPixelRaster myResult = theRaster.clone();
		float[] myRasterData = theRaster.data();
		float[] myBGData = _myBackground.data();
		int myLength = theRaster.width() * theRaster.height();
		for (int i = 0; i < myLength; i++) {
			myResult.data()[i] = (1-myBGData[i]/255.0f) * (myRasterData[i] - myBGData[i]);
		}
		return myResult;
	}

}

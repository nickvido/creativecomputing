package cc.creativecomputing.cv;

import cc.creativecomputing.cv.filter.CCIRasterFilter;
import cc.creativecomputing.cv.filter.CCSimpleBackgroundSubtraction;


public class CCBackgroundSubtraction implements CCIRasterFilter {
	
	private CCPixelRaster _myBGRaster = null;
	private CCPixelRaster _mySumRaster = null;
	private CCSimpleBackgroundSubtraction _myBGFilter;
	
	private static int AVG_FRAMES = 3600; 
	
	
	public CCBackgroundSubtraction() {
		_myBGFilter = new CCSimpleBackgroundSubtraction();
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBGRaster == null) {
			_myBGRaster = theRaster.clone();
			_mySumRaster = theRaster.clone();
			_myBGFilter.setBackgroundRaster(_myBGRaster);
		}
		
		_mySumRaster.add(theRaster.clone());
		_mySumRaster.scale(1/AVG_FRAMES);
		_myBGRaster = _mySumRaster.clone();
	
		return _myBGFilter.filter(theRaster);
	}

}

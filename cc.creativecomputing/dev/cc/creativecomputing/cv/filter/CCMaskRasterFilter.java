package cc.creativecomputing.cv.filter;

import cc.creativecomputing.cv.CCPixelRaster;

public class CCMaskRasterFilter implements CCIRasterFilter {

    CCPixelRaster _myMask = null; 

    public CCMaskRasterFilter( CCPixelRaster theMask ) {
        _myMask = theMask;
    }

	public CCPixelRaster filter(CCPixelRaster theRaster) {

        if (theRaster.width() != _myMask.width()
            && theRaster.height() != _myMask.height()) 
        {
            throw new RuntimeException("Input raster must have the same dimensions as the Mask");
        }

        CCPixelRaster myResult = theRaster.clone();
        int myLength = theRaster.width() * theRaster.height();
        float[] myMaskData = _myMask.data();
        float[] myRasterData = theRaster.data();
        for (int i = 0; i < myLength; i++) {
            myResult.data()[i] = (myMaskData[i]/255.0f) * myRasterData[i];
        }

        return myResult;

	}
}

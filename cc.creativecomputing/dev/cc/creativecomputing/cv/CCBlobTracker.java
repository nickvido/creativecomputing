package cc.creativecomputing.cv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.filter.CCIRasterFilter;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;


public class CCBlobTracker implements CCUpdateListener {
	
	public static enum CCProxyMatrixDrawMode{
		NOT, RAW, FILTERED, DENOISED, MOMENT, ALL, SMOOTHED
	}
	
	private Queue<CCTouch> _myAddedTouches = new LinkedList<CCTouch>();
	private Queue<CCTouch> _myMovedTouches = new LinkedList<CCTouch>();
	private Queue<CCTouch> _myRemovedTouches = new LinkedList<CCTouch>();
	
	@CCControl(name = "matrix draw mode")
	private CCProxyMatrixDrawMode _cDrawMatrixMode = CCProxyMatrixDrawMode.NOT;
	
	private static class CCPlacementSettings{
		@CCControl(name = "matrix offset", minX = -1, maxX = 1, minY = -1, maxY = 1)
		private CCVector2f _cOffset = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
															// screen size offset
		@CCControl(name = "offset scale", min = 0, max = 1)
		private float _cOffsetScale = 1f;
		
		private CCVector2f offset() {
			return _cOffset.clone().scale(_cOffsetScale);
		}

		@CCControl(name = "scaling", minX = 0.5f, maxX = 2f, minY = 0.5f, maxY = 2f, x = 1, y = 1)
		private CCVector2f _cScaling = new CCVector2f(1, 1); // multiplier
		
		@CCControl(name = "scaling scale", min = 0, max = 1)
		private float _cScalingScale = 1f;
		
		private CCVector2f scaling() {
			return _cScaling.clone().scale(_cScalingScale);
		}

		@CCControl(name = "scale point", minX = -1, maxX = 1, minY = -1, maxY = 1)
		private CCVector2f _cScalePoint = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
															// screen size offset
		@CCControl(name = "scale pointscale", min = 0, max = 1)
		private float _cScalePointScale = 1f;
		
		private CCVector2f scalePoint() {
			return _cScalePoint.clone().scale(_cScalePointScale);
		}
	}

	@CCControl(name = "draw blobs")
	private boolean _cDrawBlobs = false;

	@CCControl(name = "draw cursors")
	private boolean _cDrawCursors = false;

	@CCControl(name = "draw scale point")
	private boolean _cDrawScalePoint = false;
	
	@CCControl(name = "image thresh", min = 0, max = 255)
	private float _cThreshold;
	
	@CCControl(name = "image amplify", min = 1, max = 10)
	private float _cAmplify;
	
	@CCControl(name = "gain power", min = 0, max = 10)
	private float _cGainPower;
	
	@CCControl(name = "component thresh", min = 0, max = 255)
	private int _cComponentThreshold;

	private boolean _myUpdateRenderTexture = false;
	private int _myMatrixWidth;
	private int _myMatrixHeight;
	
	private CCPixelRaster _myRawRaster;
	private CCPixelRaster[] _myLastRasters = new CCPixelRaster[5];
	private int _myRasterID = 0;
	private CCPixelRaster _myBufferedRaster;
	private CCPixelRaster _myDenoisedRaster;
	private CCPixelRaster _myFilteredRaster;
	private CCPixelRaster _myMomentRaster;
    private List<CCIRasterFilter> _myRawFilters;
	
	private CCConnectedPixelAreas _myConnectedPixelAreas;
	private List<CCConnectedPixelArea> _myPixelAreas= new ArrayList<CCConnectedPixelArea>();
	private List<CCVector2f> _myCenterPositions = new ArrayList<CCVector2f>();
	
	private Map<Integer, CCTouch> _myTouchMap = new HashMap<Integer, CCTouch>();
	
	private int _myIDCounter;
	
	private float _myTileSizeX;
	private float _myTileSizeY;
	
	private int _myScreenWidth = 0;
	private int _myScreenHeight = 0;
	
	private CCMatrix32f _myMatrix;
	
	private List<CCTouchListener> _myTouchListener = new ArrayList<CCTouchListener>();
	
	private CCPlacementSettings _myPlacementSettings = new CCPlacementSettings();

	public CCBlobTracker(
		final int theMatrixWidth, final int theMatrixHeight, 
		final int theScreenWidth, final int theScreenHeight
	) {
		_myMatrixWidth = theMatrixWidth;
		_myMatrixHeight = theMatrixHeight;
		_myScreenWidth = theScreenWidth;
		_myScreenHeight = theScreenHeight;

		_myConnectedPixelAreas = new CCConnectedPixelAreas();
		_myRawFilters = new ArrayList<CCIRasterFilter>();
	}
	
	public CCBlobTracker(
		final CCApp theApp, 
		final int theMatrixWidth, final int theMatrixHeight, 
		final int theScreenWidth, final int theScreenHeight
	) {
		this(theMatrixWidth, theMatrixHeight, theScreenWidth, theScreenHeight);
		_myScreenWidth = theScreenWidth;
		_myScreenHeight = theScreenHeight;
		
		onChangeMatrixSize(theMatrixWidth, theMatrixHeight);

		_myConnectedPixelAreas = new CCConnectedPixelAreas();

		theApp.addControls("sensors", "Calibration", 0,this);
		theApp.addControls("sensors", "placement", 1,_myPlacementSettings);
		theApp.addUpdateListener(this);
	}
	
	public int matrixWidth() {
		return _myMatrixWidth;
	}

	public int matrixHeight() {
		return _myMatrixHeight;
	}
	
	public List<CCTouch> touches(){
		return new ArrayList<CCTouch>(_myTouchMap.values());
	}
	
	public CCPixelRaster rawRaster() {
		return _myRawRaster;
	}
	
	public CCPixelRaster momentRaster() {
		return _myMomentRaster;
	}
	
	public void addListener(final CCTouchListener theListener) {
		_myTouchListener.add(theListener);
	}

 
    public void addRawFilter(CCIRasterFilter theFilter) {
    	_myRawFilters.add(theFilter);
    }
	
	public void update(final float theDeltaTime) {
		synchronized (_myAddedTouches) {
			while(!_myAddedTouches.isEmpty()) {
				CCTouch myAddedTouch = _myAddedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchPress(myAddedTouch);
				}
			}
		}

		synchronized (_myMovedTouches) {
			while(!_myMovedTouches.isEmpty()) {
				CCTouch myMovedTouch = _myMovedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchMove(myMovedTouch);
				}
			}
		}

		synchronized (_myRemovedTouches) {
			while(!_myRemovedTouches.isEmpty()) {
				CCTouch myRemovedTouch = _myRemovedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchRelease(myRemovedTouch);
				}
			}
		}
	}
	
	private CCVector2f interpolateMaximum(CCVector2i theCenter, float theMaximum) {
        CCVector2f myResult = new CCVector2f();

        // centroid based calculation
        float myMx   = 0.0f;
        float myMy   = 0.0f;
        float myMass = 0.0f;
        
        // NOTE: window size can be increased for higher stabilty. 
        for (int x=-1; x<=1; x++) {
        	for (int y=-1; y<=1; y++) {
        		float myValue = _myDenoisedRaster.get(theCenter.x + x, theCenter.y() + y);
        		if (myValue > 0.0f) {
        			myMx += (x + theCenter.x)*myValue;
        			myMy += (y + theCenter.y())*myValue;
        			myMass += myValue;
        		}
        	}
        }

        myResult = new CCVector2f( myMx/myMass, myMy/myMass );  
        return myResult;
    }

    List<CCVector2f> computeCursorPositions(final List<CCConnectedPixelArea> theConnectedPixelAreas) {
       List<CCVector2f> myResult = new ArrayList<CCVector2f>();
        
        for (CCConnectedPixelArea myPixelArea:theConnectedPixelAreas) {
            
            CCVector2i myMaximumPosition = new CCVector2i();
            float myMaximum = myPixelArea.max(_myMomentRaster, myMaximumPosition);

            CCVector2f myCenter = interpolateMaximum(myMaximumPosition, myMaximum);
            myCenter.scale(1f / (_myMatrixWidth), 1f / (_myMatrixHeight));

            if(myCenter.isNAN()) {
            	System.out.println("NAN:" + myMaximum + ":"+myMaximumPosition);
            }else {
            	myResult.add(myCenter);
            }
        }

        return myResult;
    }
    
    private void correlatePositions(
    	final List<CCVector2f> thePositions,
    	final List<CCConnectedPixelArea> theConnectedPixelAreas
    ){

        // populate a map with all distances between existing cursors and new positions
        Map<Float, List<CCVector2i>> myDistanceMap = new TreeMap<Float, List<CCVector2i>>();
        float myDistanceThreshold = 4.0f / ((_myMatrixWidth + _myMatrixHeight) / 2f);
        
        for (int myCursorID:_myTouchMap.keySet()) {
            //computeIntensity(myCursorIt, myRawRaster);
        	CCTouch myTouch = _myTouchMap.get(myCursorID);
        	myTouch.isCorrelated(false);
            for (int i = 0; i < thePositions.size();i++) {
                float myDistance = CCVecMath.add(myTouch.relativePosition(),myTouch.motion()).distance(thePositions.get(i));
                if (myDistance < myDistanceThreshold) {
                	List<CCVector2i> myList = myDistanceMap.get(myDistance);
                	if(myList == null) {
                		myList = new ArrayList<CCVector2i>();
                		myDistanceMap.put(myDistance, myList);
                	}
                	myList.add(new CCVector2i(i, myCursorID));
                }
            }
        }

        // will contain the correlated cursor id at index n for position n or -1 if uncorrelated
        List<Integer> myCorrelatedPositions = new ArrayList<Integer>();
        for(int i = 0; i < thePositions.size();i++) {
        	myCorrelatedPositions.add(-1);
        }

        // iterate through the distance map and correlate cursors in increasing distance order
        synchronized (_myMovedTouches) {
        	for (List<CCVector2i> myEntry:myDistanceMap.values()){
            	for(CCVector2i myIndices:myEntry) {
    	            // check if we already have correlated one of our nodes
    	            int myPositionIndex = myIndices.x;
    	            int myCursorId = myIndices.y();
    	            
    	            CCTouch myTouch = _myTouchMap.get(myCursorId);
    	            CCVector2f myRelativePosition = thePositions.get(myPositionIndex);
    	            CCVector2f myPosition = _myMatrix.transform(myRelativePosition);
    	            
    	            if (!myTouch.isCorrelated()) {
    	                if (myCorrelatedPositions.get(myPositionIndex) == -1)  {
    	                    // correlate
    	                    myCorrelatedPositions.set(myPositionIndex,myCursorId);
    	                    myTouch.isCorrelated(true);
    	
    	                    // update cursor with new position
    	                    myTouch.motion(CCVecMath.subtract(myRelativePosition, myTouch.relativePosition()));
    	                    myTouch.relativePosition().set(myRelativePosition);
    	                    myTouch.position().set(myPosition);
    	
    	                    // post a move event
    	                    _myMovedTouches.add(myTouch);
    	                }
    	            }
            	}
            }
		}
        

    	// Now let us iterate through all new positions and create 
        //"cursor add" events for every uncorrelated position

		synchronized (_myAddedTouches) {
			for (int i = 0; i < thePositions.size(); ++i) {
				if (myCorrelatedPositions.get(i) == -1) {
					// new cursor
					int myNewID = _myIDCounter++;
					if (Float.isNaN(thePositions.get(i).x)) {
						System.out.println("new cursor " + myNewID + " at " + thePositions.get(i));
					}
					myCorrelatedPositions.set(i, myNewID);

					CCVector2f myRelativePosition = thePositions.get(i);
					CCVector2f myPosition = _myMatrix.transform(myRelativePosition);
					
					CCTouch myTouch = new CCTouch(myPosition, myRelativePosition);
					myTouch.id(myNewID);
					myTouch.isCorrelated(true);

					_myTouchMap.put(myNewID, myTouch);
					_myAddedTouches.add(myTouch);
				}
			}
		}
        

        List<Integer> myIdsToRemove = new ArrayList<Integer>();
        
    	// Now let us iterate through all cursors and create 
        //"cursor remove" events for every uncorrelated cursors
        synchronized (_myRemovedTouches) {
            for (Entry<Integer, CCTouch> myEntry:_myTouchMap.entrySet()) {
                if (!myEntry.getValue().isCorrelated()) {
                    // cursor removed
                    _myRemovedTouches.add(myEntry.getValue());
                    myIdsToRemove.add(myEntry.getKey());
                }
            }
		}
        
        for(int myID:myIdsToRemove) {
            _myTouchMap.remove(myID);
        }
    }

	/* (non-Javadoc)
	 * @see de.artcom.deutschebank.brandspace.centerboard.input.CCProxyMatrixClientListener#onChangeMatrixSize(int, int)
	 */
	public void onChangeMatrixSize(int theNewMatrixWidth, int theNewMatrixHeight) {
		_myMatrixWidth = theNewMatrixWidth;
		_myMatrixHeight = theNewMatrixHeight;
	
		_myTileSizeX = _myScreenWidth / _myMatrixWidth;
		_myTileSizeY = _myScreenHeight / _myMatrixHeight;
		
		_myUpdateRenderTexture = true;
	}

	/* (non-Javadoc)
	 * @see de.artcom.deutschebank.brandspace.centerboard.input.CCProxyMatrixClientListener#onUpdateRaster(de.artcom.deutschebank.brandspace.centerboard.input.CCPixelRaster)
	 */
	public void onUpdateRaster(CCPixelRaster theRaster) {
		
		_myRawRaster = theRaster.clone();
		
		_myFilteredRaster = _myRawRaster.clone();
		for (CCIRasterFilter myFilter : _myRawFilters) {
			_myFilteredRaster = myFilter.filter(_myFilteredRaster);
		}
        		
		_myLastRasters[_myRasterID] = _myFilteredRaster.clone();

		_myRasterID++;
		_myRasterID %= _myLastRasters.length;
		
		_myBufferedRaster = new CCPixelRaster("BUFFERED",_myMatrixWidth, _myMatrixHeight);
		
		for(CCPixelRaster myRaster:_myLastRasters) {
			if(myRaster != null)_myBufferedRaster.add(myRaster);
		}
		_myBufferedRaster.scale(1f / _myLastRasters.length);
		
		_myDenoisedRaster = _myBufferedRaster.clone();
		_myDenoisedRaster.threshold(_cThreshold);
		
		_myMomentRaster = _myDenoisedRaster.clone();
		_myMomentRaster.scale(_cAmplify);
		_myMomentRaster.power(_cGainPower);
		
		_myPixelAreas = _myConnectedPixelAreas.connectedPixelAreas(_myBufferedRaster, _cComponentThreshold);
    	
    	CCVector2f myOffset = _myPlacementSettings.offset();
    	CCVector2f myScaling = _myPlacementSettings.scaling();
    	CCVector2f myScalingPoint = _myPlacementSettings.scalePoint();
    	
    	_myMatrix = new CCMatrix32f();
		_myMatrix.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
		_myMatrix.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
		_myMatrix.scale(myScaling.x * _myMatrixWidth * _myTileSizeX, myScaling.y * -_myMatrixHeight * _myTileSizeY);
		_myMatrix.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
    	
    	_myCenterPositions = computeCursorPositions(_myPixelAreas);
    	correlatePositions(_myCenterPositions, _myPixelAreas);
	}
	
	private void drawRaster(CCGraphics g, CCPixelRaster theRaster) {
		for (int x = 0; x < _myMatrixWidth; x++) {
			for (int y = 0; y < _myMatrixHeight; y++) {
				g.color((int)theRaster.get(x, y));
				g.rect(x * _myTileSizeX, -_myTileSizeY - y * _myTileSizeY, _myTileSizeX, _myTileSizeY);
			}
		}
	}

	public void draw(CCGraphics g) {
		
		g.pushAttribute();
		
		if(_myRawRaster == null)return;

		CCVector2f myOffset = _myPlacementSettings.offset();	
		CCVector2f myScaling = _myPlacementSettings.scaling();
		CCVector2f myScalingPoint = _myPlacementSettings.scalePoint();

		if (_cDrawMatrixMode != CCProxyMatrixDrawMode.NOT && _cDrawMatrixMode != CCProxyMatrixDrawMode.ALL) {
			g.pushMatrix();

			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
//			g.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
//			
//			g.scale(myScaling.x, myScaling.y);
//			g.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
			
			switch(_cDrawMatrixMode) {
			case DENOISED:
				drawRaster(g,_myDenoisedRaster);
				break;
			case FILTERED:
				drawRaster(g, _myFilteredRaster);
				break;
			case MOMENT:
				drawRaster(g,_myMomentRaster);
				break;
			case RAW:
				drawRaster(g, _myRawRaster);
				break;
			}

			g.popMatrix();
		}
		
		if(_cDrawMatrixMode == CCProxyMatrixDrawMode.ALL) {
			g.pushMatrix();
			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.scale(0.5f);
			drawRaster(g, _myRawRaster);
			g.popMatrix();
			
			g.pushMatrix();
			g.translate(0, _myScreenHeight * 0.5f);
			g.scale(0.5f);
			drawRaster(g, _myDenoisedRaster);
			g.popMatrix();
			
			g.pushMatrix();
			g.translate(-_myScreenWidth * 0.5f, 0);
			g.scale(0.5f);
			drawRaster(g, _myMomentRaster);
			g.popMatrix();

			
//			g.pushMatrix();
//			g.translate(0, 0);
//			g.scale(0.5f);
//			drawTexture(g, _myHeatMap);
//			g.popMatrix();
		}

		if (_cDrawBlobs) {
			g.pushMatrix();

			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
			
			g.scale(_myTileSizeX, - _myTileSizeY);
			g.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
			g.color(1f,0.5f);
			for(CCConnectedPixelArea myPixelArea:_myPixelAreas) {
				myPixelArea.draw(g);
			}

			g.popMatrix();
		}
		
		if (_cDrawCursors) {
			g.pushMatrix();

			
			
			for(CCTouch myCursors:touches()) {
				g.color(0,255,0);
				g.ellipse(myCursors.position(), 10f);
				g.color(0,0,255);
				g.text(""+myCursors.id(), myCursors.position());
			}
			
			g.popMatrix();
		}

		if (_cDrawScalePoint) {
			g.pushMatrix();

			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
			
			g.scale(myScaling.x * _myTileSizeX, -myScaling.y * _myTileSizeY);
			g.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
			g.color(0f,1f,0,0.5f);

			g.ellipse( -myScalingPoint.x * _myScreenWidth, myScalingPoint.y * _myScreenHeight, 3);

			g.popMatrix();
		}
		
		g.popAttribute();
	}
}

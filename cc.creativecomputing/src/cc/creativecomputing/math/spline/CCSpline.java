/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * <p>
 * In computer graphics splines are popular curves because of the simplicity of their construction, their ease and accuracy of evaluation, and their capacity to approximate complex
 * shapes through curve fitting and interactive curve design.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Spline_(mathematics)">spline at wikipedia</a>
 */
public abstract class CCSpline {

	public static enum CCSplineType {
		LINEAR, CATMULL_ROM, BEZIER, NURB, BLEND
	}

	protected List<CCVector3f>	_myControlPoints	= new ArrayList<CCVector3f>();

	protected boolean			_myIsClosed;
	protected List<Float>		_mySegmentsLength;
	protected float				_myTotalLength;
	protected CCSplineType		_myType;

	protected boolean			_myIsModified		= true;
	
	protected int _myInterpolationIncrease = 1;

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (CCSplineType theSplineType, boolean theIsClosed) {
		_myType = theSplineType;
		_myIsClosed = theIsClosed;
	}

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theControlPoints an array of vector to use as control points of the spline
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (
		CCSplineType theSplineType, CCVector3f[] theControlPoints,
		boolean theIsClosed) {
		this(theSplineType, theIsClosed);
		addControlPoints(theControlPoints);
	}

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theControlPoints a list of vector to use as control points of the spline
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (
		CCSplineType theSplineType, List<CCVector3f> theControlPoints,
		boolean theIsClosed) {
		this(theSplineType, theIsClosed);
		addControlPoints(theControlPoints);
	}

	/**
	 * Use this method to mark the spline as modified, this is only necessary
	 * if you directly add points using the reference passed by the {@linkplain #controlPoints()} method.
	 */
	public void beginEditSpline () {
		if (_myIsModified) return;

		_myIsModified = true;

		if (_myControlPoints.size() > 2 && _myIsClosed) {
			_myControlPoints.remove(_myControlPoints.size() - 1);
		}

	}

	public void endEditSpline () {
		if (!_myIsModified) return;

		_myIsModified = false;

		if (_myControlPoints.size() >= 2 && _myIsClosed) {
			_myControlPoints.add(_myControlPoints.get(0));
		}

		if (_myControlPoints.size() > 1) {
			computeTotalLentgh();
		}
	}

	/**
	 * remove the controlPoint from the spline
	 * @param controlPoint the controlPoint to remove
	 */
	public void removeControlPoint (CCVector3f controlPoint) {
		beginEditSpline();
		_myControlPoints.remove(controlPoint);
	}

	/**
	 * Adds a controlPoint to the spline.
	 * <p>
	 * If you add one control point to a bezier spline and the added point is 
	 * not the first point of the spline, there are two more
	 * points added as control points these points will be the previous point
	 * and the added point, resulting in a straight line.
	 * </p>
	 * @param theControlPoint a position in world space
	 */
	public void addControlPoint (CCVector3f theControlPoint) {
		beginEditSpline();
		_myControlPoints.add(theControlPoint);
	}

	/**
	 * Adds the given control points to the spline
	 * @param theControlPoints
	 */
	public void addControlPoints (CCVector3f... theControlPoints) {
		for (CCVector3f myPoint : theControlPoints) {
			addControlPoint(myPoint);
		}
	}

	/**
	 * Adds the given control points to the spline
	 * @param theControlPoints
	 */
	public void addControlPoints (List<CCVector3f> theControlPoints) {
		for (CCVector3f myPoint : theControlPoints) {
			addControlPoint(myPoint);
		}
	}

	protected abstract void computeTotalLengthImpl ();

	/**
	 * This method computes the total length of the curve.
	 */
	protected void computeTotalLentgh () {
		_myTotalLength = 0;

		if (_mySegmentsLength == null) {
			_mySegmentsLength = new ArrayList<Float>();
		} else {
			_mySegmentsLength.clear();
		}
		computeTotalLengthImpl();
	}

	/**
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the current control point and the next one
	 * @param theControlPointIndex the current control point
	 * @return the position
	 */
	public abstract CCVector3f interpolate (float theBlend, int theControlPointIndex);
	
	/**
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
	 * @return the position
	 */
	public CCVector3f interpolate (float theBlend){
		float myLength = _myTotalLength * CCMath.saturate(theBlend);
		float myReachedLength = 0;
		int myIndex = 0;
		
		while(myReachedLength + _mySegmentsLength.get(myIndex) < myLength){
			myReachedLength += _mySegmentsLength.get(myIndex);
			myIndex ++;
		}
		
		float myLocalLength = myLength - myReachedLength;
		float myLocalBlend = myLocalLength / _mySegmentsLength.get(myIndex);
		return interpolate(myLocalBlend, myIndex * _myInterpolationIncrease);
	}

	/**
	 * returns true if the spline cycle
	 * @return
	 */
	public boolean isClosed () {
		return _myIsClosed;
	}

	/**
	 * set to true to make the spline cycle
	 * @param theIsClosed
	 */
	public void isClosed (boolean theIsClosed) {
		if (theIsClosed == _myIsClosed) return;
		beginEditSpline();
		_myIsClosed = theIsClosed;
		endEditSpline();
	}

	/**
	 * return the total length of the spline
	 * @return
	 */
	public float totalLength () {
		return _myTotalLength;
	}

	/**
	 * return the type of the spline
	 * @return
	 */
	public CCSplineType type () {
		return _myType;
	}

	/**
	 * returns this spline control points
	 * @return
	 */
	public List<CCVector3f> controlPoints () {
		return _myControlPoints;
	}
	
	/**
	 * Returns the number of segments in this spline
	 * @return
	 */
	public int numberOfSegments(){
		return _mySegmentsLength.size();
	}

	/**
	 * returns a list of float representing the segments length
	 * @return
	 */
	public List<Float> segmentsLength () {
		return _mySegmentsLength;
	}

	public void draw (CCGraphics g) {

	}
	
	public CCVector3f closestPoint(CCVector3f thePoint){
		float myMinDistanceSq = Float.MAX_VALUE;
		CCVector3f myPoint = null;
		for(CCVector3f myControlPoint:_myControlPoints){
			float myDistSq = thePoint.distanceSquared(myPoint);
			if(myDistSq < myMinDistanceSq){
				myMinDistanceSq = myDistSq;
				myPoint = myControlPoint;
			}
		}
		return myPoint;
	}
	
	/**
	 * Removes all points from the spline
	 */
	public void clear(){
		_myControlPoints.clear();
		if(_mySegmentsLength != null)_mySegmentsLength.clear();
		_myTotalLength = 0;
	}

}
package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCLine3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.CCTuple;

public class CCLinearSpline extends CCSpline{
	
	private List<CCLine3f> _myLines = new ArrayList<CCLine3f>();
	
	public CCLinearSpline(boolean theIsClosed) {
		super(CCSplineType.LINEAR, theIsClosed);
	}

	public CCLinearSpline(CCVector3f[] theControlPoints, boolean theIsClosed) {
		super(CCSplineType.LINEAR, theControlPoints, theIsClosed);
	}

	public CCLinearSpline(List<CCVector3f> theControlPoints, boolean theIsClosed) {
		super(CCSplineType.LINEAR, theControlPoints, theIsClosed);
	}

	@Override
	public void computeTotalLengthImpl() {
		_myLines.clear();
		if (_myControlPoints.size() > 1) {
			for (int i = 0; i < _myControlPoints.size() - 1; i++) {
				CCLine3f myLine = new CCLine3f(_myControlPoints.get(i), _myControlPoints.get(i + 1));
				float myLength = myLine.length();
				_mySegmentsLength.add(myLength);
				_myTotalLength += myLength;
				_myLines.add(myLine);
			}
		}
	}

	@Override
	public CCVector3f interpolate(float value, int currentControlPoint) {
		endEditSpline();
		return CCVecMath.blend(
			value, 
			_myControlPoints.get(currentControlPoint), 
			_myControlPoints.get(currentControlPoint + 1)
		);
	}
	
	public CCTuple<Integer, Float>closestInterpolation(CCVector3f thePoint,  int theStart, int theEnd){
		if(theEnd < theStart)theEnd += _myLines.size();
		
		int myIndex = theStart;
		float myBlend = 0;
		
		float myMinDistSq = Float.MAX_VALUE;
		
		for(int i = theStart; i < theEnd;i++){
			CCLine3f myLine = _myLines.get(i % _myLines.size());
			CCVector3f myPoint = myLine.closestPoint(thePoint);
			float myDistSq = myPoint.distanceSquared(thePoint);
			if(myDistSq < myMinDistSq){
				myIndex = i % _myLines.size();
				myBlend = myLine.closestPointBlend(thePoint);
				myMinDistSq = myDistSq;
			}
		}
		
		return new CCTuple<Integer, Float>(myIndex, myBlend);
	}
	
	@Override
	public CCVector3f closestPoint (CCVector3f thePoint) {
		return closestPoint(thePoint, 0, _myLines.size());
	}
	
	public CCVector3f closestPoint (CCVector3f thePoint, int theStart, int theEnd) {
		if(theEnd < theStart)theEnd += _myLines.size();
		CCVector3f myClosestPoint = null;
		float myMinDistSq = Float.MAX_VALUE;
		
		for(int i = theStart; i < theEnd;i++){
			CCLine3f myLine = _myLines.get(i % _myLines.size());
			CCVector3f myPoint = myLine.closestPoint(thePoint);
			float myDistSq = myPoint.distanceSquared(thePoint);
			if(myDistSq < myMinDistSq){
				myClosestPoint = myPoint;
				myMinDistSq = myDistSq;
			}
		}
		
		return myClosestPoint;
	}
	@Override
	public void clear () {
		super.clear();
		_myLines.clear();
	}

	@Override
	public void draw(CCGraphics g) {
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(CCVector3f myPoint:_myControlPoints){
			g.vertex(myPoint);
		}
		g.endShape();
	}
	
	
}

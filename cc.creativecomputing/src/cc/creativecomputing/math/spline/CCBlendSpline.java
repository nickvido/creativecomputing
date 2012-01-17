package cc.creativecomputing.math.spline;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCBlendSpline extends CCSpline{

	private float _myBlend = 0;
	
	private CCSpline _mySpline1;
	private CCSpline _mySpline2;
	
	public CCBlendSpline(CCSpline theSpline1, CCSpline theSpline2) {
		super(CCSplineType.BLEND, false);
		
		_mySpline1 = theSpline1;
		_mySpline2 = theSpline2;
	}

	public void blend(float theBlend){
		_myBlend = theBlend;
	}
	
	@Override
	protected void computeTotalLengthImpl() {
		
	}
	
	@Override
	public float totalLength() {
		return CCMath.blend(_mySpline1.totalLength(), _mySpline2.totalLength(), _myBlend);
	}
	
	@Override
	public int numberOfSegments() {
		return CCMath.max(_mySpline1.numberOfSegments(), _mySpline2.numberOfSegments());
	}

	@Override
	public CCVector3f interpolate(float theBlend, int theControlPointIndex) {
		return null;
	}
	
	@Override
	public CCVector3f interpolate(float theBlend) {
		return CCVecMath.blend(
			_myBlend,
			_mySpline1.interpolate(theBlend), 
			_mySpline2.interpolate(theBlend)
		);
	}

	@Override
	public void draw(CCGraphics g) {
		float myDivisions = numberOfSegments() * 30;
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i <= myDivisions; i++){
			g.vertex(interpolate(i/myDivisions));
		}
		g.endShape();
	}
}

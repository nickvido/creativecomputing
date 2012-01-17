package cc.creativecomputing.demo.math.spline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;

public class CCCatmulRomSplineClosestDemo extends CCApp {
	
	@CCControl(name = "tension", min = -3, max = 3)
	private float _cCurveTension;
	
	@CCControl(name = "interpolation", min = 0, max = 1)
	private float _cInterpolation = 0;
	
	private CCCatmulRomSpline _mySpline;
	private CCVector3f _myClosestPoint = new CCVector3f();

	@Override
	public void setup() {
		CCMath.randomSeed(0);
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		for(int i = 0; i < 10;i++){
			_mySpline.addControlPoint(new CCVector3f(CCMath.random(-width/3, width/3), CCMath.random(-height/3, height/3)));
		}
		
		addControls("app", "app", this);
		g.pointSize(8);
		frameRate(20);
	}
	
	@CCControl(name = "close")
	public void closeCurve(boolean theIsClosed){
		_mySpline.isClosed(theIsClosed);
	}

	@Override
	public void update(final float theDeltaTime) {
		_mySpline.curveTension(_cCurveTension);
		_myClosestPoint = _mySpline.closestPoint(new CCVector3f(mouseX - width/2, height/2 - mouseY));
	}

	@Override
	public void draw() {
		g.clear();
		
		g.color(255);
		_mySpline.draw(g);
		
		g.beginShape(CCDrawMode.POINTS);
		g.color(255,0,0);
		g.vertex(_mySpline.controlPoints().get(0));
		g.color(255);
		for(int i = 1; i < _mySpline.controlPoints().size() - 1;i++){
			g.vertex(_mySpline.controlPoints().get(i));
		}
		g.color(0,0,255);
		g.vertex(_mySpline.controlPoints().get(_mySpline.controlPoints().size() - 1));
		g.endShape();
		
		CCVector3f myInterploatedValue = _mySpline.interpolate(_cInterpolation);
		g.ellipse(myInterploatedValue, 10);
		
		g.ellipse(_myClosestPoint, 10);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCatmulRomSplineClosestDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


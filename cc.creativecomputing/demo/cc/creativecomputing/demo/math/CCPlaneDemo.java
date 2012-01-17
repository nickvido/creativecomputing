package cc.creativecomputing.demo.math;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCPlaneDemo extends CCApp {

	@CCControlClass(name = "plane")
	private static class CCPlaneControl {
		
		@CCControl(name = "norm x", min = -1, max = 1)
		private static float normX = 0;
		

		@CCControl(name = "norm y", min = -1, max = 1)
		private static float normY = 0;
		
		@CCControl(name = "norm z", min = -1, max = 1)
		private static float normZ = 0;
		
		
		@CCControl(name = "planeConstant", min = -400, max = 400)
		private static float planeConstant = 0;
	}
	
	private CCPlane3f _myPlane;
	private CCArcball _myArcball;
	@Override
	public void setup() {
		addControls(CCPlaneControl.class);
		_myPlane = new CCPlane3f();
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myPlane.normal().set(CCPlaneControl.normX, CCPlaneControl.normY, CCPlaneControl.normZ).normalize();
		_myPlane.constant(CCPlaneControl.planeConstant);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myPlane.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPlaneDemo.class);
		myManager.settings().size(1200, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


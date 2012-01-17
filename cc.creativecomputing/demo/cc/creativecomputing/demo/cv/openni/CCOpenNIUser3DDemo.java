package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.util.CCArcball;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUser3DDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	private CCOpenNIRenderer _myRenderer;

	public void setup() {
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myRenderer = new CCOpenNIRenderer(_myOpenNI); 

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		// enable skeleton generation for all joints
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		g.strokeWeight(3);
		g.perspective(95, width / (float) height, 10, 150000);
	}

	public void draw() {
		g.clear();

		// set the scene pos
		_myArcball.draw(g);
		//
		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myRenderer.drawDepthMesh(g);
		
		g.color(255);
		g.strokeWeight(1);

		g.noDepthTest();
//		_myDepthGenerator.drawDepthMap(g);

		g.color(255, 0, 0);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.draw(g);
		}
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIUser3DDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

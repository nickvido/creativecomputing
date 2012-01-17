package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIImageGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUserDemo extends CCApp {

	private CCOpenNI _myOpenNI;
	private CCOpenNIImageGenerator _myImageGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;

	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");

		_myImageGenerator = _myOpenNI.createImageGenerator();
		_myUserGenerator = _myOpenNI.createUserGenerator();
		
		_myOpenNI.start();

		g.strokeWeight(3);
	}

	public void draw() {
		g.clear();

		g.color(255);
		g.image(_myImageGenerator.texture(), 0, 0);

		g.color(255, 0, 0);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.draw2D(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIUserDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

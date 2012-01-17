package cc.creativecomputing.animation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCPulser;
import cc.creativecomputing.util.logging.CCLog;

/**
 * CCPulserTest. Demonstrates the use of CCPulser
 * 
 * @author jenswunderling
 * 
 */
public class CCPulserTest extends CCApp {
	//the target values, changed using reflection
	public float pulse;

	public void setup() {
		frameRate(60);
		CCPulser p = new CCPulser();
		p.start(this, "pulse", 2, 1, "pulseAction");
	}

	public void update(float theDeltaTime) {
		CCAnimationManager.update(theDeltaTime);
	}

	public void pulseAction() {
		CCLog.info("pulseAction");
	}

	public void draw() {
		g.color(0, 0.1f);
		g.rect(-g.width / 2, -g.height / 2, g.width, g.height);
		g.color(255, pulse);
		g.ellipse(0, 0, 10);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPulserTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}

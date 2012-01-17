package cc.creativecomputing.animation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCValueSequencer;
import cc.creativecomputing.util.logging.CCLog;

/**
 * CCPulserTest. Demonstrates the use of CCPulser
 * 
 * @author jenswunderling
 * 
 */
public class CCValueSequencerTest extends CCApp {
	//the target values, changed using reflection
	public float sequencedValue;
	public float size = 10;

	public void setup() {
		frameRate(60);
		CCValueSequencer seq = new CCValueSequencer();
		seq.start(this, "sequencedValue");
		seq.addStep(0.2f, -10);
		seq.addStep(0.5f, 60);
		seq.addStep(1, 100, "trigger");
		seq.addStep(0.1f, -100);

	}

	public void trigger() {
		size = (float) (Math.random() * 20 + 5);

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
		g.color(255);
		g.ellipse(0, sequencedValue, size);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCValueSequencerTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}

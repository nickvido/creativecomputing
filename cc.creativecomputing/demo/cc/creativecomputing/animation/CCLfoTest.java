package cc.creativecomputing.animation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCLfo;
import cc.creativecomputing.animation.CCAbstractAnimation.CCEaseCurve;
import cc.creativecomputing.animation.CCLfo.CCLfoCycleMode;
import cc.creativecomputing.math.easing.CCEasingMode;

public class CCLfoTest extends CCApp {

	public float lfoTargetUsingReflection, otherLfoTargetUsingReflection;

	private CCLfo lfo = new CCLfo();
	private CCLfo otherLfo = new CCLfo();
	private CCLfo horizontalLfo = new CCLfo();

	@Override
	public void setup() {
		frameRate(60);

		horizontalLfo.setRange(-g.width / 2, g.width / 2);
		//start lfo without target. you need to get the value using .getAnimationValue()
		horizontalLfo.start(null, null, 5);
		otherLfo.start(
				this,
				"otherLfoTargetUsingReflection",
				1,
				CCEasingMode.SINE,
				CCEaseCurve.IN_OUT);
		otherLfo.setCycleMode(CCLfoCycleMode.UP_DOWN);

		lfo.start(
				this,
				"lfoTargetUsingReflection",
				1,
				CCEasingMode.LINEAR,
				CCEaseCurve.IN_OUT);

	}

	@Override
	public void update(float theDeltaTime) {
		CCAnimationManager.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		g.color(0, 0.1f);
		g.rect(-g.width / 2, -g.height / 2, g.width, g.height);
		g.color(255);
		g.ellipse(horizontalLfo.getAnimationValue(), 100 * lfoTargetUsingReflection, 3);
		g.ellipse(horizontalLfo.getAnimationValue(), -200 + 100 * otherLfoTargetUsingReflection, 3);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCLfoTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}

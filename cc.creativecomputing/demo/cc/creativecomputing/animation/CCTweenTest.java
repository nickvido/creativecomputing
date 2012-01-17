package cc.creativecomputing.animation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCTween;
import cc.creativecomputing.animation.CCAbstractAnimation.CCEaseCurve;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.math.easing.CCEasingMode;

/**
 * CCTWeenTest. Demonstrates the use of tweens click to try out
 * 
 * @author jenswunderling
 * 
 */
public class CCTweenTest extends CCApp {
	//the target values, changed using reflection
	public float tweenX, tweenY;
	
	public void setup() {
		frameRate(60);
	}

	public void update(float theDeltaTime) {
		CCAnimationManager.update(theDeltaTime);
	}

	public void draw() {
		g.translate(-g.width / 2, -g.height / 2);
		g.color(0, 0.1f);
		g.rect(0, 0, g.width, g.height);
		g.color(255);
		g.ellipse(tweenX, tweenY, size);

	}

	public float size = 2;

	public void mousePressed(CCMouseEvent e) {
		System.out.print(e.position().toString());
		CCTween tx = new CCTween();
		tx.start(
				this,
				"tweenX",
				tweenX,
				e.position().x,
				1.0f,
				null,
				CCEasingMode.CIRCULAR,
				CCEaseCurve.IN_OUT);
		CCTween ty = new CCTween();
		ty.start(
				this,
				"tweenY",
				tweenY,
				g.height - e.position().y,
				1.0f,
				"finished",
				CCEasingMode.QUINTIC,
				CCEaseCurve.IN_OUT);

	}

	//finished is called when ty is finished
	public void finished() {
		size = 10;
		CCTween tx = new CCTween();
		tx.start(
				this,
				"size",
				size,
				2f,
				1.0f,
				null,
				CCEasingMode.QUADRATIC,
				CCEaseCurve.IN);
	}

	public static void main(String[] args) {
		CCApplicationManager _myManager = new CCApplicationManager(CCTweenTest.class);
		_myManager.settings().size(500, 500);
		_myManager.start();
	}

}

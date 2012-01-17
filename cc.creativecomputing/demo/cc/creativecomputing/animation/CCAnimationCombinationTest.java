package cc.creativecomputing.animation;

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCTween;
import cc.creativecomputing.animation.CCValueSequencer;
import cc.creativecomputing.animation.CCAbstractAnimation.CCEaseCurve;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.easing.CCEasingMode;
import cc.creativecomputing.math.random.CCRandom;

/**
 * CCPulserTest. Demonstrates the use of CCPulser
 * 
 * @author jenswunderling
 * 
 */
public class CCAnimationCombinationTest extends CCApp {
	//the target values, changed using reflection
	public float sequencedValue;
	public float size = 10;
	public ArrayList<AnimatedEllipse> _myEllipses = new ArrayList<AnimatedEllipse>();
	private CCRandom rnd = new CCRandom();

	public void setup() {
		frameRate(60);
		CCValueSequencer seq = new CCValueSequencer();
		seq.start(this, "sequencedValue");
		seq.addStep(0.2f, -10, "trigger");
		seq.addStep(0.5f, 60, "trigger");
		seq.addStep(1, 100, "trigger");
		seq.addStep(0.1f, -100, "trigger");
	}

	public class AnimatedEllipse {
		private boolean _myIsFinished = false;
		public float x, y;
		public float radius = 2;

		public AnimatedEllipse(float theX) {
			x = theX;
			CCTween tX = new CCTween();
			CCTween tY = new CCTween();
			tX.start(
					this,
					"x",
					0,
					rnd.random(-g.width / 2, g.width / 2),
					1,
					"flash",
					CCEasingMode.QUADRATIC,
					CCEaseCurve.OUT);
			tY.start(
					this,
					"y",
					0,
					rnd.random(-g.height / 2, g.height / 2),
					1,
					null,
					CCEasingMode.QUINTIC,
					CCEaseCurve.OUT);
			_myEllipses.add(this);

		}

		public void draw(CCGraphics g) {

			g.color(255);
			g.ellipse(x, y, radius);

		}

		public void flash() {
			CCTween t = new CCTween();
			radius = 10;
			t.start(
					this,
					"radius",
					10,
					0,
					1,
					"finish",
					CCEasingMode.EXPONENTIAL,
					CCEaseCurve.OUT);
		}

		public void finish() {
			_myIsFinished = true;
		}

		public boolean isFinished() {
			return _myIsFinished;
		}

	}

	public void trigger() {
		//		size = (float) (Math.random() * 20 + 5);
		new AnimatedEllipse(sequencedValue);
	}

	public void update(float theDeltaTime) {
		CCAnimationManager.update(theDeltaTime);
		ArrayList<AnimatedEllipse> ellipsesToRemove = new ArrayList<AnimatedEllipse>();
		for (AnimatedEllipse e : _myEllipses) {
			if (e.isFinished())
				ellipsesToRemove.add(e);

		}
		for (AnimatedEllipse e : ellipsesToRemove) {
			_myEllipses.remove(e);
		}
	}

	public void draw() {
		g.color(0, 0.1f);
		g.rect(-g.width / 2, -g.height / 2, g.width, g.height);
		g.color(255);
		//		g.ellipse(0, sequencedValue, size);

		for (AnimatedEllipse e : _myEllipses) {
			e.draw(g);
		}

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAnimationCombinationTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}

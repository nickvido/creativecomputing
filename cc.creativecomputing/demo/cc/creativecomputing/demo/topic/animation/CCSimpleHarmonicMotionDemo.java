package cc.creativecomputing.demo.topic.animation;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;

/**
 * based on "Pendulum Waves" http://j.mp/lxZfTY including all numbers (0.85Hz for slowest pendulum, 1.083Hz for fastest)
 * if the above shortened link dies, try:
 * http://sciencedemonstrations.fas.harvard.edu/icb/icb.do?keyword=k16940&pageid=icb
 * .page80863&pageContentId=icb.pagecontent341734
 * &state=maximize&view=view.do&viewParam_name=indepth.html#a_icb_pagecontent341734
 * 
 * based on processing sketch by Memo Akten, May 2011. http://www.memo.tv
 * http://openprocessing.org/visuals/?visualID=28555
 * 
 * @author christianriekoff
 * 
 */
public class CCSimpleHarmonicMotionDemo extends CCApp {
	class Pendulum {
		// physical params
		int index;
		float freq; // oscilations per second

		// physical vars
		float _myPosition; // position of pendulum (-1...1)
		float vel; // current velocity
		float _myX;

		// contructor
		Pendulum(int _index, float _freq) {
			index = _index;
			_myX = CCMath.map(_index, 0, numPendulums - 1, pendulumSize / 2, width - pendulumSize / 2);
			freq = _freq;
			_myPosition = 1;
			vel = 0;
		}

		// update position and trigger sound if nessecary
		void update(float t) {
			float oldPos = _myPosition; // save old position
			float oldVel = vel; // save old direction

			_myPosition = CCMath.cos(freq * t * 2 * CCMath.PI); // calculate new position
			vel = _myPosition - oldPos; // calculte new direction

		}

		// draw
		void draw(float size) {
			g.color(255);
			g.ellipse(_myX, CCMath.map(_myPosition, -1, 1, size / 2, height - size / 2), size, size);
		}
	}

	final int numPendulums = 60;
	private List<Pendulum> _myPendulums;

	@CCControl(name = "time", min = 0, max = 100)
	private float _cTime = 0;

	// size to draw pendulum
	float pendulumSize;

	// -------------------------------------
	public void setup() {

		// create list of pendulums
		_myPendulums = new ArrayList<Pendulum>();
		pendulumSize = width * 1.0f / numPendulums;

		// loop through and init each pendulum
		for (int c = 0; c < numPendulums; c++) {
			// first pendulum oscillates 51 times per minute
			// each successive has one more oscillation per minute
			float freq = (51.0f + c) / 60.0f;

			// init pendulum instance
			_myPendulums.add(new Pendulum(c, freq));
		}

		addControls("app", "app", this);
	}

	float _myTime = 0;
	
	@Override
	public void update(float theDeltaTime) {
		_myTime += theDeltaTime * 0.5;
		for (Pendulum myPendulum : _myPendulums) {
			myPendulum.update(_myTime);
		}
	}

	@Override
	public void draw() {
		g.clear();

		g.translate(-width/2, -height/2);
		for (Pendulum myPendulum : _myPendulums) {
			myPendulum.draw(pendulumSize);
		}
	}

	@Override
	public void keyPressed(CCKeyEvent theEvent) {
//		_myTime = 0;
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSimpleHarmonicMotionDemo.class);
		myManager.settings().size(1400, 250);
		myManager.settings().vsync(true);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCMath;

public class CCRandomDemo extends CCApp {

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		CCMath.randomSeed(0);
		for(int i = 0; i < width;i++) {
			g.line(-width/ 2 + i,0,-width/ 2 + i,CCMath.random(-height/2, height/2));
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRandomDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.random.CCCurlNoise;
import cc.creativecomputing.math.util.CCArcball;

public class CCCurlNoiseTest extends CCApp {

	private CCCurlNoise _myCurlNoise;
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		_myCurlNoise = new CCCurlNoise();
		_myArcball = new CCArcball(this);
	}

	float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		_myTime+= theDeltaTime;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		for(int x = -width/2; x < width/2; x+=15) {
			for(int y = -height/2; y < height/2; y+=15) {
				CCVector3f myCurlNoise = _myCurlNoise.noise(x * 0.002f, y * 0.002f, _myTime * 0.05f).scale(25);
				g.line(x,y,0, x + myCurlNoise.x, y + myCurlNoise.y, myCurlNoise.z);
			}
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCurlNoiseTest.class);
		myManager.settings().size(1400, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


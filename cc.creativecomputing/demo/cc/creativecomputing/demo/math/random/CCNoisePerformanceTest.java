package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCFarbrauschNoise;
import cc.creativecomputing.math.signal.CCPerlinNoise;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.util.CCStopWatch;

public class CCNoisePerformanceTest extends CCApp {
	
	private CCFarbrauschNoise _myPerlinNoise;
	private CCPerlinNoise _myPerlinNoise2;
	private CCSimplexNoise _mySimplexNoise;
	
	private CCStopWatch _myStopWatch;
	
	public void setup() {
		g.clearColor(255);
		
		_myPerlinNoise = new CCFarbrauschNoise();
		_mySimplexNoise = new CCSimplexNoise();
		_myPerlinNoise2 = new CCPerlinNoise();
		
		_myStopWatch = CCStopWatch.instance();
	}

	public void draw() {
		for(int i = 0; i < 100000;i++){
			_myPerlinNoise.value(i, i, i);
		}
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCNoisePerformanceTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

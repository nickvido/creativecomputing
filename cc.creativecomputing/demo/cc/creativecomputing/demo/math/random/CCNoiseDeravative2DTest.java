package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCPerlinNoise;

public class CCNoiseDeravative2DTest extends CCApp {
	
	private CCPerlinNoise _myNoise;
	
	@CCControl(name = "scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "gain", min = 0, max = 1f)
	private float _cNoiseGain = 0;
	
	@CCControl(name = "bands", min = 1, max = 10f)
	private float _cNoiseBands = 0;
	
	@CCControl(name = "lacunarity", min = 0, max = 10f)
	private float _cLacunarity = 0;
	
	public void setup() {
		frameRate(10);
		_myNoise = new CCPerlinNoise();
		
		addControls("app", "app", this);
	}
	
	@Override
	public void update(float theDeltaTime) {
		_myNoise.scale(_cNoiseScale);
		_myNoise.bands(_cNoiseBands);
		_myNoise.gain(_cNoiseGain);
		_myNoise.lacunarity(_cLacunarity);
	}

	public void draw() {
		g.clearColor(255);
		g.clear();
		g.smooth();
		g.blend(CCBlendMode.BLEND);
		g.color(0,10);
		g.beginShape(CCDrawMode.LINES);
		for(float x = -width/2; x <= width/2;x+=1){
			for(float y = -height/2; y <= height/2;y+=1) {
				g.vertex(x,y);
				float[] myDNoise = _myNoise.values(x + width/2,y + height/2);
				g.vertex(x + (myDNoise[1] - 0.5f) * 160,y + (myDNoise[2] - 0.5f) * 160);
			}
		}
		g.endShape();
		
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCNoiseDeravative2DTest.class);
		myManager.settings().size(1400, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

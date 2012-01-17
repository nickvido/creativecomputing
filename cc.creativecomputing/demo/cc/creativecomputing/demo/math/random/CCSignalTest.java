package cc.creativecomputing.demo.math.random;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.signal.CCFarbrauschNoise;
import cc.creativecomputing.math.signal.CCPerlinNoise;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.signal.CCSinSignal;
import cc.creativecomputing.math.signal.CCWorleyNoise;

public class CCSignalTest extends CCApp {
	
	private List<CCSignal> _myNoises = new ArrayList<CCSignal>();
	
	public void setup() {
		_myNoises.add(new CCFarbrauschNoise());
		_myNoises.add(new CCPerlinNoise());
		_myNoises.add(new CCSimplexNoise());
		_myNoises.add(new CCWorleyNoise(new CCWorleyNoise.CCWorleyF1F0Formular()));
		_myNoises.add(new CCSinSignal());
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		
		frameRate(20);
	}

	public void draw() {
		g.clear();

		float noiseScale = mouseX/((float)width*10);
		float noiseOffset = mouseY/10f;
		
		CCColor myColor = new CCColor();
		
		float i = 0;
		
		for(CCSignal myNoise:_myNoises) {
			float myHue = i / _myNoises.size();
			myColor.setHSB(myHue, 1, 1);
			g.color(myColor);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(float x = -width/2; x <= width/2;x++){
				float y = (myNoise.value((x + width/2) * noiseScale + noiseOffset) - 0.5f) * height;
				g.vertex(x,y);
			}
			g.endShape();
			
			g.text(myNoise.getClass().getSimpleName(),-width/2 + 20,i * 20 - height/2 + 20);
			
			i++;
		}
		
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCSignalTest.class);
		myManager.settings().size(800, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

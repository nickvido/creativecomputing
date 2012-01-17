package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.text.CCVectorMeshText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCVectorFontMeshTextTest extends CCApp {
	
	private CCVectorMeshText _myText;
	private CCArcball _myArcball;
	
	public void setup() {
		frameRate(30);
		_myText = new CCVectorMeshText("Arial", 20);
		
		for(int i = 0; i < 1000;i++) {
			_myText.addText(i+"", CCVecMath.random3f(CCMath.random(1000)));
		}
		_myText.updateMesh();
		
		_myArcball = new CCArcball(this);
	}

	public void draw() {
		g.clear();
		g.color(255);
		g.noDepthTest();
		g.translate(0, 0, -1000);
		_myArcball.draw(g);
		_myText.draw(g);
		
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCVectorFontMeshTextTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}

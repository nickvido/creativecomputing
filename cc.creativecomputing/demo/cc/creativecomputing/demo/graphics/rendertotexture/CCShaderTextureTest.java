package cc.creativecomputing.demo.graphics.rendertotexture;

import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.math.CCMath;

public class CCShaderTextureTest extends CCApp {
	
	private CCShaderTexture _myShaderTexture;

	public void setup() {
		frameRate(30);
		
		_myShaderTexture = new CCShaderTexture(400, 400);
		g.pointSize(2);
		g.smooth();
		
//		g.debug();
	}

	public void draw() {

		g.clearColor(0);
		g.clear();
		_myShaderTexture.beginDraw();

		g.clearColor(255,0,0);
		g.clear();
		g.color(255);
		CCMath.randomSeed(0);
		for(int i = 0; i < 200;i++) {
			g.color(CCMath.random(),CCMath.random(),CCMath.random());
			g.ellipse(CCMath.random(400),CCMath.random(400),0,20,20);
		}
		g.rect(-200,-200, 50,50);
		_myShaderTexture.endDraw();
		
		FloatBuffer outputData0 = _myShaderTexture.getData(0);
		System.err.printf("toutput0\toutput1\toutput2\toutput3\n");
	    for (int i = 0; i < _myShaderTexture.width() * _myShaderTexture.height() * 3; i++)
	    	System.err.printf("t%.2f\t%.2f\t%.2f\n", outputData0.get(), outputData0.get(), outputData0.get());
	
		g.color(255);
		g.image(_myShaderTexture, 0,0,200,200);
//		g.texture(_myRenderBuffer);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(-200, -200, 0, 0f);
//		g.vertex( 200, -200, 1, 0f);
//		g.vertex( 200,  200, 1, 1);
//		g.vertex(-200,  200, 0, 1);
//		g.endShape();
//		g.noTexture();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderTextureTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

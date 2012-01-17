package cc.creativecomputing.demo.graphics.rendertotexture;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;

public class CCRenderTextureTest extends CCApp {
	
	private CCRenderTexture _myRenderBuffer;

	public void setup() {
		frameRate(30);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
//		myAttributes.samples(8);
		
		_myRenderBuffer = new CCRenderTexture(g, CCTextureTarget.TEXTURE_RECT, myAttributes, 400, 400);
		g.pointSize(2);
		g.smooth();
		
//		g.debug();
	}

	public void draw() {

		g.clearColor(0);
		g.clear();
		_myRenderBuffer.beginDraw();

		g.clearColor(255,0,0);
		g.clear();
		g.color(255);
		CCMath.randomSeed(0);
		for(int i = 0; i < 200;i++) {
			g.color(CCMath.random(),CCMath.random(),CCMath.random());
			g.ellipse(CCMath.random(-width/2,width/2),CCMath.random(-height/2,height/2),CCMath.random(200),20,20);
		}
		g.rect(-200,-200, 50,50);
		_myRenderBuffer.endDraw();
		
		g.color(255);
		g.image(_myRenderBuffer, 0,0,200,200);
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
		CCApplicationManager myManager = new CCApplicationManager(CCRenderTextureTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

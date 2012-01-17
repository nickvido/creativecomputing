package cc.creativecomputing.demo.graphics.shader.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

import com.jogamp.opengl.cg.CGparameter;

public class CCGeometryBufferDemo extends CCApp {
	
	private class Box{
		private float size;
		private CCVector3f position;
		
		private Box() {
			size = CCMath.random(15, 75);
			position = CCVecMath.random(-width/2, width/2, -height/2, height/2, -width/2, width/2);
		}
		
		void draw() {
			g.pushMatrix();
			g.translate(position);
			g.box(size);
			g.popMatrix();
		}
	}
	
	@CCControl(name = "near clip", min = 0, max = 50000)
	private float _cNearClip = 43.30127f;
	
	@CCControl(name = "far clip", min = 0, max = 5000)
	private float _cFarClip = 4330.127f;
	
	private CCGeometryBuffer _myRenderContext;
	
	private CCCGShader _myDepthBufferShader;
	private CGparameter _myDepthTextureParameter;
	
	private CCArcball _myArcball;
	
	private List<Box> _myBoxes = new ArrayList<Box>();

	public void setup() {
		frameRate(20);
		
		System.out.println(g.camera().near() +":" + g.camera().far());
		
		_myRenderContext = new CCGeometryBuffer(g,width,height);
		g.camera().far(1000);
		
		for(int i = 0; i< 100;i++) {
			_myBoxes.add(new Box());
		}
		g.debug();
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "cam", this);
	}
	
	public void update(final float theDeltaTime) {
//		g.camera().near(_cNearClip);
//		g.camera().far(_cFarClip);
	}

	public void draw() {
		g.clear();
		_myRenderContext.beginDraw();
		g.clearColor(0, 255);
		g.clear();
		_myArcball.draw(g);
		for(Box myBox:_myBoxes) {
			myBox.draw();
		}
		_myRenderContext.endDraw();
		
		g.texture(0,_myRenderContext.data(),1);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0f, 0f);
		g.vertex(-width / 2, -height / 2, 0);
		g.textureCoords(0f, 1f);
		g.vertex(-width / 2, height / 2, 0);
		g.textureCoords(1f, 1f);
		g.vertex(width / 2, height / 2, 0);
		g.textureCoords(1f, 0f);
		g.vertex(width / 2, -height / 2, 0);
		g.endShape();
		g.noTexture();
		
//		_myRenderContext.renderTexture().bindDepthTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGeometryBufferDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

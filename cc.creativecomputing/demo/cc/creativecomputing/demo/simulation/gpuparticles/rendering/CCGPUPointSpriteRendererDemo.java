package cc.creativecomputing.demo.simulation.gpuparticles.rendering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUPointSpriteRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCGPUPointSpriteRendererDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCTexture2D _myPointSpriteTexture;
	private CCGPUPointSpriteRenderer _myRenderer;
	
	@CCControl(name = "emit particles", min = 0, max = 500)
	private int _cEmitParticles = 50;
	
	@CCControl(name = "point size", min = 0, max = 50)
	private int _cPointSize = 5;

	public void setup() {
		_myArcball = new CCArcball(this);
		_myPointSpriteTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/spheres.png"));
		_myPointSpriteTexture.generateMipmaps(true);
		_myPointSpriteTexture.textureFilter(CCTextureFilter.LINEAR);
		_myPointSpriteTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(2,0,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myRenderer = new CCGPUPointSpriteRenderer(g,_myPointSpriteTexture,5, 1);
		_myRenderer.pointSize(3);
		_myParticles = new CCGPUQueueParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	private boolean _myPause = false;
	
	public void update(final float theDeltaTime){
		if(_myPause)return;
		
		_myTime += 1/30f * 0.5f;
		_myParticles.update(theDeltaTime);
		for(int i = 0; i < _cEmitParticles; i++){
			_myParticles.allocateParticle(
//				new CCVector3f(),
				new CCVector3f(-width/2,CCMath.random(-height/2, height/2),0),
				CCVecMath.random3f(10),
				10, false
			);
		}
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myRenderer.pointSize(_cPointSize);
		_myRenderer.fadeOut(false);
	}

	public void draw() {
		g.clearColor(255);
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255);
		g.blend(CCBlendMode.ALPHA);
		_myParticles.draw();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myParticles.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.getKeyCode()){
		case CCKeyEvent.VK_R:
			_myParticles.reset();
			break;
		case CCKeyEvent.VK_P:
			_myPause = !_myPause;
			break;
		case CCKeyEvent.VK_S:
			CCScreenCapture.capture("export/db01/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUPointSpriteRendererDemo.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUCurveField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesCurveFlowFieldDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUCurveField _myCurveField = new CCGPUCurveField(1.0f, 1.0f);
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(10,0,0));
	private CCGPUAttractor _myAttractor = new CCGPUAttractor(new CCVector3f(), 0, 0);
	
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float _cFieldStrength = 0;
	
	@CCControl(name = "attractor strength", min = -10, max = 10)
	private float _cAttractorStrength = 0;
	
	@CCControl(name = "attractor radius", min = 0, max = 300)
	private float _cAttractorRadius = 0;
	
	@CCControl(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCControl(name = "curve strength", min = 0, max = 10)
	private float _cCurveStrength = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cCurveSpeed = 0;

	@CCControl(name = "prediction", min = 0, max = 1)
	private float _cPrediction = 0;
	
	@CCControl(name = "curveNoiseOffset", min = 0, max = 1)
	private float _cCurveNoiseOffset = 0;
	
	@CCControl(name = "curveNoiseScale", min = 0, max = 1)
	private float _cCurveNoiseScale = 0;
	
	@CCControl(name = "curveOutputScale", min = 0, max = 200)
	private float _cCurveOuputScale = 0;
	
	@CCControl(name = "curveRadius", min = 0, max = 400)
	private float _cCurveRadius = 0;
	
	@CCControl(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myCurveField);
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 2000; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-_cEmitRadius, _cEmitRadius),CCMath.random(-50, 50)),
				CCVecMath.random3f(10),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
		
		_myGravity.strength(_cGravityStrength);
		
		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myAttractor.strength(_cAttractorStrength);
		_myAttractor.radius(_cAttractorRadius);
		_myAttractor.position().x = mouseX - width/2;
		_myAttractor.position().y = height/2 - mouseY;
		
		_myCurveField.strength(_cCurveStrength);
		_myCurveField.curveOutputScale(_cCurveOuputScale);
		_myCurveField.curveSpeed(_cCurveSpeed);
		_myCurveField.curveScale(_cCurveNoiseScale / 100);
		_myCurveField.noiseRadius(_cCurveRadius);
		
		_myCurveField.prediction(_cPrediction);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255,50);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		_myParticles.draw();
//		g.noSmooth();
//		g.noPointSprite();
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
		case CCKeyEvent.VK_S:
			CCScreenCapture.capture("export/db04/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesCurveFlowFieldDemo.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

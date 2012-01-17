package cc.creativecomputing.demo.simulation.gpuparticles.openni;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserJointType;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserLimb;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUFloorConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUCurveField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesCurveFlowFieldDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUCurveField _myCurveField = new CCGPUCurveField(1.0f, 1.0f);
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(10,0,0));
	private CCGPUAttractor _myAttractor0 = new CCGPUAttractor(new CCVector3f(), 0, 0);
	private CCGPUAttractor _myAttractor1 = new CCGPUAttractor(new CCVector3f(), 0, 0);
	private CCGPUFloorConstraint _myFloorConstraint;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	
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
	
	@CCControl(name = "floor Y", min = -300, max = 300)
	private float _cFloorY = 0;
	
	@CCControl(name = "resilience", min = 0, max = 1f)
	private float _cResilience = 1f;
	
	@CCControl(name = "friction", min = 0, max = 1f)
	private float _cFriction = 1f;
	
	@CCControl(name = "user scale", min = 0, max = 2)
	private float _cUserScale = 0;
	
	@CCControl(name = "user translate", min = -5000, max = 5000)
	private float _cUserTranslate = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myCurveField);
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor0);
		myForces.add(_myAttractor1);
		
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(_myFloorConstraint = new CCGPUFloorConstraint(-300, 0.3f, 1f, 0.01f));
		
		_myParticles = new CCGPUQueueParticles(g, myForces, myConstraints, 700,700);
		
		addControls("app", "app", this);
		
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		// enable skeleton generation for all joints
		_myUserGenerator = _myOpenNI.createUserGenerator();

		g.strokeWeight(3);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.25f;
		for(int i = 0; i < 1000; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-_cCurveRadius, _cCurveRadius),CCMath.random(-50, 50)),
				CCVecMath.random3f(10),
				5, false
			);
		}
		_myParticles.update(theDeltaTime * 0.5f);
		
		_myGravity.strength(_cGravityStrength);
		
		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		if( _myUserGenerator.user().size() > 0) {
			CCOpenNIUser myUser = _myUserGenerator.user().iterator().next();
			CCVector3f myVectorPos = myUser.joint(CCUserJointType.LEFT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
			_myAttractor0.strength(_cAttractorStrength);
			_myAttractor0.radius(_cAttractorRadius);
			_myAttractor0.position().set(myVectorPos);
			
			myVectorPos = myUser.joint(CCUserJointType.RIGHT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
			_myAttractor1.strength(_cAttractorStrength);
			_myAttractor1.radius(_cAttractorRadius);
			_myAttractor1.position().set(myVectorPos);
		}else {
			_myAttractor0.strength(0);
			_myAttractor1.strength(0);
		}
		
		_myCurveField.strength(_cCurveStrength);
		_myCurveField.curveOutputScale(_cCurveOuputScale);
		_myCurveField.curveSpeed(_cCurveSpeed);
		_myCurveField.curveScale(_cCurveNoiseScale / 100);
		_myCurveField.noiseRadius(_cCurveRadius);
		
		_myCurveField.prediction(_cPrediction);
		
		_myFloorConstraint.y(_cFloorY);
		_myFloorConstraint.resilience(_cResilience);
		_myFloorConstraint.friction(_cFriction);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend();
		g.depthMask();
		g.color(255, 0, 0);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			g.beginShape(CCDrawMode.LINES);
			for(CCUserLimb myLimb:myUser.limbs()) {
				g.vertex(myLimb.joint1().position().clone().scale(_cUserScale).add(0,0,_cUserTranslate));
				g.vertex(myLimb.joint2().position().clone().scale(_cUserScale).add(0,0,_cUserTranslate));
			}
			g.endShape();
		}
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		g.color(255,50);
		g.noDepthMask();
		_myParticles.draw();
		//

//		_myDepthGenerator.drawDepthMap(g);

		
		
//		_myOpenNI.drawCamFrustum(g);
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myParticles.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesCurveFlowFieldDemo.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

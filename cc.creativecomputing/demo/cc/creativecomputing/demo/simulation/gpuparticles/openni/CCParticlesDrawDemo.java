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
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUCombinedForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTimeForceBlend;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesDrawDemo extends CCApp {
	
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float _cFieldStrength = 0;
	
	@CCControl(name = "drag strength", min = 0, max = 1)
	private float _cDragStrength = 0;
	
	@CCControl(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCControl(name = "gravity x", min = -10, max = 10)
	private float _cGravityX = 0;
	
	@CCControl(name = "gravity y", min = -10, max = 10)
	private float _cGravityY = 0;
	
	@CCControl(name = "gravity z", min = -10, max = 10)
	private float _cGravityZ = 0;
	
	@CCControl(name = "floor Y", min = -300, max = 300)
	private float _cFloorY = 0;
	
	@CCControl(name = "resilience", min = 0, max = 1f)
	private float _cResilience = 1f;
	
	@CCControl(name = "user scale", min = 0, max = 2)
	private float _cUserScale = 0;
	
	@CCControl(name = "user translate", min = -5000, max = 5000)
	private float _cUserTranslate = 0;
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
//	private CCGPUCurveField _myCurveField = new CCGPUCurveField(1.0f, 1.0f);
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(0,10,0));
	private CCGPUGravity _myGravity1 = new CCGPUGravity(new CCVector3f(0,0,0));
	private CCGPUTimeForceBlend _myTimeBlendForce;
	private CCGPUViscousDrag _myDrag;
	
	private CCGPUCombinedForce _myCombineForce;
	
	private CCGPUFloorConstraint _myFloorConstraint;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;

	public void setup() {
		_myArcball = new CCArcball(this);

		final List<CCGPUForce> myCombinedForces = new ArrayList<CCGPUForce>();
		myCombinedForces.add(_myGravity);
		myCombinedForces.add(_myForceField);
		_myCombineForce = new CCGPUCombinedForce(myCombinedForces);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myDrag = new CCGPUViscousDrag(0.3f));
//		myForces.add(_myCurveField);
//		myForces.add(_myGravity);
//		myForces.add(_myForceField);
//		myForces.add(_myAttractor0);
//		myForces.add(_myAttractor1);
		
		_myTimeBlendForce = new CCGPUTimeForceBlend(0,4, _myGravity1, _myCombineForce);
		_myTimeBlendForce.blend(0, 1f);
		myForces.add(_myTimeBlendForce);
		
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(_myFloorConstraint = new CCGPUFloorConstraint(-300, 0.3f, 1f, 0.01f));
		
		_myParticles = new CCGPUQueueParticles(g, myForces, myConstraints, 700,700);
		
		addControls("app", "app", this);
		
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		// enable skeleton generation for all joints
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		g.strokeWeight(3);
	}
	
	private float _myTime = 0;
	
	private CCVector3f _myLast1;
	private CCVector3f _myLast2;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		if( _myUserGenerator.user().size() > 0) {
			CCOpenNIUser myUser = _myUserGenerator.user().iterator().next();
			CCVector3f myVectorPos1 = myUser.joint(CCUserJointType.LEFT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
			CCVector3f myVectorPos2 = myUser.joint(CCUserJointType.RIGHT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
			
			if(_myLast1 != null) {
//				CCVector3f myVel1 = CCVecMath.subtract(myVectorPos1, _myLast1).scale(10);
				for(int i = 0; i < 300;i++) {
					CCVector3f myParticlePos1 = CCVecMath.blend(CCMath.random(), myVectorPos1, _myLast1);
					_myParticles.allocateParticle(
							myParticlePos1,
						CCVecMath.random3f(20),//.add(myVel1),
						10, false
					);
					CCVector3f myParticlePos2 = CCVecMath.blend(CCMath.random(), myVectorPos2, _myLast2);
					_myParticles.allocateParticle(
						myParticlePos2,
						CCVecMath.random3f(20),
						10, false
					);
				}
			}
			
			_myLast1 = myVectorPos1;
			_myLast2 = myVectorPos2;
		}else {
			_myLast1 = null;
			_myLast2 = null;
		}
		
		_myParticles.update(theDeltaTime);
		
		_myDrag.strength(_cDragStrength);
		
		_myGravity.strength(_cGravityStrength);
		_myGravity.direction().set(_cGravityX, _cGravityY, _cGravityZ);
		
		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myFloorConstraint.y(_cFloorY);
		_myFloorConstraint.resilience(_cResilience);
		
//		if( _myUserGenerator.user().size() > 0) {
//			CCUser myUser = _myUserGenerator.user().iterator().next();
//			CCVector3f myVectorPos = myUser.joint(CCUserJointType.LEFT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
//			_myAttractor0.strength(_cAttractorStrength);
//			_myAttractor0.radius(_cAttractorRadius);
//			_myAttractor0.position().set(myVectorPos);
//			
//			myVectorPos = myUser.joint(CCUserJointType.RIGHT_HAND).position().clone().scale(_cUserScale).add(0,0,_cUserTranslate);
//			_myAttractor1.strength(_cAttractorStrength);
//			_myAttractor1.radius(_cAttractorRadius);
//			_myAttractor1.position().set(myVectorPos);
//		}else {
//			_myAttractor0.strength(0);
//			_myAttractor0.strength(1);
//		}
		
//		_myCurveField.strength(_cCurveStrength);
//		_myCurveField.curveOutputScale(_cCurveOuputScale);
//		_myCurveField.curveSpeed(_cCurveSpeed);
//		_myCurveField.curveScale(_cCurveNoiseScale / 100);
//		_myCurveField.noiseRadius(_cCurveRadius);
//		
//		_myCurveField.prediction(_cPrediction);
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
		//

//		_myDepthGenerator.drawDepthMap(g);

		g.color(255, 0, 0);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			g.beginShape(CCDrawMode.LINES);
			for(CCUserLimb myLimb:myUser.limbs()) {
				g.vertex(myLimb.joint1().position().clone().scale(_cUserScale).add(0,0,_cUserTranslate));
				g.vertex(myLimb.joint2().position().clone().scale(_cUserScale).add(0,0,_cUserTranslate));
			}
			g.endShape();
		}
		
//		_myOpenNI.drawCamFrustum(g);
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myParticles.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesDrawDemo.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

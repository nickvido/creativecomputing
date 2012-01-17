package cc.creativecomputing.demo.simulation.gpuparticles.openni;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserLimb;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
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

public class CCParticlesDrawBodyDemo extends CCApp {
	
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
	private CCOpenNISceneAnalyzer _mySceneGenerator;
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
		_myParticles.update(0);
		addControls("app", "app", this);
		
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_mySceneGenerator = _myOpenNI.createSceneAnalyzer();
		// enable skeleton generation for all joints
		_myUserGenerator = _myOpenNI.createUserGenerator();
		
		_myOpenNI.start();
//
//		g.strokeWeight(1);
	}
	
	private float _myTime = 0;
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
			ShortBuffer mySceneRaw = _mySceneGenerator.rawData();
			for(int i = 0; i < 3000;i++) {
				int myIndex = (int)CCMath.random(_myDepthGenerator.depthMapRealWorld(1).length);
				int myScene = mySceneRaw.get(myIndex);
				if(myScene <= 0)continue;
				CCVector3f rawData = _myDepthGenerator.depthMapRealWorld(1)[myIndex].clone().scale(_cUserScale).add(0,0,_cUserTranslate);
				_myParticles.allocateParticle(
					rawData,
					new CCVector3f(),//CCVecMath.random3f(20),//.add(myVel1),
					10, false
				);
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
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255,50);
		g.blend();
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
		
g.image(_mySceneGenerator.texture(), 0, 0);
		
		System.out.println(frameRate+":"+_mySceneGenerator.rawData());
		
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesDrawBodyDemo.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}

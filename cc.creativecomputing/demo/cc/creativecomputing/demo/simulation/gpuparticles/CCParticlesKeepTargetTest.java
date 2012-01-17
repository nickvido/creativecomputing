package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup.CCGPUTargetMaskSetupPlane;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesKeepTargetTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	private List<CCVector3f> _myEmitters = new ArrayList<CCVector3f>();

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-1,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
		
		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("demo/particles/squarepusher.png"),1,CCGPUTargetMaskSetupPlane.XY );
		_myTargetMaskSetup.keepTargets(true);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		
		for(int i = 0; i < 10;i++) {
			_myEmitters.add(new CCVector3f(CCMath.random(-100,100),400));
		}
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		for(int i = 0; i < 5000; i++){
			int myIndex = _myParticles.nextFreeId();
			if(myIndex < 0)break;
			
			CCVector3f myTarget = _myTargetMaskSetup.target(myIndex);
			float myMinDistance = Float.MAX_VALUE;
			CCVector3f myNearestEmitter = _myEmitters.get(0);
			
			for(CCVector3f myEmitter:_myEmitters) {
				float myDistanceSquared = myEmitter.distanceSquared(myTarget);
		
				if(myDistanceSquared < myMinDistance) {
					myNearestEmitter = myEmitter;
					myMinDistance = myDistanceSquared;
				}
			}
//			System.out.println(myNearestEmitter+":"+myTarget);
			_myParticles.allocateParticle(
				myNearestEmitter,
				CCVecMath.random3f(10),
				15, false
			);
			
			
		}
		float myBlend = mouseX / (float)width;
		_myForceField.strength(1 - myBlend);
		_myTargetForce.strength(myBlend);
		
		_myTime += theDeltaTime * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend(CCBlendMode.BLEND);
		g.color(255,50);
		_myParticles.draw();
		g.noTexture();
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesKeepTargetTest.class);
		myManager.settings().size(800, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

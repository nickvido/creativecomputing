package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCParticlesNoiseFlowFieldTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 2000; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),CCMath.random(-height/2, height/2)),
				CCVecMath.random3f(10),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
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
	
	public void keyPressed(CCKeyEvent theEvent) {
		_myParticles.reset();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesNoiseFlowFieldTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
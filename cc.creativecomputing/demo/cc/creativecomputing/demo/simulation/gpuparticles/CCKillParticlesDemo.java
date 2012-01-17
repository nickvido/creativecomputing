package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
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

public class CCKillParticlesDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));

	public void setup() {
		hideControls();
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0.5f,0,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 3000; i++){
			_myParticles.allocateParticle(
				new CCVector3f(-400,0,0),
				CCVecMath.random3f(10),
				6, true
			);
		}
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f + 0.005f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,30);
		
		_myParticles.draw();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myParticles.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent){
		switch (theEvent.keyCode()) {
		case CCKeyEvent.VK_K:
			_myParticles.kill();
			break;

		default:
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCKillParticlesDemo.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

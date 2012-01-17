package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;

public class CCParticlesGravityDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-1,0)));
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),600,600);
		_myParticles.reset();
		
		frameRate(35);
	}
	
	float angle = 0;
	
	public void update(final float theDeltaTime){
		for(int i = 0; i < 800; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-100,100), height/2,CCMath.random(-100,100)),
				new CCVector3f(0,00,0),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		
		g.noDepthTest();
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(0, 0, -1000);
		g.rotateY(angle);
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.draw();
		
		

		g.popMatrix();
		
		g.text(frameRate,0,-350);
		
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 'f':
			frameRate(-1);
			break;
		case 'd':
			frameRate(30);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesGravityDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

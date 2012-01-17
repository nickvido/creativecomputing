package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesAttractorTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private List<CCGPUAttractor> _myAttractors = new ArrayList<CCGPUAttractor>();
	private boolean _myDrawEmitters = true;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-1,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		for(int i = 0; i < 9;i++){
			CCGPUAttractor myAttractor = new CCGPUAttractor(
				CCVecMath.random(-g.width/2, g.width/2, -g.height/2, g.height/2,-500,500),
				CCMath.random(15,30),1000
			);
			myForces.add(myAttractor);
			_myAttractors.add(myAttractor);
		}
		
		_myParticles = new CCGPUQueueParticles(g,myForces, new ArrayList<CCGPUConstraint>(),700,700);
		_myParticles.reset();
	}
	
	float angle = 0;
	
	public void update(final float theDeltaTime){
		angle += theDeltaTime * 30;
		for(int i = 0; i < 1600; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-500,500), height/2,CCMath.random(-500,500)),
				new CCVector3f(CCMath.random(-20,20),CCMath.random(-20,20),CCMath.random(-2,2)),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		
		g.noDepthTest();
		g.clear();
		g.color(255,0,0);
		g.pushMatrix();
//		g.translate(0, 0, -1000);
		g.rotateY(angle);
//		_myArcball.draw(g);
		g.blend();
		g.color(255,50);
		_myParticles.draw();
		
		if(_myDrawEmitters){
			g.color(255,10);
			g.polygonMode(CCPolygonMode.LINE);
			for(CCGPUAttractor myAttractor:_myAttractors){
				g.pushMatrix();
				g.translate(myAttractor.position());
				g.sphere(myAttractor.radius()/2);
				g.popMatrix();
			}
			g.polygonMode(CCPolygonMode.FILL);
		}

		g.popMatrix();
		
		g.blend();
		
		g.color(255);
		g.text(frameRate,0,0);
		g.text(_myParticles.particlesInUse(),0,-20);
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 'a':
			for(CCGPUAttractor myAttractor:_myAttractors){
				myAttractor.position(CCVecMath.random(-g.width/2, g.width/2, -g.height/2, g.height/2,-200,200));
				myAttractor.strength(CCMath.random(-20,40));
				myAttractor.radius(CCMath.random(500,1000));
			}
			break;
		case 'd':
			_myDrawEmitters = !_myDrawEmitters;
			break;
		case 's':
			CCScreenCapture.capture("export/attractor/attractor"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesAttractorTest.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

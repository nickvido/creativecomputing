package cc.creativecomputing.demo.simulation.gpuparticles.rendering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQuadParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCQuadParticlesTest extends CCApp {
	
	private CCGPUQuadParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,10,new CCVector3f(100,20,30));

	public void setup() {
		hideControls();
//		fixUpdateTime(1/25f);
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();

		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		_myParticles = new CCGPUQuadParticles(g,myForces, new ArrayList<CCGPUConstraint>(),500,500);
		g.smooth();
		
//		g.lights();
//		g.light(new CCDirectionalLight(0,0,1,1,-0.5f,0));
//		g.light(new CCDirectionalLight(1,0,0,0,-0.5f,1));
	}
	
	private float _myTimer = 0;
	
	public void update(final float theDeltaTime){
		_myTimer += theDeltaTime;
		for(int i = 0; i < 1000; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-20,20), CCMath.random(-20,20),CCMath.random(-20,20)),
				new CCVector3f(CCMath.random(-20,20),CCMath.random(-20,20),CCMath.random(-2,2)),
				30, false
			);
		}
		_myParticles.update(theDeltaTime*0.4f);
		_myParticles.quadSize(4);
		_myForceField.noiseOffset(new CCVector3f(_myTimer*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTimer * 0.5f)+1) * 0.0025f+0.005f);
	}

	public void draw() {
		g.noDepthTest();
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(0, 0, 0);
		g.rotateY(_myTimer);
		_myArcball.draw(g);
		g.blend(CCBlendMode.BLEND);
		g.color(255);
		_myParticles.draw();
		
		g.popMatrix();
		
		g.blend();
		
		g.color(255);
		g.text(frameRate + ":" + _myParticles.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/quad/quad"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCQuadParticlesTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

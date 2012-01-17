package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTerrainForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCTerrainForceDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCTexture2D _myTexture;
	private CCGPUTerrainForce _myTerrainForce;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0.1,0,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/heightmap.png"), CCTextureTarget.TEXTURE_RECT);
		_myTerrainForce = new CCGPUTerrainForce(
			_myTexture,
			new CCVector3f(4f,300,4f), 
			new CCVector3f(200, 00, 100)
		);
		myForces.add(_myTerrainForce);
	
		_myParticles = new CCGPUQueueParticles(g,myForces,myConstraints,800,800);
		g.smooth();
		addControls(CCTerrainForceDemo.class);
	}
	
	float angle = 0;
	
	public void update(final float theDeltaTime){
	
		angle += theDeltaTime * 30;
		for(int i = 0; i < 100; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(-400,400), height/2,CCMath.random(-400,400)),
				new CCVector3f(CCVecMath.random3f(20)),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		
		g.noDepthTest();
		g.clear();
		g.color(255,50);
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,25);
		_myParticles.draw();
		

		g.popMatrix();
		
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/heightmap/heightmap"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTerrainForceDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}

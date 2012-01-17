package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUFloorConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCEmitFountainDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	
	private CCGPUGravity _myGravity;
	
	@CCControl(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCControl(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;
	
	@CCControl(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;
	
	@CCControl(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;
	
	@CCControl(name = "random pos", min = 0, max = 10)
	private float _cRandomPos = 3f;
	
	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;
	
	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;
	
	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;
	
	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	private CCGPUFloorConstraint _myFloorContraint;
	
	private CCGPUForceField _myForceField;
	
	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;
	
	@CCControl(name = "n speed", min = 0, max = 3)
	private float _cNSpeed = 0;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myGravity = new CCGPUGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCGPUForceField(0.01f, 1f, new CCVector3f()));
//		myForces.add(new CCGPUViscousDrag(0.2f));
		
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(_myFloorContraint = new CCGPUFloorConstraint(-400, 1.0f, 0f, 0.1f));
		
		_myParticles = new CCGPUQueueParticles(g, myForces, myConstraints, 700, 700);
		
		addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}
	
	float _myOffset = 0;

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < _cEmit;i++){
			_myParticles.allocateParticle(new CCVector3f(0, -300, 0).add(CCVecMath.random3f(CCMath.random(_cRandomPos))), new CCVector3f(0,_cInitVel,0).add(CCVecMath.random3f(CCMath.random(_cRandomVel))), _cLifeTime);
		}
		
		_myOffset += theDeltaTime * _cNSpeed;
		
		_myGravity.direction().set(_cX, _cY,_cZ);
		_myGravity.strength(_cGStrength);
		
		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		_myForceField.noiseOffset(new CCVector3f(0,0,_myOffset));
		
		_myParticles.update(theDeltaTime * 2);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.noDepthTest();
		g.color(1f, _cAlpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
		g.noBlend();
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.getKeyCode()){
		case CCKeyEvent.VK_R:
			_myParticles.reset();
			break;
		case CCKeyEvent.VK_S:
			CCScreenCapture.capture("export/db02/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEmitFountainDemo.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}


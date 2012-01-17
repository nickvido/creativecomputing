package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUFloorConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;

public class CCEmitDemo extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	
	private CCGPUGravity _myGravity;
	
	@CCControl(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;
	
	@CCControl(name = "init vel", min = 0, max = 20)
	private float _cInitVel = 3f;
	
	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;
	
	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;
	
	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;
	
	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;
	
	private CCGPUFloorConstraint _myFloorContraint;
	
	private CCGPUForceField _myForceField;
	
	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;
	
	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;

	@Override
	public void setup() {
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myGravity = new CCGPUGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCGPUForceField(0.01f, 1f, new CCVector3f()));
		myForces.add(new CCGPUViscousDrag(0.2f));
		
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myConstraints.add(_myFloorContraint = new CCGPUFloorConstraint(-400, 1.0f, 0f, 0.1f));
		
		_myParticles = new CCGPUQueueParticles(g, myForces, myConstraints, 800, 800);
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		float x = mouseX - width/2;
		float y = height/2 - mouseY;
		for(int i = 0; i < 1000;i++){
			_myParticles.allocateParticle(new CCVector3f(x, y, 0), new CCVector3f(CCVecMath.random3f(CCMath.random(_cInitVel))), _cLifeTime);
		}
		
		_myGravity.direction().set(_cX, _cY,_cZ);
		_myGravity.strength(_cGStrength);
		
		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		
		g.noDepthTest();
		g.color(255, 50);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		
		g.noBlend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEmitDemo.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}


package cc.creativecomputing.particles.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.particles.CCParticles;
import cc.creativecomputing.particles.forces.CCAttractor;
import cc.creativecomputing.particles.forces.CCGravity;
import cc.creativecomputing.particles.forces.CCViscousDrag;

public class CCParticlesDemo extends CCApp {
	
	private CCParticles _myParticles;
	private CCArcball _myArcball;

	private CCGravity _myGravity;
	private CCViscousDrag _myViscousDrag;
	private CCAttractor _myAttractor;
	
	@Override
	public void setup() {
//		frameRate(20);
		_myParticles = new CCParticles(g,50000);
		
		_myParticles.addForce(_myGravity = new CCGravity());
		_myParticles.addForce(_myViscousDrag = new CCViscousDrag());
		_myParticles.addForce(_myAttractor = new CCAttractor());
		
		addControls("forces", "gravity", _myGravity);
		addControls("forces", "viscousDrag", _myViscousDrag);
		addControls("forces", "attractor", _myAttractor);
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 1500;i++){
			_myParticles.emiter().allocateParticle(CCVecMath.random3f(100), CCVecMath.random3f(30), CCMath.random(10));
		}
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myParticles.draw(g);
		System.out.println(frameRate);
		
//		CCScreenCapture.capture("export/openclparticles01/particles_", width, height);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesDemo.class);
		myManager.settings().size(1200, 700);
		myManager.settings().vsync(false);
		myManager.start();
	}
}

package cc.creativecomputing.particles.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.particles.CCParticles;
import cc.creativecomputing.particles.forces.CCNoiseField;
import cc.creativecomputing.particles.forces.CCGravity;
import cc.creativecomputing.particles.forces.CCViscousDrag;

public class CCNoiseFieldDemo extends CCApp {
	
	private CCParticles _myParticles;
	private CCArcball _myArcball;

	private CCGravity _myGravity;
	private CCViscousDrag _myViscousDrag;
	private CCNoiseField _myField;
	
	@Override
	public void setup() {
		frameRate(20);
		_myParticles = new CCParticles(g,150000);
		
		_myParticles.addForce(_myGravity = new CCGravity());
		_myParticles.addForce(_myViscousDrag = new CCViscousDrag());
		_myParticles.addForce(_myField = new CCNoiseField());
		
		addControls("forces", "gravity", _myGravity);
		addControls("forces", "viscousDrag", _myViscousDrag);
		addControls("forces", "field", _myField);
		
		_myArcball = new CCArcball(this);
		
//		fixUpdateTime(1f/30);
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 1000; i++){
			_myParticles.emiter().allocateParticle(
				new CCVector3f(CCVecMath.random3f(20)),
				new CCVector3f(CCVecMath.random3f(2)),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		g.blend(CCBlendMode.ADD);
		g.color(255,100);
		_myParticles.draw(g);
		System.out.println(frameRate);
		
//		CCScreenCapture.capture("export/openclparticles01/particles_", width, height);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseFieldDemo.class);
		myManager.settings().size(1200, 700);
		myManager.settings().antialiasing(8);
		myManager.settings().vsync(false);
		myManager.start();
	}
}

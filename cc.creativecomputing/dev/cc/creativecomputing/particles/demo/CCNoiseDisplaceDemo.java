package cc.creativecomputing.particles.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.particles.CCParticles;
import cc.creativecomputing.particles.forces.CCAttractor;
import cc.creativecomputing.particles.forces.CCNoiseDisplace;
import cc.creativecomputing.particles.forces.CCTextureDisplace;
import cc.creativecomputing.particles.forces.CCGravity;
import cc.creativecomputing.particles.forces.CCViscousDrag;

public class CCNoiseDisplaceDemo extends CCApp {
	
	private CCParticles _myParticles;
	private CCArcball _myArcball;

	private CCGravity _myGravity;
	private CCViscousDrag _myViscousDrag;
	private CCNoiseDisplace _myDisplace;
	
	@Override
	public void setup() {
//		frameRate(20);
		_myParticles = new CCParticles(g,500000);
		
		_myParticles.addForce(_myGravity = new CCGravity());
		_myParticles.addForce(_myViscousDrag = new CCViscousDrag());
		_myParticles.addForce(_myDisplace = new CCNoiseDisplace());
		
		addControls("forces", "gravity", _myGravity);
		addControls("forces", "viscousDrag", _myViscousDrag);
		addControls("forces", "displace", _myDisplace);
		
		_myArcball = new CCArcball(this);
		
//		fixUpdateTime(1f/30);
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 1000; i++){
			_myParticles.emiter().allocateParticle(
				new CCVector3f(CCMath.random(-400,400), 0,CCMath.random(-400,400)),
				new CCVector3f(CCVecMath.random3f(20)),
				10, false
			);
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
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseDisplaceDemo.class);
		myManager.settings().size(1200, 700);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

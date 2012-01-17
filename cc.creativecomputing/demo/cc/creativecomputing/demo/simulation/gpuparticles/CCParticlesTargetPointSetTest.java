package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetPointSetSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.util.CCStringUtil;

public class CCParticlesTargetPointSetTest extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	
	private CCGPUTargetPointSetSetup _myTargetSetup;
	
	private CCGPUGravity _myGravity;
	
	@CCControl(name = "gravity x", min = -1, max = 1)
	private float _cGravityX = 0;
	@CCControl(name = "gravity y", min = -1, max = 1)
	private float _cGravityY = 0;
	@CCControl(name = "gravity z", min = -1, max = 1)
	private float _cGravityZ = 0;
	@CCControl(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCControl(name = "target strength", min = 0, max = 1)
	private float _cTargetStrength = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float _cNoiseStrength = 0;
	@CCControl(name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		myForces.add(_myGravity = new CCGPUGravity(new CCVector3f()));
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 600,600);
		
		_myTargetSetup = new CCGPUTargetPointSetSetup(1f);
		loadPoints("demo/city.csv.gz");
		_myTargetForce.addTargetSetup(_myTargetSetup);
		
		for(int i = 0; i < 600 * 600; i++){
			_myParticles.allocateParticle(
				new CCVector3f(0,0,0),
				CCVecMath.random3f(10),
				10, true
			);
		}
		
		addControls("app", "app", this);
	}
	
	private void loadPoints(String path) {
		String[] raw = CCIOUtil.loadStrings(path);
		for (int i = 0; i < raw.length; i++) {
			String[] thisLine = CCStringUtil.split(raw[i], ",");
			_myTargetSetup.points().add(new CCVector3f(
				Float.parseFloat(thisLine[0]) / 1000,
				Float.parseFloat(thisLine[1]) / 1000,
				Float.parseFloat(thisLine[2]) / 1000
			));

			// colors[i*4] = new Float(thisLine[3]).floatValue()/3f ;
			// colors[i*4+1] = new Float(thisLine[3]).floatValue()/3f ;
			// colors[i*4+2] = 0f ;
			// colors[i*4+3] = 100f ;

		}
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		
		float myBlend = mouseX / (float)width;
//		_myForceField.strength(1 - myBlend);
		_myTargetForce.strength(myBlend);
		
		_myTime += 1/30f * _cNoiseSpeed;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		
		_myGravity.direction().set(_cGravityX, _cGravityY, _cGravityZ);
		_myGravity.strength(_cGravityStrength);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend();
		g.color(255,50);
		_myParticles.draw();
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTargetPointSetTest.class);
		myManager.settings().size(1024, 1024);
		myManager.settings().display(1);
		myManager.settings().location(0, 0);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

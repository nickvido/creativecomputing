package cc.creativecomputing.demo.simulation.gpuparticles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUNearestGridPositionTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticlesNearestTargetForceDebug extends CCApp {
	
	private CCGPUQueueParticles _myParticles;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUNearestGridPositionTargetForce _myTargetForce = new CCGPUNearestGridPositionTargetForce(10,10);
	
	@CCControl(name = "texture offset x", min = -400, max = 400)
	private float _cTextureOffsetX = 0;
	
	@CCControl(name = "texture offset y", min = -400, max = 400)
	private float _cTextureOffsetY = 0;
	
	@CCControl(name = "scale x", min = 0.1f, max = 4)
	private float _cTextureScaleX = 1;
	
	@CCControl(name = "scale y", min = 0.1f, max = 4)
	private float _cTextureScaleY = 1;
	
	@CCControl(name = "blend", min = 0f, max = 1f)
	private float _cBlend = 0;
	
	@CCControl(name = "lookAhead", min = -1, max = 1)
	private float _cLookAhead = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-1,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		
		_myParticles = new CCGPUQueueParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 10,10);
		
		frameRate(20);
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	private boolean _myDoDebug = false;
	private boolean _myDoNextFrame = false;
	
	public void update(final float theDeltaTime){
		if(_myDoDebug && !_myDoNextFrame)return;
		
		for(int i = 0; i < 1; i++){
			_myParticles.allocateParticle(
				new CCVector3f(CCMath.random(0, 10),height/2,0),
				CCVecMath.random3f(10),
				10
			);
		}
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.textureOffset(_cTextureOffsetX, _cTextureOffsetY);
		_myTargetForce.textureScale(_cTextureScaleX, _cTextureScaleY);
		
		_myForceField.strength(1 - _cBlend);
		_myTargetForce.strength(_cBlend);
//		_myTargetForce.center(new CCVector3f(0,height/2 - mouseY,0));
		
		_myTime += 1/30f * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		
		if(_myDoDebug) {
			System.out.println("#### PARTICLE INFOS ######");
			
			FloatBuffer myData = _myTargetForce.particleTargetInfos().getData();
			while(myData.hasRemaining()){
				for(int i = 0; i < 10;i++){
					System.out.print("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
				}
				System.out.println();
			}
			_myDoNextFrame = false;
			
			System.out.println("#### TARGET INFOS ######");
			myData = _myTargetForce.targetInfos().getData();
			while(myData.hasRemaining()){
				for(int i = 0; i < 10;i++){
					System.out.print("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
				}
				System.out.println();
			}
			_myDoNextFrame = false;
		}
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend();
		g.color(255);
		_myParticles.draw();
//		g.noBlend();
		g.color(255);
		g.image(_myTargetForce.particleTargetInfos(), -400,0);
		g.image(_myTargetForce.targetInfos(), -400,-400);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		case 'd':
			_myDoDebug = !_myDoDebug;
			break;
		case 'f':
			_myDoNextFrame = true;
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesNearestTargetForceDebug.class);
		myManager.settings().size(1024, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

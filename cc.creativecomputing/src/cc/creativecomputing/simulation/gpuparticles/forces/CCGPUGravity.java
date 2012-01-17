package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUGravity extends CCGPUForce{
	private CGparameter _myGravityParameter;
	private CCVector3f _myGravity;
	
	public CCGPUGravity(final CCVector3f theGravity) {
		super("Gravity");
		_myGravity = theGravity;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myGravityParameter  = parameter("gravity");
	}
	
	public CCVector3f direction() {
		return _myGravity;
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myGravityParameter, _myGravity);
	}
	
	
}

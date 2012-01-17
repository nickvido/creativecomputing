package cc.creativecomputing.simulation.gpuparticles.constrains;

import cc.creativecomputing.simulation.gpuparticles.CCGPUVelocityShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

public abstract class CCGPUConstraint {
	
	protected String _myParameterIndex;
	protected String _myShaderTypeName;
	protected CCGPUVelocityShader _myVelocityShader;
	

	private float _myResilience;
	private float _myFriction;
	private float _myMinimalVelocity;
	
	private CGparameter _myResilienceParameter;
	private CGparameter _myFrictionarameter;
	private CGparameter _myMinimalVelocityParameter;
	
	public CCGPUConstraint(final String theShaderTypeName, final float theResilience, final float theFriction, final float theMinimalVelocity){
		_myShaderTypeName = theShaderTypeName;

		_myResilience = theResilience;
		_myFriction = theFriction;
		_myMinimalVelocity = theMinimalVelocity;
	}
	
	public void setShader(CCGPUVelocityShader theShader, final int theIndex, final int theWidth, final int theHeight){
		_myVelocityShader = theShader;
		_myParameterIndex = "constraints["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		
		_myResilienceParameter  = parameter("resilience");
		_myFrictionarameter  = parameter("friction");
		_myMinimalVelocityParameter  = parameter("minimalVelocity");

		resilience(_myResilience);
		friction(_myFriction);
		minimalVelocity(_myMinimalVelocity);
		
		setupParameter(theWidth, theHeight);
		_myVelocityShader.checkError("Problem creating constrain.");
	}
	
	public abstract void setupParameter(final int theWidth, final int theHeight);
	
	public CGparameter parameter(final String theName){
		return _myVelocityShader.fragmentParameter(_myParameterIndex+"."+theName);
	}
	
	public void resilience(final float theResilience) {
		_myVelocityShader.parameter(_myResilienceParameter, theResilience);
	}
	
	public void friction(final float theFriction) {
		_myVelocityShader.parameter(_myFrictionarameter, 1 - theFriction);
	}
	
	public void minimalVelocity(final float theMinimalVelocity) {
		_myVelocityShader.parameter(_myMinimalVelocityParameter, theMinimalVelocity);
	}
}

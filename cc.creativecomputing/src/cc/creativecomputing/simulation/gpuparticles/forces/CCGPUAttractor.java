package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUAttractor extends CCGPUForce{
	private CCVector3f _myPosition;
	private float _myRadius;
	
	private CGparameter _myPositionParameter;
	private CGparameter _myRadiusParameter;
	
	public CCGPUAttractor(final CCVector3f thePosition, final float theStrength, final float theEpsilon){
		super("Attractor");
		_myPosition = thePosition;
		_cStrength = theStrength;
		_myRadius = theEpsilon;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myPositionParameter = parameter("position");
		_myRadiusParameter = parameter("radius");
	}
	
	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myPositionParameter, _myPosition);
		_myVelocityShader.parameter(_myStrengthParameter, _cStrength);
		_myVelocityShader.parameter(_myRadiusParameter, _myRadius);
	}
	
	public void position(final CCVector3f thePosition){
		_myPosition = thePosition;
	}
	
	public CCVector3f position(){
		return _myPosition;
	}
	
	public float strength(){
		return _cStrength;
	}
	
	public void radius(final float theRadius){
		_myRadius = theRadius;
	}
	
	public float radius(){
		return _myRadius;
	}

}

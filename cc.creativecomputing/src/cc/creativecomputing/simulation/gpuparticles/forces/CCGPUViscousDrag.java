package cc.creativecomputing.simulation.gpuparticles.forces;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUViscousDrag extends CCGPUForce{
	private CGparameter _myCoefficientParameter;
	private float _myCoefficient;
	
	public CCGPUViscousDrag(final float theCoefficient) {
		super("ViscousDrag");
		_myCoefficient = theCoefficient;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myCoefficientParameter  = parameter("coefficient");
	}


	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myCoefficientParameter, _myCoefficient);
	}
	
	
}

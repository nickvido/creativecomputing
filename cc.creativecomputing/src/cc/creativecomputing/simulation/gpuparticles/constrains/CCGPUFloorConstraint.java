package cc.creativecomputing.simulation.gpuparticles.constrains;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUFloorConstraint extends CCGPUConstraint{
	private CGparameter _myYparameter;
	
	private float _myY;
	
	public CCGPUFloorConstraint(final float theY, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("FloorConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myY = theY;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myYparameter  = parameter("y");
		
		y(_myY);
	}
	
	public void y(final float theY) {
		_myVelocityShader.parameter(_myYparameter, theY);
	}
}

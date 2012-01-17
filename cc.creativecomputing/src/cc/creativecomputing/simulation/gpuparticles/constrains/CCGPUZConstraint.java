package cc.creativecomputing.simulation.gpuparticles.constrains;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUZConstraint extends CCGPUConstraint{
	private CGparameter _myZparameter;
	
	private float _myZ;
	
	public CCGPUZConstraint(final float theY, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("ZConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myZ = theY;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myZparameter  = parameter("z");
		
		z(_myZ);
	}
	
	public void z(final float theZ) {
		_myVelocityShader.parameter(_myZparameter, theZ);
	}
}

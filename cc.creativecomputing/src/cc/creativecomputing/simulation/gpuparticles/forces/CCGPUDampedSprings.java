package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Adds support of spring forces to the particle system.
 * 
 * @author info
 * 
 */
public class CCGPUDampedSprings extends CCGPUSprings {
	private CGparameter _mySpringDampingParameter;
	private float _mySpringDamping;
	
	public CCGPUDampedSprings(final CCGraphics g, final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		this(g, 4,theSpringConstant, theSpringDamping, theRestLength);
	}

	public CCGPUDampedSprings(final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		super("DampedSprings", g, theNumberOfSprings,theSpringConstant, theRestLength);
		_mySpringDamping = theSpringDamping;
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_mySpringDampingParameter = parameter("springDamping");
		springDamping(_mySpringDamping);
	}

	public void springDamping(final float theSpringDamping) {
		_myVelocityShader.parameter(_mySpringDampingParameter, theSpringDamping);
	}
}

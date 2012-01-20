package cc.creativecomputing.simulation.gpuparticles.constrains;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUBoxConstraint extends CCGPUConstraint{
	private CGparameter _myMinCornerParameter;
	private CGparameter _myMaxCornerParameter;
	
	private CCVector3f _myMinCorner;
	private CCVector3f _myMaxCorner;
	
	public CCGPUBoxConstraint(
		final CCVector3f theMinCorner, final CCVector3f theMaxCorner, 
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		super("BoxConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myMinCorner = theMinCorner;
		_myMaxCorner = theMaxCorner;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myMinCornerParameter  = parameter("minCorner");
		_myMaxCornerParameter  = parameter("maxCorner");
		
		minCorner(_myMinCorner);
		maxCorner(_myMaxCorner);
	}
	
	public void minCorner(final CCVector3f theMinCorner) {
		_myVelocityShader.parameter(_myMinCornerParameter, theMinCorner);
	}
	
	public void maxCorner(final CCVector3f theMaxCorner) {
		_myVelocityShader.parameter(_myMaxCornerParameter, theMaxCorner);
	}
	
}
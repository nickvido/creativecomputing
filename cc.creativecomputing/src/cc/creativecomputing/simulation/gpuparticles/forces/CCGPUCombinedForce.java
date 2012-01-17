package cc.creativecomputing.simulation.gpuparticles.forces;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;
import cc.creativecomputing.simulation.gpuparticles.CCGPUVelocityShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

/**
 * The combined force is useful when blending forces. This way it is possible to blend
 * between two different sets of forces.
 * @author info
 *
 */
public class CCGPUCombinedForce extends CCGPUForce{
	
	private List<CCGPUForce> _myForces;
	
	/**
	 * Create a new combined force using the given forces
	 * @param theForces
	 */
	public CCGPUCombinedForce(final List<CCGPUForce> theForces){
		super("CombinedForce");
		_myForces = theForces;
	}

	@Override
	/**
	 * @invisible
	 */
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, int theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = "forces["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		_myVelocityShader.checkError("Problem creating force.");
		
		CGparameter myForcesParameter = _myVelocityShader.fragmentParameter(_myParameterIndex +".forces");
		CgGL.cgSetArraySize(myForcesParameter, _myForces.size());
		
		int myCounter = 0;
		
		for(CCGPUForce myForce:_myForces) {
			myForce.setShader(theParticles, theShader, _myParameterIndex + ".forces[" + myCounter + "]", theWidth, theHeight);
			myCounter++;
		}
		setupParameter(0, 0);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		_myVelocityShader.checkError("Problem creating force.");
		
		CGparameter myForcesParameter = _myVelocityShader.fragmentParameter(_myParameterIndex + ".forces");
		CgGL.cgSetArraySize(myForcesParameter, _myForces.size());
		
		int myCounter = 0;
		
		for(CCGPUForce myForce:_myForces) {
			myForce.setShader(theParticles, theShader, _myParameterIndex + ".forces[" + myCounter + "]", theWidth, theHeight);
			myCounter++;
		}
		setupParameter(0, 0);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		for(CCGPUForce myForce:_myForces) {
			myForce.setupParameter(theWidth, theHeight);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		for(CCGPUForce myForce:_myForces) {
			myForce.setSize(theG, theWidth, theHeight);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		for(CCGPUForce myForce:_myForces) {
			myForce.update(theDeltaTime);
		}
	}
}

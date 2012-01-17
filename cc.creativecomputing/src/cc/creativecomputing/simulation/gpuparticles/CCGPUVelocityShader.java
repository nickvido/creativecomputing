package cc.creativecomputing.simulation.gpuparticles;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

/**
 * @invisible
 * @author info
 *
 */
public class CCGPUVelocityShader extends CCCGShader{

	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;
	protected CGparameter _myDeltaTimeParameter;
	
	protected CGparameter _myForcesParameter;
	protected CGparameter _myConstraintsParameter;
	
	protected CCGPUVelocityShader(
		final CCGPUParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCGPUForce> theForces , 
		final List<CCGPUConstraint> theConstrains,
		final String[] theShaderFile,
		final int theWidth,
		final int theHeight
	){
		super(null,theShaderFile);

		checkError("created velocity shader");
		
		_myForcesParameter = fragmentParameter("forces");
		CgGL.cgSetArraySize(_myForcesParameter, theForces.size());
		
		int myIndex = 0;
		for(CCGPUForce myForce:theForces){
			myForce.setShader(theParticles, this, myIndex++, theWidth, theHeight);
		}
		
		_myConstraintsParameter = fragmentParameter("constraints");
		CgGL.cgSetArraySize(_myConstraintsParameter, theConstrains.size());
		
		int myConstraintIndex = 0;
		for(CCGPUConstraint myConstraint:theConstrains){
			myConstraint.setShader(this, myConstraintIndex++, theWidth, theHeight);
		}
		
		_myPositionTextureParameter = fragmentParameter("positionTexture");
		_myInfoTextureParameter = fragmentParameter("infoTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myDeltaTimeParameter = fragmentParameter("deltaTime");
		
		load();
		
//		for(CCGPUForce myForce:theForces){
//			myForce.setupParameter(theWidth, theHeight);
//		}
		CCGPUNoise.attachFragmentNoise(this);
	}
	
	public CCGPUVelocityShader(
		final CCGPUParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCGPUForce> theForces, 
		final List<CCGPUConstraint> theConstrains,
		final int theWidth,
		final int theHeight
	){
		this(
			theParticles,
			theGraphics, 
			theForces, theConstrains, 
			new String[] {
				CCIOUtil.classPath(CCGPUNoise.class,"shader/simplex.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/forces.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/constrains.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/velocity.fp")
			},
			theWidth, theHeight
		);
	}
	
	public void positions(final CCShaderTexture thePositionTexture){
		texture(_myPositionTextureParameter, thePositionTexture.id(0));
		texture(_myInfoTextureParameter, thePositionTexture.id(1));
	}
	
	public void velocities(final CCTexture2D theVelocityTexture){
		texture(_myVelocityTextureParameter, theVelocityTexture.id());
	}
	
	public void deltaTime(final float theDeltaTime){
		parameter(_myDeltaTimeParameter, theDeltaTime);
	}
	
}
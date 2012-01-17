package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;
import cc.creativecomputing.simulation.gpuparticles.CCGPUVelocityShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

/**
 * This force takes a forces and a texture to define the influence of the force per particle. 
 * The texture needs to be in the same size as the data textures of the particle system.
 * @author christianriekoff
 *
 */
public class CCGPUIDTextureBlendForce extends CCGPUForce{
	
	private CCTexture2D _myTexture;
	private float _myBlend;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myBlendParameter;
	
	private CCGPUForce _myForce;
	
	public CCGPUIDTextureBlendForce(
		final CCTexture2D theTexture,
		final CCGPUForce theForce
	){
		super("IDTextureBlendForce");
		_myTexture = theTexture;
		_myBlend = 1;
		
		_myForce = theForce;
	}

	@Override
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, int theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = "forces["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(0, 0);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
	}
	
	@Override
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(0, 0);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myTextureParameter = parameter("texture");
		_myBlendParameter = parameter("blend");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myBlendParameter, _myBlend);
		
		_myForce.update(theDeltaTime);
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
	}
	
	public void blend(final float theBlend) {
		_myBlend = theBlend;
	}
}

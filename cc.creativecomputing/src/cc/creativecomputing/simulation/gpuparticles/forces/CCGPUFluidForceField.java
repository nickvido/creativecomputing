package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.simulation.gpuparticles.fluidfield.CCGPUFluid;

import com.jogamp.opengl.cg.CGparameter;

/**
 * This force creates a texture based force field, every pixel of the
 * texture holds a direction which acts as a force on the particle. 
 * To read out the force from the texture it can be placed and scaled
 * on the xy plane. The xy coords of the particles will than be used to
 * read the force from the texture.
 * @author christian riekoff
 *
 */
public class CCGPUFluidForceField extends CCGPUForce{
	
	private CCVector2f _myTextureScale;
	private CCVector2f _myTextureOffset;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;
	private CGparameter _myTextureSizeParameter;
	
	private CCTexture2D _myTexture;
	private CCGPUFluid _myFluid;
	
	public CCGPUFluidForceField(
		final CCGPUFluid theFluid,
		final CCVector2f theTextureScale,
		final CCVector2f theTextureOffset
	){
		super("FluidForceField");
		_myFluid = theFluid;
		_myTexture = _myFluid.velocityTexture();
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
	}
	
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myTextureParameter = parameter("texture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myTextureSizeParameter = parameter("textureSize");
	}
	
	public CCGPUFluid fluid() {
		return _myFluid;
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myVelocityShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myVelocityShader.parameter(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
		
		_myFluid.update(theDeltaTime);
		
	}
	
	public boolean addToForceArray(){
		return true;
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
	}
	
	public CCVector2f textureScale() {
		return _myTextureScale;
	}
	
	public CCVector2f textureOffset() {
		return _myTextureOffset;
	}
	
	
}

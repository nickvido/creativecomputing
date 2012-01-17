package cc.creativecomputing.simulation.gpuparticles.constrains;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUCableConstraint extends CCGPUConstraint{
	private CGparameter _myYparameter;
	
	private CGparameter _myID1TextureParameter;
	private CGparameter _myID2TextureParameter;

	private CCCGShader _myInitValue01Shader;
	private CCShaderTexture _myID1Texture;
	private CCShaderTexture _myID2Texture;
	
	private CCGraphics _myGraphics;
	
	public CCGPUCableConstraint(final CCGraphics g, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("FloorConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myInitValue01Shader = new CCCGShader(null, CCIOUtil.classPath(CCGPUCableConstraint.class, "shader/initvalue.fp"));
		_myInitValue01Shader.load();
		
		_myGraphics = g;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myYparameter  = parameter("y");
	}
	
	public void y(final float theY) {
		_myVelocityShader.parameter(_myYparameter, theY);
	}
}

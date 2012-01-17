package cc.creativecomputing.simulation.gpuparticles;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @invisible
 * @author info
 *
 */
public class CCGPUQuadVelocityShader extends CCGPUVelocityShader{
	
	private CGparameter _myUpTextureParameter;
	
	public CCGPUQuadVelocityShader(
		final CCGPUParticles theParticles, final CCGraphics theGraphics, 
		final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstrains, 
		final int theWidth, final int theHeight
	){
		super(
			theParticles,
			theGraphics,
			theForces,theConstrains,
			new String[] {
				CCIOUtil.classPath(CCGPUNoise.class,"shader/simplex.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/forces.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/constrains.fp"),
				CCIOUtil.classPath(CCGPUQuadVelocityShader.class,"shader/velocity_quads.fp")
			},
			theWidth, theHeight
		);

		_myUpTextureParameter = fragmentParameter("upTexture");
	}
	
	public void ups(final CCShaderTexture theUpsTexture){
		texture(_myUpTextureParameter, theUpsTexture.id(1));
	}
	
}

package cc.creativecomputing.simulation.gpuparticles;

import java.io.File;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @invisible
 * @author info
 *
 */
public class CCGPUMovementShader extends CCCGShader{

	private CGparameter _myPositionTextureParameter;
	private CGparameter _myInfoTextureParameter;
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myDeltaTimeParameter;
	private CGparameter _myAppWidthParameter;
	private CGparameter _myMake2DParameter;
	
	public CCGPUMovementShader(final CCGraphics theGraphics){
		super(null,CCIOUtil.classPath(CCGPUMovementShader.class, "shader/movement.fp"));
		_myPositionTextureParameter = fragmentParameter("positionTexture");
		_myInfoTextureParameter = fragmentParameter("infoTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myDeltaTimeParameter = fragmentParameter("deltaTime");
		_myAppWidthParameter = fragmentParameter("appWidth");
		_myMake2DParameter = fragmentParameter("make2D");
		width(theGraphics.width);
		load();
	}
	
	public void positions(final CCShaderTexture thePositionTexture){
		texture(_myPositionTextureParameter, thePositionTexture.id(0));
		texture(_myInfoTextureParameter, thePositionTexture.id(1));
	}
	
	public void velocities(final CCShaderTexture theVelocityTexture){
		texture(_myVelocityTextureParameter, theVelocityTexture.id());
	}
	
	public void deltaTime(final float theDeltaTime){
		parameter(_myDeltaTimeParameter, theDeltaTime);
	}
	
	public void width(final float theWidth){
		parameter(_myAppWidthParameter, theWidth);
	}
	
	public void make2D(final boolean theMake2D) {
		if(theMake2D)parameter(_myMake2DParameter, 0);
		else parameter(_myMake2DParameter, 1);
	}
}

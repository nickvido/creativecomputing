package cc.creativecomputing.simulation.gpuparticles.render;

import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUDisplayShader extends CCCGShader{
	
	CGparameter _myPointSizeParameter;
	CGparameter _myMinPointSizeParameter;
	CGparameter _myMaxPointSizeParameter;
	
	CGparameter _myTangHalfFovParameter;
	
	public CCGPUDisplayShader(final String theVertexFile, final String theFragmentFile) {
		super(theVertexFile, theFragmentFile);
		
		_myPointSizeParameter = vertexParameter("pointSize");
		_myMinPointSizeParameter = vertexParameter("minPointSize");
		_myMaxPointSizeParameter = vertexParameter("maxPointSize");
		
		_myTangHalfFovParameter = vertexParameter("tanHalfFov");
		load();
		
		pointSize(50);
		minPointSize(0.5f);
		maxPointSize(20);
	}
	
	public CCGPUDisplayShader(){
		this(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/points/display.vp"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/points/display.fp")
		);
		
		
	}
	
	public void pointSize(final float thePointSize) {
		parameter(_myPointSizeParameter, thePointSize);
	}
	
	public void minPointSize(final float theMinPointSize) {
		parameter(_myMinPointSizeParameter, theMinPointSize);
	}
	
	public void maxPointSize(final float theMaxPointSize) {
		parameter(_myMaxPointSizeParameter, theMaxPointSize);
	}
	
	public void tangHalfFov(final float theTangHalfFov) {
		parameter(_myTangHalfFovParameter, theTangHalfFov);
	}
}

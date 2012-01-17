package cc.creativecomputing.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUQuadParticles extends CCGPUQueueParticles{
	
	private CCShaderTexture _myQuadsPositionTexture;
	
	private CCCGShader _myUpdateQuadsShader;
	private CGparameter _myPositionTextureParameter;
	private CGparameter _myForwardTextureParameter;
	private CGparameter _mySideTextureParameter;
	private CGparameter _myUpTextureParameter;
	private CGparameter _myScaleParameter;
	
	public CCGPUQuadParticles(
		final CCGraphics g, 
		final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstrains,
		final int theWidth, final int theHeight
	){
		super(g, theForces, theConstrains, theWidth, theHeight);
		
		_myVelocityShader = new CCGPUQuadVelocityShader(this, g,theForces,theConstrains, theWidth, theHeight);
		
		_myUpdateQuadsShader = new CCCGShader(null,CCIOUtil.classPath(this,"shader/update_quads.fp"));
		_myUpdateQuadsShader.load();
		
		_myPositionTextureParameter = _myUpdateQuadsShader.fragmentParameter("positionTexture");
		_myForwardTextureParameter = _myUpdateQuadsShader.fragmentParameter("forwardTexture");
		_mySideTextureParameter = _myUpdateQuadsShader.fragmentParameter("sideTexture");
		_myUpTextureParameter = _myUpdateQuadsShader.fragmentParameter("upTexture");
		_myScaleParameter = _myUpdateQuadsShader.fragmentParameter("scale");
		
		_myQuadsPositionTexture = new CCShaderTexture(32,4,2,_myWidth * 4,_myHeight);
		
		_myCurrentVelocityTexture = new CCShaderTexture(32,3,4,_myWidth,_myHeight);
//		_myCurrentVelocityTexture.loadData(_myVelocityBuffer);
		
		_myDestinationVelocityTexture = new CCShaderTexture(32,3,4,_myWidth,_myHeight);
		
//		_myMesh = new CCVBOMesh(CCGraphics.QUADS,_myWidth * _myHeight * 4);
		
		reset();
	}
	
	public CCGPUQuadParticles(final CCGraphics g, final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstrains) {
		this(g, theForces, theConstrains, 100, 400);
	}
	
	public CCGPUQuadParticles(final CCGraphics g, final List<CCGPUForce> theForces) {
		this(g, theForces, new ArrayList<CCGPUConstraint>());
	}

	boolean first = true;
	
	public void update(final float theDeltaTime){
		super.update(theDeltaTime);
		

		_myUpdateQuadsShader.texture(_myPositionTextureParameter, _myCurrentPositionTexture.id(0));
		_myUpdateQuadsShader.texture(_myUpTextureParameter, _myCurrentVelocityTexture.id(1));
		_myUpdateQuadsShader.texture(_mySideTextureParameter, _myCurrentVelocityTexture.id(2));
		_myUpdateQuadsShader.texture(_myForwardTextureParameter, _myCurrentVelocityTexture.id(3));
		_myUpdateQuadsShader.start();
		_myQuadsPositionTexture.draw();
		_myUpdateQuadsShader.end();

//		_myMesh.vertices(_myQuadsPositionTexture,0);
//		_myMesh.normals(_myQuadsPositionTexture,1);
		
		quadSize(10);
		
		_myGraphics.blend();
	}
	
	public void quadSize(final float theSize) {
		_myUpdateQuadsShader.parameter(_myScaleParameter, theSize/2);
	}
}

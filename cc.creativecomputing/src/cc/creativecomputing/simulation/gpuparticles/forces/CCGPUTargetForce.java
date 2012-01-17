package cc.creativecomputing.simulation.gpuparticles.forces;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUTargetForce extends CCGPUForce {

	private List<Object> _myTargetPositionTextures;

	private CGparameter _myTargetPositionTextureParameter;
	private CGparameter _myCenterParameter;
	private CGparameter _myVelocityReductionParameter;

	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;

	private CCCGShader _myInitValueShader;

	private int _myCurrentIndex = 0;

	public CCGPUTargetForce() {
		super("TargetForce");
		_myTargetPositionTextures = new ArrayList<Object>();
	}

	@Override
	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myTargetPositionTextureParameter = parameter("targetPositionTexture");
		_myCenterParameter = parameter("center");
		_myVelocityReductionParameter = parameter("velocityReduction");
		velocityReduction(1f);

	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
	}
	
	public void velocityReduction(final float theVelocityReduction) {
		_myVelocityShader.parameter(_myVelocityReductionParameter, theVelocityReduction);
	}

	public void center(final CCVector3f theCenter) {
		_myVelocityShader.parameter(_myCenterParameter, theCenter);
	}

	public void center(final float theX, final float theY, final float theZ) {
		_myVelocityShader.parameter(_myCenterParameter, theX, theY, theZ);
	}

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticles.class, "shader/initvalue01.fp"));
		_myInitValueShader.load();
	}

	public void addTargetSetup(final CCGPUTargetSetup theSetup) {
		CCShaderTexture myTexture = new CCShaderTexture(32, 3, _myWidth, _myHeight);
		_myTargetPositionTextures.add(myTexture);
		
		myTexture.beginDraw();
		_myInitValueShader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
		theSetup.setParticleTargets(_myGraphics, _myWidth, _myHeight);
		_myGraphics.endShape();

		_myInitValueShader.end();
		myTexture.endDraw();
		
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderTexture)_myTargetPositionTextures.get(_myCurrentIndex)).id());
			}
		}
	}
	
	public void addTargetSetup(final CCTexture2D theShaderTexture) {
		_myTargetPositionTextures.add(theShaderTexture);
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCTexture2D)_myTargetPositionTextures.get(_myCurrentIndex)).id());
			}
		}
	}
	
	public void addTargetSetup(final int theTextureID) {
		_myTargetPositionTextures.add(theTextureID);
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderTexture)_myTargetPositionTextures.get(_myCurrentIndex)).id());
			}
		}
	}

	public void changeTexture(int theIndex) {
		_myCurrentIndex = theIndex;
		Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
		if(myObject instanceof Integer) {
			_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
		}else {
			_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderTexture)_myTargetPositionTextures.get(_myCurrentIndex)).id());
		}
	}

}

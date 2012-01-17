package cc.creativecomputing.simulation.gpuparticles.forces;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;
import cc.creativecomputing.simulation.gpuparticles.fluidfield.CCGPUFluid;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUAnchoredSprings extends CCGPUForce {
	
	private CGparameter _mySpringConstantParameter;
	private CGparameter _myDampingParameter;
	private CGparameter _myRestLengthParameter;
	
	private CGparameter _myAnchorPositionTextureParameter;

	private CCCGShader _myInitValue01Shader;
	private CCShaderTexture _myAnchorPositionTexture;
	
	private CCVector3f[] _myAnchorPositions;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();
	protected PriorityQueue<CCGPUParticle> _myActiveParticles = new PriorityQueue<CCGPUParticle>();
	
	private float _mySpringConstant;
	private float _myDamping;
	private float _myRestLength;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth = 0;
	private int _myHeight = 0;
	
	protected double _myCurrentTime = 0;

	public CCGPUAnchoredSprings(final CCGraphics g, final float theSpringConstant, final float theDamping, final float theRestLength) {
		super("AnchoredSprings");
		_myInitValue01Shader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticles.class,"shader/initvalue.fp"));
		_myInitValue01Shader.load();
		
		_myGraphics = g;
		
		_mySpringConstant = theSpringConstant;
		_myDamping = theDamping;
		_myRestLength = theRestLength;
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myAnchorPositions = new CCVector3f[theWidth * theHeight];
		
		_myAnchorPositionTexture = new CCShaderTexture(32, 4, theWidth, theHeight);
		_myAnchorPositionTexture.beginDraw();
		_myGraphics.pushAttribute();
		_myGraphics.clearColor(0,0);
		_myGraphics.clear();
		_myGraphics.popAttribute();
		_myAnchorPositionTexture.endDraw();
		
		_mySpringConstantParameter = parameter("springConstant");
		_myDampingParameter = parameter("springDamping");
		_myRestLengthParameter = parameter("restLength");
		
		_myAnchorPositionTextureParameter = parameter("anchorPositionTexture");
		
		springDamping(_myDamping);
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
		_myVelocityShader.texture(_myAnchorPositionTextureParameter, _myAnchorPositionTexture.id());
	}
	
	public void addSpring(final CCGPUParticle theParticle, final CCVector3f theAnchor) {
		if(theParticle == null)return;
		_myActiveParticles.add(theParticle);
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	public void setSpringPos(final CCGPUParticle theParticle, final CCVector3f theAnchor) {
		if(theParticle == null || theParticle.index() < 0)return;
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	public void springConstant(final float theSpringConstant) {
		_myVelocityShader.parameter(_mySpringConstantParameter, theSpringConstant);
	}
	
	public void springDamping(final float theSpringDamping) {
		_myVelocityShader.parameter(_myDampingParameter, theSpringDamping);
	}
	
	public void restLength(final float theRestLength) {
		_myVelocityShader.parameter(_myRestLengthParameter, theRestLength);
	}

	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myGraphics.noBlend();
		
		
		_myAnchorPositionTexture.beginDraw();
		_myInitValue01Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		CCVector3f myAnchor;
		for (int mySpring:_myChangedSprings){
			myAnchor = _myAnchorPositions[mySpring];
			_myGraphics.textureCoords(0, myAnchor.x, myAnchor.y, myAnchor.z, 1);
			_myGraphics.vertex(mySpring % _myWidth, mySpring / _myWidth);
		}
		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
			CCGPUParticle myParticle = _myActiveParticles.poll();
			_myGraphics.textureCoords(0, 0, 0, 0, 0);
			_myGraphics.vertex(myParticle.index() % _myWidth,myParticle.index() / _myWidth);
		}
		_myGraphics.endShape();
		_myInitValue01Shader.end();
		_myAnchorPositionTexture.endDraw();
		
		_myChangedSprings.clear();
		_myCurrentTime += theDeltaTime;
	}
	
	public CCShaderTexture anchorPositionTexture() {
		return _myAnchorPositionTexture;
	}
}

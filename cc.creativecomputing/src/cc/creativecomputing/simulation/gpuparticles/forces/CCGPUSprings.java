package cc.creativecomputing.simulation.gpuparticles.forces;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

/**
 * Adds support of spring forces to the particle system.
 * <p>
 * You can create a spring force between two particles using their index or position inside the data texture. 
 * Be aware that you can not create endless springs. The number of springs per particle is dependent on the 
 * number of spring textures that store the data. So in the constructor of the gpu springs you can define how 
 * many springs you want to attach per particle.
 * </p>
 * <p>
 * A spring can work in two different ways. The particles can be force to keep the defined rest length this
 * means they will be pulled together if they exceed the rest length and pushed away from each other if they get
 * closer than the given rest length. This is the case if the <code>forceRestLength</code> parameter is set
 * <code>true</code>. If you do not force the rest length particles are allowed to to get closer than the given 
 * rest length.
 * </p>
 * @author christian riekoff
 *
 */
public class CCGPUSprings extends CCGPUForce {
	
	private CGparameter _mySpringConstantParameter;
	private CGparameter _myRestLengthParameter;
	
	private CGparameter[] _myIDTextureParameters;
	private CGparameter[] _myInfoTextureParameters;

	private CCCGShader _myInitValue01Shader;
	
	private CCShaderTexture[] _myIDTextures;
	private CCShaderTexture[] _myInfoTextures;
	private int _myNumberOfSpringTextures;
	
	private float _mySpringConstant;
	private float _myRestLength;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth;
	private int _myHeight;
	
	private int[][] _mySprings;
	private float[][] _myRestLengths;
	private boolean[][] _myForceRestLengths;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();
	protected PriorityQueue<CCGPUParticle> _myActiveParticles = new PriorityQueue<CCGPUParticle>();

	protected double _myCurrentTime = 0;

	/**
	 * Creates a new spring force with the given number of springs per particle, the given 
	 * spring constant and the given rest length.
	 * @param g reference to the graphics object
	 * @param theNumberOfSprings number of springs attached to a particle
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCGPUSprings(final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theRestLength) {
		this("Springs", g, theNumberOfSprings, theSpringConstant, theRestLength);
	}
	
	/**
	 * Creates a new spring force with the given 
	 * spring constant and the given rest length. The number of springs that can be attached to a particle
	 * is set to 4.
	 * 
	 * @param g reference to the graphics object
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCGPUSprings(final CCGraphics g, final float theSpringConstant, final float theRestLength) {
		this("Springs", g, 4, theSpringConstant, theRestLength);
	}
	
	protected CCGPUSprings(final String theForceName, final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theRestLength) {
		super(theForceName);
		_myInitValue01Shader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticles.class,"shader/initvalue.fp"));
		_myInitValue01Shader.load();
		
		_myGraphics = g;
		
		_mySpringConstant = theSpringConstant;
		_myRestLength = theRestLength;
		
		_myNumberOfSpringTextures = theNumberOfSprings / 2 + theNumberOfSprings % 1;
		_myIDTextures = new CCShaderTexture[_myNumberOfSpringTextures];
		_myInfoTextures = new CCShaderTexture[_myNumberOfSpringTextures];
	}

	private void resetTextures() {
		for(int i = 0; i < _myNumberOfSpringTextures;i++) {
			CCShaderTexture myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.textureCoords(0, -1f, -1f, -1f, -1f);
			_myGraphics.vertex(0,0);
			_myGraphics.vertex(_myWidth,0);
			_myGraphics.vertex(_myWidth,_myHeight);
			_myGraphics.vertex(0,_myHeight);
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw();
			
			CCShaderTexture myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.textureCoords(0, 0f, 0f, 0f, 0f);
			_myGraphics.vertex(0,0);
			_myGraphics.vertex(_myWidth,0);
			_myGraphics.vertex(_myWidth,_myHeight);
			_myGraphics.vertex(0,_myHeight);
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw();
		}
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_mySprings = new int[_myWidth * _myHeight][_myNumberOfSpringTextures * 2];
		_myRestLengths = new float[_myWidth * _myHeight][_myNumberOfSpringTextures * 2];
		_myForceRestLengths = new boolean[_myWidth * _myHeight][_myNumberOfSpringTextures * 2];
		for(int i = 0; i < _mySprings.length;i++) {
			for(int j = 0; j < _myNumberOfSpringTextures * 2;j++) {
				_mySprings[i][j] = -1;
				_myRestLengths[i][j] = 0;
				_myForceRestLengths[i][j] = true;
			}
		}
		
		for(int i = 0; i < _myNumberOfSpringTextures;i++) {
			_myIDTextures[i] = new CCShaderTexture(32, 4, theWidth, theHeight);
			_myIDTextures[i].clear();

			_myInfoTextures[i] = new CCShaderTexture(32, 4, theWidth, theHeight);
			_myInfoTextures[i].clear();
		}
		
		resetTextures();
		
		_mySpringConstantParameter = parameter("springConstant");
		_myRestLengthParameter = parameter("restLength");
		
		CGparameter myIdTexturesParameter = parameter("idTextures");
		CgGL.cgSetArraySize(myIdTexturesParameter, _myNumberOfSpringTextures);
		_myIDTextureParameters = new CGparameter[_myNumberOfSpringTextures];
		
		CGparameter myInfoTexturesParameter = parameter("infoTextures");
		CgGL.cgSetArraySize(myInfoTexturesParameter, _myNumberOfSpringTextures);
		_myInfoTextureParameters = new CGparameter[_myNumberOfSpringTextures];
		
		for(int i = 0; i < _myNumberOfSpringTextures;i++) {
			_myIDTextureParameters[i] = parameter("idTextures["+i+"]");
			_myVelocityShader.texture(_myIDTextureParameters[i], _myIDTextures[i].id());

			_myInfoTextureParameters[i] = parameter("infoTextures["+i+"]");
			_myVelocityShader.texture(_myInfoTextureParameters[i], _myInfoTextures[i].id());
		}
		
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
		_myGraphics.noBlend();
	}
	
	private int getFreeSpringIndex(final CCGPUParticle theParticle) {
		for(int i = 0; i < _myNumberOfSpringTextures * 2;i++) {
			if(_mySprings[theParticle.index()][i] == -1) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * <p>
	 * Creates a spring force between the particles with the given index. Be aware that you can not create
	 * endless springs. The number of springs per particle is dependent on the number of springtextures that
	 * store the data. So in the constructor of the gpu springs you can define how many springs you want to 
	 * attach per particle.
	 * </p>
	 * <p>
	 * A spring can work in two different ways. The particles can be force to keep the defined rest length this
	 * means they will be pulled together if they exceed the rest length and pushed away from each other if they get
	 * closer than the given rest length. This is the case if the <code>forceRestLength</code> parameter is set
	 * <code>true</code>. If you do not force the rest length particles are allowed to to get closer than the given 
	 * rest length.
	 * </p>
	 * @param theA index of the particle a
	 * @param theB index of the particle b
	 * @param theRestLength rest length of the spring to create
	 * @param theForceRestLength if this is <code>true</code> particles are forced to keep the rest length
	 * @return <code>true</code> if the spring could be created otherwise <code>false</code>
	 */
	public boolean addSpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength, boolean theForceRestLength) {
		if(theA == null || theB == null)return false;
		_myActiveParticles.add(theA);
		_myActiveParticles.add(theB);
		
		int myIndexA = getFreeSpringIndex(theA);
		int myIndexB = getFreeSpringIndex(theB);
		
		if(myIndexA > -1 && myIndexB > -1) {
			_mySprings[theA.index()][myIndexA] = theB.index();
			_mySprings[theB.index()][myIndexB] = theA.index();
			
			_myRestLengths[theA.index()][myIndexA] = theRestLength;
			_myRestLengths[theB.index()][myIndexB] = theRestLength;
			
			_myForceRestLengths[theA.index()][myIndexA] = theForceRestLength;
			_myForceRestLengths[theB.index()][myIndexB] = theForceRestLength;
			
			_myChangedSprings.add(theA.index());
			_myChangedSprings.add(theB.index());
			return true;
		}else {
			return false;
		}
	}
	
	public boolean addSpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength) {
		return addSpring(theA, theB, theRestLength, true);
	}
	
	public boolean addSpring(final CCGPUParticle theParticleA, final CCGPUParticle theParticleB) {
		return addSpring(theParticleA, theParticleB, _myRestLength);
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength, boolean theForceRestLength) {
		int myIndexB = getFreeSpringIndex(theB);
		
		if(myIndexB > -1) {
			_mySprings[theB.index()][myIndexB] = theA.index();
			_myRestLengths[theB.index()][myIndexB] = theRestLength;
			_myForceRestLengths[theB.index()][myIndexB] = theForceRestLength;
			_myChangedSprings.add(theB.index());
			return true;
		}else {
			return false;
		}
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength) {
		return addOneWaySpring(theA, theB, theRestLength, true);
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB) {
		return addOneWaySpring(theA, theB, _myRestLength, true);
	}
	
	public void springConstant(final float theSpringConstant) {
		_myVelocityShader.parameter(_mySpringConstantParameter, theSpringConstant);
	}
	
	public void restLength(final float theRestLength) {
		_myVelocityShader.parameter(_myRestLengthParameter, theRestLength);
	}
	
	private List<CCGPUParticle> _myParticlesToRemove = new ArrayList<CCGPUParticle>();
	
	public void removeSpring(final CCGPUParticle theParticle) {
		_myParticlesToRemove.add(theParticle);
	}

	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myGraphics.noBlend();
		
		_myActiveParticles.remove(_myParticlesToRemove);
		
		List<CCGPUParticle> myDeadParticles = new ArrayList<CCGPUParticle>(_myParticlesToRemove);
		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
			myDeadParticles.add(_myActiveParticles.poll());
		}
		
		_myParticlesToRemove.clear();
		
		for(int i = 0; i < _myNumberOfSpringTextures;i++) {
			CCShaderTexture myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				int myIndex1 = _mySprings[myID][i*2];

				float myX1 = myIndex1 == -1 ? -1 : myIndex1 % _myWidth;
				float myY1 = myIndex1 == -1 ? -1 : myIndex1 / _myWidth;
				
				int myIndex2 = _mySprings[myID][i*2 + 1];
				float myX2 = myIndex2 == -1 ? -1 : myIndex2 % _myWidth;
				float myY2 = myIndex2 == -1 ? -1 : myIndex2 / _myWidth;
				
				_myGraphics.textureCoords(0, myX1, myY1, myX2, myY2);
				_myGraphics.vertex(myID % _myWidth, myID / _myWidth);
			}
			
			for(CCGPUParticle myDeadParticle:myDeadParticles) {
				_myGraphics.textureCoords(0, -1, -1, -1, -1);
				_myGraphics.vertex(myDeadParticle.index() % _myWidth, myDeadParticle.index() / _myWidth);
				
				int myIndex1 = _mySprings[myDeadParticle.index()][i*2];
				if(myIndex1 >= 0) {
					_mySprings[myDeadParticle.index()][i*2] = -1;
					_myGraphics.textureCoords(0, -1, -1, -1, -1);
					_myGraphics.vertex(myIndex1 % _myWidth, myIndex1 / _myWidth);
				}
				int myIndex2 = _mySprings[myDeadParticle.index()][i*2 + 1];
				if(myIndex2 >= 0) {
					_mySprings[myDeadParticle.index()][i*2 + 1] = -1;
					_myGraphics.textureCoords(0, -1, -1, -1, -1);
					_myGraphics.vertex(myIndex2 % _myWidth, myIndex2 / _myWidth);
				}
				
			}
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw();
			
			CCShaderTexture myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				float myRestLength1 = _myRestLengths[myID][i*2];
				float myRestLength2 = _myRestLengths[myID][i*2 + 1];
				
				float myForceRestLength1 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				float myForceRestLength2 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				
				_myGraphics.textureCoords(0, myRestLength1, myRestLength2, myForceRestLength1, myForceRestLength2);
				_myGraphics.vertex(myID % _myWidth, myID / _myWidth);
			}
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw();
		}

		_myChangedSprings.clear();

		_myCurrentTime += theDeltaTime;
	}
	
	public CCShaderTexture idTexture(int theIndex) {
		return _myIDTextures[theIndex]; 
	}
	
	public CCShaderTexture infoTexture(int theIndex) {
		return _myInfoTextures[theIndex]; 
	}
	
	@Override
	public void reset() {
		resetTextures();
		
		for(int i = 0; i < _mySprings.length;i++) {
			for(int j = 0; j < _myNumberOfSpringTextures * 2;j++) {
				_mySprings[i][j] = -1;
				_myRestLengths[i][j] = 0;
				_myForceRestLengths[i][j] = true;
			}
		}
		
		_myActiveParticles.clear();
	}
	
	public List<Integer> lineIndices(){
		List<Integer> myIndices = new ArrayList<Integer>();
		
		for(int i = 0; i < _mySprings.length;i++) {
			for(int j = 0; j < _myNumberOfSpringTextures * 2;j++) {
				if(_mySprings[i][j] != -1) {
					myIndices.add(i);
					myIndices.add(_mySprings[i][j]);
				}
			}
		}
		return myIndices;
	}
}

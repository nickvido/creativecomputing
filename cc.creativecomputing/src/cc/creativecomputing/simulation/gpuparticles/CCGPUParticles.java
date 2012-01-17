package cc.creativecomputing.simulation.gpuparticles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticleRenderer;

/**
 * This particle system renders particles as points. You can add different forces
 * and constraints to change the behavior of the particles.
 * @shortdesc Particle System that treats particles as simple points.
 * @author info
 * @demo cc.creativecomputing.gpu.particles.demo.CCParticlesNoiseFlowFieldTest
 * @see CCGPUQuadParticles
 */
public abstract class CCGPUParticles{
	
	
	
	protected CCGraphics _myGraphics;
	protected List<CCGPUForce> _myForces;
	
	protected final int _myWidth;
	protected final int _myHeight;
	
	protected CCGPUMovementShader _myMovementShader;
	protected CCGPUVelocityShader _myVelocityShader;
	
	protected CCCGShader _myInitValue01Shader;
	protected CCCGShader _myInitValue0Shader;
	protected CCCGShader _myInitValue1Shader;
	
	protected CCShaderTexture _myCurrentPositionTexture;
	protected CCShaderTexture _myDestinationPositionTexture;
	
	protected CCShaderTexture _myCurrentVelocityTexture;
	protected CCShaderTexture _myDestinationVelocityTexture;
	
	protected double _myCurrentTime = 0;
	
	protected FloatBuffer _myPositionBuffer;
	protected FloatBuffer _myVelocityBuffer;
	
	private CCGPUParticleRenderer _myParticleRender;
	
	/**
	 * <p>
	 * Creates a new particle system. To create a new particle system you have to
	 * pass the CCGraphics instance and a list with forces. You can also pass
	 * a list of constraints that act as boundary so that the particles bounce at
	 * collision.
	 * </p>
	 * <p>
	 * The number of particles you can create depends on the size of the texture
	 * that holds the particle data on the gpu. You can define this size by passing
	 * a width and height value. The number of particles you can allocate is 
	 * width * height.
	 * </p>
	 * <p>
	 * How the particles are drawn is defined by a shader. You can pass a custom
	 * shader to the particle system to define how the particles are drawn. To
	 * create your own shader you need to extend the CCGPUDisplayShader and write your
	 * own cg shader.
	 * </p>
	 * 
	 * @param g graphics object used to initialize shaders and meshes for drawing
	 * @param theDisplayShader custom shader for displaying the particles
	 * @param theForces list with the forces applied to the particles
	 * @param theConstraints list with constraints applied to the particles
	 * @param theWidth width of particle system texture
	 * @param theHeight height of the particle system texture
	 */
	public CCGPUParticles(
		final CCGraphics g, final CCGPUParticleRenderer theRender,
		final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstraints, 
		final int theWidth, final int theHeight
	){
		_myGraphics = g;
		_myForces = theForces;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		for(CCGPUForce myForce:theForces) {
			myForce.setSize(g, theWidth, theHeight);
		}
		
		_myMovementShader = new CCGPUMovementShader(g);
		
		_myInitValue01Shader = new CCCGShader(null,CCIOUtil.classPath(this, "shader/initvalue01.fp"));
		_myInitValue01Shader.load();
		
		_myInitValue1Shader = new CCCGShader(null,CCIOUtil.classPath(this, "shader/initvalue1.fp"));
		_myInitValue1Shader.load();
		
		_myInitValue0Shader = new CCCGShader(null,CCIOUtil.classPath(this, "shader/initvalue.fp"));
		_myInitValue0Shader.load();
		
		_myCurrentPositionTexture = new CCShaderTexture(32,4,2,_myWidth,_myHeight);
		g.clearColor(0,0,1f);
		_myCurrentPositionTexture.beginDraw(0);
		g.clear();
		_myCurrentPositionTexture.endDraw();
		_myCurrentPositionTexture.beginDraw(1);
		g.clear();
		_myCurrentPositionTexture.endDraw();
		g.clearColor(0);
		
		_myDestinationPositionTexture = new CCShaderTexture(32,4,2,_myWidth,_myHeight);
		
		_myGraphics.noBlend();
		_myCurrentPositionTexture.beginDraw();
		_myInitValue01Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myWidth * _myHeight; i++){
			_myGraphics.textureCoords(0, -200f,0f,0f);
			_myGraphics.textureCoords(1, 1, 1, 1);
			_myGraphics.vertex(i % _myWidth,i / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue01Shader.end();
		_myCurrentPositionTexture.endDraw();
		
		_myParticleRender = theRender;
		_myParticleRender.setup(this);
		_myVelocityShader = new CCGPUVelocityShader(this, g,theForces, theConstraints,_myWidth,_myHeight);

		_myCurrentVelocityTexture = new CCShaderTexture(16,3,_myWidth,_myHeight);
		_myDestinationVelocityTexture = new CCShaderTexture(16,3,_myWidth,_myHeight);
		
		reset();
	}
	
	public CCGPUParticles(
		final CCGraphics g, 
		final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstraints, 
		final int theWidth, final int theHeight
	){
		this(g, new CCGPUParticlePointRenderer(), theForces, theConstraints, theWidth, theHeight);
	}
	
	public CCGPUParticles(final CCGraphics g, final List<CCGPUForce> theForces, final List<CCGPUConstraint> theConstraints){
		this(g,theForces, theConstraints,200,200);
	}
	
	public CCGPUParticles(final CCGraphics g, final List<CCGPUForce> theForces) {
		this(g, theForces, new ArrayList<CCGPUConstraint>());
	}
	
	public CCCGShader initValueShader() {
		return _myInitValue01Shader;
	}
	
	public void make2D(final boolean theMake2D) {
		_myMovementShader.make2D(theMake2D);
	}
	
	public double currentTime() {
		return _myCurrentTime;
	}
	
	public void reset(){
		_myCurrentPositionTexture.clear();
		_myCurrentVelocityTexture.clear();
		
		_myCurrentPositionTexture.beginDraw();
		_myInitValue01Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myWidth * _myHeight; i++){
			_myGraphics.textureCoords(0, Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
			_myGraphics.textureCoords(1, 1, 1, 1);
			_myGraphics.vertex(i % _myWidth,i / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue01Shader.end();
		_myCurrentPositionTexture.endDraw();
		
		for(CCGPUForce myForce:_myForces) {
			myForce.reset();
		}
	}
	
	/**
	 * Returns the width of the texture containing the particle data
	 * @return width of the particle texture
	 */
	public int width() {
		return _myWidth;
	}
	
	/**
	 * Returns the height of the texture containing the particle data
	 * @return height of the particle texture
	 */
	public int height() {
		return _myHeight;
	}
	
	public int size() {
		return _myWidth * _myHeight;
	}
	
	/**
	 * Returns the texture with the current positions of the particles.
	 * @return texture containing the positions of the particles
	 */
	public CCShaderTexture positions() {
		return _myCurrentPositionTexture;
	}

	public CCShaderTexture destinationPositions() {
		return _myDestinationPositionTexture;
	}
	
	public CCShaderTexture velocities() {
		return _myCurrentVelocityTexture;
	}
	
	protected abstract void beforeUpdate();
	
	protected abstract void afterUpdate();
	
	public void update(final float theDeltaTime){
		if(theDeltaTime <= 0)return;
		
		_myGraphics.noBlend();
		beforeUpdate();
		
		for(CCGPUForce myForce:_myForces) {
			myForce.update(theDeltaTime);
		}

		_myVelocityShader.positions(_myCurrentPositionTexture);
		_myVelocityShader.velocities(_myCurrentVelocityTexture);
		_myVelocityShader.deltaTime(theDeltaTime);
		_myVelocityShader.start();
		_myDestinationVelocityTexture.draw();
		_myVelocityShader.end();
		
		CCShaderTexture myVelocityTemp = _myDestinationVelocityTexture;
		_myDestinationVelocityTexture = _myCurrentVelocityTexture;
		_myCurrentVelocityTexture = myVelocityTemp;
		
		_myMovementShader.positions(_myCurrentPositionTexture);
		_myMovementShader.velocities(_myCurrentVelocityTexture);
		_myMovementShader.start();
		_myMovementShader.deltaTime(theDeltaTime);
		_myDestinationPositionTexture.draw();
		_myMovementShader.end();
		
		CCShaderTexture myTemp = _myDestinationPositionTexture;
		_myDestinationPositionTexture = _myCurrentPositionTexture;
		_myCurrentPositionTexture = myTemp;
		
		afterUpdate();
		_myCurrentTime += theDeltaTime;

		_myParticleRender.update(theDeltaTime);
//		_myGraphics.blend();
	}
	
	public void draw() {
		_myParticleRender.draw(_myGraphics);
	}
}

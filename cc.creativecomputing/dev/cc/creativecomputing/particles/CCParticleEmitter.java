package cc.creativecomputing.particles;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLMemory.Mem;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.opencl.CCCLCommandQueue;
import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;

public class CCParticleEmitter {
	
	private static class CCGPUParticleData {
		private int index;
		private CCVector3f position;
		private CCVector3f velocity;
		private float lifeTime;
		private float isPermanent;
	}
	
	private double _myCurrentTime;
	private CCParticles _myParticles;
	private CCOpenCL _myOpenCL;
	
	protected int[] _myAvailableIndices;
	private CCParticleWaitingList _myParticleWaitingList;
	protected final CCParticle[] _myActiveParticlesArray;
	protected int _myAllocatedParticleCounter = 0;
	protected int _myActiveParticleCounter = 0;
	
	protected List<CCGPUParticleData> _myParticleDatas = new ArrayList<CCGPUParticleData>();
	
	private CCCLProgram _myEmitProgram;
	private CCCLKernel _myEmitKernel;
	
	public CCParticleEmitter(CCParticles theParticles){
		_myParticles = theParticles;
		_myOpenCL = _myParticles.openCL();
		
		_myParticleWaitingList = new CCParticleWaitingList(0.5f);
		_myActiveParticlesArray = new CCParticle[theParticles.size()];
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			_myActiveParticlesArray[i] = new CCParticle(this,i);
		}
		
		_myEmitProgram = new CCCLProgram(_myOpenCL);
		_myEmitProgram.appendSource(CCParticleEmitter.class, "emit.cl");
		_myEmitProgram.build();
		_myEmitKernel = _myEmitProgram.createKernel("emit");
		_myEmitKernel.argument(4, _myParticles.velocities());
		_myEmitKernel.argument(5, _myParticles.positions());
		_myEmitKernel.argument(6, _myParticles.infos());
		
		reset();
	}
	
	public double currentTime() {
		return _myCurrentTime;
	} 
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCParticle allocateParticle(
		final int theIndex,
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		boolean theIsPermanent
	){
		CCParticle myActiveParticle = _myActiveParticlesArray[theIndex];
		myActiveParticle.timeOfDeath(theIsPermanent ? Float.MAX_VALUE : _myCurrentTime + theLifeTime);
		myActiveParticle.timeOfBirth(_myCurrentTime);
		myActiveParticle.index = theIndex;
		myActiveParticle.lifeTime(theLifeTime);

		_myParticleWaitingList.add(myActiveParticle, theLifeTime);

		// only create new objects if necessary reuse old ones to avoid garbage collection
		CCGPUParticleData myParticleData;
		if(_myParticleDatas.size() > _myAllocatedParticleCounter) {
			myParticleData = _myParticleDatas.get(_myAllocatedParticleCounter); 
		}else {
			myParticleData = new CCGPUParticleData();
			_myParticleDatas.add(myParticleData);
		}
		_myAllocatedParticleCounter++;
		
		myParticleData.index = theIndex;
		myParticleData.position = thePosition;
		myParticleData.velocity = theVelocity;
		myParticleData.lifeTime = theLifeTime;
		myParticleData.isPermanent = theIsPermanent ? 1 : 0;
		
		return myActiveParticle;
	}

	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCParticle allocateParticle(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		boolean theIsPermanent
	){
		if (_myActiveParticleCounter >= _myParticles.size())return null;

		int myFreeIndex = _myAvailableIndices[_myActiveParticleCounter];
		_myAvailableIndices[_myActiveParticleCounter] = 0;
		_myActiveParticleCounter++;
		
		return allocateParticle(myFreeIndex, thePosition, theVelocity, theLifeTime, theIsPermanent);
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * The number of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @return the allocated particle or <code>null</code>
	 */
	public CCParticle allocateParticle(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime
	) {
		return allocateParticle(thePosition, theVelocity, theLifeTime,false);
	}

	private void cleanUpParticles() {
		
		for(CCParticle myParticle: _myParticleWaitingList.deadParticles()) {
			_myActiveParticleCounter--;
			_myAvailableIndices[_myActiveParticleCounter] = myParticle.index;
		}
		
		_myParticleWaitingList.deadParticles().clear();
	}
	
	public void update(final float theDeltaTime){
		_myCurrentTime += theDeltaTime;
		_myParticleWaitingList.update(theDeltaTime);
		cleanUpParticles();
		
		
		//Arrays.sort(_myAvailableIndices);
		
//		System.out.println("++++++++++");
//		for(int myIndex:_myAvailableIndices) {
//			System.out.println(myIndex);
//		}
		//_myRadixSort.sort(_myAvailableIndices,CCMath.countDigits(_myParticles.size()));
	}
	
	CLBuffer<IntBuffer> _myIndices;
	CLBuffer<FloatBuffer> _myPositions;
	CLBuffer<FloatBuffer> _myVelocities;
	CLBuffer<FloatBuffer> _myInfos;
	
	public void addCommands(CCCLCommandQueue theCommandQueue){
		if(_myAllocatedParticleCounter == 0)return;
		
		if(_myIndices != null){
			_myIndices.release();
			_myPositions.release();
			_myVelocities.release();
			_myInfos.release();
		}
		_myIndices = _myOpenCL.context().createIntBuffer(_myAllocatedParticleCounter, Mem.READ_ONLY);
		_myPositions = _myOpenCL.context().createFloatBuffer(_myAllocatedParticleCounter * 4, Mem.READ_ONLY);
		_myVelocities = _myOpenCL.context().createFloatBuffer(_myAllocatedParticleCounter * 4, Mem.READ_ONLY);
		_myInfos = _myOpenCL.context().createFloatBuffer(_myAllocatedParticleCounter * 4, Mem.READ_ONLY);
		
		for (int i = 0; i < _myAllocatedParticleCounter;i++){
			CCGPUParticleData myParticle = _myParticleDatas.get(i);
			_myIndices.getBuffer().put(i, myParticle.index);
			
			_myPositions.getBuffer().put(i * 4 + 0, myParticle.position.x);
			_myPositions.getBuffer().put(i * 4 + 1, myParticle.position.y);
			_myPositions.getBuffer().put(i * 4 + 2, myParticle.position.z);
			
			_myVelocities.getBuffer().put(i * 4 + 0, myParticle.velocity.x);
			_myVelocities.getBuffer().put(i * 4 + 1, myParticle.velocity.y);
			_myVelocities.getBuffer().put(i * 4 + 2, myParticle.velocity.z);
			
			_myInfos.getBuffer().put(i * 4 + 0, myParticle.isPermanent);
			_myInfos.getBuffer().put(i * 4 + 1, myParticle.lifeTime);
			_myInfos.getBuffer().put(i * 4 + 2, myParticle.lifeTime);
		}
		
		_myEmitKernel.argument(0, _myIndices);
		_myEmitKernel.argument(1, _myVelocities);
		_myEmitKernel.argument(2, _myPositions);
		_myEmitKernel.argument(3, _myInfos);
		
		theCommandQueue.putWriteBuffer(_myIndices, false);
		theCommandQueue.putWriteBuffer(_myVelocities, false);
		theCommandQueue.putWriteBuffer(_myPositions, false);
		theCommandQueue.putWriteBuffer(_myInfos, false);
		theCommandQueue.put1DKernel(_myEmitKernel,  _myAllocatedParticleCounter);

		// reset counter
		_myAllocatedParticleCounter = 0;
	}
	
	public void reset() {

		_myAvailableIndices = new int[_myParticles.size()];

		for (int i = 0; i < _myParticles.size(); i++){
			_myAvailableIndices[i] = i;
		}
		
		_myParticleWaitingList.reset();
	}
}

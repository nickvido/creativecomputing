/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.graphics.CCBufferObject;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.opencl.CCCLCommandQueue;
import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;
import cc.creativecomputing.particles.forces.CCForce;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLMemory.Mem;
import com.jogamp.opencl.gl.CLGLBuffer;

/**
 * @author christianriekoff
 *
 */
public class CCParticles implements CCDisposeListener{
	
	public static int WORK_GROUP_SIZE = 0;
	
	private interface CCParticlePipeline{
		public CLMemory<?> accelerations();
		public CLMemory<?> velocities();
		public CLMemory<?> positions();
		public CLMemory<?> infos();
		
		public void begin();
		public void end();
		public CLMemory<?> createBuffer();
	}
	
	/**
	 * Can be used when opencl device supports gl sharing
	 * much faster
	 * @author christianriekoff
	 *
	 */
	private class CCParticleGPUInternalPipeline implements CCParticlePipeline{
		private CLGLBuffer<?> _myPositions;
		
		private CCBufferObject _myGLVelocityBuffer;
		private CLGLBuffer<?> _myVelocities;
		
		private CCBufferObject _myGLAccelerationBuffer;
		private CLGLBuffer<?> _myAccelerations;
		
		private CCBufferObject _myGLInfosBuffer;
		private CLGLBuffer<?> _myInfos;
		
		private CCParticleGPUInternalPipeline() {
			_myPositions = _myOpenCL.createFromGLBuffer(_myMesh.vertexBuffer(), Mem.READ_WRITE);
			
			_myGLVelocityBuffer = new CCBufferObject(_myNumberOfParticles * 4 * CCBufferUtil.SIZE_OF_FLOAT);
			_myVelocities = _myOpenCL.createFromGLBuffer(_myGLVelocityBuffer, Mem.READ_WRITE);
			
			_myGLAccelerationBuffer = new CCBufferObject(_myNumberOfParticles * 4 * CCBufferUtil.SIZE_OF_FLOAT);
			_myAccelerations = _myOpenCL.createFromGLBuffer(_myGLAccelerationBuffer, Mem.READ_WRITE);
			
			_myGLInfosBuffer = new CCBufferObject(_myNumberOfParticles * 4 * CCBufferUtil.SIZE_OF_FLOAT);
			_myInfos = _myOpenCL.createFromGLBuffer(_myGLInfosBuffer, Mem.READ_WRITE);
			_myCommandQueue.putAcquireGLObject(_myInfos.ID);
			_myCommandQueue.putAcquireGLObject(_myAccelerations.ID);
			_myCommandQueue.putAcquireGLObject(_myVelocities.ID);
		}

		@Override
		public CLMemory<?> positions() {
			return _myPositions;
		}

		@Override
		public CLMemory<?> velocities() {
			return _myVelocities;
		}

		@Override
		public CLMemory<?> accelerations() {
			return _myAccelerations;
		}
		
		@Override
		public CLMemory<?> infos(){
			return _myInfos;
		}

		@Override
		public void begin() {
			_myCommandQueue.putAcquireGLObject(_myPositions.ID);
		}

		@Override
		public void end() {
//			_myCommandQueue.putReleaseGLObject(_myInfos.ID);
//			_myCommandQueue.putReleaseGLObject(_myAccelerations.ID);
//			_myCommandQueue.putReleaseGLObject(_myVelocities.ID);
			_myCommandQueue.putReleaseGLObject(_myPositions.ID);
		}

		/* (non-Javadoc)
		 * @see cc.creativecomputing.particles.CCParticles.CCParticlePipeline#createBuffer()
		 */
		@Override
		public CLMemory<?> createBuffer() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * must be used in case opengl sharing  is not supported
	 * slow because of copying buffers
	 * @author christianriekoff
	 *
	 */
	private class CCParticleCopyBufferPipeline implements CCParticlePipeline{

		private CLBuffer<FloatBuffer> _myPositions;
		private CLBuffer<FloatBuffer> _myVelocities;
		private CLBuffer<FloatBuffer> _myAccelerations;
		private CLBuffer<FloatBuffer> _myInfos;
		
		private CCParticleCopyBufferPipeline() {
			_myPositions = _myOpenCL.createCLFloatBuffer(_myNumberOfParticles * 4, Mem.READ_WRITE);
			_myVelocities = _myOpenCL.createCLFloatBuffer(_myNumberOfParticles * 4, Mem.READ_WRITE);
			_myAccelerations = _myOpenCL.createCLFloatBuffer(_myNumberOfParticles * 4, Mem.READ_WRITE);
			_myInfos = _myOpenCL.createCLFloatBuffer(_myNumberOfParticles * 4, Mem.READ_WRITE);

			_myCommandQueue.putWriteBuffer(_myAccelerations, false);
			_myCommandQueue.putWriteBuffer(_myVelocities, false);
			_myCommandQueue.putWriteBuffer(_myPositions, false);
			_myCommandQueue.putWriteBuffer(_myInfos, false);
		}
		
		@Override
		public CLMemory<?> positions() {
			return _myPositions;
		}

		@Override
		public CLMemory<?> velocities() {
			return _myVelocities;
		}

		@Override
		public CLMemory<?> accelerations() {
			return _myAccelerations;
		}
		
		@Override
		public CLMemory<?> infos() {
			return _myInfos;
		}

		@Override
		public void begin() {
		}

		@Override
		public void end() {
			_myCommandQueue.putReadBuffer(_myPositions, true);
			_myMesh.vertices(_myPositions.getBuffer());
//			System.out.println(_myPositions.getBuffer().capacity()+ ":" + _myNumberOfParticles);
//			for(int i = 0; i < 1 * 4;i+=4) {
//				System.out.println(
//						_myInfos.getBuffer().get(i + 0) + " , " +
//						_myInfos.getBuffer().get(i + 1) + " , " +
//						_myInfos.getBuffer().get(i + 2) + " , " +
//						_myInfos.getBuffer().get(i + 3) 
//				);
//			}
		}

		@Override
		public CLMemory<?> createBuffer() {
			return _myOpenCL.context().createFloatBuffer(_myNumberOfParticles * 4, Mem.READ_WRITE);
		}
		
	}

	private CCOpenCL _myOpenCL;
	private int _myNumberOfParticles;
	
	
	private CCParticlePipeline _myPipeline;
	
	private CCCLProgram _myParticleProgram;
	private CCCLCommandQueue _myCommandQueue;
	
	private CCCLKernel _myIntegrateKernel;
	private CCCLKernel _myClearBufferKernel;
	
	private CCVBOMesh _myMesh;
	
	private CCParticleEmitter _myEmitter;
	
	private List<CCForce> _myForces = new ArrayList<CCForce>();
	
	public CCParticles(CCGraphics g, int theNumberOfParticles) {
		_myNumberOfParticles = theNumberOfParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myNumberOfParticles, 4);
		
		_myOpenCL = new CCOpenCL("Apple",0,g);
		_myOpenCL.printInfos();
		
		_myParticleProgram = new CCCLProgram(_myOpenCL);
		_myParticleProgram.appendSource(CCParticles.class, "integrate.cl");
		_myParticleProgram.build();
		
		_myCommandQueue = new CCCLCommandQueue(_myOpenCL);
		
		if(_myOpenCL.isGLSharing()) {
			System.out.println("GL SHARING");
			_myPipeline = new CCParticleGPUInternalPipeline();
		}else {
			_myPipeline = new CCParticleCopyBufferPipeline();
		}
		
		_myIntegrateKernel = _myParticleProgram.createKernel("integrate");
		_myIntegrateKernel.argument(0, _myPipeline.accelerations());
		_myIntegrateKernel.argument(1, _myPipeline.velocities());
		_myIntegrateKernel.argument(2, _myPipeline.positions());
		_myIntegrateKernel.argument(3, _myPipeline.infos());
		
		_myClearBufferKernel = _myParticleProgram.createKernel("clearBuffer");
		_myClearBufferKernel.argument(0, _myPipeline.accelerations());
		
		_myEmitter = new CCParticleEmitter(this);
	}
	
	public int size(){
		return _myNumberOfParticles;
	}
	
	public CCOpenCL openCL(){
		return _myOpenCL;
	}
	
	public CLMemory<?> createBuffer(){
		return _myPipeline.createBuffer();
	}
	
	public CLMemory<?> positions(){
		return _myPipeline.positions();
	}
	
	public CLMemory<?> velocities(){
		return _myPipeline.velocities();
	}
	
	public CLMemory<?> accelerations(){
		return _myPipeline.accelerations();
	}
	
	public CLMemory<?> infos(){
		return _myPipeline.infos();
	}
	
	public CCCLProgram program() {
		return _myParticleProgram;
	}
	
	public CCParticleEmitter emiter(){
		return _myEmitter;
	}
	
	public void addForce(CCForce theForce) {
		theForce.build(this, accelerations());
		_myForces.add(theForce);
	}
	
	public void removeForce(CCForce theForce) {
		if(_myForces.remove(theForce))theForce.release();
	}
	
	public void clearBuffer(CLMemory<?> theBuffer) {
		_myClearBufferKernel.argument(0, theBuffer);
		_myCommandQueue.put1DKernel(_myClearBufferKernel, _myNumberOfParticles);
	}
	
	public void update(final float theDeltaTime){
		if(theDeltaTime <= 0)return;
		
		_myPipeline.begin();
		
		_myEmitter.update(theDeltaTime);
		_myEmitter.addCommands(_myCommandQueue);
		
		clearBuffer(accelerations());
		
		for(CCForce myForce:_myForces) {
			myForce.addCommands(_myCommandQueue, theDeltaTime);
		}
		
		_myIntegrateKernel.argument1f(4, theDeltaTime);
		_myCommandQueue.put1DKernel(_myIntegrateKernel, _myNumberOfParticles);
		
		_myPipeline.end();
		
		_myCommandQueue.finish();
	}
	
	public void draw(CCGraphics g){
		_myMesh.draw(g);
	}
	
	@Override
	public void dispose(){
		
	}
}

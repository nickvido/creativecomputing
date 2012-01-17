package cc.creativecomputing.opencl;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLImage2d;

public class CCCLCommandQueue {

	private CCOpenCL _myCL;
	private CLCommandQueue _myCommandQueue;
	
	public CCCLCommandQueue(CCOpenCL theCL){
		_myCL = theCL;
		_myCommandQueue = _myCL.createCommandQueue();
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkOffset, int theGlobalWorkSize, int theLocalWorkSize){
		_myCommandQueue.put1DRangeKernel(theKernel.clKernel(), theGlobalWorkOffset, theGlobalWorkSize, theLocalWorkSize);
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkOffset, int theGlobalWorkSize){
		put1DKernel(theKernel, theGlobalWorkOffset, theGlobalWorkSize, 0);
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkSize){
		put1DKernel(theKernel, 0, theGlobalWorkSize, 0);
	}

	public void putReadBuffer(CLBuffer<?> theBuffer, boolean theIsBlocking) {
		_myCommandQueue.putReadBuffer(theBuffer, theIsBlocking);
	}

	public void putWriteBuffer(CLBuffer<?> theBuffer, boolean theIsBlocking) {
		_myCommandQueue.putWriteBuffer(theBuffer, theIsBlocking);
	}

	public void putWriteImage(CLImage2d<?> theImage, boolean theIsBlocking) {
		_myCommandQueue.putWriteImage(theImage, theIsBlocking);
	}

	public void putAcquireGLObject(long iD) {
		_myCommandQueue.putAcquireGLObject(iD);
	}

	public void putReleaseGLObject(long iD) {
		_myCommandQueue.putReleaseGLObject(iD);
	}

	public void finish() {
		_myCommandQueue.finish();
	}
}

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
package cc.creativecomputing.opencl.demos;

import java.nio.FloatBuffer;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLMemory.Mem;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;

public class CCSimpleAddKernelDemo extends CCApp {
	
	CCOpenCL _myOpenCL;
	
	CCCLProgram _myCLProgram;
	CLCommandQueue _myCommands;
	CCCLKernel _myKernel;
	
	int _mySize = 1000000;
	
	CLBuffer<FloatBuffer> _myBufferA;
	CLBuffer<FloatBuffer> _myBufferB;
	CLBuffer<FloatBuffer> _myResultBuffer;

	@Override
	public void setup() {
		_myOpenCL = new CCOpenCL("Apple", 0, null);
		
		CCOpenCL.printInfos();
		
		_myBufferA = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);
		_myBufferB = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);
		_myResultBuffer = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);
		
		for(int i = 0; i < _mySize;i++) {
			_myBufferA.getBuffer().put(i);
			_myBufferB.getBuffer().put(i * 2);
			_myResultBuffer.getBuffer().put(0);
		}
		_myBufferA.getBuffer().rewind();
		_myBufferB.getBuffer().rewind();
		_myResultBuffer.getBuffer().rewind();
		
		_myCLProgram = new CCCLProgram(_myOpenCL);
		_myCLProgram.appendSource(CCSimpleAddKernelDemo.class, "simpleAdd.cl");
		_myCLProgram.build();
		
		
		
		_myKernel = _myCLProgram.createKernel("simpleAdd");
		
		_myCommands = _myOpenCL.createCommandQueue();
		System.out.println(_myCommands.getDevice().getVendor());
		System.out.println(_myCommands.getDevice().getMaxComputeUnits());
		System.out.println(_myCommands.getDevice().getMaxWorkGroupSize());
		
		
		long time = System.currentTimeMillis();
		
		_myCommands.putWriteBuffer(_myBufferA, false);
		_myCommands.putWriteBuffer(_myBufferB, false);
		_myCommands.putWriteBuffer(_myResultBuffer, false);

		_myKernel.argument(0, _myBufferA);
		_myKernel.argument(1, _myBufferB);
		_myKernel.argument(2, _myResultBuffer);
		_myCommands.put1DRangeKernel(_myKernel.clKernel(), 0, _mySize, 100);
		
		_myCommands.putReadBuffer(_myResultBuffer, true);
		_myCommands.finish();
		
		_myBufferA.getBuffer().rewind();
		_myBufferB.getBuffer().rewind();
		_myResultBuffer.getBuffer().rewind();
		
		System.out.println("ms spend: " + (System.currentTimeMillis() - time));
		
//		for(int i = 0; i < _mySize;i++) {
//			System.out.println(
//				i + 
//				": A=" + _myBufferA.getBuffer().get() + 
//				": B=" + _myBufferB.getBuffer().get() + 
//				": RESULT=" + _myResultBuffer.getBuffer().get()
//			);
//		}
		
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSimpleAddKernelDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


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
package cc.creativecomputing.opencl.programmingguide;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import cc.creativecomputing.io.CCBufferUtil;

import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opencl.CL;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLException;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLMemory.Mem;

/**
 * @author christianriekoff
 * 
 */
public class CCChapter0201OpenClHelloWord {
	
	void run() throws Exception {
		CLPlatform.initialize();
		
		// create a opencl context on the first available platform
		// First, select an OpenCL platform to run on. For this example, 
		// we simply choose the first available platform. Normally, you 
		// would query for all available platforms and select the most 
		// appropriate one.
		CLPlatform myPlatForm = CLPlatform.listCLPlatforms()[0];
		
		// Next, create an OpenCL context on the platform. Attempt to 
		// create a GPU-based context, and if that fails, try to create 
		// a CPU-based context. 
		CLContext myContext;
		try {
			myContext = CLContext.create(myPlatForm, CLDevice.Type.GPU);
		}catch(CLException e) {
			myContext = CLContext.create(myPlatForm, CLDevice.Type.CPU);
		}
		
		// In this example, we just choose the first available device. 
		// In a real program, you would likely use all available devices 
		// or choose the highest performance device based on OpenCL device 
		// queries 
		CLDevice myDevice = myContext.getDevices()[0];
		
		// Create a command-queue on the first device available 
		// on the created context
		CLCommandQueue myCommandQueue = myDevice.createCommandQueue();
		
		// Create OpenCL program from HelloWorld.cl kernel source
		// load sources, create and build program
        CLProgram myProgram = myContext.createProgram(CCChapter0201OpenClHelloWord.class.getResourceAsStream("Add.cl"));
        myProgram.build();
        
        // Create OpenCL kernel
        CLKernel myKernel = myProgram.createCLKernel("add");
        CLKernel myKernel2 = myProgram.createCLKernel("multiply");
        
        int myArraySize = 100;
        
        // Create memory objects that will be used as arguments to 
        // kernel. First create host memory arrays that will be 
        // used to store the arguments to the kernel
        CLBuffer<FloatBuffer> myA = myContext.createFloatBuffer(myArraySize, Mem.READ_WRITE);
        CLBuffer<FloatBuffer> myB = myContext.createFloatBuffer(myArraySize, Mem.READ_WRITE);
        
        for(int i = 0; i < myArraySize;i++) {
        	myA.getBuffer().put(i, i);
        	myB.getBuffer().put(i, i);
        }
        
        // Set the kernel arguments (result, a, b)
        myKernel.setArg(0, myA);
        myKernel.setArg(1, myB);
        
        for(int j = 0; j < 10; j++) {
        // Queue the kernel up for execution across the array
        myCommandQueue.putWriteBuffer(myA, false);
        myCommandQueue.putWriteBuffer(myB, false);
        myCommandQueue.put1DRangeKernel(myKernel, 0, myArraySize, 1);
        myCommandQueue.putReadBuffer(myB, true);
        
        // Output the result buffer 
        for (int i = 0; i < myArraySize; i++) {
        	System.out.println(myB.getBuffer().get(i) + ":" );
        }
        }
        myContext.release();
	}
		
	public static void main(String[] args) {
		try {
			new CCChapter0201OpenClHelloWord().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

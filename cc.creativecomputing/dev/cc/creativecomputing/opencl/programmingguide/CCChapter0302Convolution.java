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

import java.nio.IntBuffer;

import com.jogamp.opencl.CL;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLDevice.Type;
import com.jogamp.opencl.CLMemory.Mem;

/**
 * @author christianriekoff
 * 
 */
public class CCChapter0302Convolution {
	// Constants 
	public static int inputSignalWidth = 8;
	public static int inputSignalHeight = 8;
	
	int[][] inputSignal = {
		{3, 1, 1, 4, 8, 2, 1, 3}, 
		{4, 2, 1, 1, 2, 1, 2, 3}, 
		{4, 4, 4, 4, 3, 2, 2, 2}, 
		{9, 8, 3, 8, 9, 0, 0, 0}, 
		{9, 3, 3, 9, 0, 0, 0, 0}, 
		{0, 9, 0, 8, 0, 0, 0, 0}, 
		{3, 0, 8, 8, 9, 4, 4, 4}, 
		{5, 9, 8, 1, 8, 1, 1, 1}
	}; 
	
	public static int outputSignalWidth = 6;
	public static int outputSignalHeight = 6;
			
	int[][] outputSignal = new int[outputSignalWidth][outputSignalHeight];
	public static int maskWidth = 3; 
	public static int maskHeight = 3;
			
	int[][] mask = {
		{1, 1, 1}, 
		{1, 0, 1}, 
		{1, 1, 1},
	};
	
	void run() throws Exception {
		CLPlatform.initialize();
		
		// query all platforms
		CLPlatform[] myPlatForms = CLPlatform.listCLPlatforms();
		CLContext myContext = CLContext.create(myPlatForms[0],Type.CPU);
		
		CLProgram myProgram = myContext.createProgram(CCChapter0201OpenClHelloWord.class.getResourceAsStream("Convolution.cl"));
        myProgram.build();
		
        CLKernel myKernel = myProgram.createCLKernel("convolve");
        
        CLBuffer<IntBuffer> myInputSignalBuffer = myContext.createIntBuffer(inputSignalWidth * inputSignalHeight, Mem.READ_ONLY);
        for(int x = 0; x < inputSignalWidth;x++) {
        	for(int y = 0; y < inputSignalHeight;y++) {
        		myInputSignalBuffer.getBuffer().put(y * inputSignalWidth + x, inputSignal[x][y]);
            }
        }
        CLBuffer<IntBuffer> myMaskSignalBuffer = myContext.createIntBuffer(maskWidth * maskHeight, Mem.READ_ONLY);
        for(int x = 0; x < maskWidth;x++) {
        	for(int y = 0; y < maskHeight;y++) {
        		myMaskSignalBuffer.getBuffer().put(y * maskWidth + x, mask[x][y]);
            }
        }
        CLBuffer<IntBuffer> outputSignalBuffer = myContext.createIntBuffer(outputSignalWidth * outputSignalHeight, Mem.WRITE_ONLY);
        
        myKernel.setArg(0, myInputSignalBuffer);
        myKernel.setArg(1, myMaskSignalBuffer);
        myKernel.setArg(2, outputSignalBuffer);
        myKernel.setArg(3, inputSignalWidth);
        myKernel.setArg(4, maskWidth);
        
        CLCommandQueue myCommandQueue = myContext.getMaxFlopsDevice().createCommandQueue();
        myCommandQueue.putWriteBuffer(myInputSignalBuffer, false);
        myCommandQueue.putWriteBuffer(myMaskSignalBuffer, false);
        myCommandQueue.put1DRangeKernel(myKernel, 0, outputSignalWidth * outputSignalHeight, 1);
        myCommandQueue.putReadBuffer(outputSignalBuffer, true);
        
        for(int x = 0; x < outputSignalWidth;x++) {
        	for(int y = 0; y < outputSignalHeight;y++) {
        		System.out.print(outputSignalBuffer.getBuffer().get(y * outputSignalWidth + x) +",");
            }
        	System.out.println();
        }
	}
		
	public static void main(String[] args) {
		try {
			new CCChapter0302Convolution().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

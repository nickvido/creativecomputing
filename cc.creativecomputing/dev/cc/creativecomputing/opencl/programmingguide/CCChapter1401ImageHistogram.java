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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import jogamp.opengl.windows.wgl.GPU_DEVICE;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.opencl.CCOpenCL;

import com.jogamp.opencl.CL;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLDevice.Type;
import com.jogamp.opencl.CLMemory.Mem;

/**
 * @author christianriekoff
 * 
 */
public class CCChapter1401ImageHistogram {
	// Constants 
//	int image_width = 1920;
//	int image_height = 1080;
//	size_t global_work_size[2];
//	size_t local_work_size[2];
//	size_t partial_global_work_size[2];
//	size_t partial_local_work_size[2];
//	
//	size_t workgroup_size; 
//	size_t num_groups; 
//	cl_kernel histogram_rgba_unorm8; 
//	cl_kernel histogram_sum_partial_results_unorm8; 
//	size_t gsize[2];
	
	void run() throws Exception {
		CCOpenCL myOpenCL = new CCOpenCL("apple",0,null);
		
		System.out.println();
		CLContext myContext = myOpenCL.context();
		CLDevice myDevice = myContext.getMaxFlopsDevice();
		
		CLProgram myProgram = myContext.createProgram(CCChapter0201OpenClHelloWord.class.getResourceAsStream("histogram.cl"));
        myProgram.build();
		
        CLKernel histogram_rgba_unorm8 = myProgram.createCLKernel("histogramPartialImageRGBAUnorm8");
        CLKernel histogram_sum_partial_results_unorm8 = myProgram.createCLKernel("histogram_sum_partial_results_unorm8");
        
        // load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");
		
        long myWorkGroupSize = histogram_rgba_unorm8.getWorkGroupSize(myDevice);
        System.out.println("myWorkGroupSize:" + myWorkGroupSize);
        
        long myWorkGroupSizeX;
        long myWorkGroupSizeY;
        
        if (myWorkGroupSize <= 256) {
        	myWorkGroupSizeX = 16; 
        	myWorkGroupSizeY = myWorkGroupSize / 16;
        } else if (myWorkGroupSize <= 1024) {
        	myWorkGroupSizeX = myWorkGroupSize / 16; 
        	myWorkGroupSizeY = 16;
        } else {
        	myWorkGroupSizeX = myWorkGroupSize / 32; 
        	myWorkGroupSizeY = 32;
        }

        int myImageWidth = myImage.width();
        int myImageHeight = myImage.height();
        
        long myGlobalWorkSizeX = ((myImageWidth + myWorkGroupSizeX - 1) / myWorkGroupSizeX); 
        long myGlobalWorkSizeY = ((myImageHeight + myWorkGroupSizeY - 1) / myWorkGroupSizeY);
        	
        long num_groups = myGlobalWorkSizeX * myGlobalWorkSizeY; 
        myGlobalWorkSizeX *= myWorkGroupSizeX; 
        myGlobalWorkSizeY *= myWorkGroupSizeY;

		// Create the memory object for the input- and output image
		CLImage2d<FloatBuffer> myInputImage = myOpenCL.createCLImage(myImage);
        CLBuffer<IntBuffer> myPartialHistogram = myContext.createIntBuffer(256 * 3 * (int)num_groups, Mem.READ_WRITE);
        CLBuffer<IntBuffer> myHistogram = myContext.createIntBuffer(256 * 3, Mem.WRITE_ONLY);
        
        histogram_rgba_unorm8.setArg(0, myInputImage);
        histogram_rgba_unorm8.setArg(1, myPartialHistogram);
        
        CLCommandQueue myCommandQueue = myDevice.createCommandQueue();
        myCommandQueue.putWriteImage(myInputImage, false);
        myCommandQueue.put2DRangeKernel(
        	histogram_rgba_unorm8, 
        	0, 0, 
        	myGlobalWorkSizeX, myGlobalWorkSizeY, 
        	myWorkGroupSizeX, myWorkGroupSizeY
        );
        System.out.println(myGlobalWorkSizeX+":"+myGlobalWorkSizeY);
//        myCommandQueue.putReadBuffer(myHistogram, true);
        
        myWorkGroupSize = histogram_sum_partial_results_unorm8.getWorkGroupSize(myDevice);
        if (myWorkGroupSize < 256) {
        	System.out.printf(
        		"A min. of 256 work-items in work-group is needed for " +
        		"histogram_sum_partial_results_unorm8 kernel. (%d)\n", (int)myWorkGroupSize);
        }
        histogram_sum_partial_results_unorm8.setArg(0, myPartialHistogram);
        histogram_sum_partial_results_unorm8.setArg(1, num_groups);
        histogram_sum_partial_results_unorm8.setArg(2, myHistogram);
        myCommandQueue.put1DRangeKernel(histogram_sum_partial_results_unorm8, 0, 256 * 3, 256);
        myCommandQueue.putReadBuffer(myPartialHistogram, false);
        myCommandQueue.putReadBuffer(myHistogram, true);
        
//        for(int g = 0; g < num_groups;g++) {
//        	int offset = g * 256 * 3;
//	        for(int i = 0; i < 256; i++) {
//	        	int c1 = myPartialHistogram.getBuffer().get(offset + i);
//	        	int c2 = myPartialHistogram.getBuffer().get(offset + i + 256);
//	        	int c3 = myPartialHistogram.getBuffer().get(offset + i + 512);
//	        	if(c1 == 0 && c2 == 0 && c3 == 0)continue;
//	        	System.out.println();
//	        	System.out.print(i + ":" + c1 + " : ");
//	        	System.out.print(c2 + " : ");
//	        	System.out.print(c3);
//	        }
//        }
        
//        for(int i = 0; i < 256;i++) {
//        	int c1 = myHistogram.getBuffer().get(i);
//        	int c2 = myHistogram.getBuffer().get(i + 256);
//        	int c3 = myHistogram.getBuffer().get(i + 512);
//        	if(c1 == 0 && c2 == 0 && c3 == 0)continue;
//        	System.out.println();
//        	System.out.print(i + ":" + c1 + " : ");
//        	System.out.print(c2 + " : ");
//        	System.out.print(c3);
//        }
        
//        CLBuffer<IntBuffer> myInputSignalBuffer = myContext.createIntBuffer(inputSignalWidth * inputSignalHeight, Mem.READ_ONLY);
//        for(int x = 0; x < inputSignalWidth;x++) {
//        	for(int y = 0; y < inputSignalHeight;y++) {
//        		myInputSignalBuffer.getBuffer().put(y * inputSignalWidth + x, inputSignal[x][y]);
//            }
//        }
//        CLBuffer<IntBuffer> myMaskSignalBuffer = myContext.createIntBuffer(maskWidth * maskHeight, Mem.READ_ONLY);
//        for(int x = 0; x < maskWidth;x++) {
//        	for(int y = 0; y < maskHeight;y++) {
//        		myMaskSignalBuffer.getBuffer().put(y * maskWidth + x, mask[x][y]);
//            }
//        }
//        CLBuffer<IntBuffer> outputSignalBuffer = myContext.createIntBuffer(outputSignalWidth * outputSignalHeight, Mem.WRITE_ONLY);
//        
//        myKernel.setArg(0, myInputSignalBuffer);
//        myKernel.setArg(1, myMaskSignalBuffer);
//        myKernel.setArg(2, outputSignalBuffer);
//        myKernel.setArg(3, inputSignalWidth);
//        myKernel.setArg(4, maskWidth);
//        
//        CLCommandQueue myCommandQueue = myContext.getMaxFlopsDevice().createCommandQueue();
//        myCommandQueue.putWriteBuffer(myInputSignalBuffer, false);
//        myCommandQueue.putWriteBuffer(myMaskSignalBuffer, false);
//        myCommandQueue.put1DRangeKernel(myKernel, 0, outputSignalWidth * outputSignalHeight, 1);
//        myCommandQueue.putReadBuffer(outputSignalBuffer, true);
//        
//        for(int x = 0; x < outputSignalWidth;x++) {
//        	for(int y = 0; y < outputSignalHeight;y++) {
//        		System.out.print(outputSignalBuffer.getBuffer().get(y * outputSignalWidth + x) +",");
//            }
//        	System.out.println();
//        }
	}
		
	public static void main(String[] args) {
		try {
			new CCChapter1401ImageHistogram().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

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
public class CCChapter1401ImageHistogramSimple {
	
	void run() throws Exception {
		CCOpenCL myOpenCL = new CCOpenCL();
		
		System.out.println();
		CLContext myContext = myOpenCL.context();
		CLDevice myDevice = myContext.getMaxFlopsDevice();
		
		CLProgram myProgram = myContext.createProgram(CCChapter0201OpenClHelloWord.class.getResourceAsStream("histogram_simple.cl"));
        myProgram.build();
		
        CLKernel myHistogramKernel = myProgram.createCLKernel("histogram");
        
        // load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");

		// Create the memory object for the input- and output image
		CLImage2d<FloatBuffer> myInputImage = myOpenCL.createCLImage(myImage);
        CLBuffer<IntBuffer> myHistogram = myContext.createIntBuffer(256 * 3, Mem.WRITE_ONLY);
        
        myHistogramKernel.setArg(0, myInputImage);
        myHistogramKernel.setArg(1, myHistogram);
        
        CLCommandQueue myCommandQueue = myDevice.createCommandQueue();
        myCommandQueue.putWriteImage(myInputImage, false);
        myCommandQueue.put2DRangeKernel(
        	myHistogramKernel, 
        	0, 0, 
        	myImage.width(), myImage.height(), 
        	1, 1
        );
        myCommandQueue.putReadBuffer(myHistogram, true);
        
        for(int i = 0; i < 256;i++) {
        	int c1 = myHistogram.getBuffer().get(i);
        	int c2 = myHistogram.getBuffer().get(i + 256);
        	int c3 = myHistogram.getBuffer().get(i + 512);
//        	if(c1 == 0 && c2 == 0 && c3 == 0)continue;
        	System.out.println();
        	System.out.print(i + ":" + c1 + " : ");
        	System.out.print(c2 + " : ");
        	System.out.print(c3);
        }
        System.out.println("YO");
	}
		
	public static void main(String[] args) {
		try {
			new CCChapter1401ImageHistogramSimple().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

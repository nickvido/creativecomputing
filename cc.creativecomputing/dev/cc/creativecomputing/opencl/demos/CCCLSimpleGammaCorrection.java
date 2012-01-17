/*
 * Copyright 2009 - 2011 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

/*
 * Created on Monday, December 13 2010 17:43
 */

package cc.creativecomputing.opencl.demos;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.opencl.CCOpenCL;
import cc.creativecomputing.opencl.CCOpenCLUtil;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLProgram.CompilerOptions;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import static com.jogamp.opencl.CLProgram.*;

/**
 * Computes the classical gamma correction for a given image. http://en.wikipedia.org/wiki/Gamma_correction
 * 
 * @author Michael Bien
 */
public class CCCLSimpleGammaCorrection extends CCApp{
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	private CCTexture2D _myTexture3;
	
	private void gammaCorrection(float gamma, CLCommandQueue queue, CLKernel kernel, CLBuffer<FloatBuffer> buffer, int localWorkSize, int globalWorkSize) {

		float scaleFactor = (float) Math.pow(255, 1.0f - gamma);

		// setup kernel
		kernel.putArg(buffer).putArg(gamma).putArg(scaleFactor).putArg(buffer.getNIOSize()).rewind();

		// CLEventList list = new CLEventList(1);

		queue.putWriteBuffer(buffer, false); // upload image
		queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize/* , list */); // execute program
		queue.putReadBuffer(buffer, true); // read results back (blocking read)

		// CLEvent event = list.getEvent(0);
		// System.out.println((event.getProfilingInfo(END)
		// - event.getProfilingInfo(START))/1000000.0);
		//        
		// long res = queue.getDevice().getProfilingTimerResolution();
		// System.out.println(res);

	}
	
	@Override
	public void setup() {
		// find a CL implementation
		CCOpenCL myOpenCl = new CCOpenCL();
		CLContext myContext = myOpenCl.context();

		// load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");

		// allocate a OpenCL buffer using the direct fb as working copy
		CLBuffer<FloatBuffer> buffer = myOpenCl.createCLFloatBuffer(myImage, CLBuffer.Mem.READ_WRITE);
		
		// create a command queue with benchmarking flag set
		CLCommandQueue queue = myContext.getDevices()[0].createCommandQueue(Mode.PROFILING_MODE);

		// Local work size dimensions
		int localWorkSize = queue.getDevice().getMaxWorkGroupSize();
		
		// rounded up to the nearest multiple of the localWorkSize
		int globalWorkSize = CCOpenCLUtil.roundUp(localWorkSize, buffer.getBuffer().capacity()); 
																
		
		// load and compile program for the chosen device
		CLProgram program = myOpenCl.createProgram(CCCLSimpleGammaCorrectionImage2d.class, "Gamma.cl");
		program.build(CompilerOptions.FAST_RELAXED_MATH);
		
		// create kernel and set function parameters
		CLKernel kernel = program.createCLKernel("gamma");

		// a few gamma corrected versions
		gammaCorrection(0.5f, queue, kernel, buffer, localWorkSize, globalWorkSize);
		_myTexture1 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		gammaCorrection(1.5f, queue, kernel, buffer, localWorkSize, globalWorkSize);
		_myTexture2 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		gammaCorrection(2.0f, queue, kernel, buffer, localWorkSize, globalWorkSize);
		_myTexture3 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		myContext.release();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#draw()
	 */
	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture1, -width/2, -height/2);
		g.image(_myTexture2, -width/2 + 512, -height/2);
		g.image(_myTexture3, -width/2 + 1024, -height/2);
	}

	public static void main(String[] args) throws IOException {
		CCApplicationManager myManager = new CCApplicationManager(CCCLSimpleGammaCorrection.class);
		myManager.settings().size(512 * 3, 512);
		myManager.start();
	}

}

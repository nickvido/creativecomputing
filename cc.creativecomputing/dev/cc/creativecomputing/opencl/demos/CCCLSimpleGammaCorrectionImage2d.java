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

import com.jogamp.common.nio.Buffers;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLImageFormat.ChannelOrder;
import com.jogamp.opencl.CLImageFormat.ChannelType;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.sun.corba.se.impl.ior.ByteBuffer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
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
public class CCCLSimpleGammaCorrectionImage2d extends CCApp {

	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@Override
	public void setup() {
		// find a CL implementation
		CCOpenCL myOpenCl = new CCOpenCL();
		CLContext myContext = myOpenCl.context();

		// load and compile program for the chosen device
		CLProgram program = myOpenCl.createProgram(CCCLSimpleGammaCorrectionImage2d.class, "gamma_image2d.cl");
		program.build(CompilerOptions.FAST_RELAXED_MATH);

		// load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");
		_myTexture1 = new CCTexture2D(myImage);

		// Create the memory object for the input- and output image
		CLImage2d<FloatBuffer> myInputImage = myOpenCl.createCLImage(myImage);
		CLImage2d<FloatBuffer> myOutputImage = myOpenCl.createCLImage(myImage.width(), myImage.height());

		// create kernel and set function parameters
		CLKernel myKernel = program.createCLKernel("gamma");

		// a few gamma corrected versions
		float gamma = 0.5f;
		float scaleFactor = (float) Math.pow(255, 1.0f - gamma);

		// setup kernel
		myKernel.setArg(0,myInputImage);
		myKernel.setArg(1,myOutputImage);
		myKernel.setArg(2,gamma);
		myKernel.setArg(3,scaleFactor);
		
		// create a command queue with benchmarking flag set
		CLCommandQueue queue = myContext.getDevices()[0].createCommandQueue(Mode.PROFILING_MODE);
		queue.putWriteImage(myInputImage, false); // upload image
		queue.put2DRangeKernel(myKernel, 0, 0, myImage.width(), myImage.height(), 0, 0); // execute program
		queue.putReadImage(myOutputImage, true);
		
		for(int i = 0; i < 512;i++) {
			System.out.println(
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get()
			);
		}
		
		CCTextureData myOutput = myOpenCl.createTextureData(myOutputImage);
		_myTexture2 = new CCTexture2D(myOutput);

		myContext.release();
	}
	
	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture1, -512, -256);
		g.image(_myTexture2, 0, -256);
	}

	public static void main(String[] args) throws IOException {
		CCApplicationManager myManager = new CCApplicationManager(CCCLSimpleGammaCorrectionImage2d.class);
		myManager.settings().size(1024, 512);
		myManager.start();
	}
}

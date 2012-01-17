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
package cc.creativecomputing.graphics.texture.video;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureUtil;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 * 
 */
public class CCScreenCaptureData extends CCVideoData {
	
	private class CaptureThread extends Thread {
		public void run() {
			while ((Thread.currentThread() == _myCaptureThread)) {
					updateTexture();

					_myIsDataUpdated = true;
				try {
					Thread.sleep(1000 / (int) _myFrameRate);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	protected CaptureThread _myCaptureThread;
	private float _myFrameRate;

	/** True if this image is currently being cropped */
	protected boolean _myIsCrop;

	protected int _myCropX;
	protected int _myCropY;
	
	private int _myCaptureX;
	private int _myCaptureY;

	protected int _myCaptureWidth;
	protected int _myCaptureHeight;

	protected BufferedImage _myScreenShot;
	
	private Robot _myRobot;
	
	/**
	 * Indicates that data has been updated inside the thread
	 */
	private boolean _myIsDataUpdated = false;

	/**
	 * @param theApp
	 */
	public CCScreenCaptureData(
		final CCAbstractApp theApp, 
		final int theX, final int theY,
		final int theWidth, final int theHeight, final int theFrameRate
	) {
		super(theApp);
		
		_myCaptureX = theX;
		_myCaptureY = theY;
		
		_myCaptureWidth = _myWidth = theWidth;
		_myCaptureHeight = _myHeight = theHeight;
		_myBorder = 0;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myFrameRate = theFrameRate;
		
		try {
			_myRobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_myCaptureThread = new CaptureThread();
		_myCaptureThread.start();
		
		_myIsFirstFrame = true;
	}
	
	public void captureArea(int theCaptureX, int theCaptureY, int theCaptureWidth, int theCaptureHeight) {
		_myCaptureX = theCaptureX;
		_myCaptureY = theCaptureY;
		_myCaptureWidth = theCaptureWidth;
		_myCaptureHeight = theCaptureHeight;
	}
	
	public void captureX(int theCaptureX) {
		_myCaptureX = theCaptureX;
	}
	
	public void captureY(int theCaptureY) {
		_myCaptureY = theCaptureY;
	}
	
	public void captureWidth(int theCaptureWidth) {
		_myCaptureWidth = theCaptureWidth;
	}
	
	public void captureHeight(int theCaptureHeight) {
		_myCaptureHeight = theCaptureHeight;
	}

	protected void updateTexture() {
		_myIsDataUpdated = true;
		long time = System.nanoTime();
		_myScreenShot = _myRobot.createScreenCapture(new Rectangle(_myCaptureX, _myCaptureY, _myCaptureWidth, _myCaptureHeight));
		
		time = System.nanoTime() - time;
	}

	public void stop() {
		_myCaptureThread = null; // unwind the thread
	}

	public void framerate(final float theFramerate) {
		if (theFramerate <= 0) {
			CCLog.error("Capture: ignoring bad framerate of " + theFramerate + " fps.");
			return;
		}
		_myFrameRate = theFramerate;
	}

	public float framerate() {
		return _myFrameRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	public void update(float theDeltaTime) {
		if (_myIsDataUpdated && _myScreenShot != null) {
			_myIsDataUpdated = false;
			CCTextureUtil.toTextureData(_myScreenShot, this);

			if (_myIsFirstFrame) {
				_myIsFirstFrame = false;
				_myListener.proxy().onInit(this);
			} else {
				_myListener.proxy().onUpdate(this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.events.CCPostListener#post()
	 */
	public void post() {
		// TODO Auto-generated method stub

	}

}

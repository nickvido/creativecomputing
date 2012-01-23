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

import java.nio.ByteBuffer;

import quicktime.Errors;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.qd.GDevice;
import quicktime.qd.PixMap;
import quicktime.qd.QDConstants;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTException;
import quicktime.std.sg.SGDeviceList;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;
import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 * 
 */
@SuppressWarnings("deprecation")
public class CCQuicktimeCapture extends CCVideoData {
	
	static public boolean QTinitialized = false;
	
	/**
	 * Get a list of all available capture devices as a String array.
	 *
	 * @return all available capture devices as a String array
	 */
	static public String[] listCaptureDevices() {
		try {
			if (!QTinitialized) {
				QTSession.open();
				QTinitialized = true;
			}
			SequenceGrabber grabber = new SequenceGrabber();
			SGVideoChannel channel = new SGVideoChannel(grabber);

			SGDeviceList deviceList = channel.getDeviceList(0); // flags is 0
			String listing[] = new String[deviceList.getCount()];
			for (int i = 0; i < deviceList.getCount(); i++) {
				listing[i] = deviceList.getDeviceName(i).getName();
			}
			// properly shut down the channel so the app can use it again
			grabber.disposeChannel(channel);
			return listing;

		} catch (QTException qte) {
			int errorCode = qte.errorCode();
			if (errorCode == Errors.couldntGetRequiredComponent) {
				throw new RuntimeException("Couldn't find any capture devices, " + "check the FAQ for more info.");
			} else {
				qte.printStackTrace();
				throw new RuntimeException("Problem listing capture devices, " + "check the FAQ for more info.");
			}
		}
		// return null;
	}

	private class CaptureThread extends Thread {
		public void run() {
			while ((Thread.currentThread() == _myCaptureThread) && (_myCapture != null)) {
				try {
					synchronized (_myCapture) {
						_myCapture.idle();
						updateTexture();

						_myIsDataUpdated = true;

					}

				} catch (QTException e) {
					// errorMessage("run", e);
				}

				try {
					Thread.sleep(1000 / (int) _myFrameRate);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static int counter = 0;

	private SequenceGrabber _myCapture;
	private SGVideoChannel _myChannel;
	private QDRect _myChannelRect;
	private boolean loaded = false;

	private RawEncodedImage _myRawImage;

	protected CaptureThread _myCaptureThread;
	private float _myFrameRate;

	/** True if this image is currently being cropped */
	protected boolean _myIsCrop;

	protected int _myCropX;
	protected int _myCropY;

	protected int _myCaptureWidth;
	protected int _myCaptureHeight;

	protected byte[] _myPixels;
	protected byte[] _myData;
	
	/**
	 * Indicates that data has been updated inside the thread
	 */
	private boolean _myIsDataUpdated = false;

	/**
	 * @param theApp
	 */
	public CCQuicktimeCapture(
		final CCAbstractApp theApp, final String theDeviceName, 
		final int theWidth, final int theHeight, final int theFrameRate
	) {
		super(theApp);

		// theApp.addDisposeListener(this);

		_myCaptureThread = new CaptureThread();

		_myWidth = theWidth;
		_myHeight = theHeight;
		_myBorder = 0;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myFrameRate = theFrameRate;
		init(theDeviceName);
		loaded = true;
	}
	
	public CCQuicktimeCapture(
		final CCAbstractApp theApp,  
		final int theWidth, final int theHeight, final int theFrameRate
	) {
		this(theApp, null, theWidth, theHeight, theFrameRate);
	}

	/** True if this image is currently being cropped */
	private void init(String theName) {
		try {
			if (!CCTextureIO.QTinitialized) {
				QTSession.open();
				CCTextureIO.QTinitialized = true;
			}

			_myChannelRect = new QDRect(_myWidth, _myHeight);
			// workaround for bug with the intel macs
			final QDGraphics myQuicktimeGraphics; // new QDGraphics(qdrect);

			if (quicktime.util.EndianOrder.isNativeLittleEndian()) {
				myQuicktimeGraphics = new QDGraphics(QDConstants.k32BGRAPixelFormat, _myChannelRect);
			} else {
				myQuicktimeGraphics = new QDGraphics(QDGraphics.kDefaultPixelFormat, _myChannelRect);
			}

			_myCapture = new SequenceGrabber();

			_myChannel = new SGVideoChannel(_myCapture);
			_myChannel.setBounds(_myChannelRect);
			_myChannel.setUsage(2); // what is this usage number?

			PixMap pixmap = myQuicktimeGraphics.getPixMap();
			_myRawImage = pixmap.getPixelData();

			if ((theName != null) && (theName.length() > 0)) {
				_myChannel.setDevice(theName);
			}

			int dataRowBytes = _myRawImage.getRowBytes();
			_myCaptureWidth = dataRowBytes / 4;
			_myCaptureHeight = _myRawImage.getSize() / dataRowBytes;

			if (_myCaptureWidth != _myWidth) {
				_myIsCrop = true;
				_myCropX = 0;
				_myCropY = 0;
			}

			_myPixels = new byte[_myWidth * _myHeight * 4];

			_myCapture.setGWorld(myQuicktimeGraphics, GDevice.get());
			_myCapture.startPreview(); // maybe this comes later?

			updateTexture();

			_myCaptureThread.start();
			// loop();
		} catch (StdQTException e) {
			e.printStackTrace();
		} catch (QTException e) {
			e.printStackTrace();
		}

		counter++;
	}

	protected void updateTexture() {
		_myIsDataUpdated = true;
		
		if (_myIsCrop) {
			// f#$)(#$ing quicktime / jni is so g-d slow, calling copyToArray
			// for the invidual rows is literally 100x slower. instead, first
			// copy the entire buffer to a separate array (i didn't need that
			// memory anyway), and do an arraycopy for each row.
			if (_myData == null) {
				_myData = new byte[_myCaptureWidth * _myCaptureHeight * 4];
			}

			_myRawImage.copyToArray(0, _myData, 0, _myCaptureWidth * _myCaptureHeight);
			int sourceOffset = _myCropX + _myCropY * _myCaptureWidth;
			int destOffset = 0;
			for (int y = 0; y < _myHeight; y++) {
				System.arraycopy(_myData, sourceOffset, _myPixels, destOffset, _myWidth);
				sourceOffset += _myCaptureWidth;
				destOffset += _myWidth;
			}
		} else { // no crop, just copy directly
			_myRawImage.copyToArray(0, _myPixels, 0, _myPixels.length);
		}

	}

	public void stop() {
		if (_myCapture != null) {
			try {
				_myCapture.stop(); // stop the "preview"
			} catch (StdQTException e) {
				e.printStackTrace();
			}
			_myCapture = null;
		}
		_myCaptureThread = null; // unwind the thread
	}

	public void framerate(final float theFramerate) {
		if (theFramerate <= 0) {
			CCLog.info("Capture: ignoring bad framerate of " + theFramerate + " fps.");
			return;
		}
		_myFrameRate = theFramerate;
	}

	public float framerate() {
		return _myFrameRate;
	}

	/**
	 * Set the video to crop from its original.
	 * <P>
	 * It seems common that captures add lines to the top or bottom of an image, so this can be useful for removing
	 * them. Internally, the pixel buffer size returned from QuickTime is often a different size than requested, so crop
	 * will be set more often than not.
	 */
	public void crop(int x, int y, int w, int h) {
		_myIsCrop = true;
		_myCropX = Math.max(0, x);
		_myCropY = Math.max(0, y);
		_myWidth = Math.min(w, _myCaptureWidth);
		_myHeight = Math.min(_myCaptureHeight, y + h) - _myCropY;

		// if size has changed, re-init this image
		// if ((_myCropWidth != width) || (_myCropHeight != height)) {
		// init(w, h, RGB);
		// }
	}

	/**
	 * Remove the cropping (if any) of the image.
	 * <P>
	 * By default, cropping is often enabled to trim out black pixels. But if you'd rather deal with them yourself (so
	 * as to avoid an extra lag while the data is moved around) you can shut it off.
	 */
	public void noCrop() {
		_myIsCrop = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	public void update(float theDeltaTime) {
		if (loaded && CCTextureIO.QTinitialized) {
			if (_myIsDataUpdated) {
				_myIsDataUpdated = false;
				buffer(ByteBuffer.wrap(_myPixels));
				
				if(_myIsFirstFrame) {
					_myIsFirstFrame = false;
					_myListener.proxy().onInit(this);
				}else {
					_myListener.proxy().onUpdate(this);
				}
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

	public void dispose() {
		stop();
		// this is important so that the next app can do video
		QTSession.close();
		QTUtils.reclaimMemory();
		System.gc();
	}

}

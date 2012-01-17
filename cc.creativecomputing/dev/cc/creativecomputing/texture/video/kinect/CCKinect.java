package cc.creativecomputing.texture.video.kinect;


import java.lang.reflect.Method;
import java.nio.ShortBuffer;

import org.openkinect.Acceleration;
import org.openkinect.ColorFormat;
import org.openkinect.Context;
import org.openkinect.Device;
import org.openkinect.LEDStatus;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.graphics.texture.video.CCVideoData;
import cc.creativecomputing.math.CCVector3f;

/**
 * Represents a kinect device
 * @author christianriekoff
 *
 */
public class CCKinect implements CCDisposeListener, Acceleration{
	
	/**
	 * pixel with of the kinect device
	 */
	public static int DEVICE_WIDTH = 640;
	
	/**
	 * pixel height of the kinect device
	 */
	public static int DEVICE_HEIGHT = 480;
	
	/**
	 * Maximum depth value of the raw depth data
	 */
	public static int DEPTH_RANGE = 2047;
	
	private class CCKinectThread extends Thread{
		public void run() {
			while (_myIsRunning) {
				boolean b = _myContext.processEvents();
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}

		}
	}

	private boolean _myIsRunning = false;

	private Context _myContext;
	private Device _myDevice;
	
	
	
	/**
	 * holds the color information
	 */
	private CCKinectColorData _myColorData;
	
	/**
	 * holds the depth information
	 */
	private CCKinectDepthData _myDepthData;
	
	
	private CCVector3f _myAcceleration = new CCVector3f();
	
	private CCApp _myApp;
	
	/**
	 * Index of the kinect device default is 0
	 */
	private int _myDeviceIndex;
	
	private CCKinectThread _myThread;

	/**
	 * Create a new instance for the given device index
	 * @param theApp
	 * @param theIndex
	 */
	public CCKinect(CCApp theApp, final int theIndex) {
		_myApp = theApp;
		_myDeviceIndex = theIndex;
		_myThread = new CCKinectThread();
		
		theApp.addDisposeListener(this);
	}
	
	/**
	 * Creates a new instance for the default device 0
	 * @param theApp
	 */
	public CCKinect(CCApp theApp) {
		this(theApp,0);
	}

	public void start() {

		_myContext = Context.getContext();
		if(_myContext.devices() < 1){
			System.out.println("No Kinect devices found.");
		}
		_myDevice = _myContext.getDevice(_myDeviceIndex);
		_myDevice.acceleration(this);
		
		_myColorData = new CCKinectColorData(_myApp);
		_myDepthData = new CCKinectDepthData(_myApp);
		_myIsRunning = true;

		_myThread.start();
	}

	/**
	 * Activate or deactive readout of the depth image. This results in better performance
	 * if you just need the color image. 
	 * @param theIsDepthActive
	 */
	public void isDepthActive(boolean theIsDepthActive) {
		if (theIsDepthActive) _myDevice.depth(_myDepthData);
		else _myDevice.depth(null);
	}

	public CCKinectDepthData depthData() {
		return _myDepthData;
	}

	public float depthFrameRate() {
		return _myDepthData.frameRate();
	}

	/**
	 * Activate or deactive readout of the color image. This results in better performance
	 * if you just need the depth image. 
	 * @param theIsDepthActive
	 */
	public void isColorActive(boolean theIsColorActive) {
		if (theIsColorActive) _myDevice.color(_myColorData);
		else _myDevice.color(null);
	}

	/**
	 * Returns video texture data holding the the color information from the kinect device.
	 * This can be passed to a video texture.
	 * @see CCVideoTexture
	 * @see CCVideoData
	 * @return video texture data
	 */
	public CCKinectColorData colorData() {
		return _myColorData;
	}

	/**
	 * Returns the framerate of the color data update
	 * @return
	 */
	public float colorFrameRate() {
		return _myColorData.frameRate();
	}
	
	public static enum CCKinectLedStatus{
		OFF(LEDStatus.LED_OFF),
		GREEN(LEDStatus.LED_GREEN),
		RED(LEDStatus.LED_RED),
		YELLOW(LEDStatus.LED_YELLOW),
		BLINK_YELLOW(LEDStatus.LED_BLINK_YELLOW),
		BLINK_GREEN(LEDStatus.LED_BLINK_GREEN),
		BLINK_RED_YELLOW(LEDStatus.LED_BLINK_RED_YELLOW);
		
		private LEDStatus _myStatus;
		
		private CCKinectLedStatus(LEDStatus theStatus) {
			_myStatus = theStatus;
		}
	}
	
	public void ledStatus(CCKinectLedStatus theStatus) {
		_myDevice.light(theStatus._myStatus);
	}


	



	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		_myDevice.color(null);
		_myDevice.depth(null);
		_myIsRunning = false;
		_myDevice.dispose();
	}

	/* (non-Javadoc)
	 * @see org.openkinect.Acceleration#direction(float, float, float)
	 */
	@Override
	public void direction(float theX, float theY, float theZ) {
		_myAcceleration.set(theX, theY, theZ);
	}
	
	public CCVector3f acceleration() {
		return _myAcceleration;
	}
	
	/**
	 * Sets the tilt of the kinect device, the range for this value goes from -31 to 31 degrees.
	 * @param theDegrees tilt of the kinect device in degrees
	 */
	public void tilt(final float theDegrees) {
		_myDevice.tilt(theDegrees);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCDisposeListener#dispose()
	 */
	@Override
	public void dispose() {
		_myDevice.color(null);
		_myDevice.depth(null);
		_myIsRunning = false;
	}
}
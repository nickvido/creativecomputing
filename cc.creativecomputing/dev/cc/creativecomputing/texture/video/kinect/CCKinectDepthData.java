package cc.creativecomputing.texture.video.kinect;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.openkinect.Image;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.graphics.texture.video.CCVideoData;
import cc.creativecomputing.graphics.texture.video.CCVideoTextureDataListener;
import cc.creativecomputing.math.CCMath;

public class CCKinectDepthData extends CCKinectData implements Image {

	private ShortBuffer _myData;
	private short[] _myLastData;
	private short[] _myLastDataCounter;
	
	private FloatBuffer _myFloatData;
	
	private boolean _myAverageData = false;
	
	public CCKinectDepthData(CCAbstractApp theApp) {
		super(theApp);
		
		_myPixelInternalFormat = CCPixelInternalFormat.LUMINANCE;
		_myPixelFormat = CCPixelFormat.LUMINANCE;
		_myPixelType = CCPixelType.FLOAT;

		_myFloatData = FloatBuffer.allocate(_myWidth * _myHeight);
		_myLastData = new short[_myWidth * _myHeight];
		_myLastDataCounter = new short[_myWidth * _myHeight];

		_myIsFirstFrame = true;
	}
	
	public void averageData(boolean theAverageData) {
		_myAverageData = theAverageData;
	}
	
	public ShortBuffer getRawData() {
		return _myData;
	}
	
	public int depth(int theX, int theY) {
		return _myLastData[theY * CCKinect.DEVICE_WIDTH + theX];
	}

	public void dataImpl(ByteBuffer data) {
		if(_myData == null) {
			_myData = data.asShortBuffer();
		}
		
		_myData = data.asShortBuffer();
		synchronized (_myFloatData) {
			_myFloatData.clear();
			
			int i = 0;
			
			while(_myData.hasRemaining()) {
				short myLastValue = _myLastData[i];
				short val = _myData.get();
				_myLastData[i] = val;
				
				
				if(_myAverageData && val >= 2047 && myLastValue < 2047 && _myLastDataCounter[i] < 10) {
					val = myLastValue;
					_myLastData[i] = val;
					_myLastDataCounter[i]++;
				}else {
					_myLastDataCounter[i] = 0;
				}
				i++;
				_myFloatData.put(val / 2047f);
			}
			_myFloatData.rewind();
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	@Override
	public void updateImpl(float theDeltaTime) {
		if (_myFloatData == null)
			return;
		
		synchronized (_myFloatData) {
			buffer(_myFloatData);
		}
	}

}

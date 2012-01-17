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

public abstract class CCKinectData extends CCVideoData implements Image {
	private long _myTime;
	private float _myFrameRate;

	private boolean _myIsDataUpdated = false;
	
	public CCKinectData(CCAbstractApp theApp) {
		super(theApp);
		
		_myWidth = CCKinect.DEVICE_WIDTH;
		_myHeight = CCKinect.DEVICE_HEIGHT;
		_myBorder = 0;
		
		_myMustFlipVertically = true;
		
		_myTime = System.currentTimeMillis();
		
		_myIsFirstFrame = true;
	}
	
	protected abstract void dataImpl(ByteBuffer theData);

	public void data(ByteBuffer theData) {
		long millis = System.currentTimeMillis();

		dataImpl(theData);
		_myIsDataUpdated = true;
		
		long myTime = System.currentTimeMillis();
		long myPassedTime = myTime - _myTime;
		_myTime = myTime;
		float _myCurrentFrameRate = 1.0f / (myPassedTime / 1000.0f);
		_myFrameRate = CCMath.blend(_myFrameRate,_myCurrentFrameRate,0.1f);
	}
	
	protected abstract void updateImpl(float theDeltaTime);
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {

		if (_myIsDataUpdated) {
			_myIsDataUpdated = false;
			
			updateImpl(theDeltaTime);

			if (_myIsFirstFrame) {
				_myIsFirstFrame = false;
				System.out.println("INIT");
				for (CCVideoTextureDataListener myListener : _myListener) {
					myListener.onInit(this);
				}
			} else {
				// System.out.println("UPDATE");
				for (CCVideoTextureDataListener myListener : _myListener) {
					myListener.onUpdate(this);
				}
			}
		}
	}

	public float frameRate() {
		return _myFrameRate;
	}

	

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCPostListener#post()
	 */
	@Override
	public void post() {
		
	}

}

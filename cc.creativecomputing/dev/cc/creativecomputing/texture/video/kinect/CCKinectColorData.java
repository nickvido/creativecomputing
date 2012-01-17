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

public class CCKinectColorData extends CCKinectData implements Image {

	private ByteBuffer _myData;
	
	public CCKinectColorData(CCAbstractApp theApp) {
		super(theApp);
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGB;
		_myPixelFormat = CCPixelFormat.RGB;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;
	}

	public void dataImpl(ByteBuffer theData) {
		_myData = theData;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	@Override
	public void updateImpl(float theDeltaTime) {
		buffer(_myData);
	}

}

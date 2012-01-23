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

import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.elements.BufferDataAppSink;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.elements.RGBDataAppSink;
import org.gstreamer.elements.RGBDataSink;
import org.gstreamer.lowlevel.GstBusAPI.BusCallback;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.GDevice;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieDrawingComplete;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;

public class CCGStreamerMovie extends CCMovieData implements CCDisposeListener {

	private class CCGStreamerEndOfStreamAction implements Bus.EOS {

		@Override
		public void endOfStream(GstObject theArg0) {
			if (_myDoRepeat) {
				goToBeginning();
			} else {
				_myIsRunning = false;
			}
			_myMovieEvents.proxy().onEnd();
		}

	}

	private class CCGStreamerUpdateAction implements RGBDataAppSink.Listener {
		@Override
		public void rgbFrame(int theWidth, int theHeight, IntBuffer theBuffer) {
			_myWidth = theWidth;
			_myHeight = theHeight;
			buffer(theBuffer);
			_myIsDataUpdated = true;
		}
	}

	private PlayBin2 gplayer;
	private String filename;

	private boolean _myIsDataUpdated = false;

	private float fps;
	private float rate;

	private boolean available;
	private boolean newFrame;

	private RGBDataAppSink _myBufferSink = null;
	private Buffer _myBuffer = null;

	private String _myCopyMask;

	/**
	 * full path of the movie file
	 * 
	 * @param thePath
	 */
	public CCGStreamerMovie(final CCAbstractApp theApp, final String thePath) {
		super(theApp);

		setMovie(thePath);

		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			_myCopyMask = "red_mask=(int)0xFF000000, green_mask=(int)0xFF0000, blue_mask=(int)0xFF00";
		} else {
			_myCopyMask = "red_mask=(int)0xFF, green_mask=(int)0xFF00, blue_mask=(int)0xFF0000";
		}

		theApp.addDisposeListener(this);
	}

	private float nanoSecToSecFrac(double theNanoSeconds) {
		theNanoSeconds /= 1E9;
		return (float)theNanoSeconds;
	}

	private long secToNanoLong(float theSeconds) {
		Float f = new Float(theSeconds * 1E9);
		return f.longValue();
	}

	/**
	 * Prints all the gstreamer elements currently used in the current player instance.
	 * 
	 */
	public void printElements() {
		List<Element> list = gplayer.getElementsRecursive();
		System.out.println(list);
		for (Element element : list) {
			System.out.println(element.getName());
			for (Pad myPad : element.getPads()) {
				System.out.println("   " + myPad.getName());
			}
		}

	}

	/**
	 * Initialize the movie by getting all data
	 * 
	 * @param thePath
	 */
	public void setMovie(final String thePath) {
		delete();
		gplayer = null;

		CCGStreamer.init();

		// first check to see if this can be read locally from a file.
		try {
			// first try a local file using the dataPath. usually this will
			// work ok, but sometimes the dataPath is inside a jar file,
			// which is less fun, so this will crap out.
			File file = new File(CCIOUtil.dataPath(thePath));

			// read from a file just hanging out in the local folder.
			// this might happen when the video library is used with some
			// other application, or the person enters a full path name
			if (!file.exists()) {
				file = new File(thePath);
			}

			gplayer = new PlayBin2("Movie Player");
			gplayer.setInputFile(file);
		} catch (Exception e) {
		} // ignored

		try {
			// Network read...
			if (gplayer == null && thePath.startsWith("http://")) {
				try {
					CCLog.info("network read");
					gplayer = new PlayBin2("Movie Player");
					gplayer.setURI(URI.create(thePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException se) {
			// online, whups. catch the security exception out here rather than
			// doing it three times (or whatever) for each of the cases above.
		}

		if (gplayer == null) {
			throw new CCTextureException("Could not load movie file " + thePath);
		}

		// we've got a valid movie! let's rock.
		try {
			// PApplet.println("we've got a valid movie! let's rock.");
			this.filename = thePath; // for error messages

			rate = 1.0f;
			fps = -1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		_myBufferSink = new RGBDataAppSink("rgb", new CCGStreamerUpdateAction());
		// _myBufferSink.setAutoDisposeBuffer(false);
		gplayer.setVideoSink(_myBufferSink);
		// The setVideoSink() method sets the videoSink as a property of the PlayBin,
		// which increments the refcount of the videoSink element. Disposing here once
		// to decrement the refcount.
		_myBufferSink.dispose();

		// captureThread.addMovie(natSink, this);

		// Creating bus to handle end-of-stream event.
		Bus bus = gplayer.getBus();
		bus.connect(new CCGStreamerEndOfStreamAction());

		_myBorder = 0;

		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;

		_myIsDataUpdated = false;
		_myIsFirstFrame = true;
	}

	/**
	 * Releases the gstreamer resources associated to this movie object. It shouldn't be used after this.
	 */
	public void delete() {
		if (gplayer == null)
			return;

		try {
			if (gplayer.isPlaying()) {
				gplayer.stop();
			}
		} catch (IllegalStateException e) {
			CCLog.error("error when deleting player, maybe some native resource is already disposed");
		} catch (Exception e) {
			e.printStackTrace();
		}

		_myBuffer = null;
		_myBufferSink.removeListener();
		_myBufferSink.dispose();
		_myBufferSink = null;

		gplayer.dispose();
		gplayer = null;
	}

	@Override
	public void dispose() {
		delete();
	}

	@Override
	public void post() {}

	/**
	 * Update the texture data
	 */
	protected void updateData() {
		// _myRaw.copyToArray(0, _myPixels, 0, _myPixels.length);
	}

	public void update(float theDeltaTime) {
		if (_myIsDataUpdated) {
			_myIsDataUpdated = false;
			if (_myIsFirstFrame) {
				_myIsFirstFrame = false;
				_myListener.proxy().onInit(this);
			} else {
				_myListener.proxy().onUpdate(this);
			}
		}
	}

	/**
	 * this should free some memory
	 */
	@Override
	public void finalize() {
		delete();
	}

	@Override
	public float duration() {
		float sec = gplayer.queryDuration().toSeconds();
		float nanosec = gplayer.queryDuration().getNanoSeconds();
		return sec + nanoSecToSecFrac(nanosec);
	}

	public void goToBeginning() {
		time(0);
	}

	@Override
	public float volume() {
		return (float) gplayer.getVolume();
	}

	@Override
	public void volume(final float theVolume) {
		if (_myIsRunning) {
			gplayer.setVolume(theVolume);
		}
	}

	@Override
	public void rate(float theSpeed) {}

	@Override
	public float rate() {
		return 1.0f;
	}

	/**
	 * Get the original framerate of the source video. Note: calling this method repeatedly can slow down playback
	 * performance.
	 * 
	 * @return float
	 */
	public float sourceFrameRate() {
		return (float) gplayer.getVideoSinkFrameRate();
	}

	public int frame() {
		return CCMath.ceil(time() * sourceFrameRate()) - 1;
	}

	public void frame(int theFrame) {
		play();

		float srcFramerate = sourceFrameRate();

		// The duration of a single frame:
		float frameDuration = 1.0f / srcFramerate;

		// We move to the middle of the frame by adding 0.5:
		float where = (theFrame + 0.5f) * frameDuration;

		// Taking into account border effects:
		float diff = duration() - where;
		if (diff < 0) {
			where += diff - 0.25 * frameDuration;
		}

		time(where);

		pause();
	}

	public int numberOfFrames() {
		return (int) (duration() * sourceFrameRate());
	}

	@Override
	public void play(boolean theDoRestart) {
		super.play(theDoRestart);
		gplayer.play();
	}

	/**
	 * Plays a movie continuously, restarting it when it is over.
	 */
	public void loop() {
		_myDoRepeat = true;
		play();
	}

	/**
	 * Pauses a movie during playback. If a movie is started again with play(), it will continue from where it was
	 * paused.
	 */
	public void pause() {
		super.pause();
		gplayer.pause();
	}

	/**
	 * Stops a movie from continuing. The playback returns to the beginning so when a movie is played, it will begin
	 * from the beginning.
	 */
	public void stop() {
		super.stop();
		gplayer.stop();
	}

	@Override
	public float time() {
		float sec = gplayer.queryPosition().toSeconds();
		float nanosec = gplayer.queryPosition().getNanoSeconds();
		return sec + nanoSecToSecFrac(nanosec);
	}

	@Override
	public void time(float theTime) {

		boolean res;
		long pos = secToNanoLong(theTime);

		res = gplayer.seek(1.0, Format.TIME, SeekFlags.FLUSH, SeekType.SET, pos, SeekType.NONE, -1);

		if (!res) {
			System.err.println("Seek operation failed.");
		}

		// getState() will wait until any async state change
		// (like seek in this case) has completed
		// seeking = true;
		gplayer.getState();
		// seeking = false;
	}
}

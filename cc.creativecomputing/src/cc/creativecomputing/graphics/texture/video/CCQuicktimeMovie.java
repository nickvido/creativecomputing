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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureException;
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

/**
 * This class uses the quicktime java api to get the data of a movie.
 * Quicktime for java is pretty much outdated  since apple is not supporting
 * it anymore. Right now there exist different alternatives hopefully
 * with one of the next version java is going to support movieplayback 
 * on different platforms.
 * @author christianriekoff
 *
 */
@SuppressWarnings("deprecation")
public class CCQuicktimeMovie extends CCMovieData{
	
	/**
	 * Enumeration for different load to ram modes
	 * @author texone
	 *
	 */
	public static enum CCQuicktimeRamMode{
		NONE(0),
		KEEP_IN_RAM(1 << 0),
		UNKEEP_IN_RAM(1 << 1), 
		FLUSH_FROM_RAM(1 << 2),
		LOOK_FORWARD_TRACK_EDITS(1 << 3),
		LOOK_BACKWARD_TRACK_EDITS(1 << 4),
		STREAM(1 << 5);
		
		public final int ID;
		
		private CCQuicktimeRamMode(final int theID){
			ID = theID;
		}
	}
	
	private RawEncodedImage _myRaw;
	private Movie _myMovie;
	private static boolean QTinitialized = false;
	
	/**
	 * indicates that the movie is loaded and ready for playback
	 */
	private boolean _myIsLoaded = false;
	
	/**
	 * Indicates that data has been updated inside the thread
	 */
	private boolean _myIsDataUpdated = false;
	
	/**
	 * keep the pixel data
	 */
	protected byte[] _myPixels;
	
	/**
	 * Do Capturing in a separate thread for better performance
	 * @author christian riekoff
	 *
	 */
	static class CaptureThread extends Thread implements MovieDrawingComplete {
		/**
		 * map to get the video data that uses a certain movie
		 */
		private Map<Movie, CCQuicktimeMovie> _myQuicktimeTextures = new HashMap<Movie, CCQuicktimeMovie>();
		
		/**
		 * list with video data to be checked for updates
		 */
		private List<CCQuicktimeMovie> _myUpdates = new ArrayList<CCQuicktimeMovie>();
		
		/**
		 * Add a movie to the thread
		 * @param theMovie quicktime object representing the movie
		 * @param theMovieTexture cc texture data
		 */
		public void addMovie(final Movie theMovie, final CCQuicktimeMovie theMovieTexture){
			_myQuicktimeTextures.put(theMovie,theMovieTexture);
		}
		
		/**
		 * Checks for updated movies and updates the data
		 */
		public void run() {
			while (true) {
				try {
					if(_myUpdates.size() > 0){
						CCQuicktimeMovie myMovieTexture = _myUpdates.remove(0);
						if(myMovieTexture._myIsDataUpdated)continue;
						myMovieTexture._myForceUpdate = false;
						myMovieTexture.updateData();

						myMovieTexture._myIsDataUpdated = true;
					}
					sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * Called by quicktime if a movie has been updated
		 */
		public int execute(Movie theMovie) {
			CCQuicktimeMovie myMovieTexture = _myQuicktimeTextures.get(theMovie);
			if (myMovieTexture._myIsRunning == false && !myMovieTexture._myForceUpdate)
				return 0;

			_myUpdates.add(myMovieTexture);
			
			return 0;
		}
	}
	
	/**
	 * Thread to update the data
	 */
	protected static CaptureThread captureThread;
	
	/**
	 * full path of the movie file
	 * @param thePath
	 */
	public CCQuicktimeMovie(final CCAbstractApp theApp, final String thePath) {
		super(theApp);

		if(captureThread == null){
			captureThread = new CaptureThread();
			captureThread.start();
			captureThread.setPriority(Thread.MIN_PRIORITY);
		}
		
		setMovie(thePath);
		_myIsLoaded = true;
	}
	
	/**
	 * This method loads a movie's data into memory. If the movie does not fit, the method throws an error. 
	 * It also allows you to specify a portion of the movie to load. The time parameter contains the starting 
	 * time of the movie segment to load. The duration parameter specifies the length of the segment to load.
	 * @param theMode Gives you explicit control over what is loaded into memory and how long to keep it around.
	 * @param theStart contains the starting time of the movie segment to load.
	 * @param theDuration specifies the length of the segment to load.
	 */
	public void loadIntoRam(final CCQuicktimeRamMode theMode,int theStart, float theDuration) {
    	try {
    		switch(theMode){
    		case KEEP_IN_RAM:
    		case UNKEEP_IN_RAM:
    		case FLUSH_FROM_RAM:
    		case LOOK_BACKWARD_TRACK_EDITS:
    		case LOOK_FORWARD_TRACK_EDITS:
    			_myMovie.loadIntoRam(theStart, (int)theDuration * 1000, theMode.ID);
    		}
		} catch (Exception e) {
			CCLog.error("ERROR @ loadIntoRam. " + e);
		}
    }
	
	public void loadIntoRam(final CCQuicktimeRamMode theMode) {
    	loadIntoRam(theMode,0,duration());
    }
    
    public void loadIntoRam(){
    	loadIntoRam(CCQuicktimeRamMode.KEEP_IN_RAM);
    }
	
	/**
	 * Initialize the movie by getting all data
	 * @param thePath
	 */
	public void setMovie(final String thePath) {
		try {
			if (!QTinitialized) {
				QTSession.open();
				QTinitialized = true;
			}

			QTFile file = new QTFile(thePath);
			OpenMovieFile openqtfile = OpenMovieFile.asRead(file);
			_myMovie = Movie.fromFile(openqtfile);

			// this should force the movie to aqcuire the playback size
			final QDRect myMovieRect = _myMovie.getBox();
			_myWidth = myMovieRect.getWidth();
			_myHeight = myMovieRect.getHeight();
			_myBorder = 0;
			
			_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
			_myPixelFormat = CCPixelFormat.BGRA;
			_myPixelType = CCPixelType.UNSIGNED_BYTE;
			
			_myIsDataCompressed = false;
			_myMustFlipVertically = true;

			final QDGraphics myQuicktimeGraphics = new QDGraphics(QDGraphics.kDefaultPixelFormat,myMovieRect);
			final PixMap myPixMap = myQuicktimeGraphics.getPixMap();
			_myRaw = myPixMap.getPixelData();
			
			_myPixels = new byte[_myWidth * _myHeight * 4];

			_myMovie.setGWorld(myQuicktimeGraphics, GDevice.get());
			_myMovie.setDrawingCompleteProc(StdQTConstants.movieDrawingCallWhenChanged, captureThread);
			captureThread.addMovie(_myMovie, this);

			_myIsDataUpdated = true;
			_myIsFirstFrame = true;
			captureThread.execute(_myMovie);
			//			loop();
		} catch (StdQTException e) {
			e.printStackTrace();
		} catch (QTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the texture data
	 */
	protected void updateData(){
		_myRaw.copyToArray(0, _myPixels, 0, _myPixels.length);
	}
	
	public void update(float theDeltaTime) {
		if (_myIsLoaded && QTinitialized) {
			try {
				Movie.taskAll(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
	/**
	 * Handle exit of the main application
	 */
	public void post() {
		System.out.println("POST:" + time() + ":" + duration());
		if (!_myIsLoaded)
			return;
		try {
			if(time() >= duration()){
				if (_myDoRepeat) {
					time(0);
				} else{
					_myMovie.stop();
				_myIsRunning = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this should free some memory
	 */
	@Override
	public void finalize() {
		QTSession.close();
		try {
			_myMovie.disposeQTObject();
			_myRaw.disposeQTObject();
		} catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		QTUtils.reclaimMemory();
		System.gc();
	}

	public float duration() {
		try {
			return _myMovie.getDuration() / (float)_myMovie.getTimeScale();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not get duration.",e);
		}
	}
	
	public void goToBeginning() {
		try {
			_myMovie.goToBeginning();
			preroll();
			_myForceUpdate = true;
		} catch (StdQTException e) {
			throw new CCTextureException("Could not go to beginning.",e);
		}
	}
	
	
	public void preroll(){
		try {
			_myMovie.preroll(_myMovie.getTime(), 1);
		} catch (StdQTException e) {
			throw new CCTextureException("Could not preroll.",e);
		}
	}

	@Override
	public float volume() {
		try {
			return _myMovie.getVolume();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not get volume.",e);
		}
	}

	@Override
	public void volume(final float theVolume) {
		try {
			_myMovie.setVolume(theVolume);
		} catch (StdQTException e) {
			throw new CCTextureException("Could not set volume.",e);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#rate(float)
	 */
	public void rate(float theSpeed) {
		try {
			_myMovie.setRate(theSpeed); // seems to run more stable
		} catch (StdQTException e) {
			throw new CCTextureException("Could not set rate.",e);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#rate()
	 */
	public float rate() {
		try {
			return _myMovie.getRate();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not get rate.",e);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#start(boolean)
	 */
	public void start(boolean theRestart) {
		try {
			if (theRestart)
				_myMovie.goToBeginning();
			_myIsRunning = true;
			_myMovie.start();
		} catch (Exception e) {
			throw new CCTextureException("Could not start movie.",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.texture.video.CCMovieData#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		try {
			_myMovie.stop();
			goToBeginning();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not stop movie.",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.texture.video.CCMovie#pause()
	 */
	@Override
	public void pause() {
		super.stop();
		try {
			_myMovie.stop();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not stop movie.",e);
		}
	}

	@Override
	public float time() {
		try {
			return _myMovie.getTime() / (float)_myMovie.getTimeScale();
		} catch (StdQTException e) {
			throw new CCTextureException("Could not get time.",e);
		}
	}

	@Override
	public void time(float theTime) {
		try {
			 int myTime = (int) (theTime * _myMovie.getTimeScale());
		     myTime = CCMath.constrain(myTime, 0, _myMovie.getDuration() - _myMovie.getTimeScale());
		     _myMovie.setTimeValue(myTime);
		     _myForceUpdate = true;
		} catch (StdQTException e) {
			throw new CCTextureException("Could not set time.",e);
		}
	}
}

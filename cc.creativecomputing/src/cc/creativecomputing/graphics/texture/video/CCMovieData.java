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

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCUpdateListener;

/**
 * This class is representing video data so the content of the object
 * is updated permanently on playback. It also implements the movie
 * interface for control of the play back.
 * @author christian riekoff
 *
 */
public abstract class CCMovieData extends CCVideoData implements CCMovie, CCUpdateListener, CCPostListener{
	
	/**
	 * set this true for looping
	 */
	protected boolean _myDoRepeat = false;
	
	/**
	 * indicates if the movie is running
	 */
	protected boolean _myIsRunning = false;

	/**
	 * Creates a new instance, without setting any parameters.
	 * @param theApp
	 */
	public CCMovieData(final CCAbstractApp theApp) {
		super(theApp);
	}

//	public CCVideoTextureData(
//		int theWidth, int theHeight, int theBorder, 
//		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, CCPixelType thePixelType,
//		boolean theIsDataCompressed, boolean theMustFlipVertically, Buffer theBuffer, Flusher theFlusher
//	) {
//		super(theWidth, theHeight, theBorder, theInternalFormat, thePixelFormat, thePixelType, theIsDataCompressed, theMustFlipVertically, theBuffer, theFlusher);
//	}
//
//	public CCVideoTextureData(
//		int theWidth, int theHeight, int theBorder, 
//		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, CCPixelType thePixelType,
//		boolean theIsDataCompressed, boolean theMustFlipVertically, Buffer[] theMipmapData, Flusher theFlusher
//	) {
//		super(theWidth, theHeight, theBorder, theInternalFormat, thePixelFormat, thePixelType, theIsDataCompressed, theMustFlipVertically, theMipmapData, theFlusher);
//	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#isRunning()
	 */
	public boolean isRunning() {
		return _myIsRunning;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#loop()
	 */
	public void loop() {
		_myDoRepeat = true;
		try {
			start();
			_myIsRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			_myIsRunning = false;
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#loop(boolean)
	 */
	public void loop(boolean theDoLoop) {
		_myDoRepeat = theDoLoop;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#progress()
	 */
	public float progress() {
		return time() / (float) duration();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#start()
	 */
	public void start() {
		start(false);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#stop()
	 */
	public void stop() {
		_myIsRunning = false;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#volume()
	 */
	public float volume() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#volume(float)
	 */
	public void volume(float theVolume) {
		
	}
	
	/**
	 * Set the time of the movie in seconds
	 * @param theTime
	 */
	public void time(float theTime) {
		
	}
	
	/**
	 * 
	 */
	public float time() {
		return 0;
	}
}
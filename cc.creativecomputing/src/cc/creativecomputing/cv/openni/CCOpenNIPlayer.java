/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.cv.openni;

import org.OpenNI.Player;
import org.OpenNI.PlayerSeekOrigin;
import org.OpenNI.StatusException;

/**
 * A Player node is a Production Node that allows playing of a recording. 
 * It supports all Production Node functions, and adds additional functions.
 * @author christianriekoff
 *
 */
public class CCOpenNIPlayer {

	private Player _myPlayer;
	
	CCOpenNIPlayer(Player thePlayer) {
		_myPlayer = thePlayer;
		repeat(true);
	}
	
	/**
	 * Gets the playback speed.
	 * @see #playBackSpeed(double)
	 * @return speed ratio
	 */
	public double playBackSpeed() {
		return _myPlayer.getPlaybackSpeed();
	}
	
	/**
	 * Sets the playback speed, as a ratio of the time passed in the recording. 
	 * A value of 1.0 means the player will try to output frames in the rate they 
	 * were recorded (according to their timestamps). A value bigger than 1.0 means 
	 * fast-forward, and a value between 0.0 and 1.0 means slow-motion.
	 * @param theSpeed The speed ratio
	 */
	public void playBackSpeed(double theSpeed) {
		try {
			_myPlayer.setPlaybackSpeed(theSpeed);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
		
		
	}
	
	/**
	 * Retrieves the number of frames of a specific generator played by a player.
	 * @param theGenerator handle to the generator
	 * @return the number of frames
	 */
	public int numberOfFrames(CCOpenNIGenerator<?> theGenerator) {
		try {
			return _myPlayer.getNumberOfFrames(theGenerator.generator());
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Gets the name of the format supported by a player.
	 * @return
	 */
	public String format() {
		return _myPlayer.getFormat();
	}
	
	/**
	 * Gets the player's source, i.e where the played events come from.
	 * @return
	 */
	public String source() {
		try {
			return _myPlayer.getSource();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Determines whether the player will automatically rewind to the 
	 * beginning of the recording when reaching the end.
	 */
	public void repeat(boolean theRepeat) {
		try {
			_myPlayer.setRepeat(theRepeat);
		} catch (StatusException e) {
//			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Seeks the player to a specific frame of a specific played node, 
	 * so that playing will continue from that frame onwards.
	 * @param theGenerator A handle to the generator.
	 * @param theFrame The number of frames to move
	 */
	public void seekToFrame(CCOpenNIGenerator<?> theGenerator, int theFrame) {
		try {
			_myPlayer.seekToFrame(theGenerator.generator(), PlayerSeekOrigin.SET, theFrame);
		} catch (StatusException e) {
//			throw new CCOpenNIException(e);
		}
	}
	
}

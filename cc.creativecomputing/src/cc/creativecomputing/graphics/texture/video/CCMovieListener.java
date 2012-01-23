/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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

/**
 * Listener to react to different events while playing a movie
 * @author christianriekoff
 *
 */
public interface CCMovieListener {

	/**
	 * Called when movie is started
	 */
	public void onPlay();
	
	/**
	 * Called when movie playback is stopped
	 */
	public void onStop();
	
	/**
	 * Called when movie playback is paused
	 */
	public void onPause();
	
	/**
	 * Called when the movie has reached its end
	 */
	public void onEnd();
}

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

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 *
 */
public interface CCOpenNIHandListener {
	/**
	 * Called when a new hand is created
	 * @param theID hand id
	 * @param thePosition hand position
	 * @param theTime time of call
	 */
	public void onCreateHands(int theID, CCVector3f thePosition, float theTime);

	/**
	 * Called when an existing hand is in a new position.
	 * @param theID hand id
	 * @param thePosition hand position
	 * @param theTime time of call
	 */
	public void onUpdateHands(int theID, CCVector3f thePosition, float theTime);

	/**
	 * Called when an existing hand disappears
	 * @param theID hand id
	 * @param theTime time of call
	 */
	public void onDestroyHands(int theID, float theTime);
}

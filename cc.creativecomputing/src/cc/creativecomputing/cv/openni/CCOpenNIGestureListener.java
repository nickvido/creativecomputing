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
public interface CCOpenNIGestureListener {
	/**
	 * Callback for the recognition of a gesture
	 * @param theGesture The gesture that was recognized.
	 * @param theIdPosition The position in which the gesture was identified.
	 * @param theEndPosition The position of the hand that performed the gesture at the end of the gesture.
	 */
	void onRecognizeGesture(String theGesture, CCVector3f theIdPosition, CCVector3f theEndPosition);

	/**
	 * Callback for indication that a certain gesture is in progress
	 * @param theGesture The gesture that is on its way to being recognized.
	 * @param thePosition The current position of the hand that is performing the gesture.
	 * @param theProgress The percentage of the gesture that was already performed.
	 */
	void onProgressGesture(String theGesture, CCVector3f thePosition, float theProgress);
}

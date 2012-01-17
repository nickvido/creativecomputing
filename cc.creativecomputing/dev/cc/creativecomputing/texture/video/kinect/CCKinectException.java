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
package cc.creativecomputing.texture.video.kinect;

/**
 * @author christianriekoff
 *
 */
public class CCKinectException extends RuntimeException{

	public CCKinectException() {
		super();
	}

	public CCKinectException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCKinectException(String theArg0) {
		super(theArg0);
	}

	public CCKinectException(Throwable theArg0) {
		super(theArg0);
	}

}

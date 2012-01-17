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
package cc.creativecomputing.opencl;

/**
 * @author christianriekoff
 *
 */
public class CCOpenCLUtil {
	
	/**
	 * Round the given global size value up to a multiple of the 
	 * given group size value. 
	 * @param theGroupSize maximum possible size of a work group
	 * @param theGlobalSize global number of work items
	 * @return the nearest multiple of the localWorkSize
	 */
	public static int roundUp(int theGroupSize, int theGlobalSize) {
		int r = theGlobalSize % theGroupSize;
		if (r == 0) {
			return theGlobalSize;
		} else {
			return theGlobalSize + theGroupSize - r;
		}
	}
}

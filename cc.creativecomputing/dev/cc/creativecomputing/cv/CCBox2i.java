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
package cc.creativecomputing.cv;

/**
 * @author christianriekoff
 *
 */
public class CCBox2i {
	
	private int _myMinX;
	private int _myMinY;
	
	private int _myMaxX;
	private int _myMaxY;

	public CCBox2i(int x1, int y1, int x2, int y2){
		_myMinX = x1;
		_myMinY = y1;
		
		_myMaxX = x2;
		_myMaxY = y2;
	}
	
	public int minX() {
		return _myMinX;
	}
	
	public int minY() {
		return _myMinY;
	}
	
	public int maxX() {
		return _myMaxX;
	}
	
	public int maxY() {
		return _myMaxY;
	}
}

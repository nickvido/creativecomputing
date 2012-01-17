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
package cc.creativecomputing.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCUpdateDemo extends CCApp {

	@Override
	public void setup() {

	}
	
	long _myTimeInMillis = 0;
	long _myLastMillis = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myLastMillis == 0) {
			_myLastMillis = System.currentTimeMillis();
		}
		long myMillis = System.currentTimeMillis();
		
		_myTimeInMillis += myMillis - _myLastMillis;
		_myLastMillis = myMillis;
		
		System.out.println(_myTimeInMillis - _myMillisSinceStart);
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUpdateDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


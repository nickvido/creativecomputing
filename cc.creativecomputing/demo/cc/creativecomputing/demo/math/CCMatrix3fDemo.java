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
package cc.creativecomputing.demo.math;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector2f;

public class CCMatrix3fDemo extends CCApp {
	
	private CCMatrix32f _myMatrix;

	@Override
	public void setup() {
		_myMatrix = new CCMatrix32f();
		_myMatrix.translate(100,100);
		_myMatrix.scale(200);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		CCVector2f myPosition = new CCVector2f();
		myPosition = _myMatrix.transform(myPosition);
		
		g.ellipse(myPosition, 50);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMatrix3fDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


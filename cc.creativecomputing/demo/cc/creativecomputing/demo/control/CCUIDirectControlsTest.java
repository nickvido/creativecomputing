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
package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.control.CCFloatControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCUIDirectControlsTest extends CCApp {
	
	@CCControlClass(name = "test")
	private static class CCControls{
		@CCControl (name = "vec", height = 100)
		private static CCVector2f vector2f = new CCVector2f();
		
		@CCControl (name = "enum")
		private static CCBlendMode blendMode;
		
		@CCControl (name = "number", min = 0, max = 1)
		private static float number = 0.5f;
		
		@CCControl (name = "inter", min = 0, max = 10)
		private static int inter = 5;
		
		@CCControl (name = "boolean")
		private static boolean booler = false;
	}
	
	private CCFloatControl _myFloatControl = new CCFloatControl("test1", 2, 0, 10, 100, 17);

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myFloatControl.min(-1);
		_myFloatControl.max(20);
	}

	@Override
	public void update(final float theDeltaTime) {
		System.out.println(_myFloatControl.value());
	
	}

	@Override
	public void draw() {
		g.clearColor(CCControls.number);
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIDirectControlsTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


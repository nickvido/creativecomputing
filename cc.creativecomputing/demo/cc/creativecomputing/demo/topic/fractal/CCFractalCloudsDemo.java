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
package cc.creativecomputing.demo.topic.fractal;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;

public class CCFractalCloudsDemo extends CCApp {
	@CCControl(name="clouds", tabName = "clouds", column = 0)
	private CCFractalClouds _myClouds;

	@Override
	public void setup() {
		_myClouds = new CCFractalClouds(g, width, height);
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myClouds.time(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(255,0,0);
		g.clear();
		g.blend();
		_myClouds.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFractalCloudsDemo.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1400 * 2, 1050);
		myManager.settings().undecorated(true);
		myManager.settings().location(0,0);
		myManager.start();
	}
}


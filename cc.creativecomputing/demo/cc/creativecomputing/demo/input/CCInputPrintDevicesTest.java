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
package cc.creativecomputing.demo.input;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;

public class CCInputPrintDevicesTest extends CCApp {

	@Override
	public void setup() {
		CCInputIO.init(this);
		CCInputIO.printDevices();

		for (int i = 0; i < CCInputIO.numberOfDevices(); i++) {
			CCInputDevice device = CCInputIO.device(i);

			System.out.println(device.name() + " has:");
			System.out.println(" " + device.numberOfSliders() + " sliders");
			System.out.println(" " + device.numberOfButtons() + " buttons");
			System.out.println(" " + device.numberOfSticks() + " sticks");

			device.printSliders();
			device.printButtons();
			device.printSticks();
		}
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCInputPrintDevicesTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

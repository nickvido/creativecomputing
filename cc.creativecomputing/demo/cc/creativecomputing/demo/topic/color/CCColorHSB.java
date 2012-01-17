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
package cc.creativecomputing.demo.topic.color;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.util.logging.CCLog;

public class CCColorHSB extends CCApp {
	
	@CCControl(name = "hue", min = 0, max = 1)
	private float _cHue;
	
	@CCControl(name = "saturation", min = 0, max = 1)
	private float _cSaturation;
	
	@CCControl(name = "brightness", min = 0, max = 1)
	private float _cBrightness;
	
	private CCColor _myColor;

	@Override
	public void setup() {
		_myColor = new CCColor();
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myColor.setHSB(_cHue, _cSaturation, _cBrightness);
	}

	@Override
	public void draw() {
		g.clearColor(_myColor);
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorHSB.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


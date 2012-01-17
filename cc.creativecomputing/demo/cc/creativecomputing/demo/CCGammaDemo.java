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
package cc.creativecomputing.demo;

import com.jogamp.opengl.util.Gamma;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;

public class CCGammaDemo extends CCApp {

	@CCControl(name = "gamma", min = 1, max = 10)
	private float _cGamma = 1;

	@CCControl(name = "brightness", min = -1, max = 1)
	private float _cBrightness = 0;

	@CCControl(name = "contrast", min = 0, max = 2)
	private float _cContrast = 1;
	
	@Override
	public void setup() {
		addControls("app", "gamma", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		System.out.println("_cGamma:"+_cGamma);
		System.out.println("_cBrightness:"+_cBrightness);
		System.out.println("_cContrast:"+_cContrast);
		Gamma.setDisplayGamma(g.gl, _cGamma, _cBrightness, _cContrast);
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGammaDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


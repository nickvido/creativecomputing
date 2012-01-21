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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.video.CCGStreamerCapture;

public class CCGStreamerCaptureDataTest extends CCApp {
	
	private CCGStreamerCapture _myData;

	@Override
	public void setup() {
		for(String myDevice : CCGStreamerCapture.list()) {
			System.out.println(myDevice);
		}
		
		_myData = new CCGStreamerCapture(this, 640, 480, 30);
		_myData.start();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
//		g.blend(CCBlendMode.ADD);
		g.translate(-width/2, -height/2);
		for(int x = 0; x < _myData.width() - 1;x+=9){
			for(int y = 0; y < _myData.height() - 1; y+=9){
				CCColor myPixel = _myData.getPixel(x,y);
				g.color(myPixel);
				float myRadius = (1 - myPixel.brightness()) * 10;
				g.ellipse(x, y, myRadius, myRadius);
				
				
			}
		}
		
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGStreamerCaptureDataTest.class);
		myManager.settings().size(640, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


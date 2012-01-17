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
import cc.creativecomputing.graphics.texture.video.CCQuicktimeMovie;
import cc.creativecomputing.io.CCIOUtil;

public class CCQuicktimeMovieDataTest extends CCApp {
	
	private CCQuicktimeMovie _myData;

	@Override
	public void setup() {
		_myData = new CCQuicktimeMovie(this, CCIOUtil.dataPath("videos/kaki.mov"));
		_myData.loop();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
//		g.blend(CCBlendMode.ADD);
		g.translate(-width/2, -height/2);
		for(int x = 0; x < _myData.width() - 1;x+=3){
			for(int y = 0; y < _myData.height() - 1; y+=3){
				CCColor myPixel = _myData.getPixel(x,y);
				g.color(myPixel);
				float myRadius = (1 - myPixel.brightness()) * 10;
				g.ellipse(x * 2, y * 2, myRadius, myRadius);
				
				myPixel = _myData.getPixel(_myData.width() - x - 1,y);
				g.color(myPixel);
				myRadius = (1 - myPixel.brightness()) * 10;
				g.ellipse(x * 2, y * 2, myRadius, myRadius);
			}
		}
		
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCQuicktimeMovieDataTest.class);
		myManager.settings().size(320 * 2, 212 * 2);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


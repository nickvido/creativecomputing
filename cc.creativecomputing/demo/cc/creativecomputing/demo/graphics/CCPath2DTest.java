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

package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCPath2D;
import cc.creativecomputing.graphics.CCPath2D.CCStrokeJoin;
import cc.creativecomputing.math.CCMath;

public class CCPath2DTest extends CCApp {

	private CCPath2D _myPath;
	
	public void setup() {
		_myPath = new CCPath2D(CCStrokeJoin.MITER, 5);
		_myPath.begin();
		for(int i = 0; i < 4; i++) {
			_myPath.addPoint(50 * i - 250, CCMath.random(-200, 200));
		}
		_myPath.end();
		_myPath.begin();
		for(int i = 6; i < 11; i++) {
			_myPath.addPoint(50 * i - 250, CCMath.random(-200, 200));
		}
		_myPath.end();
		
		frameRate(20);
	}

	public void draw() {
		g.clear();
		_myPath.draw(g);
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyChar()) {
		case ' ':
			_myPath.clear();
			_myPath.begin();
			for(int i = 0; i < 40; i++) {
				_myPath.addPoint(width/40 * i - width/2, CCMath.random(-200, 200));
			}
			_myPath.end();
			break;
		}
	}


	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPath2DTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

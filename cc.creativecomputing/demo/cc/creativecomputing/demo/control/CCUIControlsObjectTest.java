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
import cc.creativecomputing.graphics.CCGraphics;

public class CCUIControlsObjectTest extends CCApp {
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private class Shape{
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2)
		protected float _cX = 0;
		
		protected float _cY = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2)
		protected void value(float theValue) {
			_cY = theValue;
		}
	}
	
	private class Rect extends Shape{
		final float DEFAULT_RADIUS = 50;
		
		@CCControl(name = "width", min = 1, max = 50)
		private float _cWidth = 0;
		
		@CCControl(name = "height", min = 1, max = 50)
		private float _cHeight = 0;
		
		public void draw(CCGraphics g) {
			g.rect(_cX, _cY, _cWidth, _cHeight);
		}
	}
	
	private class Circle extends Shape{
		final float DEFAULT_RADIUS = 50;
		
		@CCControl(name = "radius", min = 1, max = 50)
		private float _cRadius = 0;
		
		public void draw(CCGraphics g) {
			g.ellipse(_cX, _cY, _cRadius);
		}
	}
	
	@CCControl(name = "rect")
	private Rect _myRect;
	
	@CCControl(name = "circle")
	private Circle _myCircle;

	@Override
	public void setup() {
		frameRate(30);
		
		_myRect = new Rect();
		_myCircle = new Circle();
		
		addControls("shapes", "shapes", 0,this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myRect.draw(g);
		_myCircle.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIControlsObjectTest.class);
		myManager.settings().size(APP_WIDTH, APP_HEIGHT);
		myManager.start();
	}
}


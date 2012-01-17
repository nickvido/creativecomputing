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
package cc.creativecomputing.demo.ui;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUIHorizontalAlignment;
import cc.creativecomputing.newui.CCUIVerticalAlignment;
import cc.creativecomputing.newui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.newui.widget.CCUIWidget;

public class CCUIWidgetPlacementDemo extends CCApp {
	
	@CCControl(name = "scale x", min = 0.1f, max = 2)
	private float _cScaleX = 0;
	
	@CCControl(name = "scale y", min = 0.1f, max = 2)
	private float _cScaleY = 0;
	
	@CCControl(name = "translate x", min = -200f, max = 200)
	private float _cTranslateX = 0;
	
	@CCControl(name = "translate y", min = -200f, max = 200)
	private float _cTranslateY = 0;
	
	@CCControl(name = "rotate", min = 0f, max = 360)
	private float _cRotate = 0;
	
	@CCControl(name = "horizontal alignment")
	private CCUIHorizontalAlignment _cHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	
	@CCControl(name = "vertical alignment")
	private CCUIVerticalAlignment _cVerticalAlignment = CCUIVerticalAlignment.BOTTOM;
	
	private CCUIWidget _myWidget;

	@Override
	public void setup() {
		_myWidget = new CCUIWidget(100, 200);
		_myWidget.border(new CCUILineBorderDecorator());
		
		addControls("ui", "ui", this);
		frameRate(20);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myWidget.property2f("scale").set(_cScaleX, _cScaleY);
		_myWidget.property2f("translation").set(_cTranslateX, _cTranslateY);
		_myWidget.property1f("rotation").set(_cRotate);
		
		_myWidget.horizontalAlignment(_cHorizontalAlignment);
		_myWidget.verticalAlignment(_cVerticalAlignment);
		
		_myWidget.update(theDeltaTime);
		
		System.out.println(_myWidget.isInside(mouseX - width/2, height/2 - mouseY));
	}

	@Override
	public void draw() {
		g.clear();
		g.line(-width/2,0,width/2,0);
		g.line(0,-height/2,0,height/2);
		_myWidget.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIWidgetPlacementDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


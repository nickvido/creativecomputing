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
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.input.CCUIInput;
import cc.creativecomputing.newui.input.CCUIMouseInput;
import cc.creativecomputing.newui.widget.CCUIWidget;

public class CCUIDecoratorDemo extends CCApp {
	
	private CCUI _myUI;
	private CCUIInput _myUIInput;
	private CCUIWidget _myContainerWidget;

	@Override
	public void setup() {
		_myUI = new CCUI(this);
		_myUI.loadUI(CCIOUtil.classPath(this, "decorator.xml"));
		_myUIInput = new CCUIMouseInput(this, _myUI);
		
		_myContainerWidget = _myUI.createWidget("widgetContainer");
	}

	@Override
	public void update(final float theDeltaTime) {
		_myUI.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myUI.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIDecoratorDemo.class);
		myManager.settings().size(800, 400);
		myManager.start();
	}
}


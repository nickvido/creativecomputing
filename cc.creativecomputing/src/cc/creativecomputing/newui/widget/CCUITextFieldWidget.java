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
package cc.creativecomputing.newui.widget;

import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCText.CCTextListener;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.CCUIInputEventType;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.decorator.CCUIForegroundDecorator;
import cc.creativecomputing.newui.decorator.CCUITextDecorator;
import cc.creativecomputing.newui.decorator.background.CCUIBackgroundDecorator;
import cc.creativecomputing.newui.decorator.controller.CCUITextDecoratorController;
import cc.creativecomputing.newui.event.CCUIWidgetEventListener;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name = "textfield_widget")
public class CCUITextFieldWidget extends CCUIWidget implements CCTextListener{
	
	private class CCUITextFieldBackgroundDecorator extends CCUIBackgroundDecorator{

		/**
		 * @param theID
		 */
		public CCUITextFieldBackgroundDecorator() {
			super("textfield_background");
		}

		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
		 */
		@Override
		public void draw(CCGraphics g, CCUIWidget theWidget) {
			if(_myTextDecorator == null)return;
		}
		
	}
	
	@CCProperty(name = "textfield")
	private CCUITextDecorator _myTextDecorator;
	
	private CCUITextFieldBackgroundDecorator _myTextBackground;
	
	private CCUITextDecoratorController _myTextController;

	public CCUITextFieldWidget() {
		_myTextDecorator = new CCUITextDecorator();
		_myBackground = _myTextBackground = new CCUITextFieldBackgroundDecorator();
		_myTextController = new CCUITextDecoratorController();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.text.CCText.CCTextListener#onChangeText(cc.creativecomputing.graphics.font.text.CCText)
	 */
	@Override
	public void onChangeText(CCText theText) {
		if(theText.lineBreak() == CCLineBreakMode.NONE) {
			width(theText.width());
			height(theText.height());
		}
	}
	
	public CCText text() {
		return _myTextDecorator.text();
	}
	
	public void valueText(boolean theIsValueBox) {
		_myTextController.valueText(theIsValueBox);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#setup(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theParent) {
		super.setup(theUI, theParent);
		
		_myTextDecorator.setup(theUI, this);
		onChangeText(_myTextDecorator.text());
		
		_myTextDecorator.text().addListener(this);
		_myTextController.append(this, _myTextDecorator);
		
		_myForeground  = _myTextDecorator;
	}
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#keyEvent(cc.creativecomputing.events.CCKeyEvent, cc.creativecomputing.newui.CCUIInputEventType)
	 */
	@Override
	public void keyEvent(CCKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		super.keyEvent(theKeyEvent, theEventType);
		if(_myTextController != null)_myTextController.keyEvent(theKeyEvent, theEventType);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myMatrix);
		
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
		g.popMatrix();
	}
}

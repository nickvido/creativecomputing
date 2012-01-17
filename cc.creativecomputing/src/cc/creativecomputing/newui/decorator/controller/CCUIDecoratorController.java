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
package cc.creativecomputing.newui.decorator.controller;

import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUIInputEventType;
import cc.creativecomputing.newui.decorator.CCUIDecorator;
import cc.creativecomputing.newui.event.CCUIWidgetEvent;
import cc.creativecomputing.newui.event.CCUIWidgetEventListener;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public abstract class CCUIDecoratorController<DecoratorType extends CCUIDecorator> implements CCUIWidgetEventListener{
	
	protected CCUIWidget _myWidget;
	protected DecoratorType _myDecorator;
	
	public void append(CCUIWidget theWidget, DecoratorType theDecorator) {
		if(_myWidget != null)_myWidget.removeListener(this);
		_myWidget = theWidget;
		_myWidget.addListener(this);
		_myDecorator = theDecorator;
		reset();
	}
	
	public void reset() {
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.CCUIWidgetEventListener#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType, cc.creativecomputing.math.CCVector2f, cc.creativecomputing.math.CCVector2f)
	 */
	@Override
	public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
		
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#keyEvent(cc.creativecomputing.events.CCKeyEvent, cc.creativecomputing.newui.CCUIInputEventType)
	 */
	public void keyEvent(CCKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		
	}
}

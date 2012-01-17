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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.event.CCUIWidgetEvent;
import cc.creativecomputing.newui.event.CCUIWidgetEventListener;
import cc.creativecomputing.newui.event.CCUIWidgetInteractionEvent;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name = "valuebox_widget")
public class CCUIValueBoxWidget extends CCUIWidget{
	
	private class CCUIValueUpDownWidgetController implements CCUIWidgetEventListener{
		
		private float _mySign;
		
		private CCUIValueUpDownWidgetController(int theSign) {
			_mySign = theSign;
		}
		
		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.CCUIWidgetEventListener#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType)
		 */
		@Override
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case PRESS:
					_myValue = Float.parseFloat(_myValueField.text().text());
					_myValue += _mySign * _myStepSize;
					updateValue();
					break;
				}
			}
		}
	}
	
	private class CCUIValueDragController implements CCUIWidgetEventListener{
		
		private CCVector2f _myStart;
		private float _myStartValue;
		private int _myStepCounter = 0;
		private boolean _myIsVertical;
		
		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.CCUIWidgetEventListener#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType)
		 */
		@Override
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case PRESS:
					_myStepCounter++;
					_myStart = myEvent.position().clone();
					_myStartValue = _myValue;
					break;
				case DRAGG:
				case DRAGG_OUTSIDE:
					if(_myStepCounter == 0)return;
					if(_myStepCounter == 1) {
						_myIsVertical = 
							CCMath.abs(_myStart.x - myEvent.position().x) < 
							CCMath.abs(_myStart.y - myEvent.position().y);
					}
					_myStepCounter++;
					float myValueChange = 0;
					if(!_myIsVertical)myValueChange = myEvent.position().x - _myStart.x;
					else myValueChange = myEvent.position().y - _myStart.y;
					
					_myValue = _myStartValue + (myValueChange / 5) * _myStepSize;
					updateValue();
					break;
				case RELEASE:
				case RELEASE_OUTSIDE:
					if(_myStepCounter == 0)return;
					_myStepCounter = 0;
					break;
				}
			}
		}
	}
	
	@CCProperty(name = "down")
	private CCUIWidget _myDownButton;
	
	@CCProperty(name = "up")
	private CCUIWidget _myUpButton;
	
	@CCProperty(name = "value_field")
	private CCUITextFieldWidget _myValueField;

	@CCProperty(name = "label", optional = true)
	private CCUIWidget _myLabel;
	
	@CCProperty(name = "min", node = false, optional = true)
	private float _myMin = 0;
	
	@CCProperty(name = "max", node = false, optional = true)
	private float _myMax = 1;
	
	@CCProperty(name = "value", node = false, optional = true)
	private float _myValue = 0;
	
	@CCProperty(name = "stepsize", node = false, optional = true)
	private float _myStepSize = 0.01f;
	
	private int _myDigits = 0;

	public void setup(CCUI theUI, CCUIWidget theParent) {
		addListener(new CCUIValueDragController());
		_myDownButton.addListener(new CCUIValueUpDownWidgetController(-1));
		_myUpButton.addListener(new CCUIValueUpDownWidgetController(1));
		
		addChild(_myDownButton);
		addChild(_myUpButton);
		addChild(_myValueField);
		
		if(_myLabel != null) {
			_myLabel.height(height());
			addChild(_myLabel);
		}
		
		super.setup(theUI, this);
		
		_myValue = CCMath.constrain(_myValue, _myMin, _myMax);
		_myValueField.text().text(_myValue);
		_myValueField.valueText(true);
		
		String myStepSizeString = Float.toString(_myStepSize);
		int myIndex = myStepSizeString.indexOf('.');
		if(myIndex > 0) {
			_myDigits = myStepSizeString.length() - myIndex - 1;
		}else {
			_myDigits = 0;
		}
	};
	
	private void updateValue() {
		_myValue = CCMath.round(_myValue, _myDigits);
		_myValue = CCMath.constrain(_myValue, _myMin, _myMax);
		_myValueField.text().text(_myValue);
	}
}

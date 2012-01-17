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
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.decorator.CCUIDecorator;
import cc.creativecomputing.newui.event.CCUIWidgetEvent;
import cc.creativecomputing.newui.event.CCUIWidgetEventListener;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;
import cc.creativecomputing.newui.event.CCUIWidgetInteractionEvent;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name = "slider_widget")
public class CCUISliderWidget extends CCUIWidget{
	
	public static interface CCUISliderChangeValueListener{
		public void onChangeValue(float theValue);
	}
	
	private float _myLastX;
	private float _myStartX;
	private boolean _myIsClicked = false;
	
	private class CCUISliderButtonController implements CCUIWidgetEventListener{
		
		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.CCUIWidgetEventListener#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType)
		 */
		@Override
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case PRESS:
					_myLastX = myEvent.position().x;
					_myStartX = myEvent.position().x - _myButton.x();
					_myIsClicked = true;
					break;
				case RELEASE:
				case RELEASE_OUTSIDE:
					_myIsClicked = false;
				}
			}
			
		}
		
	}
	
	private class CCUISliderBarController implements CCUIWidgetEventListener{
		
		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.CCUIWidgetEventListener#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType)
		 */
		@Override
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case DRAGG:
				case DRAGG_OUTSIDE:
					if(!_myIsClicked || _myLastX == -1)return;
					float myInX = CCMath.constrain(myEvent.position().x, _myStartX, width() - _myButton.width() + _myStartX);
					float myX = myInX - _myLastX;
					myX += _myButton.x();
					
					_myButton.x(myX);
					_myLastX = myInX;
					
					_myValue = myX / (width() - _myButton.width());
					
					break;
				}
			}
		}
		
	}
	
	@CCProperty(name = "bar")
	private CCUIWidget _myBar;
	
	@CCProperty(name = "button")
	private CCUIWidget _myButton;
	
	@CCProperty(name = "label", optional = true)
	private CCUIWidget _myLabel;
	
	private float _myValue;

	/**
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUISliderWidget(float theWidth, float theHeight) {
		super(theWidth, theHeight);
		
		_myBar = new CCUIWidget(theWidth, theHeight);
		_myBar.addListener(new CCUISliderBarController());
		
		_myButton = new CCUIWidget(theWidth / 10, theHeight);
		_myButton.addListener(new CCUISliderButtonController());
		
		_myLabel = new CCUIWidget(0,0);
		
		_myValue = 0;
		
		addChild(_myBar);
		addChild(_myButton);
		addChild(_myLabel);
	}
	
	private CCUISliderWidget() {
		
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.widget.CCUIWidget#setup(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theParent) {
		
		_myBar.width(width());
		if(_myBar.height() == 0)_myBar.height(height());
		_myBar.addListener(new CCUISliderBarController());
		
		if(_myButton.width() == 0)_myButton.width(width()/10);
		if(_myButton.height() == 0)_myButton.height(height());
		_myButton.addListener(new CCUISliderButtonController());
		
		addChild(_myBar);
		addChild(_myButton);
		if(_myLabel != null) {
			_myLabel.height(height());
			addChild(_myLabel);
		}
		
		super.setup(theUI, this);
	}

	public CCUIWidget bar() {
		return _myBar;
	}
	
	public CCUIWidget button() {
		return _myButton;
	}
	
	public CCUIWidget label() {
		return _myLabel;
	}
	
	public float value() {
		return _myValue;
	}
}

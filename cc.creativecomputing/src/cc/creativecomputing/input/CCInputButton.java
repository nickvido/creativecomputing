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
package cc.creativecomputing.input;

import java.util.ArrayList;
import java.util.List;


import net.java.games.input.Component;

/**
 * This class represents a button of a device. You can use the pressed() 
 * method to see if a button is pressed or add listeners to 
 * handle events.
 * @shortdesc This class represents a button of a device.
 * @see CCInputSlider
 * @see CCInputStick
 * @see CCInputDevice
 */
public class CCInputButton extends CCInput{
	
	protected boolean _myIsPressed = false;
	protected boolean _myOldIsPressed = false;
	
	private List<CCInputButtonListener> _myButtonListener = new ArrayList<CCInputButtonListener>();
		
	/**
	 * Initializes a new Slider.
	 * @param theComponent
	 */
	CCInputButton(final Component theComponent){
		super(theComponent);
	}
	
	/**
	 * This method is called before each frame to update the button state.
	 */
	void update(final float theDeltaTime){
		_myActualValue = _myComponent.getPollData()*8;
		_myIsPressed = _myActualValue > 0f;
		
		if(_myIsPressed && !_myOldIsPressed){
			for(CCInputButtonListener myListener:_myButtonListener) {
				myListener.onPress();
			}
		}else if(!_myIsPressed&& _myOldIsPressed){
			for(CCInputButtonListener myListener:_myButtonListener) {
				myListener.onRelease();
			}
		}
		
		_myOldIsPressed = _myIsPressed;
	}
	
	/**
	 * Use this method to add a listener that handles button events. A listener
	 * needs to implement the CCInputButtonListener interface and its onPress and
	 * onRelease methods.
	 * @shortdesc Adds a new listener to react on button presses and releases.
	 * @param theListener the listener to handle events
	 */
	public void addListener(final CCInputButtonListener theListener) {
		_myButtonListener.add(theListener);
	}
	
	/**
	 * @shortdesc This method returns true if the button was pressed. 
	 * @return boolean, true if the button was pressed
	 * @see CCInputButton
	 */
	public boolean pressed(){
		return _myIsPressed;
	}
}

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

import net.java.games.input.Component;

/**
 * Base class for input elements of a controller.
 * 
 * @invisible
 */
abstract class CCInput {
	/**
	 * The current state of the input
	 */
	protected float _myActualValue = 0f;

	/**
	 * JInput Component representing this Slider
	 */
	final Component _myComponent;

	/**
	 * The name of the Slider
	 */
	private final String _myName;

	/**
	 * Initializes a new Slider.
	 * 
	 * @param theComponent
	 */
	CCInput(final Component theComponent) {
		_myComponent = theComponent;
		_myName = _myComponent.getName();
	}

	/**
	 * Returns the name of the input.
	 * 
	 * @return the name of the input element
	 */
	public String name() {
		return _myName;
	}

	/**
	 * Gives you the current value of an input.
	 * 
	 * @return the actual value of the slider
	 * @see CCInputSlider
	 */
	public float value() {
		return _myActualValue;
	}

	/**
	 * This method is called before each frame to update the slider values.
	 */
	abstract void update(final float theDeltaTime);

}

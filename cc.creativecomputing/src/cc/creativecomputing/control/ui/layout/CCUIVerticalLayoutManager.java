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
package cc.creativecomputing.control.ui.layout;

import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;

/**
 * This layout manager places the added elements in a vertical row
 * @author christian riekoff
 *
 */
public class CCUIVerticalLayoutManager extends CCUILayoutManager{
	
	private float _mySliderOffset = 0;
	private float _mySpace;

	/**
	 * @param theComponent
	 */
	public CCUIVerticalLayoutManager(CCUIComponent theComponent, final float theSpace) {
		super(theComponent);
		_mySpace = theSpace;
	}
	
	@Override
	public void layout(CCUIElement theElement, float...theData) {
		theElement.position().y = _mySliderOffset;
		theElement.setupText();
		_mySliderOffset += _mySpace + theElement.bounds().height();
	};

}

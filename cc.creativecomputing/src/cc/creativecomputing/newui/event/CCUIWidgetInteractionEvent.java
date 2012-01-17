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
package cc.creativecomputing.newui.event;

import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCUIWidgetInteractionEvent extends CCUIWidgetEvent{
	
	private CCVector2f _myPosition;
	private CCVector2f _myTransformedPosition;

	/**
	 * @param theType
	 */
	public CCUIWidgetInteractionEvent(CCUIWidgetEventType theType, CCVector2f thePosition, CCVector2f theTransformedPosition) {
		super(theType);
		_myPosition = thePosition;
		_myTransformedPosition = theTransformedPosition;
	}
	
	public CCVector2f position() {
		return _myPosition;
	}
	
	public CCVector2f transformedPosition() {
		return _myTransformedPosition;
	}

}

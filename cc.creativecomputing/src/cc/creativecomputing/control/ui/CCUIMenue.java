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
package cc.creativecomputing.control.ui;

import java.util.List;

import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCUIMenue extends CCUIComponent{
	
	private boolean _myIsVertical;
	private float _myOffset;
	
	private List<CCUIButton> _myMenueButtons;

	/**
	 * @param theLabel
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIMenue(String theLabel, CCVector2f thePosition, CCVector2f theDimension, final boolean theIsVertical) {
		super(theLabel, thePosition, theDimension);
		_myIsVertical = theIsVertical;
	}
	
	public CCUIMenue(String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
		this(theLabel, thePosition, theDimension, true);
	}
	
	
	public void addButton(final CCUIButton theButton) {
		
	}
}

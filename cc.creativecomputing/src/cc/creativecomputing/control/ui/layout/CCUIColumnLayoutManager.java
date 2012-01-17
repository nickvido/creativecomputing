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

import java.util.Arrays;

import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;

/**
 * @author christianriekoff
 *
 */
public class CCUIColumnLayoutManager extends CCUILayoutManager{
	
	private float[]_myHeights = new float[100];
	private float _myWidth;
	private float _mySpace;
	private float _myXOffset;

	/**
	 * @param theComponent
	 */
	public CCUIColumnLayoutManager(CCUIComponent theComponent, final float theWidth, final float theSpace, final float theXOffset, final float theYOffset) {
		super(theComponent);
		_myWidth = theWidth;
		_mySpace = theSpace;
		_myXOffset = theXOffset;
		
		Arrays.fill(_myHeights, theYOffset);
	}
	
	public void yOffset(final float theYOffset) {
		Arrays.fill(_myHeights, theYOffset);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.layout.CCUILayoutManager#layout(cc.creativecomputing.control.CCUIElement)
	 */
	@Override
	public void layout(CCUIElement theElement, float...theData) {
		int myColum = theData.length > 0 ? (int)theData[0] : 0;
		theElement.position().set(myColum * _myWidth + _myXOffset, _myHeights[myColum]);
		theElement.setupText();
		_myHeights[myColum] += theElement.bounds().height() + _mySpace;
	}
}

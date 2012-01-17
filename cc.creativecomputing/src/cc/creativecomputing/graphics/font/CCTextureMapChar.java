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
package cc.creativecomputing.graphics.font;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;

public class CCTextureMapChar extends CCChar{
	private final CCVector2f _myMin;
	private final CCVector2f _myMax;
	
	public CCTextureMapChar(final char theChar, final int theGlyphCode, final float theWidth, final float theHeight, final CCVector2f theMin, final CCVector2f theMax){
		super(theChar, theGlyphCode, theWidth, theHeight);
		_myMin = theMin;
		_myMax = theMax;
	}

	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		final float myWidth = _myWidth * theSize;
		final float myHeight = _myHeight * theSize;
		g.vertex(theX,			theY,			 _myMin.x, _myMin.y);
		g.vertex(theX + myWidth,theY,			 _myMax.x, _myMin.y);
		g.vertex(theX + myWidth,theY - myHeight, _myMax.x, _myMax.y);
		g.vertex(theX,			theY - myHeight, _myMin.x, _myMax.y);
		
		return myWidth;
	}
	
	public CCVector2f min(){
		return _myMin;
	}
	
	public CCVector2f max(){
		return _myMax;
	}
}
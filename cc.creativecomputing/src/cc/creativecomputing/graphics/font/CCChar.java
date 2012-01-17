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

/**
 * @author info
 *
 */
public abstract class CCChar {

	/**
	 * glyph code for kerning information
	 */
	protected final int _myGlyphCode;
	protected final char _myChar;
	protected final float _myWidth;
	protected final float _myHeight;
	
	CCChar(final char theChar, final int theGlyphCode, final float theWidth, final float theHeight){
		_myChar = theChar;
		_myGlyphCode = theGlyphCode;
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public int glyphCode() {
		return _myGlyphCode;
	}
	
	public char getChar() {
		return _myChar;
	}
	
	public float width() {
		return _myWidth;
	}
	
	public abstract float draw(CCGraphics g, float theX, float theY, float theZ, float theSize);
}

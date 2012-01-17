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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author info
 *
 */
public class CCGlutChar extends CCChar{
	
	private final GLUT _myGlut;
	private final int _myFontType;

	/**
	 * @param theChar
	 * @param theWidth
	 */
	CCGlutChar(char theChar, float theWidth, float theHeight, final GLUT theGlut, final int theFontType) {
		super(theChar, -1, theWidth, theHeight);
		_myGlut = theGlut;
		_myFontType = theFontType;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		g.gl.glRasterPos3f(theX, theY, theZ);
		_myGlut.glutBitmapCharacter(_myFontType, _myChar);
		return _myGlut.glutBitmapWidth(_myFontType, _myChar);
	}

}

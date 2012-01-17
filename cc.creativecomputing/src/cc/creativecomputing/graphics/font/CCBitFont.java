/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCBitFont extends CCTextureMapFont{
	
	private CCTextureData _myData;

	public CCBitFont(CCTextureData theData, int theDescent) {
		super(null);
		_myData = theData;
		_myCharCount = 255;
		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myHeight = _mySize = theData.height();
		_myNormalizedHeight = 1;
		
		_myAscent = _myHeight - theDescent;
		_myDescent = theDescent;
//		
		_myNormalizedAscent = (float)_myAscent / _mySize;
		_myNormalizedDescent = (float)_myDescent / _mySize;
		
		_myLeading = _myHeight + 2;
		
		createChars();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#index(char)
	 */
	@Override
	public int index(char theChar) {
		int c = (int) theChar;
		return c - 32;
	}

	protected void createChars() {
		
		_mySize = _myData.height();
			      
		int index = 0;

		int myX = 0;
		int myY = 0;
		
		// array passed to createGylphVector
		char textArray[] = new char[1];
		
		int myLastX = 0;
		
		for (int x = 0; x < _myData.width(); x++) {

        	myX++;
        	
        	boolean myApplyCut = !_myData.getPixel(x, _myData.height() - 1).equals(CCColor.RED);

        	for(int y = 0; y < _myData.height(); y++) {
        		if(_myData.getPixel(x, y).equals(CCColor.BLACK)) {
        			_myData.setPixel(x, y, CCColor.WHITE);
        		}else {
        			_myData.setPixel(x, y, CCColor.TRANSPARENT);
        		}
        	}
        	
	        if (myApplyCut) {
	        	continue;
	        }
	        
	        
	        
	        float myCharWidth = myX - myLastX;
	        
	        if(index == 0) {
	    		_mySpaceWidth = myCharWidth / _mySize;
	        }
	        
	        char c = (char)(index + 32);
			
			_myCharCodes[index] = c;
			
			System.out.println(c + ":" + myCharWidth);
			
			
			_myChars[index] = new CCTextureMapChar(c, -1, myCharWidth / _mySize, height(),
				new CCVector2f(
					myLastX / (float)_myData.width(),
					1f
				),
				new CCVector2f(
					myX / (float)_myData.width(),
					0
				)
			);
			myLastX = myX;

			index++;
		}
		
		_myCharCount = index;
		
//		_myAscent = _myFontMetrics.getAscent();
//		_myDescent = _myFontMetrics.getDescent();
//		
//		_myLeading = _myFontMetrics.getLeading();
//		_mySpacing = _myFontMetrics.getHeight();
//		
//		_myNormalizedAscent = (float)_myFontMetrics.getAscent() / _mySize;
//		_myNormalizedDescent = (float)_myDescent / _mySize;
		_myFontTexture = new CCTexture2D(_myData);
		_myFontTexture.textureFilter(CCTextureFilter.NEAREST);
	}
	
	
}

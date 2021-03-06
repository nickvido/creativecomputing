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
package cc.creativecomputing.graphics.font.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.font.CCFont;

/**
 * @author info
 *
 */
public class CCTextBreaker {

	
	public List<String> breakText(final String theText, final CCFont theFont, final float theTextWidth) {
		final List<String> myResult = new ArrayList<String>();
		final float mySpaceWidth = theFont.spaceWidth();
		float myRunningX = 0; 

		final int myStringLength = theText.length();
		
		char[] _myTextBuffer = new char[myStringLength + 10];
		theText.getChars(0, myStringLength, _myTextBuffer, 0);

		int wordStart = 0;
		int wordStop = 0;
		int lineStart = 0;
		int index = 0;
		
		while (index < myStringLength){
			if ((_myTextBuffer[index] == ' ') || (index == myStringLength - 1)){
				// boundary of a word
				float wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFont.size();

				if (myRunningX + wordWidth > theTextWidth){
					if (myRunningX == 0){ // boxX1) {
						// if this is the first word, and its width is
						// greater than the width of the text box,
						// then break the word where at the max width,
						// and send the rest of the word to the next line.
						do{
							index--;
							if (index == wordStart){
								// not a single char will fit on this line. screw 'em.
								return myResult;
							}
							wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFont.size();
						}while (wordWidth > theTextWidth);
						myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
					}else{
						// next word is too big, output current line
						// and advance to the next line
						myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
						// only increment index if a word wasn't broken inside the
						// do/while loop above.. also, this is a while() loop too,
						// because multiple spaces don't count for shit when they're
						// at the end of a line like this.
						// index = wordStop; // back that ass up
						while ((index < myStringLength) && (_myTextBuffer[index] == ' ')){
							index++;
						}
					}
					lineStart = index;
					wordStart = index;
					wordStop = index;
					myRunningX = 0;
				}else{
					myRunningX += wordWidth + mySpaceWidth;
					// on to the next word
					wordStop = index;
					wordStart = index + 1;
				}
			}else if (_myTextBuffer[index] == '\n'){
				if (lineStart != index){ // if line is not empty
					myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
				}
				lineStart = index + 1;
				wordStart = lineStart;
				myRunningX = 0;
			}
			index++;
		}
		if ((lineStart < myStringLength) && (lineStart != index)){
			myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
		}
		return myResult;
	}
}

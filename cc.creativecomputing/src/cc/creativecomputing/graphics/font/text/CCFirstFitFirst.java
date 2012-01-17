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
package cc.creativecomputing.graphics.font.text;

/**
 * @author christianriekoff
 * 
 */
public class CCFirstFitFirst extends CCLineBreaking {
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.text.CCLineBreaking#breakText(cc.creativecomputing.graphics.font.text.CCText)
	 */
	@Override
	public void breakText(CCText theText) {
		theText.textGrid().clear();
		
		float myX1 = 0;
		float myX2 = theText.blockWidth();
		
		float myY1 = 0;
		float myY2 = theText.blockHeight();

		final float mySpaceWidth = theText.font().spaceWidth();
		float myRunningX = myX1;
		float myCurrentY = myY1; 
		
		// if the box is already too small, tell em to f off
		if (myCurrentY > myY2){
			return; // boxY2) return;
		}

		int myWordStart = 0;
		int myWordStop = 0;
		int lineStart = 0;
		int index = 0;
		int myLineIndex = 0;
		
		char[] myTextBuffer = theText.text().toCharArray();
		
		while (index < myTextBuffer.length){
			if ((myTextBuffer[index] == ' ') || (index == myTextBuffer.length - 1)){
				// boundary of a word
				float wordWidth = theText.font().width(myTextBuffer, myWordStart, index) * theText.size();

				if (myRunningX + wordWidth > myX2){ // boxX2) {
					if (myRunningX == myX1){ // boxX1) {
						// if this is the first word, and its width is
						// greater than the width of the text box,
						// then break the word where at the max width,
						// and send the rest of the word to the next line.
						do{
							index--;
							if (index == myWordStart){
								// not a single char will fit on this line. screw 'em.
								return;
							}
							wordWidth = theText.font().width(myTextBuffer, myWordStart, index) * theText.size();
						}while (wordWidth > theText.width());
						
						theText.textGrid().createGridLine(myLineIndex++, lineStart, index, myTextBuffer, myX1, myCurrentY);
					}else{
						// next word is too big, output current line
						// and advance to the next line
						theText.textGrid().createGridLine(myLineIndex++, lineStart, myWordStop, myTextBuffer, myX1, myCurrentY);
						// only increment index if a word wasn't broken inside the
						// do/while loop above.. also, this is a while() loop too,
						// because multiple spaces don't count for shit when they're
						// at the end of a line like this.

						index = myWordStop; // back that ass up
						while ((index < myTextBuffer.length) && (myTextBuffer[index] == ' ')){
							index++;
						}
					}
					lineStart = index;
					myWordStart = index;
					myWordStop = index;
					myRunningX = myX1; // boxX1;
					myCurrentY -= theText.leading();
					// if (currentY > boxY2) return; // box is now full
					if (myCurrentY > myY2){
						return; // box is now full
					}

				}else{
					myRunningX += wordWidth + mySpaceWidth;
					// on to the next word
					myWordStop = index;
					myWordStart = index + 1;
				}

			}else if (myTextBuffer[index] == '\n'){
				if (lineStart != index){ // if line is not empty
					theText.textGrid().createGridLine(myLineIndex++, lineStart, index, myTextBuffer, myX1, myCurrentY);
				}
				lineStart = index + 1;
				myWordStart = lineStart;
				myRunningX = myX1; // fix for bug 188
				myCurrentY -= theText.leading();
				// if (currentY > boxY2) return; // box is now full
				if (myCurrentY > myY2){
					return; // box is now full
				}
			}
			index++;
		}
		if ((lineStart < myTextBuffer.length) && (lineStart != index)){
			theText.textGrid().createGridLine(myLineIndex++, lineStart, index, myTextBuffer, myX1, myCurrentY);
		}
		
		theText.changeWidth(theText.blockWidth());
		theText.changeHeight(-myCurrentY);
	}
}

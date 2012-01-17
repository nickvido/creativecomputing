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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.font.text.CCText.CCTextGridLine;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCLineBreaking {

	public void breakText(final CCText theText){
		theText.textGrid().clear();
		
		int myStart = 0;
		int myIndex = 0;

		float myX = 0;
		float myY = 0;

		int myLineIndex = 0;
		
		float myWidth = 0;
		
		char[] myTextBuffer = theText.text().toCharArray();
		
		while (myIndex < myTextBuffer.length) {
			if (myTextBuffer[myIndex] == '\n') {
				//myTextBuffer[myIndex] = '#';
				CCTextGridLine myLine = theText.textGrid().createGridLine(myLineIndex++,myStart, myIndex + 1, myTextBuffer, myX, myY);
				if(myLine != null)myWidth = CCMath.max(myWidth, myLine.width());
				myStart = myIndex + 1;
				myY -= theText.leading();
			}
			myIndex++;
		}
		
		if (myStart < myTextBuffer.length) {
			CCTextGridLine myLine = theText.textGrid().createGridLine(myLineIndex++,myStart, myIndex, myTextBuffer, myX, myY);
			if(myLine != null)myWidth = CCMath.max(myWidth, myLine.width());
			myY -= theText.leading();
		}

		theText.changeWidth(myWidth);
		theText.changeHeight(-myY);
	}
}

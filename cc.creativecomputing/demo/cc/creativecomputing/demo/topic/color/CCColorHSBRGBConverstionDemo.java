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
package cc.creativecomputing.demo.topic.color;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 * 
 */
public class CCColorHSBRGBConverstionDemo {
	public static void main(String[] args) {

		CCColor myColor1 = new CCColor(0.4f, 0.3f, 0.8f, 1.0f);
		CCLog.info("1. r: " + myColor1.r + " g: " + myColor1.g + " b:" + myColor1.b);
		float[] hsb = myColor1.hsb();
		CCLog.info("1. h: " + hsb[0] + " s: " + hsb[1] + " b:" + hsb[2]);

		CCColor myColor2 = new CCColor();
		myColor2.setHSB(hsb[0], hsb[1], hsb[2], myColor1.alpha());
		CCLog.info("2. r: " + myColor2.r + " g: " + myColor2.g + " b:" + myColor2.b);
	}
}

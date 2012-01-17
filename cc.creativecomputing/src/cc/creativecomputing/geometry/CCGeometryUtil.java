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
package cc.creativecomputing.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCGeometryUtil {

	public static List<CCVector2f> convexHull(List<CCVector2f> thePoints) {
		CCVector2f t = thePoints.get(0);

		// find the lowest point in the set. If two or more points have
		// the same minimum y coordinate choose the one with the minimu x.
		// This focal point is put in array location pts[0].
		
		for (CCVector2f myPoint:thePoints) {
			if (myPoint.y < t.y || (myPoint.y == t.y && myPoint.x < t.x)) {
				t = myPoint;
			}
		}

		CCRadialComparator c = new CCRadialComparator(t);
		
		// sort the points radially around the focal point.
		Collections.sort(thePoints, c);
		CCVector2f p;
		

		// palautettava pino
		Stack<CCVector2f> s = new Stack<CCVector2f>();

		s.push(thePoints.get(thePoints.size() - 1));
		s.push(thePoints.get(0));
		s.push(thePoints.get(1));
		for (int i = 2; i < thePoints.size() - 1; i++) {
			p = s.pop();
			c.setOrigin(s.peek());

			while (c.compare(p, thePoints.get(i)) > 0) {
				p = s.pop();
				c.setOrigin(s.peek());
			}

			s.push(p);
			s.push(thePoints.get(i));
		}

		p = s.pop();

		c.setOrigin(s.peek());
		if (c.compare(p, thePoints.get(thePoints.size() - 1)) <= 0)
			s.push(p);

		s.push(thePoints.get(thePoints.size() - 1));

		return new ArrayList<CCVector2f>(s);
	}
}

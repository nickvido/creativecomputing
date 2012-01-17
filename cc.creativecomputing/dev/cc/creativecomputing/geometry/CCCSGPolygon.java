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


/**
 * Represents a convex polygon. The vertices used to initialize a polygon must
 * be coplanar and form a convex loop. They do not have to be `CSG.Vertex`
 * instances but they must behave similarly (duck typing can be used for
 * customization).
 * 
 * Each convex polygon has a `shared` property, which is shared between all
 * polygons that are clones of each other or were split from the same polygon.
 * This can be used to define per-polygon properties (such as surface color).
 * 
 * @author christianriekoff
 *
 */
public class CCCSGPolygon {
	public List<CCCSGVertex> vertices;
	public boolean shared;
	public CCCSGPlane plane;
	
	public CCCSGPolygon(List<CCCSGVertex> vertices, boolean theIsShared) {
		vertices = vertices;
		shared = theIsShared;
		plane = CCCSGPlane.fromPoints(vertices.get(0).pos, vertices.get(1).pos, vertices.get(2).pos);
		}

	public CCCSGPolygon clone() {
		List<CCCSGVertex> myVertices = new ArrayList<CCCSGVertex>();
		    return new CCCSGPolygon(myVertices, shared);
		 }

	public void flip() {
		Collections.reverse(vertices);
		for(CCCSGVertex myVertex:vertices) {
			myVertex.flip();
		}
		plane.flip();
		  }
}

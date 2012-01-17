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
import java.util.List;


/**
 * Constructive Solid Geometry (CSG) is a modeling technique that uses Boolean
 * operations like union and intersection to combine 3D solids. This library
 * implements CSG operations on meshes elegantly and concisely using BSP trees,
 * and is meant to serve as an easily understandable implementation of the
 * algorithm. All edge cases involving overlapping coplanar polygons in both
 * solids are correctly handled.
 * 
 * Example usage:
 * 
//	     var cube = CSG.cube();
//	     var sphere = CSG.sphere({ radius: 1.3 });
//	     var polygons = cube.subtract(sphere).toPolygons();
 * 
 * ## Implementation Details
 * 
 * All CSG operations are implemented in terms of two functions, `clipTo()` and
 * `invert()`, which remove parts of a BSP tree inside another BSP tree and swap
 * solid and empty space, respectively. To find the union of `a` and `b`, we
 * want to remove everything in `a` inside `b` and everything in `b` inside `a`,
 * then combine polygons from `a` and `b` into one solid:
 * 
//	     a.clipTo(b);
//	     b.clipTo(a);
//	     a.build(b.allPolygons());
 * 
 * The only tricky part is handling overlapping coplanar polygons in both trees.
 * The code above keeps both copies, but we need to keep them in one tree and
 * remove them in the other tree. To remove them from `b` we can clip the
 * inverse of `b` against `a`. The code for union now looks like this:
 * 
//	     a.clipTo(b);
//	     b.clipTo(a);
//	     b.invert();
//	     b.clipTo(a);
//	     b.invert();
//	     a.build(b.allPolygons());
 * 
 * Subtraction and intersection naturally follow from set operations. If
 * union is `A | B`, subtraction is `A - B = ~(~A | B)` and intersection is
 * `A & B = ~(~A | ~B)` where `~` is the complement operator.
 * 
 * ## License
 * 
 * Copyright (c) 2011 Evan Wallace (http://madebyevan.com/), under the MIT license.

 * # class CSG

 * Holds a binary space partition tree representing a 3D solid. Two solids can
 * be combined using the `union()`, `subtract()`, and `intersect()` methods.
 * @author christianriekoff
 *
 */
public class CCCSG {
	private List<CCCSGPolygon> polygons = new ArrayList<CCCSGPolygon>();

	public CCCSG() {
	}
	/**
	* Construct a CSG solid from a list of `CSG.Polygon` instances.
	*/
	public CCCSG(List<CCCSGPolygon> thePolygons) {
		polygons = thePolygons;
	}

	
	public CCCSG clone() {
		CCCSG csg = new CCCSG();
	    csg.polygons = new ArrayList<CCCSGPolygon>();
	    for(CCCSGPolygon myPolygon:polygons) {
	    	csg.polygons.add(myPolygon.clone());
	    }
	    return csg;
	  }

	  public List<CCCSGPolygon> toPolygons() {
	    return polygons;
	  }
	  /**
	  * Return a new CSG solid representing space in either this solid or in the
	  * solid `csg`. Neither this solid nor the solid `csg` are modified.
	  * 
	  *     A.union(B)
	  * 
	  *     +-------+            +-------+
	  *     |       |            |       |
	  *     |   A   |            |       |
	  *     |    +--+----+   =   |       +----+
	  *     +----+--+    |       +----+       |
	  *          |   B   |            |       |
	  *          |       |            |       |
	  *          +-------+            +-------+
	  **/ 
	 public CCCSG union(CCCSG csg) {
		 CCCSGNode a = new CCCSGNode(this.clone().polygons);
		 CCCSGNode b = new CCCSGNode(csg.clone().polygons);
	    a.clipTo(b);
	    b.clipTo(a);
	    b.invert();
	    b.clipTo(a);
	    b.invert();
	    a.build(b.allPolygons());
	    return new CCCSG(a.allPolygons());
	  }

	 /**
	  * Return a new CSG solid representing space in this solid but not in the
	  * solid `csg`. Neither this solid nor the solid `csg` are modified.
	  * 
	  *     A.subtract(B)
	  * 
	  *     +-------+            +-------+
	  *     |       |            |       |
	  *     |   A   |            |       |
	  *     |    +--+----+   =   |    +--+
	  *     +----+--+    |       +----+
	  *          |   B   |
	  *          |       |
	  *          +-------+
	  **/
	 public CCCSG subtract(CCCSG csg) {
		 CCCSGNode a = new CCCSGNode(this.clone().polygons);
		 CCCSGNode b = new CCCSGNode(csg.clone().polygons);
	    a.invert();
	    a.clipTo(b);
	    b.clipTo(a);
	    b.invert();
	    b.clipTo(a);
	    b.invert();
	    a.build(b.allPolygons());
	    a.invert();
	    return new CCCSG(a.allPolygons());
	  }

	 /**
	  * Return a new CSG solid representing space both this solid and in the
	  * solid `csg`. Neither this solid nor the solid `csg` are modified.
	  * 
	  *     A.intersect(B)
	  * 
	  *     +-------+
	  *     |       |
	  *     |   A   |
	  *     |    +--+----+   =   +--+
	  *     +----+--+    |       +--+
	  *          |   B   |
	  *          |       |
	  *          +-------+
	  **/
	 public CCCSG intersect(CCCSG csg) {
		 CCCSGNode a = new CCCSGNode(this.clone().polygons);
		 CCCSGNode b = new CCCSGNode(csg.clone().polygons);
	    a.invert();
	    b.clipTo(a);
	    b.invert();
	    a.clipTo(b);
	    b.clipTo(a);
	    a.build(b.allPolygons());
	    a.invert();
	    return new CCCSG(a.allPolygons());
	  }

	  /**
	  * Return a new CSG solid with solid and empty space switched. This solid is
	  * not modified.
	  **/
	  public CCCSG inverse() {
	    CCCSG csg = this.clone();
	    csg.polygons.map(function(p) { p.flip(); });
	    return csg;
	  }
}

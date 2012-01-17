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

import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Represents a plane in 3D space.
 * 
 * @author christianriekoff
 * 
 */
public class CCCSGPlane {
	/**
	 * `EPSILON` is the tolerance used by `splitPolygon()` to decide if a point is on the plane.
	 **/
	public static float EPSILON = 1e-5f;

	public CCVector3f normal;
	public float w;

	public CCCSGPlane(CCVector3f normal, float w) {
		this.normal = normal;
		this.w = w;
	}

	public static CCCSGPlane fromPoints(CCVector3f a, CCVector3f b, CCVector3f c) {
		CCVector3f n = CCVecMath.normal(a, b, c);
		return new CCCSGPlane(n, n.dot(a));
	}

	public CCCSGPlane clone() {
		return new CCCSGPlane(this.normal.clone(), this.w);
	}

	public void flip() {
		this.normal.negate();
		this.w = -this.w;
	}

	private final static int COPLANAR = 0;
	private final static int FRONT = 1;
	private final static int BACK = 2;
	private final static int SPANNING = 3;

	/**
	 * Split `polygon` by this plane if needed, then put the polygon or polygon fragments in the appropriate lists.
	 * Coplanar polygons go into either `coplanarFront` or `coplanarBack` depending on their orientation with respect to
	 * this plane. Polygons in front or in back of this plane go into either `front` or `back`.
	 **/
	public void splitPolygon(CCCSGPolygon polygon, List<CCCSGPolygon> coplanarFront, List<CCCSGPolygon> coplanarBack, List<CCCSGPolygon> front, List<CCCSGPolygon> back) {

		// Classify each point as well as the entire polygon into one of the above
		// four classes.
		int polygonType = 0;
		List<Integer> types = new ArrayList<Integer>();
		for (CCCSGVertex myVertex : polygon.vertices) {
			float t = this.normal.dot(myVertex.pos) - this.w;
			int type = (t < -EPSILON) ? BACK : (t > EPSILON) ? FRONT : COPLANAR;
			polygonType |= type;
			types.add(type);
		}

		// Put the polygon in the correct list, splitting it when necessary.
		switch (polygonType) {
		case COPLANAR:
			(this.normal.dot(polygon.plane.normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
			break;
		case FRONT:
			front.add(polygon);
			break;
		case BACK:
			back.add(polygon);
			break;
		case SPANNING:
			List<CCCSGVertex> f = new ArrayList<CCCSGVertex>();
			List<CCCSGVertex> b = new ArrayList<CCCSGVertex>();
			for (int i = 0; i < polygon.vertices.size(); i++) {
				int j = (i + 1) % polygon.vertices.size();
				int ti = types.get(i), tj = types.get(j);
				CCCSGVertex vi = polygon.vertices.get(i), vj = polygon.vertices.get(j);
				if (ti != BACK)
					f.add(vi);
				if (ti != FRONT)
					b.add(ti != BACK ? vi.clone() : vi);
				if ((ti | tj) == SPANNING) {
					float t = (this.w - this.normal.dot(vi.pos)) / this.normal.dot(CCVecMath.subtract(vj.pos, vi.pos));
					CCCSGVertex v = vi.interpolate(vj, t);
					f.add(v);
					b.add(v.clone());
				}
			}
			if (f.size() >= 3)
				front.add(new CCCSGPolygon(f, polygon.shared));
			if (b.size() >= 3)
				back.add(new CCCSGPolygon(b, polygon.shared));
			break;
		}
	}
}

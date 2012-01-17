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

import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * * # class Vertex
 * 
 * Represents a vertex of a polygon. Use your own vertex class instead of this one to provide additional features like
 * texture coordinates and vertex colors. Custom vertex classes need to provide a `pos` property and `clone()`,
 * `flip()`, and `interpolate()` methods that behave analogous to the ones defined by `CSG.Vertex`. This class provides
 * `normal` so convenience functions like `CSG.sphere()` can return a smooth vertex normal, but `normal` is not used
 * anywhere else.
 * 
 * @author christianriekoff
 * 
 */
public class CCCSGVertex {

	public CCVector3f pos;
	public CCVector3f normal;

	public CCCSGVertex(CCVector3f pos, CCVector3f normal) {
		this.pos = pos;
		this.normal = normal;
	}

	public CCCSGVertex clone() {
		return new CCCSGVertex(this.pos.clone(), this.normal.clone());
	}

	/**
	 * Invert all orientation-specific data (e.g. vertex normal). Called when the orientation of a polygon is flipped.
	 * */
	public void flip() {
		this.normal.negate();
	}

	/**
	 * Create a new vertex between this vertex and `other` by linearly interpolating all properties using a parameter of
	 * `t`. Subclasses should override this to interpolate additional properties.
	 **/
	public CCCSGVertex interpolate(CCCSGVertex other, float t) {
		return new CCCSGVertex(CCVecMath.blend(t, this.pos, other.pos), CCVecMath.blend(t, this.normal, other.normal));
	}
}

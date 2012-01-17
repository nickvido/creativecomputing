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

import cc.creativecomputing.math.CCMath;


/**
 * @author christianriekoff
 *
 */
public class CCCSGVector {
	
	public float x;
	public float y;
	public float z;
	
	CCCSGVector(float x, float y, float z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	public CCCSGVector clone() {
	    return new CCCSGVector(this.x, this.y, this.z);
	  }

	  public CCCSGVector negated() {
	    return new CCCSGVector(-this.x, -this.y, -this.z);
	  }

	  public CCCSGVector plus(CCCSGVector a) {
	    return new CCCSGVector(this.x + a.x, this.y + a.y, this.z + a.z);
	  }

	  public CCCSGVector minus(CCCSGVector a) {
	    return new CCCSGVector(this.x - a.x, this.y - a.y, this.z - a.z);
	  }

	  public CCCSGVector times(float a) {
	    return new CCCSGVector(this.x * a, this.y * a, this.z * a);
	  }

	  public CCCSGVector dividedBy(float a) {
	    return new CCCSGVector(this.x / a, this.y / a, this.z / a);
	  }

	  public float dot(CCCSGVector a) {
	    return this.x * a.x + this.y * a.y + this.z * a.z;
	  }

	  public CCCSGVector lerp(CCCSGVector a, float t) {
	    return this.plus(a.minus(this).times(t));
	  }

	  public float length() {
	    return CCMath.sqrt(this.dot(this));
	  }

	  public CCCSGVector unit() {
	    return this.dividedBy(this.length());
	  }

	  public CCCSGVector cross(CCCSGVector a) {
	    return new CCCSGVector(
	      this.y * a.z - this.z * a.y,
	      this.z * a.x - this.x * a.z,
	      this.x * a.y - this.y * a.x
	    );
	  }
}

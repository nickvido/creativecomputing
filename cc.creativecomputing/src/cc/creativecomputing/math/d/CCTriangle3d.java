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
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cc.creativecomputing.math.d;


/**
 * <code>Triangle</code> defines a object for containing triangle information. The triangle is defined by a collection
 * of three <code>CCVector3d</code> objects.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author Christian Riekoff
 */
public class CCTriangle3d {

	private CCVector3d _myPoint0;
	private CCVector3d _myPoint1;
	private CCVector3d _myPoint2;

	private transient CCVector3d _myCenter;
	private transient CCVector3d _myNormal;

	private float _myProjection;

	private int _myIndex;

	public CCTriangle3d() {
		_myPoint0 = new CCVector3d();
		_myPoint1 = new CCVector3d();
		_myPoint2 = new CCVector3d();
	}

	/**
	 * Constructor instantiates a new <Code>Triangle</code> object with the supplied vectors as the points. It is
	 * recommended that the vertices be supplied in a counter clockwise winding to support normals for a right handed
	 * coordinate system.
	 * 
	 * @param thePoint1 the first point of the triangle.
	 * @param thePoint2 the second point of the triangle.
	 * @param thePoint3 the third point of the triangle.
	 */
	public CCTriangle3d(final CCVector3d thePoint1, final CCVector3d thePoint2, final CCVector3d thePoint3) {
		_myPoint0 = new CCVector3d(thePoint1);
		_myPoint1 = new CCVector3d(thePoint2);
		_myPoint2 = new CCVector3d(thePoint3);
	}

	/**
	 * 
	 * <code>get</code> retrieves a point on the triangle denoted by the index supplied.
	 * 
	 * @param theIndex the index of the point.
	 * @return the point.
	 */
	public CCVector3d get(final int theIndex) {
		switch (theIndex) {
		case 0:
			return _myPoint0;
		case 1:
			return _myPoint1;
		case 2:
			return _myPoint2;
		default:
			return null;
		}
	}
	
	/**
	 * Returns the first point of triangle
	 * @return the first point of triangle
	 */
	public CCVector3d p0(){
		return _myPoint0;
	}
	
	/**
	 * Returns the second point of triangle
	 * @return the second point of triangle
	 */
	public CCVector3d p1(){
		return _myPoint1;
	}
	
	/**
	 * Returns the third point of triangle
	 * @return the third point of triangle
	 */
	public CCVector3d p2(){
		return _myPoint2;
	}

	/**
	 * 
	 * <code>set</code> sets one of the triangles points to that specified as a parameter.
	 * 
	 * @param theIndex the index to place the point.
	 * @param thePoint the point to set.
	 */
	public void set(final int theIndex, final CCVector3d thePoint) {
		switch (theIndex) {
		case 0:
			_myPoint0.set(thePoint);
			break;
		case 1:
			_myPoint1.set(thePoint);
			break;
		case 2:
			_myPoint2.set(thePoint);
			break;
		}
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateCenter() {
		if (_myCenter == null)
			_myCenter = new CCVector3d(_myPoint0);
		else
			_myCenter.set(_myPoint0);
		_myCenter.add(_myPoint1).add(_myPoint2).scale(1f / 3f);
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateNormal() {
		if (_myNormal == null)
			_myNormal = new CCVector3d(_myPoint1);
		else
			_myNormal.set(_myPoint1);
		
		_myNormal.subtract(_myPoint0);
		_myNormal = _myNormal.cross(_myPoint2.x - _myPoint0.x, _myPoint2.y - _myPoint0.y, _myPoint2.z - _myPoint0.z);
		_myNormal.normalize();
	}

	/**
	 * obtains the center point of this triangle (average of the three triangles)
	 * 
	 * @return the center point.
	 */
	public CCVector3d center() {
		if (_myCenter == null) {
			calculateCenter();
		}
		return _myCenter;
	}

	/**
	 * obtains the unit length normal vector of this triangle, if set or calculated
	 * 
	 * @return the normal vector
	 */
	public CCVector3d normal() {
		if (_myNormal == null) {
			calculateNormal();
		}
		return _myNormal;
	}

	/**
	 * sets the normal vector of this triangle (to conform, must be unit length)
	 * 
	 * @param theNormal the normal vector.
	 */
	public void normal(final CCVector3d theNormal) {
		_myNormal = theNormal;
	}

	/**
	 * obtains the projection of the vertices relative to the line origin.
	 * 
	 * @return the projection of the triangle.
	 */
	public float projection() {
		return _myProjection;
	}

	/**
	 * sets the projection of the vertices relative to the line origin.
	 * 
	 * @param theProjection the projection of the triangle.
	 */
	public void projection(final float theProjection) {
		_myProjection = theProjection;
	}

	/**
	 * obtains an index that this triangle represents if it is contained in a OBBTree.
	 * 
	 * @return the index in an OBBtree
	 */
	public int index() {
		return _myIndex;
	}

	/**
	 * sets an index that this triangle represents if it is contained in a OBBTree.
	 * 
	 * @param theIndex the index in an OBBtree
	 */
	public void index(final int theIndex) {
		_myIndex = theIndex;
	}

	@Override
	public CCTriangle3d clone() {
		try {
			CCTriangle3d t = (CCTriangle3d) super.clone();
			t._myPoint0 = _myPoint0.clone();
			t._myPoint1 = _myPoint1.clone();
			t._myPoint2 = _myPoint2.clone();
			return t;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	/**
	 * Subtracts the given vector from all vectors off the triangle.
	 * @param theVector
	 * @return reference to the triangle
	 */
	public CCTriangle3d subtract(CCVector3d theVector){
		_myCenter = null;
		_myPoint0.subtract(theVector);
		_myPoint1.subtract(theVector);
		_myPoint2.subtract(theVector);
		return this;
	}
	
	/**
	 * Subtracts the given vector from all vectors off the triangle.
	 * @param theVector
	 * @return reference to the triangle
	 */
	public CCTriangle3d add(CCVector3d theVector){
		_myCenter = null;
		_myPoint0.add(theVector);
		_myPoint1.add(theVector);
		_myPoint2.add(theVector);
		return this;
	}
}

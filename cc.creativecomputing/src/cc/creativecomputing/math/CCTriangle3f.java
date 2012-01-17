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

package cc.creativecomputing.math;


/**
 * <code>Triangle</code> defines a object for containing triangle information. The triangle is defined by a collection
 * of three <code>CCVector3f</code> objects.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author Christian Riekoff
 */
public class CCTriangle3f {

	private CCVector3f _myPointA;
	private CCVector3f _myPointB;
	private CCVector3f _myPointC;

	private transient CCVector3f _myCenter;
	private transient CCVector3f _myNormal;

	private float _myProjection;

	private int _myIndex;

	public CCTriangle3f() {}

	/**
	 * Constructor instantiates a new <Code>Triangle</code> object with the supplied vectors as the points. It is
	 * recommended that the vertices be supplied in a counter clockwise winding to support normals for a right handed
	 * coordinate system.
	 * 
	 * @param thePoint1 the first point of the triangle.
	 * @param thePoint2 the second point of the triangle.
	 * @param thePoint3 the third point of the triangle.
	 */
	public CCTriangle3f(final CCVector3f thePoint1, final CCVector3f thePoint2, final CCVector3f thePoint3) {
		_myPointA = new CCVector3f(thePoint1);
		_myPointB = new CCVector3f(thePoint2);
		_myPointC = new CCVector3f(thePoint3);
	}

	/**
	 * 
	 * <code>get</code> retrieves a point on the triangle denoted by the index supplied.
	 * 
	 * @param theIndex the index of the point.
	 * @return the point.
	 */
	public CCVector3f get(final int theIndex) {
		switch (theIndex) {
		case 0:
			return _myPointA;
		case 1:
			return _myPointB;
		case 2:
			return _myPointC;
		default:
			return null;
		}
	}

	/**
	 * 
	 * <code>set</code> sets one of the triangles points to that specified as a parameter.
	 * 
	 * @param theIndex the index to place the point.
	 * @param thePoint the point to set.
	 */
	public void set(final int theIndex, final CCVector3f thePoint) {
		switch (theIndex) {
		case 0:
			_myPointA.set(thePoint);
			break;
		case 1:
			_myPointB.set(thePoint);
			break;
		case 2:
			_myPointC.set(thePoint);
			break;
		}
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateCenter() {
		if (_myCenter == null)
			_myCenter = new CCVector3f(_myPointA);
		else
			_myCenter.set(_myPointA);
		_myCenter.add(_myPointB).add(_myPointC).scale(1f / 3f);
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateNormal() {
		if (_myNormal == null)
			_myNormal = new CCVector3f(_myPointB);
		else
			_myNormal.set(_myPointB);
		_myNormal.subtract(_myPointA);
		_myNormal = _myNormal.cross(_myPointC.x() - _myPointA.x(), _myPointC.y() - _myPointA.y(), _myPointC.z() - _myPointA.z());
		_myNormal.normalize();
	}

	/**
	 * obtains the center point of this triangle (average of the three triangles)
	 * 
	 * @return the center point.
	 */
	public CCVector3f getCenter() {
		if (_myCenter == null) {
			calculateCenter();
		}
		return _myCenter;
	}

	/**
	 * sets the center point of this triangle (average of the three triangles)
	 * 
	 * @param theCenter the center point.
	 */
	public void setCenter(final CCVector3f theCenter) {
		_myCenter = theCenter;
	}

	/**
	 * obtains the unit length normal vector of this triangle, if set or calculated
	 * 
	 * @return the normal vector
	 */
	public CCVector3f normal() {
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
	public void normal(final CCVector3f theNormal) {
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
	public CCTriangle3f clone() {
		try {
			CCTriangle3f t = (CCTriangle3f) super.clone();
			t._myPointA = _myPointA.clone();
			t._myPointB = _myPointB.clone();
			t._myPointC = _myPointC.clone();
			return t;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}

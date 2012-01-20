/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 Part of the Processing project - http://processing.org

 Copyright (c) 2005-08 Ben Fry and Casey Reas

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General
 Public License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 Boston, MA  02111-1307  USA
 */

package cc.creativecomputing.math;

import cc.creativecomputing.util.CCFormatUtil;

/**
 * 3x2 affine matrix implementation.
 */
public class CCMatrix32f {

	public float m00, m01, m02;
	public float m10, m11, m12;

	/**
	 * Creates a new identy matrix
	 */
	public CCMatrix32f() {
		reset();
	}

	/**
	 * Creates a new matrix
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m10
	 * @param m11
	 * @param m12
	 */
	public CCMatrix32f(
		float m00, float m01, float m02, 
		float m10, float m11, float m12
	) {
		set(m00, m01, m02, m10, m11, m12);
	}

	/**
	 * Creates a matrix by copying the given matrix
	 * @param theMatrix
	 */
	public CCMatrix32f(final CCMatrix32f theMatrix) {
		set(theMatrix);
	}

	/**
	 * Sets this matrix to the identity matrix
	 */
	public void reset() {
		set(
			1, 0, 0, 
			0, 1, 0
		);
	}

	/**
	 * Returns a copy of this matrix.
	 */
	public CCMatrix32f clone() {
		CCMatrix32f outgoing = new CCMatrix32f();
		outgoing.set(this);
		return outgoing;
	}

	/**
	 * Copies the matrix contents into a 6 entry float array. 
	 * If target is null (or not the correct size), a new array will be created.
	 *
	 * @param theTarget the array to fill with the data
	 * @return 
	 */
	public float[] get(float[] theTarget) {
		if ((theTarget == null) || (theTarget.length != 6)) {
			theTarget = new float[6];
		}
		theTarget[0] = m00;
		theTarget[1] = m01;
		theTarget[2] = m02;

		theTarget[3] = m10;
		theTarget[4] = m11;
		theTarget[5] = m12;

		return theTarget;
	}

	/**
	 * Sets this matrix to the given matrix
	 * @param theMatrix matrix with new values
	 */
	public void set(final CCMatrix32f theMatrix) {
		set(theMatrix.m00, theMatrix.m01, theMatrix.m02, theMatrix.m10, theMatrix.m11, theMatrix.m12);
	}

	/**
	 * Sets this matrix from the values of the given array.
	 * The given array needs to contain six values. The
	 * first 3 values are taken as first row of the matrix, 
	 * the second 3 values as second row.
	 * @param theSource new values
	 */
	public void set(float[] theSource) {
		m00 = theSource[0];
		m01 = theSource[1];
		m02 = theSource[2];

		m10 = theSource[3];
		m11 = theSource[4];
		m12 = theSource[5];
	}

	/**
	 * Sets this matrix to the given values
	 * @param m00 1. row 1. column
	 * @param m01 1. row 2. column
	 * @param m02 1. row 3. column
	 * @param m10 2. row 1. column
	 * @param m11 2. row 2. column
	 * @param m12 2. row 3. column
	 */
	public void set(
		float m00, float m01, float m02, 
		float m10, float m11, float m12
	) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
	}

	/**
	 * Applies a translation to this matrix
	 * @param theX translation in x direction
	 * @param theY translation in y direction
	 */
	public void translate(final float theX, final float theY) {
		m02 = theX * m00 + theY * m01 + m02;
		m12 = theX * m10 + theY * m11 + m12;
	}
	
	/**
	 * Applies a translation to this matrix
	 * @param theVector translation vector
	 */
	public void translate(final CCVector2f theVector) {
		translate(theVector.x, theVector.y);
	}
	
	/**
	 * Applies a rotation to this matrix
	 * @param theAngle angle of the applied rotation
	 */
	public void rotate(final float theAngle) {
		float s = CCMath.sin(theAngle);
		float c = CCMath.cos(theAngle);

		float temp1 = m00;
		float temp2 = m01;
		m00 = c * temp1 + s * temp2;
		m01 = -s * temp1 + c * temp2;
		temp1 = m10;
		temp2 = m11;
		m10 = c * temp1 + s * temp2;
		m11 = -s * temp1 + c * temp2;
	}

	/**
	 * Applies a scale to this matrix
	 * @param theScale applied scale
	 */
	public void scale(final float theScale) {
		scale(theScale, theScale);
	}

	/**
	 * Applies a scale in x and y direction to this matrix
	 * @param theScaleX
	 * @param theScaleY
	 */
	public void scale(final float theScaleX, final float theScaleY) {
		m00 *= theScaleX;
		m01 *= theScaleY;
		m10 *= theScaleX;
		m11 *= theScaleY;
	}

	public void skewX(float angle) {
		apply(1, 0, 1, angle, 0, 0);
	}

	public void skewY(float angle) {
		apply(1, 0, 1, 0, angle, 0);
	}

	/**
	 * Applies the transformation of the given matrix
	 * @param source
	 */
	public void apply(CCMatrix32f source) {
		apply(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12);
	}

	public void apply(
		float n00, float n01, float n02, 
		float n10, float n11, float n12
	) {
		float t0 = m00;
		float t1 = m01;
		m00 = n00 * t0 + n10 * t1;
		m01 = n01 * t0 + n11 * t1;
		m02 += n02 * t0 + n12 * t1;

		t0 = m10;
		t1 = m11;
		m10 = n00 * t0 + n10 * t1;
		m11 = n01 * t0 + n11 * t1;
		m12 += n02 * t0 + n12 * t1;
	}

	/**
	 * Apply another matrix to the left of this one.
	 */
	public void preApply(CCMatrix32f left) {
		preApply(left.m00, left.m01, left.m02, left.m10, left.m11, left.m12);
	}

	public void preApply(float n00, float n01, float n02, float n10, float n11, float n12) {
		float t0 = m02;
		float t1 = m12;
		n02 += t0 * n00 + t1 * n01;
		n12 += t0 * n10 + t1 * n11;

		m02 = n02;
		m12 = n12;

		t0 = m00;
		t1 = m10;
		m00 = t0 * n00 + t1 * n01;
		m10 = t0 * n10 + t1 * n11;

		t0 = m01;
		t1 = m11;
		m01 = t0 * n00 + t1 * n01;
		m11 = t0 * n10 + t1 * n11;
	}
	
	/**
	 * Multiplies the given source vector against this matrix
	 * @param theSource the vector to multiply with this matrix
	 * @param theTarget if this vector is not null the result will be stored in this vector
	 * @return the result of the multiplication
	 */
	public CCVector2f transform(final CCVector2f theSource, CCVector2f theTarget) {
		if (theTarget == null) {
			theTarget = new CCVector2f();
		}

		theTarget.x = m00 * theSource.x + m01 * theSource.y + m02;
		theTarget.y = m10 * theSource.x + m11 * theSource.y + m12;
		return theTarget;
	}
	
	/**
	 * Multiplies the given source vector against this matrix
	 * @param theSource the vector to multiply with this matrix
	 * @return the result of the multiplication
	 */
	public CCVector2f transform(CCVector2f theSource) {
		return transform(theSource, null);
	}

	/**
	 * Multiply a two element vector against this matrix. If out is null or not length four, a new float array will be
	 * returned. The values for vec and out can be the same (though that's less efficient).
	 */
	public float[] mult(float vec[], float out[]) {
		if (out == null || out.length != 2) {
			out = new float[2];
		}

		if (vec == out) {
			float tx = m00 * vec[0] + m01 * vec[1] + m02;
			float ty = m10 * vec[0] + m11 * vec[1] + m12;

			out[0] = tx;
			out[1] = ty;

		} else {
			out[0] = m00 * vec[0] + m01 * vec[1] + m02;
			out[1] = m10 * vec[0] + m11 * vec[1] + m12;
		}

		return out;
	}

	public float multX(float x, float y) {
		return m00 * x + m01 * y + m02;
	}

	public float multY(float x, float y) {
		return m10 * x + m11 * y + m12;
	}

	/**
	 * Transpose this matrix.
	 */
	public void transpose() {}

	/**
	 * Invert this matrix. Implementation stolen from OpenJDK.
	 * 
	 * @return true if successful
	 */
	public boolean invert() {
		float determinant = determinant();
		if (Math.abs(determinant) <= Float.MIN_VALUE) {
			return false;
		}

		float t00 = m00;
		float t01 = m01;
		float t02 = m02;
		float t10 = m10;
		float t11 = m11;
		float t12 = m12;

		m00 = t11 / determinant;
		m10 = -t10 / determinant;
		m01 = -t01 / determinant;
		m11 = t00 / determinant;
		m02 = (t01 * t12 - t11 * t02) / determinant;
		m12 = (t10 * t02 - t00 * t12) / determinant;

		return true;
	}
	
	public CCMatrix32f inverse() {
		CCMatrix32f myResult = clone();
		myResult.invert();
		return myResult;
	}

	/**
	 * @return the determinant of the matrix
	 */
	public float determinant() {
		return m00 * m11 - m01 * m10;
	}

	// ////////////////////////////////////////////////////////////

	public void print() {
		int big = (int) CCMath.abs(
			CCMath.max(
				CCMath.max(CCMath.abs(m00), CCMath.abs(m01), CCMath.abs(m02)), 
				CCMath.max(CCMath.abs(m10), CCMath.abs(m11), CCMath.abs(m12))
			)
		);

		int digits = 1;
		if (Float.isNaN(big) || Float.isInfinite(big)) { // avoid infinite loop
			digits = 5;
		} else {
			while ((big /= 10) != 0)
				digits++; // cheap log()
		}

		System.out.println(CCFormatUtil.nfs(m00, digits, 4) + " " + CCFormatUtil.nfs(m01, digits, 4) + " " + CCFormatUtil.nfs(m02, digits, 4));
		System.out.println(CCFormatUtil.nfs(m10, digits, 4) + " " + CCFormatUtil.nfs(m11, digits, 4) + " " + CCFormatUtil.nfs(m12, digits, 4));
		System.out.println();
	}

	// ////////////////////////////////////////////////////////////

	// TODO these need to be added as regular API, but the naming and
	// implementation needs to be improved first. (e.g. actually keeping track
	// of whether the matrix is in fact identity internally.)

	protected boolean isIdentity() {
		return ((m00 == 1) && (m01 == 0) && (m02 == 0) && (m10 == 0) && (m11 == 1) && (m12 == 0));
	}

	// TODO make this more efficient, or move into PMatrix2D
	protected boolean isWarped() {
		return ((m00 != 1) || (m01 != 0) && (m10 != 0) || (m11 != 1));
	}
}
/* 
 * Copyright (c) 2006, 2007 Karsten Schmidt
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package cc.creativecomputing.math.signal;

import java.util.Random;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

public class CCFarbrauschNoise extends CCSignal {

	private static final int PERLIN_Y_WRAP_B = 4;

	private static final int PERLIN_Y_WRAP = 1 << PERLIN_Y_WRAP_B;

	private static final int PERLIN_Z_WRAP_B = 8;

	private static final int PERLIN_Z_WRAP = 1 << PERLIN_Z_WRAP_B;

	private static final int PERLIN_SIZE = 4095;

	private float[] _myRandomTable;

	private Random _myPerlinRandom;

	/**
	 * Computes the Perlin noise function value at x, y, z.
	 */
	public float[] signalImpl(float theX, float theY, float theZ) {
		if (_myRandomTable == null) {
			if (_myPerlinRandom == null) {
				_myPerlinRandom = new Random();
			}
			_myRandomTable = new float[PERLIN_SIZE + 1];
			for (int i = 0; i < PERLIN_SIZE + 1; i++) {
				_myRandomTable[i] = _myPerlinRandom.nextFloat();
			}
		}

		theX += _myOffsetX;
		theY += _myOffsetY;
		theZ += _myOffsetZ;

		theX *= _myScale;
		theY *= _myScale;
		theZ *= _myScale;

		if (theX < 0)
			theX = -theX;
		if (theY < 0)
			theY = -theY;
		if (theZ < 0)
			theZ = -theZ;

		int xi = (int) theX;
		int yi = (int) theY;
		int zi = (int) theZ;

		float xf = theX - xi;
		float yf = theY - yi;
		float zf = theZ - zi;
		float rxf, ryf;

		float r = 0;

		float n1, n2, n3;

		int of = xi + (yi << PERLIN_Y_WRAP_B) + (zi << PERLIN_Z_WRAP_B);

		rxf = noise_fsc(xf);
		ryf = noise_fsc(yf);

		n1 = _myRandomTable[of & PERLIN_SIZE];
		n1 += rxf * (_myRandomTable[(of + 1) & PERLIN_SIZE] - n1);
		n2 = _myRandomTable[(of + PERLIN_Y_WRAP) & PERLIN_SIZE];
		n2 += rxf * (_myRandomTable[(of + PERLIN_Y_WRAP + 1) & PERLIN_SIZE] - n2);
		n1 += ryf * (n2 - n1);

		of += PERLIN_Z_WRAP;
		n2 = _myRandomTable[of & PERLIN_SIZE];
		n2 += rxf * (_myRandomTable[(of + 1) & PERLIN_SIZE] - n2);
		n3 = _myRandomTable[(of + PERLIN_Y_WRAP) & PERLIN_SIZE];
		n3 += rxf * (_myRandomTable[(of + PERLIN_Y_WRAP + 1) & PERLIN_SIZE] - n3);
		n2 += ryf * (n3 - n2);

		n1 += noise_fsc(zf) * (n2 - n1);

		r += n1;
		xi <<= 1;
		xf *= 2;
		yi <<= 1;
		yf *= 2;
		zi <<= 1;
		zf *= 2;

		if (xf >= 1.0f) {
			xi++;
			xf--;
		}
		if (yf >= 1.0f) {
			yi++;
			yf--;
		}
		if (zf >= 1.0f) {
			zi++;
			zf--;
		}

		return new float[] {r};
	}

	private float noise_fsc(float i) {
		return 0.5f * (1.0f - CCMath.cos(i * CCMath.PI));
	}

	public void noiseSeed(long what) {
		if (_myPerlinRandom == null)
			_myPerlinRandom = new Random();
		_myPerlinRandom.setSeed(what);
	}
}
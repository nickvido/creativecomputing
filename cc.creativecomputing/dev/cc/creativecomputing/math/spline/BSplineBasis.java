package cc.creativecomputing.math.spline;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.CCArrayUtil;

class BSplineBasis {

	protected int mNumCtrlPoints; // n+1
	protected int mDegree; // d
	protected float[] mKnots; // knot[n+d+2]
	protected boolean mOpen, mUniform;

	// Storage for the basis functions and their derivatives first three
	// derivatives. The basis array is always allocated by the constructor
	// calls. A derivative basis array is allocated on the first call to a
	// derivative member function.
	protected float[][] m_aafBD0; // bd0[d+1][n+d+1]
	protected float[][] m_aafBD1; // bd1[d+1][n+d+1]
	protected float[][] m_aafBD2; // bd2[d+1][n+d+1]
	protected float[][] m_aafBD3; // bd3[d+1][n+d+1]

	public BSplineBasis() {
		mKnots = null;
		m_aafBD0 = null;
		m_aafBD1 = null;
		m_aafBD2 = null;
		m_aafBD3 = null;
		mNumCtrlPoints = -1;
	}

	// Open uniform or periodic uniform. The knot array is internally
	// generated with equally spaced elements.
	public BSplineBasis(int iNumCtrlPoints, int iDegree, boolean bOpen) {
		create(iNumCtrlPoints, iDegree, bOpen);
	}

	public void create(int iNumCtrlPoints, int iDegree, boolean bOpen) {
		mUniform = true;

		int i, iNumKnots = initialize(iNumCtrlPoints, iDegree, bOpen);
		float fFactor = (1.0f) / (iNumCtrlPoints - mDegree);
		if (mOpen) {
			for (i = 0; i <= mDegree; i++) {
				mKnots[i] = (float) 0.0;
			}

			for (/**/; i < iNumCtrlPoints; i++) {
				mKnots[i] = (i - mDegree) * fFactor;
			}

			for (/**/; i < iNumKnots; i++) {
				mKnots[i] = (float) 1.0;
			}
		} else {
			for (i = 0; i < iNumKnots; i++) {
				mKnots[i] = (i - mDegree) * fFactor;
			}
		}
	}

	// Open nonuniform. The knot array must have n-d elements. The elements
	// must be nondecreasing. Each element must be in [0,1]. The caller is
	// responsible for deleting afKnot. An internal copy is made, so to
	// dynamically change knots you must use the setKnot function.
	public BSplineBasis(int aNumCtrlPoints, int iDegree, float[] afKnot) {
		create(mNumCtrlPoints, iDegree, afKnot);
	}

	public void create(int aNumCtrlPoints, int iDegree, float[] afKnot) {
		mUniform = false;

		mNumCtrlPoints = aNumCtrlPoints;

		int i, iNumKnots = initialize(mNumCtrlPoints, iDegree, true);
		for (i = 0; i <= mDegree; i++) {
			mKnots[i] = (float) 0.0;
		}

		for (int j = 0; i < mNumCtrlPoints; i++, j++) {
			mKnots[i] = afKnot[j];
		}

		for ( /**/; i < iNumKnots; i++) {
			mKnots[i] = (float) 1.0;
		}
	}

	public BSplineBasis(BSplineBasis basis) {
		mKnots = basis.mKnots;
		m_aafBD0 = basis.m_aafBD0;
		m_aafBD1 = basis.m_aafBD1;
		m_aafBD2 = basis.m_aafBD2;
		m_aafBD3 = basis.m_aafBD3;
	}

	public BSplineBasis set(BSplineBasis basis) {

		mNumCtrlPoints = basis.mNumCtrlPoints;
		mDegree = basis.mDegree;
		mOpen = basis.mOpen;
		mUniform = basis.mUniform;

		if (mNumCtrlPoints > 0) {
			int numKnots = initialize(mNumCtrlPoints, mDegree, mOpen);
			mKnots = CCArrayUtil.copyOf(basis.mKnots, basis.mKnots.length);
		} else {
			mKnots = null;
			m_aafBD0 = m_aafBD1 = m_aafBD2 = m_aafBD3 = null;
		}
		return this;
	}

	public int getNumControlPoints() {
		return mNumCtrlPoints;
	}

	public int getDegree() {
		return mDegree;
	}

	public boolean isOpen() {
		return mOpen;
	}

	public boolean isUniform() {
		return mUniform;
	}

	// The knot values can be changed only if the basis function is nonuniform
	// and the input index is valid (0 <= i <= n-d-1). If these conditions
	// are not satisfied, getKnot returns MAX_REAL.
	public void setKnot(int i, float fKnot) {
		if (!mUniform) {
			// access only allowed to elements d+1 <= j <= n
			int j = i + mDegree + 1;
			if (mDegree + 1 <= j && j <= mNumCtrlPoints - 1) {
				mKnots[j] = fKnot;
			}
		}
	}

	public float getKnot(int i) {
		if (!mUniform) {
			// access only allowed to elements d+1 <= j <= n
			int j = i + mDegree + 1;
			if ((mDegree + 1 <= j) && (j <= mNumCtrlPoints - 1)) {
				return mKnots[j];
			}
		}

		return Float.MAX_VALUE;
	}

	// access basis functions and their derivatives
	public float getD0(int i) {
		return m_aafBD0[mDegree][i];
	}

	public float getD1(int i) {
		return m_aafBD1[mDegree][i];
	}

	public float getD2(int i) {
		return m_aafBD2[mDegree][i];
	}

	public float getD3(int i) {
		return m_aafBD3[mDegree][i];
	}

	// Determine knot index i for which knot[i] <= rfTime < knot[i+1].
	protected int getKey(float rfTime) {
		if (mOpen) {
			// open splines clamp to [0,1]
			if (rfTime <= (float) 0.0) {
				rfTime = (float) 0.0;
				return mDegree;
			} else if (rfTime >= (float) 1.0) {
				rfTime = (float) 1.0;
				return mNumCtrlPoints - 1;
			}
		} else {
			// periodic splines wrap to [0,1]
			if (rfTime < 0.0f || rfTime >= 1.0f) {
				rfTime -= CCMath.floor(rfTime);
			}
		}

		int i;
		if (mUniform) {
			i = mDegree + (int) ((mNumCtrlPoints - mDegree) * rfTime);
		} else {
			for (i = mDegree + 1; i <= mNumCtrlPoints; i++) {
				if (rfTime < mKnots[i]) {
					break;
				}
			}
			i--;
		}

		return i;
	}

	// evaluate basis functions and their derivatives
	public void compute(float fTime, int uiOrder, int riMinIndex, int riMaxIndex) {
		// only derivatives through third order currently supported
		assert (uiOrder <= 3);

		if (uiOrder >= 1) {
			if (m_aafBD1 == null) {
				m_aafBD1 = allocate();
			}
		}
		if (uiOrder >= 2) {
			if (m_aafBD2 == null) {
				m_aafBD2 = allocate();
			}
		}
		if (uiOrder >= 3) {
			if (m_aafBD3 == null) {
				m_aafBD3 = allocate();
			}
		}

		int i = getKey(fTime);
		m_aafBD0[0][i] = (float) 1.0f;

		if (uiOrder >= 1) {
			m_aafBD1[0][i] = (float) 0.0f;
			if (uiOrder >= 2) {
				m_aafBD2[0][i] = (float) 0.0f;
				if (uiOrder >= 3) {
					m_aafBD3[0][i] = (float) 0.0f;
				}
			}
		}

		float fN0 = fTime - mKnots[i], fN1 = mKnots[i + 1] - fTime;
		float fInvD0, fInvD1;
		int j;
		for (j = 1; j <= mDegree; j++) {
			fInvD0 = ((float) 1.0) / (mKnots[i + j] - mKnots[i]);
			fInvD1 = ((float) 1.0) / (mKnots[i + 1] - mKnots[i - j + 1]);

			m_aafBD0[j][i] = fN0 * m_aafBD0[j - 1][i] * fInvD0;
			m_aafBD0[j][i - j] = fN1 * m_aafBD0[j - 1][i - j + 1] * fInvD1;

			if (uiOrder >= 1) {
				m_aafBD1[j][i] = (fN0 * m_aafBD1[j - 1][i] + m_aafBD0[j - 1][i]) * fInvD0;
				m_aafBD1[j][i - j] = (fN1 * m_aafBD1[j - 1][i - j + 1] - m_aafBD0[j - 1][i - j + 1]) * fInvD1;

				if (uiOrder >= 2) {
					m_aafBD2[j][i] = (fN0 * m_aafBD2[j - 1][i] + ((float) 2.0) * m_aafBD1[j - 1][i]) * fInvD0;
					m_aafBD2[j][i - j] = (fN1 * m_aafBD2[j - 1][i - j + 1] - ((float) 2.0) * m_aafBD1[j - 1][i - j + 1]) * fInvD1;

					if (uiOrder >= 3) {
						m_aafBD3[j][i] = (fN0 * m_aafBD3[j - 1][i] + ((float) 3.0) * m_aafBD2[j - 1][i]) * fInvD0;
						m_aafBD3[j][i - j] = (fN1 * m_aafBD3[j - 1][i - j + 1] - ((float) 3.0) * m_aafBD2[j - 1][i - j + 1]) * fInvD1;
					}
				}
			}
		}

		for (j = 2; j <= mDegree; j++) {
			for (int k = i - j + 1; k < i; k++) {
				fN0 = fTime - mKnots[k];
				fN1 = mKnots[k + j + 1] - fTime;
				fInvD0 = ((float) 1.0) / (mKnots[k + j] - mKnots[k]);
				fInvD1 = ((float) 1.0) / (mKnots[k + j + 1] - mKnots[k + 1]);

				m_aafBD0[j][k] = fN0 * m_aafBD0[j - 1][k] * fInvD0 + fN1 * m_aafBD0[j - 1][k + 1] * fInvD1;

				if (uiOrder >= 1) {
					m_aafBD1[j][k] = (fN0 * m_aafBD1[j - 1][k] + m_aafBD0[j - 1][k]) * fInvD0 + (fN1 * m_aafBD1[j - 1][k + 1] - m_aafBD0[j - 1][k + 1]) * fInvD1;

					if (uiOrder >= 2) {
						m_aafBD2[j][k] = (fN0 * m_aafBD2[j - 1][k] + ((float) 2.0) * m_aafBD1[j - 1][k]) * fInvD0
								+ (fN1 * m_aafBD2[j - 1][k + 1] - ((float) 2.0) * m_aafBD1[j - 1][k + 1]) * fInvD1;

						if (uiOrder >= 3) {
							m_aafBD3[j][k] = (fN0 * m_aafBD3[j - 1][k] + ((float) 3.0) * m_aafBD2[j - 1][k]) * fInvD0
									+ (fN1 * m_aafBD3[j - 1][k + 1] - ((float) 3.0) * m_aafBD2[j - 1][k + 1]) * fInvD1;
						}
					}
				}
			}
		}

		riMinIndex = i - mDegree;
		riMaxIndex = i;
	}

	protected int initialize(int iNumCtrlPoints, int iDegree, boolean bOpen) {
		assert (iNumCtrlPoints >= 2);
		assert (1 <= iDegree && iDegree <= iNumCtrlPoints - 1);

		mNumCtrlPoints = iNumCtrlPoints;
		mDegree = iDegree;
		mOpen = bOpen;

		int iNumKnots = mNumCtrlPoints + mDegree + 1;
		mKnots = new float[iNumKnots];

		m_aafBD0 = allocate();
		m_aafBD1 = null;
		m_aafBD2 = null;
		m_aafBD3 = null;

		return iNumKnots;
	}

	protected float[][] allocate() {
		int iRows = mDegree + 1;
	    int iCols = mNumCtrlPoints + mDegree;
	    return new float[iCols][iRows];
	}

}
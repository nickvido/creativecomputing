package cc.creativecomputing.math.spline;

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

class BSpline
{
	
	protected int mNumCtrlPoints;
	protected CCVector3f[] mCtrlPoints;  // ctrl[n+1]
	protected boolean mLoop;
	protected BSplineBasis mBasis;
	protected int mReplicate;  // the number of replicated control points
  
	// Construction and destruction.  The caller is responsible for deleting
	// the input arrays if they were dynamically allocated.  Internal copies
	// of the arrays are made, so to dynamically change control points or
	// knots you must use the 'setControlPoint', 'getControlPoint', and
	// 'Knot' member functions.

	// Uniform spline.  The number of control points is n+1 >= 2.  The degree
	// of the B-spline is d and must satisfy 1 <= d <= n.  The knots are
	// implicitly calculated in [0,1].  If bOpen is 'true', the spline is
	// open and the knots are
	//   t[i] = 0,               0 <= i <= d
	//          (i-d)/(n+1-d),   d+1 <= i <= n
	//          1,               n+1 <= i <= n+d+1
	// If bOpen is 'false', the spline is periodic and the knots are
	//   t[i] = (i-d)/(n+1-d),   0 <= i <= n+d+1
	// If bLoop is 'true', extra control points are added to generate a closed
	// curve.  For an open spline, the control point array is reallocated and
	// one extra control point is added, set to the first control point
	// C[n+1] = C[0].  For a periodic spline, the control point array is
	// reallocated and the first d points are replicated.  In either case the
	// knot array is calculated accordingly.
	public BSpline(List<CCVector3f> points, int degree, boolean loop, boolean open ){
		assert(points.size() >= 2);
		assert( ( 1 <= degree ) && ( degree <= (int)points.size() - 1 ) );

		mLoop = loop;
		mNumCtrlPoints = (int)points.size();
		mReplicate = ( mLoop ? (open ? 1 : degree) : 0);
		createControl( points.toArray(new CCVector3f[points.size()]));
		mBasis.create( mNumCtrlPoints + mReplicate, degree, open );
	}

	// Open, nonuniform spline.  The knot array must have n-d elements.  The
	// elements must be nondecreasing.  Each element must be in [0,1].
	public BSpline() {
		mCtrlPoints = null;
		mNumCtrlPoints = -1;
	}
	public BSpline( int numControlPoints, CCVector3f[] controlPoints, int degree, boolean loop, float[] knots )
	{
		mNumCtrlPoints = numControlPoints;
		mLoop = loop;
		
		assert( mNumCtrlPoints >= 2);
		assert( ( 1 <= degree ) && ( degree <= mNumCtrlPoints - 1 ) );

		mReplicate = (mLoop ? 1 : 0);
		createControl( controlPoints );
		mBasis.create( mNumCtrlPoints + mReplicate, degree, knots );
	}

	public int getNumControlPoints()  { return mNumCtrlPoints; }
	public int getDegree()  { return mBasis.getDegree(); }
	public int getNumSpans()  { return mNumCtrlPoints - mBasis.getDegree(); }
	public boolean isOpen()  { return mBasis.isOpen(); }
	public boolean isUniform()  { return mBasis.isUniform(); }
	public boolean isLoop()  { return mLoop; }

	// Control points may be changed at any time.  The input index should be
	// valid (0 <= i <= n).  If it is invalid, getControlPoint returns a
	// vector whose components are all MAX_REAL.
	public void setControlPoint( int i, CCVector3f rkCtrl ){
		if( ( 0 <= i ) && ( i < mNumCtrlPoints ) ) {
			// set the control point
			mCtrlPoints[i] = rkCtrl;

			// set the replicated control point
			if( i < mReplicate ) {
				mCtrlPoints[mNumCtrlPoints+i] = rkCtrl;
			}
		}
	}
	public CCVector3f getControlPoint( int i ) {
	    if( ( 0 <= i ) && ( i < mNumCtrlPoints) ) {
			return mCtrlPoints[i];
	    }

	    return null;
	}

	// The knot values can be changed only if the basis function is nonuniform
	// and the input index is valid (0 <= i <= n-d-1).  If these conditions
	// are not satisfied, getKnot returns MAX_REAL.
	public void setKnot( int i, float fKnot ){
	    mBasis.setKnot( i, fKnot );
	}
	public float getKnot( int i ) {
	    return mBasis.getKnot( i );
	}

	// The spline is defined for 0 <= t <= 1.  If a t-value is outside [0,1],
	// an open spline clamps t to [0,1].  That is, if t > 1, t is set to 1;
	// if t < 0, t is set to 0.  A periodic spline wraps to to [0,1].  That
	// is, if t is outside [0,1], then t is set to t-floor(t).
	public CCVector3f getPosition( float t ){
		CCVector3f kPos = new CCVector3f();
		get( t, kPos, null, null, null);
		return kPos;
	}
	public CCVector3f getDerivative( float t ){
		CCVector3f kDer1 = new CCVector3f();
		get( t, null, kDer1, null, null );
		return kDer1;
	}
	public CCVector3f getSecondDerivative( float t ) {
		CCVector3f kDer2 = new CCVector3f();
		get( t, null, null, kDer2, null );
		return kDer2;
	}
	public CCVector3f getThirdDerivative( float t ) {
		CCVector3f kDer3 = new CCVector3f();
		get( t, null, null, null, kDer3 );
		return kDer3;
	}
	public float getSpeed( float t )  { return getDerivative( t ).length(); }

	public float getLength( float fT0, float fT1 ){
		if( fT0 >= fT1 )
			return (float)0.0;

	    return rombergIntegral<typename T::TYPE>( 10, fT0, fT1, getSpeedWithData<T>, (void*)this );
	}

	// If you need position and derivatives at the same time, it is more
	// efficient to call these functions.  Pass the addresses of those
	// quantities whose values you want.  You may pass 0 in any argument
	// whose value you do not want.
	public void get( float t, CCVector3f position, CCVector3f firstDerivative, CCVector3f secondDerivative, CCVector3f thirdDerivative){
		int i, iMin, iMax;
		if( thirdDerivative != null ) {
			mBasis.compute( t, 3, iMin, iMax );
		}
		else if( secondDerivative !=null) {
			mBasis.compute( t, 2, iMin, iMax );
		}
		else if( firstDerivative != null) {
			mBasis.compute( t, 1, iMin, iMax );
		}
		else {
	        mBasis.compute( t, 0, iMin, iMax );
		}

		if( position != null) {
			position.set(0,0,0);
			for( i = iMin; i <= iMax; i++ ) {
				float weight = mBasis.getD0( i );
				position.add(mCtrlPoints[i].clone().scale(weight));
			}
		}

		if( firstDerivative != null) {
			firstDerivative.set(0,0,0);
			for( i = iMin; i <= iMax; i++ ) {
				firstDerivative.add(mCtrlPoints[i].clone().scale(mBasis.getD1( i )));
			}
		}

		if( secondDerivative != null) {
			secondDerivative.set(0,0,0);
			for( i = iMin; i <= iMax; i++ ) {
				secondDerivative.add(mCtrlPoints[i].clone().scale(mBasis.getD2( i )));
			}
		}

		if( thirdDerivative != null) {
			thirdDerivative.set(0,0,0);
			for (i = iMin; i <= iMax; i++) {
				thirdDerivative.add(mCtrlPoints[i].clone().scale(mBasis.getD3( i )));
			}
		}
	}
	
	//! Returns the time associated with an arc length in the range [0,getLength(0,1)]
	public float getTime( float length ) {
		int MAX_ITERATIONS = 32;
		 float TOLERANCE = 1.0e-03f;
		// ensure that we remain within valid parameter space
		float totalLength = getLength( 0, 1 );
		if( length >= totalLength )
			return 1;
		if( length <= 0 )
			return 0;

		// initialize bisection endpoints
		float a = 0, b = 1;
		float p = length / totalLength;    // make first guess

		// iterate and look for zeros
		for ( int i = 0; i < MAX_ITERATIONS; ++i ) {
			// compute function value and test against zero
			float func = getLength( 0, p ) - length;
			if( CCMath.abs( func ) < TOLERANCE ) {
				return p;
			}

			 // update bisection endpoints
			if( func < 0 ) {
				a = p;
			}
			else {
				b = p;
			}

			// get speed along curve
			float speed = getSpeed( p );

			// if result will lie outside [a,b] 
			if( ((p-a)*speed - func)*((p-b)*speed - func) > -TOLERANCE ) {
				// do bisection
				p = 0.5f*(a+b);
			}    
			else {
				// otherwise Newton-Raphson
				p -= func/speed;
			}
		}

		// We failed to converge, but hopefully 'p' is close enough anyway
		return p;
	}


	// Access the basis function to compute it without control points.  This
	// is useful for least squares fitting of curves.
	public BSplineBasis getBasis(){
		return mBasis;
	}

 
    // Replicate the necessary number of control points when the create
    // function has bLoop equal to true, in which case the spline curve must
    // be a closed curve.
	protected void createControl(CCVector3f[] akCtrlPoint ){
		int iNewNumCtrlPoints = mNumCtrlPoints + mReplicate;
		mCtrlPoints = new T[iNewNumCtrlPoints];
		size_t uiSrcSize = mNumCtrlPoints*sizeof(T);
		memcpy( mCtrlPoints, akCtrlPoint, uiSrcSize );
		
		for( int i = 0; i < mReplicate; i++ ) {
			mCtrlPoints[mNumCtrlPoints+i] = akCtrlPoint[i];
		}
	}

	
}
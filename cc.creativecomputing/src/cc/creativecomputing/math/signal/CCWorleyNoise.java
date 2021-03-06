/* 
	Copyright 1994, 2002 by Steven Worley
	This software may be modified and redistributed without restriction
	provided this comment header remains intact in the source code.
	This code is provided with no warrantee, express or implied, for
	any purpose.
	
	A detailed description and application examples can be found in the
	1996 SIGGRAPH paper "A Cellular Texture Basis Function" and
	especially in the 2002 book "Texturing and Modeling, a Procedural
	Approach, 3rd edition." There is also extra information on the web
	site http://www.worley.com/cellular.html .
	
	If you do find interesting uses for this tool, and especially if
	you enhance it, please drop me an email at steve@worley.com.
	
	An implementation of the key cellular texturing basis
	function. This function is hardwired to return an average F_1 value
	of 1.0. It returns the <n> most closest feature point distances
	F_1, F_2, .. F_n the vector delta to those points, and a 32 bit
	seed for each of the feature points.  This function is not
	difficult to extend to compute alternative information such as
	higher order F values, to use the Manhattan distance metric, or
	other fun perversions.
	
	<at>    The input sample location. 
	<max_order>  Smaller values compute faster. < 5, read the book to extend it.
	<F>     The output values of F_1, F_2, ..F[n] in F[0], F[1], F[n-1]
	<delta> The output vector difference between the sample point and the n-th
			closest feature point. Thus, the feature point's location is the
			hit point minus this value. The DERIVATIVE of F is the unit
			normalized version of this vector.
	<ID>    The output 32 bit ID number which labels the feature point. This
			is useful for domain partitions, especially for coloring flagstone
			patterns.
			
	This implementation is tuned for speed in a way that any order > 5
	will likely have discontinuous artifacts in its computation of F5+.
	This can be fixed by increasing the internal points-per-cube
	density in the source code, at the expense of slower
	computation. The book lists the details of this tuning.
*/

/*
	* Edited by: Carl-Johan Ros�n, Link�ping University
	* Date: 2006-02-23
	* Contact: cj dot rosen at gmail dot com
	* 
	* This is the main class for generating cell noise. It uses the data 
	* structure CellDataStruct also in this package. The class implements 
	* one, two and three dimensional cell noise.
	*/


package cc.creativecomputing.math.signal;

import cc.creativecomputing.math.CCMath;



public class CCWorleyNoise extends CCSignal{
	
	public static interface CCWorleyFormular{
		public int neededDistances();
		public double[] value(double[] values);
	}
	
	public class CCWorleyDefaultFormular implements CCWorleyFormular{

		@Override
		public int neededDistances() {
			return _myMaxOrder;
		}

		@Override
		public double[] value(double[] theValues) {
			return theValues;
		}
		
	}
	
	public static class CCWorleyF0Formular implements CCWorleyFormular{

		@Override
		public int neededDistances() {
			return 1;
		}

		@Override
		public double[] value(double[] theValues) {
			return new double[] {theValues[0]};
		}
		
	}
	
	public static class CCWorleyF1Formular implements CCWorleyFormular{

		@Override
		public int neededDistances() {
			return 2;
		}

		@Override
		public double[] value(double[] theValues) {
			return new double[] {theValues[1]};
		}
	}
	
	public static class CCWorleyF2Formular implements CCWorleyFormular{

		@Override
		public int neededDistances() {
			return 3;
		}

		@Override
		public double[] value(double[] theValues) {
			return new double[] {theValues[2]};
		}
	}
	
	public static class CCWorleyF1F0Formular implements CCWorleyFormular{

		@Override
		public int neededDistances() {
			return 3;
		}

		@Override
		public double[] value(double[] theValues) {
			return new double[] {theValues[1] - theValues[0]};
		}
	}
	
	private static class CellDataStruct{
		
		private double[] F;
		
		private double[][] delta;
		
		private long[] ID;
		
		private CellDataStruct(int theMaxOrder, int theDimension) {
			F = new double[theMaxOrder];
			// initialize F
			for (int i = 0; i < theMaxOrder; i++)
				F[i] = Double.MAX_VALUE;
			
			delta = new double[theMaxOrder][theDimension];
			ID = new long[theMaxOrder];
		}
	}
	
	/**
	 * A hard wired lookup table to quickly determine how many feature points 
	 * should be in each spatial cube. We use a table so we don't need to 
	 * multiple slower tests. A random number indexed into this array will 
	 * give an approximate Poisson distribution of mean density 2.5. Read 
	 * the book for the long-winded explanation.
	 */
	private int[] Poisson_count = {
		4, 3, 1, 1, 1, 2, 4, 2, 2, 2, 5, 1, 0, 2, 1, 2, 2, 0, 4, 3, 2, 1, 2, 
		1, 3, 2, 2, 4, 2, 2, 5, 1, 2, 3, 2, 2, 2, 2, 2, 3, 2, 4, 2, 5, 3, 2, 
		2, 2, 5, 3, 3, 5, 2, 1, 3, 3, 4, 4, 2, 3, 0, 4, 2, 2, 2, 1, 3, 2, 2, 
		2, 3, 3, 3, 1, 2, 0, 2, 1, 1, 2, 2, 2, 2, 5, 3, 2, 3, 2, 3, 2, 2, 1, 
		0, 2, 1, 1, 2, 1, 2, 2, 1, 3, 4, 2, 2, 2, 5, 4, 2, 4, 2, 2, 5, 4, 3, 
		2, 2, 5, 4, 3, 3, 3, 5, 2, 2, 2, 2, 2, 3, 1, 1, 5, 2, 1, 3, 3, 4, 3, 
		2, 4, 3, 3, 3, 4, 5, 1, 4, 2, 4, 3, 1, 2, 3, 5, 3, 2, 1, 3, 1, 3, 3, 
		3, 2, 3, 1, 5, 5, 4, 2, 2, 4, 1, 3, 4, 1, 5, 3, 3, 5, 3, 4, 3, 2, 2, 
		1, 1, 1, 1, 1, 2, 4, 5, 4, 5, 4, 2, 1, 5, 1, 1, 2, 3, 3, 3, 2, 5, 2, 
		3, 3, 2, 0, 2, 1, 1, 4, 2, 1, 3, 2, 1, 2, 2, 3, 2, 5, 5, 3, 4, 5, 5, 
		2, 4, 4, 5, 3, 2, 2, 2, 1, 4, 2, 3, 3, 4, 2, 5, 4, 2, 4, 2, 2, 2, 4, 
		5, 3, 2 };	
	
	/**
	 * This constant is manipulated to make sure that the mean value of 
	 * F[0] is 1.0. This makes an easy natural 'scale' size of the cellular 
	 * features.
	 *
	 * Its inverse is also kept, to improve speed.
	 */
	private static double DENSITY_ADJUSTMENT_2D = 0.594631;
	private static double DENSITY_ADJUSTMENT_INV_2D = 1.0 / DENSITY_ADJUSTMENT_2D;

	private static double DENSITY_ADJUSTMENT_3D = 0.398150;
	private static double DENSITY_ADJUSTMENT_INV_3D = 1.0 / DENSITY_ADJUSTMENT_3D;

	
	/**
	 * This is the largest number possible to represent with a 32 bit 
	 * unsigned integer. It's used in the overflow control function u32 
	 * below.
	 */
	private static long b32 = (long) Math.pow(2, 32);
	

	
	/**
	 * This function implements the unsigned 32 bit integer overflow 
	 * used in the original C++ code. The integer is represented by 
	 * a 64 bit signed integer in Java since Java doesn't implement 
	 * unsigned integers.
	 *
	 * This function slows the application down severely, but allows
	 * us to keep the low pattern generated by the constants
	 * extracted by the original author, Steven Worley.
	 */
	private static long u32(long s) {
		s = s % b32;
		if (s < 0)
			s += b32;
		return s;
	}
	
	/**
	* Distance measure type constants
	*/
	public static enum CCWorleyDistType{
		EUCLIDEAN,
		CITYBLOCK,
		MANHATTAN,
		QUADRATIC
	}
	
	private int _myMaxOrder = 1;
	private CCWorleyFormular _myFormular;
	private CCWorleyDistType _myDistType = CCWorleyDistType.EUCLIDEAN;
	
	
	/**
	* Constructor.
	*/
	public CCWorleyNoise(CCWorleyFormular theFormular) {
		_myFormular = theFormular;
		_myMaxOrder = theFormular.neededDistances();
	}
	
	public CCWorleyNoise() {
		_myFormular = new CCWorleyDefaultFormular();
	}
	
	/**
	 * Generating the sample points in the grid
	 * 3D
	 */
	private void addSamples(
		int xi, int yi, int zi, 
		double xd, double yd, double zd, 
		CellDataStruct cd
	) {
		
		// Generating a random seed, based on the cube's ID number. The seed might be 
		// better if it were a nonlinear hash like Perlin uses for noise, but we do very 
		// well with this faster simple one.
		// Our LCG uses Knuth-approved constants for maximal periods.
		long seed = u32(u32(702395077 * xi) + u32(915488749 * yi) + u32(2120969693 * zi));
		
		// Number of feature points in this cube.
		int count = Poisson_count[(int) (0xFF & (seed >> 24))];
		
		// Churn the seed with good Knuth LCG.
		seed = u32(1402024253 * seed + 586950981);
		
		for (int j = 0; j < count; j++) {
			long this_id = seed;
			seed = u32(1402024253 * seed + 586950981);
			
			/* Compute the 0..1 feature point location's xyz. */
			double fx = (seed + 0.5) / 4294967296.0;
			seed = u32(1402024253 * seed + 586950981);
			double fy = (seed + 0.5) / 4294967296.0;
			seed = u32(1402024253 * seed + 586950981);
			double fz = (seed + 0.5) / 4294967296.0;
			seed = u32(1402024253 * seed + 586950981);
			
			// Delta from feature point to sample location
			double dx = xi + fx - xd;
			double dy = yi + fy - yd;
			double dz = zi + fz - zd;
			
			double d2;
			
			// Distance computation
			switch(_myDistType) {
			case CITYBLOCK:
				d2 = CCMath.max( CCMath.max(CCMath.abs(dx), CCMath.abs(dy)), CCMath.abs(dz));
				d2 *= d2;
			case MANHATTAN:
				d2 = CCMath.abs(dx) + CCMath.abs(dy) + CCMath.abs(dz);
				d2 *= d2;
				break;
			case QUADRATIC:
				d2 = dx*dx + dy*dy + dz*dz + dx*dy + dx*dz + dy*dz;
				d2 *= d2;
			default:
				// EUCLIDEAN
				d2 = dx * dx + dy * dy + dz * dz;
			
			}
			
			// Store points that are close enough to remember.
			if (d2 < cd.F[_myMaxOrder - 1]) {
				int index = _myMaxOrder;
				while (index > 0 && d2 < cd.F[index - 1]) {
					index--;
				}
				for (int i = _myMaxOrder - 1; i-- > index;) {
					cd.F[i + 1] = cd.F[i];
					cd.ID[i + 1] = cd.ID[i];
					cd.delta[i + 1][0] = cd.delta[i][0];
					cd.delta[i + 1][1] = cd.delta[i][1];
					cd.delta[i + 1][2] = cd.delta[i][2];
				}
				cd.F[index] = d2;
				cd.ID[index] = this_id;
				cd.delta[index][0] = dx;
				cd.delta[index][1] = dy;
				cd.delta[index][2] = dz;
			}
		}
	}
	
	/**
	 * Noise function for three dimensions. Coordinating the search on the 
	 * above cube level. Deciding in which cubes to search.
	 */	
	public double[] signalImpl(double theX, double theY, double theZ) {
		CellDataStruct cd = new CellDataStruct(_myMaxOrder,3);
		
		double xd = DENSITY_ADJUSTMENT_3D * theX;
		double yd = DENSITY_ADJUSTMENT_3D * theY;
		double zd = DENSITY_ADJUSTMENT_3D * theZ;
		
		int xi = CCMath.floor(xd);
		int yi = CCMath.floor(yd);
		int zi = CCMath.floor(zd);
		
		// The center cube. It's very likely that the closest feature 
		// point will be found in this cube.
		addSamples(xi, yi, zi, xd, yd, zd, cd);
		
		
		// We test if the cubes are even possible contributors by examining 
		// the combinations of the sum of the squared distances from the 
		// cube's lower or upper corners.
		double x2 = xd - xi;
		double y2 = yd - yi;
		double z2 = zd - zi;
		double mx2 = (1.0 - x2) * (1.0 - x2);
		double my2 = (1.0 - y2) * (1.0 - y2);
		double mz2 = (1.0 - z2) * (1.0 - z2);
		x2 *= x2;
		y2 *= y2;
		z2 *= z2;
		
		
		// The 6 facing neighbors of center cube. These are the closest 
		// and most likely to have a close feature point.
		if ( x2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi, zi, xd, yd, zd, cd);
		if ( y2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi - 1, zi, xd, yd, zd, cd);
		if ( z2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi, zi - 1, xd, yd, zd, cd);
		if (mx2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi, zi, xd, yd, zd, cd);
		if (my2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi + 1, zi, xd, yd, zd, cd);
		if (mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi, zi + 1, xd, yd, zd, cd);
		
		// The 12 edge cubes. These are next closest.
		if ( x2 +  y2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi - 1, zi, xd, yd, zd, cd);
		if ( x2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi, zi - 1, xd, yd, zd, cd);
		if ( y2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi - 1, zi - 1, xd, yd, zd, cd);
		if (mx2 + my2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi + 1, zi, xd, yd, zd, cd);
		if (mx2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi, zi + 1, xd, yd, zd, cd);
		if (my2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi + 1, zi + 1, xd, yd, zd, cd);
		if ( x2 + my2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi + 1, zi, xd, yd, zd, cd);
		if ( x2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi, zi + 1, xd, yd, zd, cd);
		if ( y2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi - 1, zi + 1, xd, yd, zd, cd);
		if (mx2 +  y2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi - 1, zi, xd, yd, zd, cd);
		if (mx2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi, zi - 1, xd, yd, zd, cd);
		if (my2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi + 1, zi - 1, xd, yd, zd, cd);
		
		// The 8 corner cubes.
		if ( x2 +  y2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi - 1, zi - 1, xd, yd, zd, cd);
		if ( x2 +  y2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi - 1, zi + 1, xd, yd, zd, cd);
		if ( x2 + my2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi + 1, zi - 1, xd, yd, zd, cd);
		if ( x2 + my2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi + 1, zi + 1, xd, yd, zd, cd);
		if (mx2 +  y2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi - 1, zi - 1, xd, yd, zd, cd);
		if (mx2 +  y2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi - 1, zi + 1, xd, yd, zd, cd);
		if (mx2 + my2 +  z2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi + 1, zi - 1, xd, yd, zd, cd);
		if (mx2 + my2 + mz2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi + 1, zi + 1, xd, yd, zd, cd);
		
		for (int i = 0; i < _myMaxOrder; i++) {
			cd.F[i] = Math.sqrt(cd.F[i]) * DENSITY_ADJUSTMENT_INV_3D;
			cd.delta[i][0] *= DENSITY_ADJUSTMENT_INV_3D;
			cd.delta[i][1] *= DENSITY_ADJUSTMENT_INV_3D;
			cd.delta[i][2] *= DENSITY_ADJUSTMENT_INV_3D;
		}

		return _myFormular.value(cd.F);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.random.CCNoise#noiseImpl(float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY, float theZ) {
		double[] myValues = signalImpl(theX * 1.0, theY * 1.0, theZ * 1.0);
		float[] myResult = new float[myValues.length];
		for(int i = 0; i < myResult.length;i++) {
			myResult[i] = (float)myValues[i];
		}
		return myResult;
	}

	/**
	 * Generating the sample points in the grid
	 * 2D
	 */
	private void addSamples(
		int xi, int yi, 
		double xd, double yd, 
		CellDataStruct cd
	) {
		
		// Generating a random seed, based on the cube's ID number. The seed might be 
		// better if it were a nonlinear hash like Perlin uses for noise, but we do very 
		// well with this faster simple one.
		// Our LCG uses Knuth-approved constants for maximal periods.
		long seed = u32(u32(702395077 * xi) + u32(915488749 * yi));
		
		// Number of feature points in this cube.
		int count = Poisson_count[(int) (0xFF & (seed >> 24))];
		
		// Churn the seed with good Knuth LCG.
		seed = u32(1402024253 * seed + 586950981);
		
		// Compute the 0..1 feature point location's xyz.
		for (int j = 0; j < count; j++) {
			long this_id = seed;
			seed = u32(1402024253 * seed + 586950981);
			
			double fx = (seed + 0.5) / 4294967296.0;
			seed = u32(1402024253 * seed + 586950981);
			double fy = (seed + 0.5) / 4294967296.0;
			seed = u32(1402024253 * seed + 586950981);
			
			// Delta from feature point to sample location.
			double dx = xi + fx - xd;
			double dy = yi + fy - yd;
			double d2;
			
			// Calculate distance.
			switch(_myDistType) {
			case CITYBLOCK:
				d2 = Math.max(Math.abs(dx), Math.abs(dy));
				d2 *= d2;
				break;
			case MANHATTAN:
				d2 = Math.abs(dx) + Math.abs(dy);
				d2 *= d2;
				break;
			case QUADRATIC:
				d2 = dx*dx + dy*dy + dx*dy;
				d2 *= d2;
				break;
			default:
				// EUCLIDEAN
				d2 = dx * dx + dy * dy;
			}
			
			
			// Store points that are close enough to remember.
			if (d2 < cd.F[_myMaxOrder - 1]) {
				int index = _myMaxOrder;
				while (index > 0 && d2 < cd.F[index - 1]) {
					index--;
				}
				for (int i = _myMaxOrder - 1; i-- > index;) {
					cd.F[i + 1] = cd.F[i];
					cd.ID[i + 1] = cd.ID[i];
					cd.delta[i + 1][0] = cd.delta[i][0];
					cd.delta[i + 1][1] = cd.delta[i][1];
				}
				cd.F[index] = d2;
				cd.ID[index] = this_id;
				cd.delta[index][0] = dx;
				cd.delta[index][1] = dy;
			}
		}
	}
	
	
	
	
	/**
	 * Noise function for two dimensions. Coordinating the search on the 
	 * above square level. Deciding in which squares to search.
	 */	
	public double[] signalImpl(double theX, double theY) {
		
		CellDataStruct cd = new CellDataStruct(_myMaxOrder,2);
		
		double xd = DENSITY_ADJUSTMENT_2D * theX;
		double yd = DENSITY_ADJUSTMENT_2D  * theY;
		
		int xi = (int) Math.floor(xd);
		int yi = (int) Math.floor(yd);
		
		
		// The center cube. It's very likely that the closest feature 
		// point will be found in this cube.
		addSamples(xi, yi, xd, yd, cd);
		
		
		// We test if the cubes are even possible contributors by examining 
		// the combinations of the sum of the squared distances from the 
		// cube's lower or upper corners.
		double x2 = xd - xi;
		double y2 = yd - yi;
		double mx2 = (1.0 - x2) * (1.0 - x2);
		double my2 = (1.0 - y2) * (1.0 - y2);
		x2 *= x2;
		y2 *= y2;
		
		// The 4 facing neighbors of center square. These are the closest 
	 	// and most likely to have a close feature point.
		if ( x2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi, xd, yd, cd);
		if ( y2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi - 1, xd, yd, cd);
		if (mx2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi, xd, yd, cd);
		if (my2 < cd.F[_myMaxOrder - 1]) addSamples(xi, yi + 1, xd, yd, cd);
		
		// The 4 edge squares. These are next closest.
		if ( x2 +  y2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi - 1, xd, yd, cd);
		if (mx2 + my2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi + 1, xd, yd, cd);
		if ( x2 + my2 < cd.F[_myMaxOrder - 1]) addSamples(xi - 1, yi + 1, xd, yd, cd);
		if (mx2 +  y2 < cd.F[_myMaxOrder - 1]) addSamples(xi + 1, yi - 1, xd, yd, cd);
				
		for (int i = 0; i < _myMaxOrder; i++) {
			cd.F[i] = Math.sqrt(cd.F[i]) * DENSITY_ADJUSTMENT_INV_2D ;
			cd.delta[i][0] *= DENSITY_ADJUSTMENT_INV_2D ;
			cd.delta[i][1] *= DENSITY_ADJUSTMENT_INV_2D ;
		}
		return _myFormular.value(cd.F);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.random.CCNoise#noiseImpl(float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY) {
		double[] myValues = signalImpl(theX * 1.0, theY * 1.0);
		float[] myResult = new float[myValues.length];
		for(int i = 0; i < myResult.length;i++) {
			myResult[i] = (float)myValues[i];
		}
		return myResult;
	}
	
	
	public void maxOrder(int theMaxOrder) {
		_myMaxOrder = theMaxOrder;
	}

	
	
}
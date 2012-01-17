/* Copyright 1994, 2002 by Steven Worley 
 * This software may be modified and redistributed without restriction 
 * provided this comment header remains intact in the source code. 
 * This code is provided with no warrantee, express or implied, for any purpose.
 *
 * A detailed description and application examples can be found in the 
 * 1996 SIGGRAPH paper “A Cellular Texture Basis Function” and especially 
 * in the 2003 book Texturing & Modeling, A Procedural Approach, 3rd edition. 
 * There is also extra information on the Web site 
 * http://www.worley.com/cellular.html.
 *
 * If you do find interesting uses for this tool, and especially if 
 * you enhance it, please drop me an email at steve@worley.com.
 *
 * An implementation of the key cellular texturing basis function. 
 * This function is hardwired to return an average F_1 value of 1.0. 
 * It returns the <n> closest feature point distances F_1, F_2, .. F_n 
 * the vector delta to those points, and a 32-bit seed for each of the 
 * feature points. This function is not difficult to extend to compute 
 * alternative information such as higher-order F values, to use the 
 * Manhattan distance metric, or other fun perversions.
 * <at>	
 *		The input sample location. 
 *
 * <max_order> 
 *		Smaller values compute faster. < 5, read the book to extend it. 
 *
 * <F>	
 *		The output values of F_1, F_2, .. F[n] in F[0], F[1], F[n-1] 
 * 
 * <delta> 
 *		The output vector difference between the sample point and the n-th
 * 		closest feature point. Thus, the feature point’s location is the hit 
 * 		point minus this value. The DERIVATIVE of F is the unit normalized 
 * 		version of this vector.
 *
 * <ID>	
 *		The output 32-bit ID number that labels the feature point. This is 
 *		useful for domain partitions, especially for coloring flagstone patterns.
 *		This implementation is tuned for speed in a way that any order > 5 will 
 *		likely have discontinuous artifacts in its computation of F5+. This can 
 *		be fixed by increasing the internal points-per-cube density in the source 
 *		code, at the expense of slower computation. 
 *		The book lists the details of this tuning. 
 */
 
 



/* the function to merge-sort a “cube” of samples 
 * into the current best-found list of values. 
 */
static void AddSamples(
	long xi, long yi, long zi, 
	long max_order, 
	float4 at, 
	float4 *F, 
	float (*delta)[3], 
	unsigned long *ID
);


/*
	

	

	

	// A simple way to compute the closest neighbors would be to test 
	// all boundary cubes exhaustively. This is simple with code like: 
	// {
	//	long ii, jj, kk; 
	//	for (ii=-1; ii<=1; ii++) 
	//		for (jj=-1; jj<=1; jj++) 
	//			for (kk=-1; kk<=1; kk++) 
	//				AddSamples(int_at[0]+ii,int_at[1]+jj,int_at[2]+kk, max_order, new_at, &F, delta, ID);
	// } 
	// But this wastes a lot of time working on cubes that are known to 
	// be too far away to matter! So we can use a more complex testing 
	// method that avoids this needless testing of distant cubes. This 
	// doubles the speed of the algorithm. 
	
	// Test the central cube for closest point(s). 
	
	AddSamples(int_at[0], int_at[1], int_at[2], max_order, new_at, &F, delta, ID);

	// We test if neighbor cubes are even POSSIBLE contributors by 
	// examining the combinations of the sum of the squared distances 
	// from the cube’s lower or upper corners.
	
	

	// Test 6 facing neighbors of center cube. These are closest 
	// and most likely to have a close feature point. 
	 
	
	*/
	
	
	/*
	
    */
    
    /* A hardwired lookup table to quickly determine how many feature points 
 * should be in each spatial cube. We use a table so we don’t need to make 
 * multiple slower tests. A random number indexed into this array will give 
 * an approximate Poisson distribution of mean density 2.5. Read the book 
 * for the long-winded explanation. 
 */



	
static void AddSamples(long xi, long yi, long zi, long max_order, float4 at, float4 *F, float (*delta)[3], unsigned long *ID){ 

	/*

	
	
	// How many feature points are in this cube? 
	 // 256 element lookup table. Use MSB 
	
	 // churn the seed with good Knuth LCG 

	// delta from feature point to sample location  
		
	
		// Distance computation! Lots of interesting variations are possible here! 
		//
		// Biased “stretched” 	A * dx * dx + B * dy * dy + C * dz * dz 
		// Manhattan distance 	fabs(dx) + fabs(dy) + fabs(dz)
		// Radial Manhattan: 	A * fabs(dR) + B * fabs(dTheta) + C * dz 
		// Superquadratic:		pow(fabs(dx), A) + pow(fabs(dy), B) + pow(fabs(dz),C)
		// 
		// Go ahead and make your own! Remember that you must insure that a 
		// new distance function causes large deltas in 3D space to map into 
		// large deltas in your distance function, so our 3D search can find them! 
		// [Alternatively, change the search algorithm for your special cases.]
		  
		 // Euclidean distance, squared 
		
		// Is this point close enough to rememember? 
		
			// Insert the information into the output arrays 
			// if it’s close enough. We use an insertion sort. 
			// No need for a binary search to find the appropriate index .. 
			// usually we’re dealing with order 2,3,4 so we can just go 
			// through the list. If you were computing order 50 (wow!!), 
			// you could get a speedup with a binary search in the sorted F[] list. 
			 
			

			// We insert this new point into slot # <index> 
			// Bump down more distant information to make room for this new point.  
			
			
			// Insert the new point’s information into the list. 
			
		}
	*/
}
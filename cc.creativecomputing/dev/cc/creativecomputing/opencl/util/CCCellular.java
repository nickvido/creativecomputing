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
package cc.creativecomputing.opencl.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCMath;

/* Copyright 1994, 2002 by Steven Worley 
 * This software may be modified and redistributed without restriction 
 * provided this comment header remains intact in the source code. 
 * This code is provided with no warrantee, express or implied, for 
 * any purpose.
 * 
 * A detailed description and application examples can be found in the 
 * 1996 SIGGRAPH paper “A Cellular Texture Basis Function” and 
 * especially in the 2003 book Texturing & Modeling, A Procedural 
 * Approach, 3rd edition. There is also extra information on the Web 
 * site http://www.worley.com/cellular.html.
 * If you do find interesting uses for this tool, and especially if 
 * you enhance it, please drop me an email at steve@worley.com.
 * 
 * An implementation of the key cellular texturing basis 
 * function. This function is hardwired to return an average F_1 value 
 * of 1.0. It returns the <n> closest feature point distances 
 * F_1, F_2, .. F_n the vector delta to those points, and a 32-bit 
 * seed for each of the feature points. This function is not 
 * difficult to extend to compute alternative information such as 
 * higher-order F values, to use the Manhattan distance metric, or 
 * other fun perversions.
 * 
 * <at>	The input sample location. 
 * <maxOrder> Smaller values compute faster. < 5, read the book to extend it. 
 * <F>	The output values of F_1, F_2, .. F[n] in F[0], F[1], F[n-1] 
 * <delta> The output vector difference between the sample point and the n-th
 * closest feature point. Thus, the feature point’s location is the 
 * hit point minus this value. The DERIVATIVE of F is the unit 
 * normalized version of this vector.
 * <ID>	The output 32-bit ID number that labels the feature point. This 
 * is useful for domain partitions, especially for coloring flagstone patterns.
 * 
 * This implementation is tuned for speed in a way that any order > 5 will 
 * likely have discontinuous artifacts in its computation of F5+. This can 
 * be fixed by increasing the internal points-per-cube density in the source 
 * code, at the expense of slower computation. The book lists the details of this tuning. */
public class CCCellular extends CCApp {
	
	/* A hardwired lookup table to quickly determine how many feature points should be in each spatial cube. We use a table so we don’t need to make multiple slower tests. A random number indexed into this array will give an approximate Poisson distribution of mean density 2.5. Read the book for the long-winded explanation. */
	static int[] Poisson_count = new int[]
	{4,3,1,1,1,2,4,2,2,2,5,1,0,2,1,2,2,0,4,3,2,1,2,1,3,2,2,4,2,2,5,1,2,3, 2,2,2,2,2,3,2,4,2,5,3,2,2,2,5,3,3,5,2,1,3,3,4,4,2,3,0,4,2,2,2,1,3,2, 2,2,3,3,3,1,2,0,2,1,1,2,2,2,2,5,3,2,3,2,3,2,2,1,0,2,1,1,2,1,2,2,1,3, 4,2,2,2,5,4,2,4,2,2,5,4,3,2,2,5,4,3,3,3,5,2,2,2,2,2,3,1,1,4,2,1,3,3, 4,3,2,4,3,3,3,4,5,1,4,2,4,3,1,2,3,5,3,2,1,3,1,3,3,3,2,3,1,5,5,4,2,2, 4,1,3,4,1,5,3,3,5,3,4,3,2,2,1,1,1,1,1,2,4,5,4,5,4,2,1,5,1,1,2,3,3,3, 2,5,2,3,3,2,0,2,1,1,4,2,1,3,2,1,2,2,3,2,5,5,3,4,5,5,2,4,4,5,3,2,2,2, 1,4,2,3,3,4,2,5,4,2,4,2,2,2,4,5,3,2};

	/* This constant is manipulated to make sure that the mean 
	 * value of F[0] is 1.0. This makes an easy natural “scale” 
	 * size of the cellular features. */
	private static float DENSITY_ADJUSTMENT = 0.398150f;
	
	/* the function to merge-sort a “cube” of samples into the current best-found list of values. */
	static void AddSamples(
		int xi, int yi, int zi, 
		int maxOrder, 
		float[] at, 
		
		WorleyResult result
	){
		float dx, dy, dz, fx, fy, fz, d2; 
		int count, i, j, index; 
		int seed, this_id;
		
		/* Each cube has a random number seed based on the cube’s ID number. 
		 * The seed might be better if it were a nonlinear hash like Perlin 
		 * uses for noise, but we do very well with this faster simple one. 
		 * Our LCG uses Knuth-approved constants for maximal periods. */
		seed = 702395077 * xi + 915488749 * yi + 2120969693 * zi;
		
		/* How many feature points are in this cube? */
		int myIndex = (seed>>24 & 0xff);
		
		if(myIndex < 0) {
			myIndex = 127 + -myIndex;
//			System.out.println(myIndex);
//			return;
		}
//		CCColor
		count = Poisson_count[myIndex]; /* 256 element lookup table. Use MSB */
		seed=1402024253*seed+586950981; /* churn the seed with good Knuth LCG */
		
		for (j=0; j<count; j++) /* test and insert each point into our solution */ {
			this_id=seed; seed=1402024253*seed+586950981; /* churn */
			
			/* compute the 0 .. 1 feature point location’s XYZ */ 
			fx=(seed + 0.5f)*(1.0f/4294967296.0f); 
			seed=1402024253*seed+586950981; /* churn */ 
			
			fy=(seed + 0.5f)*(1.0f/4294967296.0f); 
			seed=1402024253*seed+586950981; /* churn */ 
			
			fz=(seed + 0.5f)*(1.0f/4294967296.0f); 
			seed=1402024253*seed+586950981; /* churn */
			
			/* delta from feature point to sample location */ 
			dx = xi + fx - at[0]; 
			dy = yi + fy - at[1]; 
			dz = zi + fz - at[2];
			
			/* Distance computation! Lots of interesting variations are possible here! 
			 * Biased “stretched” A*dx*dx+B*dy*dy+C*dz*dz 
			 * Manhattan distance fabs(dx)+fabs(dy)+fabs(dz)
			 * Radial Manhattan: A*fabs(dR)+B*fabs(dTheta)+C*dz 
			 * Superquadratic:	pow(fabs(dx), A) + pow(fabs(dy), B) + pow(fabs(dz),C)
			 * 
			 * Go ahead and make your own! Remember that you must insure that a 
			 * new distance function causes large deltas in 3D space to map into 
			 * large deltas in your distance function, so our 3D search can find them! 
			 * [Alternatively, change the search algorithm for your special cases.]
			 */
			
			d2=dx*dx+dy*dy+dz*dz; /* Euclidean distance, squared */
			
			if (d2<result.F[maxOrder-1]) /* Is this point close enough to rememember? */
			{
				/* Insert the information into the output arrays if it’s close enough. 
				 * We use an insertion sort. No need for a binary search to find the 
				 * appropriate index .. usually we’re dealing with order 2,3,4 so we 
				 * can just go through the list. If you were computing order 50 (wow!!), 
				 * you could get a speedup with a binary search in the sorted F[] list. */
				index=maxOrder; 
				
				while (index>0 && d2<result.F[index-1]) index--;
				
				/* We insert this new point into slot # <index> */
				/* Bump down more distant information to make room for this new point. */
				for (i=maxOrder-1; i-->index;){
					result.F[i+1]=result.F[i]; 
					result.id[i+1]=result.id[i]; 
					result.delta[i+1][0]=result.delta[i][0]; 
					result.delta[i+1][1]=result.delta[i][1]; 
					result.delta[i+1][2]=result.delta[i][2];
				}
				
				/* Insert the new point’s information into the list. */
				result.F[index]=d2; 
				result.id[index]=this_id; 
				result.delta[index][0]=dx; 
				result.delta[index][1]=dy; 
				result.delta[index][2]=dz;
			}
		}
	}
	
	static class WorleyResult{
		float[] F;
		float[][] delta;
		long[] id;
		
		WorleyResult(int maxOrder){
			F = new float[maxOrder];
			/* Initialize the F values to “huge” so they will be replaced by the
			 * first real sample tests. Note we’ll be storing and comparing the
			 * SQUARED distance from the feature points to avoid lots of slow
			 * sqrt() calls. We’ll use sqrt() only on the final answer. */
			for (int i=0; i<maxOrder; i++) {
				F[i]=Float.MAX_VALUE;
			}
			delta = new float[maxOrder][3];
			id = new long[maxOrder];
		}
	}
	
	/* The main function! */
	WorleyResult Worley(float[] at, int maxOrder){
		float x2,y2,z2, mx2, my2, mz2;
		float[] new_at = new float[3];
		
		/* Make our own local copy, multiplying to make mean(F[0])==1.0 */
		new_at[0]=DENSITY_ADJUSTMENT*at[0];
		new_at[1]=DENSITY_ADJUSTMENT*at[1]; 
		new_at[2]=DENSITY_ADJUSTMENT*at[2];
		
		/* Find the integer cube holding the hit point */ 
		int[] int_at = new int[3];
		int_at[0]=CCMath.floor(new_at[0]); /* A macro could make this slightly faster */ 
		int_at[1]=CCMath.floor(new_at[1]); 
		int_at[2]=CCMath.floor(new_at[2]);
		
		WorleyResult myResult = new WorleyResult(maxOrder);
		
		/* Test the central cube for closest point(s). */
		AddSamples(int_at[0], int_at[1], int_at[2], maxOrder, new_at, myResult);
		/* We test if neighbor cubes are even POSSIBLE contributors by examining the 
		 * combinations of the sum of the squared distances from the cube’s lower or 
		 * upper corners.*/
		x2=new_at[0]-int_at[0]; 
		y2=new_at[1]-int_at[1]; 
		z2=new_at[2]-int_at[2]; 
		
		mx2=(1.0f-x2)*(1.0f-x2); 
		my2=(1.0f-y2)*(1.0f-y2); 
		mz2=(1.0f-z2)*(1.0f-z2); 
		x2*=x2;
		y2*=y2; 
		z2*=z2;
		
		/* Test 6 facing neighbors of center cube. These are closest and most likely to have a close feature point. */
		if (x2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1] , int_at[2] , maxOrder, new_at, myResult);
		if (y2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]-1, int_at[2] , maxOrder, new_at, myResult);
		if (z2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1] , int_at[2]-1, maxOrder, new_at, myResult);
		if (mx2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1] , int_at[2] , maxOrder, new_at, myResult);
		if (my2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]+1, int_at[2] , maxOrder, new_at, myResult);
		if (mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1] , int_at[2]+1, maxOrder, new_at, myResult);
		
		/* Test 12 “edge cube” neighbors if necessary. They’re next closest. */
		if ( x2+ y2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]-1, int_at[2] ,maxOrder, new_at, myResult); 
		if ( x2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1] , int_at[2]-1, maxOrder, new_at, myResult); 
		if ( y2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]-1, int_at[2]-1, maxOrder, new_at, myResult); 
		if (mx2+my2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]+1, int_at[2] , maxOrder, new_at, myResult); 
		if (mx2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1] , int_at[2]+1, maxOrder, new_at, myResult); 
		if (my2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]+1, int_at[2]+1, maxOrder, new_at, myResult); 
		if ( x2+my2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]+1, int_at[2] , maxOrder, new_at, myResult); 
		if ( x2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1] , int_at[2]+1, maxOrder, new_at, myResult); 
		if ( y2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]-1, int_at[2]+1, maxOrder, new_at, myResult); 
		if (mx2+ y2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]-1, int_at[2] , maxOrder, new_at, myResult);
		if (mx2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1] , int_at[2]-1, maxOrder, new_at, myResult);
		if (my2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0] , int_at[1]+1, int_at[2]-1, maxOrder, new_at, myResult);
		
		/* Final 8 “corner” cubes */
		if ( x2+ y2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]-1, int_at[2]-1, maxOrder, new_at, myResult); 
		if ( x2+ y2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]-1, int_at[2]+1, maxOrder, new_at, myResult); 
		if ( x2+my2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]+1, int_at[2]-1, maxOrder, new_at, myResult); 
		if ( x2+my2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]-1, int_at[1]+1, int_at[2]+1, maxOrder, new_at, myResult); 
		if (mx2+ y2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]-1, int_at[2]-1, maxOrder, new_at, myResult); 
		if (mx2+ y2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]-1, int_at[2]+1, maxOrder, new_at, myResult); 
		if (mx2+my2+ z2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]+1, int_at[2]-1, maxOrder, new_at, myResult); 
		if (mx2+my2+mz2<myResult.F[maxOrder-1]) AddSamples(int_at[0]+1, int_at[1]+1, int_at[2]+1, maxOrder, new_at, myResult);
		
		/* We’re done! Convert everything to right size scale */
		for (int i=0; i<maxOrder; i++){
			myResult.F[i]=CCMath.sqrt(myResult.F[i])*(1.0f/DENSITY_ADJUSTMENT); 
			myResult.delta[i][0]*=(1.0/DENSITY_ADJUSTMENT); 
			myResult.delta[i][1]*=(1.0/DENSITY_ADJUSTMENT); 
			myResult.delta[i][2]*=(1.0/DENSITY_ADJUSTMENT);
		}
		return myResult;
	}
	
	private CCTexture2D _myTexture;
	private CCTextureData _myData;
	
	@Override
	public void setup() {
		_myTexture = new CCTexture2D(200,200);
		_myData = new CCTextureData(200,200);
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int x = 0; x < _myData.width();x++) {
			for(int y = 0; y < _myData.height();y++) {
				float[]at = new float[] {x * 0.03f,y * 0.03f,0};
				WorleyResult myRes = Worley(at, 3);
				_myData.setPixel(x, y, new CCColor((myRes.F[2] - myRes.F[1]) / 2 ) );
			}
		}
		_myTexture.updateData(_myData);
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myTexture,0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCellular.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


/**
 * Clears a given 1D kernel by setting all values to 0
 * @ param theBuffer buffer to be cleared
 **/
kernel void clear1(
	global float * theBuffer
) {
	unsigned int myIndex = get_global_id(0);
	theBuffer[myIndex] = 0;
}

/**
 * Clears a given 2D kernel by setting all values to 0
 * @ param theBuffer buffer to be cleared
 **/
kernel void clear2(
	global float2 * theBuffer
) {
	unsigned int myIndex = get_global_id(0);
	theBuffer[myIndex] = 0;
}

/**
 * Clears a given 4D kernel by setting all values to 0
 * @ param theBuffer buffer to be cleared
 **/
kernel void clear4(
	global float4 * theBuffer
) {
	unsigned int myIndex = get_global_id(0);
	theBuffer[myIndex] = 0;
}

/**
 * Scale the given 1D input buffer by the given factor and adds it 
 * to the output buffer.
 * @param theOutput buffer to add the result to
 * @param theInput buffer to be scale and added to ouput
 * @param factor to scale the input values
 **/
kernel void addSource1(
	global float * theOutput, 
	global float * theInput, 
	float theFactor
) {
	unsigned int myIndex = get_global_id(0);
	theOutput[myIndex] += theFactor * theInput[myIndex];
}

/**
 * Scale the given 2D input buffer by the given factor and adds it 
 * to the output buffer
 * @param theOutput buffer to add the result to
 * @param theInput buffer to be scale and added to ouput
 * @param factor to scale the input values
 **/
kernel void addSource2(
	global float2 * theOutput, 
	global float2 * theInput, 
	float theFactor
) {
	unsigned int myIndex = get_global_id(0);
	theOutput[myIndex] += theFactor * theInput[myIndex];
}

/**
 * Scale the given 2D input buffer by the given factor and adds it 
 * to the output buffer
 * @param theOutput buffer to add the result to
 * @param theInput buffer to be scale and added to ouput
 * @param factor to scale the input values
 **/
kernel void addSource4(
	global float4 * theOutput, 
	global float4 * theInput, 
	float theFactor
) {
	unsigned int myIndex = get_global_id(0);
	theOutput[myIndex] += theFactor * theInput[myIndex];
}

private int I(int theX, int theY, int n) {
	return theX + (n + 2) * theY;
}

/**
 * Calculates the cell index based on the given 2D position in the grid.
 * @param theX x coord of the 2D cell index
 * @param theY y coord of the 2D cell index
 * @return 1D cell index
 */
private int index(int theX, int theY, int theWidth) {
	return theX + theY * theWidth;
}

/**
 * Iterative linear system solver using the Gauss-sidel relaxation
 * technique. Room for much improvement here...
 * 
 **/

kernel void linearSolver1(
	global float * x, 
	global float * x0, 
	float a, 
	float c, 
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX > 0 && theX < theWidth - 1 && theY > 0 && theY < theHeight - 1){
		x[myIndex] = (a * (
			x[myIndex - 1] + 
			x[myIndex + 1] + 
			x[myIndex - theWidth] + 
			x[myIndex + theWidth]
		) + x0[myIndex]) / c;	
	}
}

kernel void linearSolver2(
	global float2 * x, 
	global float2 * x0, 
	float a, 
	float c, 
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX > 0 && theX < theWidth - 1 && theY > 0 && theY < theHeight - 1){
		x[myIndex] = (a * (
			x[myIndex - 1] + 
			x[myIndex + 1] + 
			x[myIndex - theWidth] + 
			x[myIndex + theWidth]
		) + x0[myIndex]) / c;	
	}
}

kernel void linearSolver4(
	global float4 * x, 
	global float4 * x0, 
	float a, 
	float c, 
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX > 0 && theX < theWidth - 1 && theY > 0 && theY < theHeight - 1){
		x[myIndex] = (a * (
			x[myIndex - 1] + 
			x[myIndex + 1] + 
			x[myIndex - theWidth] + 
			x[myIndex + theWidth]
		) + x0[myIndex]) / c;	
	}
}

// specifies simple boundary conditions.
kernel void setBoundary1(
	global float * theData,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX == 0){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex + 1];
		}
	}else if(theX == theWidth - 1){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex - 1];
		}
	}else if(theY == 0){
		theData[myIndex] = theData[myIndex + theWidth];
	}else if(theY == theHeight - 1){
		theData[myIndex] =  theData[myIndex - theWidth];
	}
}

// specifies simple boundary conditions.
kernel void setBoundary2(
	global float2 * theData,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX == 0){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex + 1];
		}
	}else if(theX == theWidth - 1){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex - 1];
		}
	}else if(theY == 0){
		theData[myIndex] = theData[myIndex + theWidth];
	}else if(theY == theHeight - 1){
		theData[myIndex] =  theData[myIndex - theWidth];
	}
}

// specifies simple boundary conditions.
kernel void setBoundary4(
	global float4 * theData,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX == 0){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex + 1];
		}
	}else if(theX == theWidth - 1){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex - 1];
		}
	}else if(theY == 0){
		theData[myIndex] = theData[myIndex + theWidth];
	}else if(theY == theHeight - 1){
		theData[myIndex] =  theData[myIndex - theWidth];
	}
}

/**
 * Calculate the input array after advection. We start with an input array
 * from the previous timestep and an and output array. For all grid cells we
 * need to calculate for the next timestep, we trace the cell's center
 * position backwards through the velocity field. Then we interpolate from
 * the grid of the previous timestep and assign this value to the current
 * grid cell.
 * 
 * @param b
 *            Flag specifying how to handle boundaries.
 * @param d	
 *            Array to store the advected field.
 * @param d0
 *            The array to advect.
 * @param du
 *            The x component of the velocity field.
 * @param dv
 *            The y component of the velocity field.
 **/

kernel void advect1(
	global float * d, 
	global float * d0, 
	global float2 * theVelocities,
	float theDeltaTime,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){

		float2 dt0 = theDeltaTime * (float2)(theWidth - 2, theHeight - 2);
	
		// go backwards through velocity field
		float2 pos = (float2)(myX, myY) - dt0 * theVelocities[myIndex];
		float x = pos.x;
		float y = pos.y;
		
		pos = min(max(pos, (float2)(0.5)), (float2)(theWidth - 1.5, theHeight - 1.5));
	
		// interpolate results
		int i0 = (int) pos.x;
		int i1 = i0 + 1;
	

		int j0 = (int) pos.y;
		int j1 = j0 + 1;
	
		float s1 = pos.x - i0;
		float s0 = 1 - s1;
		float t1 = pos.y - j0;
		float t0 = 1 - t1;
	
		d[myIndex] = 
			s0 * (t0 * d0[index(i0, j0, theWidth)] + t1 * d0[index(i0, j1, theWidth)]) +
			s1 * (t0 * d0[index(i1, j0, theWidth)] + t1 * d0[index(i1, j1, theWidth)]);
	}
}

kernel void advect2(
	global float2 * d, 
	global float2 * d0, 
	global float2 * theVelocities,
	float theDeltaTime,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){

		float2 dt0 = theDeltaTime * (float2)(theWidth - 2, theHeight - 2);
	
		// go backwards through velocity field
		float2 pos = (float2)(myX, myY) - dt0 * theVelocities[myIndex];
		float x = pos.x;
		float y = pos.y;
		
		pos = min(max(pos, (float2)(0.5)), (float2)(theWidth - 1.5, theHeight - 1.5));
	
		// interpolate results
		int i0 = (int) pos.x;
		int i1 = i0 + 1;
	

		int j0 = (int) pos.y;
		int j1 = j0 + 1;
	
		float s1 = pos.x - i0;
		float s0 = 1 - s1;
		float t1 = pos.y - j0;
		float t0 = 1 - t1;
	
		d[myIndex] = 
			s0 * (t0 * d0[index(i0, j0, theWidth)] + t1 * d0[index(i0, j1, theWidth)]) +
			s1 * (t0 * d0[index(i1, j0, theWidth)] + t1 * d0[index(i1, j1, theWidth)]);
	}
}

kernel void advect4(
	global float4 * d, 
	global float4 * d0, 
	global float2 * theVelocities,
	float theDeltaTime,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){

		float2 dt0 = theDeltaTime * (float2)(theWidth - 2, theHeight - 2);
	
		// go backwards through velocity field
		float2 pos = (float2)(myX, myY) - dt0 * theVelocities[myIndex];
		float x = pos.x;
		float y = pos.y;
		
		pos = min(max(pos, (float2)(0.5)), (float2)(theWidth - 1.5, theHeight - 1.5));
	
		// interpolate results
		int i0 = (int) pos.x;
		int i1 = i0 + 1;
	

		int j0 = (int) pos.y;
		int j1 = j0 + 1;
	
		float s1 = pos.x - i0;
		float s0 = 1 - s1;
		float t1 = pos.y - j0;
		float t0 = 1 - t1;
	
		d[myIndex] = 
			s0 * (t0 * d0[index(i0, j0, theWidth)] + t1 * d0[index(i0, j1, theWidth)]) +
			s1 * (t0 * d0[index(i1, j0, theWidth)] + t1 * d0[index(i1, j1, theWidth)]);
	}
}

/**
 * Calculate the curl at position (i, j) in the fluid grid. Physically this represents the vortex strength at the
 * cell. Computed as follows: w = (del x U) where U is the velocity vector at (i, j).
 * 
 * @param theX The x index of the cell.
 * @param theY The y index of the cell.
 **/
kernel void curl(
	global float * theCurl, 
	global float2 * theVelocities,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){	
		float du_dy = (theVelocities[myIndex +  theWidth].x - theVelocities[myIndex - theWidth].x) * 0.5f;
		float dv_dx = (theVelocities[myIndex + 1].y - theVelocities[myIndex - 1].y) * 0.5f;

		theCurl[myIndex] = fabs(du_dy - dv_dx);
	}
}

/**
 * Calculate the vorticity confinement force for each cell in the fluid grid. At a point (i,j), Fvc = N x w where w
 * is the curl at (i,j) and N = del |w| / |del |w||. N is the vector pointing to the vortex center, hence we add
 * force perpendicular to N.
 * 
 * @param Fvc_x The array to store the x component of the vorticity confinement force for each cell.
 * @param Fvc_y The array to store the y component of the vorticity confinement force for each cell.
 **/

kernel void vorticityConfinement(
	global float * theCurl, 
	global float2 * Fvc, 
	float theDeltaTime,
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 1 && myX < theWidth - 2 && myY > 1 && myY < theHeight - 2){
		// Find derivative of the magnitude (n = del |w|)
		float dw_dx = (theCurl[myIndex + 1] - theCurl[myIndex - 1]) * 0.5f;
		float dw_dy = (theCurl[myIndex + theWidth] - theCurl[myIndex - theWidth]) * 0.5f;

		// Calculate vector length. (|n|)
		// Add small factor to prevent divide by zeros.
		float length = sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;

		// N = ( n/|n| )
		dw_dx /= length;
		dw_dy /= length;
	
		float v = theCurl[myIndex];
	
		// N x w
		Fvc[myIndex] += (float2)(theDeltaTime * dw_dy * -v, theDeltaTime * dw_dx * v);
	}
}

kernel void divergency(
	global float * div,
	global float2 * theVelocities,
	int theWidth,
	int theHeight
){
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
			
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){	
		div[myIndex] = (
			theVelocities[myIndex + 1].x - 
			theVelocities[myIndex - 1].x + 
			theVelocities[myIndex + theWidth].y - 
			theVelocities[myIndex - theWidth].y
		) * -0.5f / ((theWidth + theHeight) / 2.0);
	}
}

kernel void massConserve(
	global float2 * theVelocities,
	global float * p,
	int theWidth,
	int theHeight
){
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % theWidth;
	unsigned int myY = myIndex / theWidth;
	
	if(myX > 0 && myX < theWidth - 1 && myY > 0 && myY < theHeight - 1){	
		theVelocities[myIndex].x -= 0.5f * theWidth * (p[myIndex + 1] - p[myIndex - 1]);
		theVelocities[myIndex].y -= 0.5f * theHeight * (p[myIndex + theWidth] - p[myIndex - theWidth]);
	}
}

kernel void projectBoundary(
	global float2 * theData, 
	int theWidth,
	int theHeight
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % theWidth;
	unsigned int theY = myIndex / theWidth;
	
	if(theX == 0){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex + 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex + 1];
			theData[myIndex].x *= -1;
		}
	}else if(theX == theWidth - 1){
		if(theY == 0){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex + theWidth]);
		}else if(theY == theHeight - 1){
			theData[myIndex] = 0.5f * (theData[myIndex - 1] + theData[myIndex - theWidth]);
		}else{
			theData[myIndex] = theData[myIndex - 1];
			theData[myIndex].x *= -1;
		}
	}else if(theY == 0){
		theData[myIndex] = theData[myIndex + theWidth];
		theData[myIndex].y *= -1;
	}else if(theY == theHeight - 1){
		theData[myIndex] = theData[myIndex - theWidth];
		theData[myIndex].y *= -1;
	}
}
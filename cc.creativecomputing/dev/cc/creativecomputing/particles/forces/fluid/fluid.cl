

kernel void clear1(
	global float * x
) {
	unsigned int myIndex = get_global_id(0);
	x[myIndex] = 0;
}

kernel void clear2(
	global float2 * x
) {
	unsigned int myIndex = get_global_id(0);
	x[myIndex] = 0;
}

kernel void addSource1(
	global float * x, 
	global float * x0, 
	float theDeltaTime
) {
	unsigned int myIndex = get_global_id(0);
	x[myIndex] += theDeltaTime * x0[myIndex];
}

kernel void addSource2(
	global float2 * x, 
	global float2 * x0, 
	float theDeltaTime
) {
	unsigned int myIndex = get_global_id(0);
	x[myIndex] += theDeltaTime * x0[myIndex];
}

private int I(int theX, int theY, int n) {
	return theX + (n + 2) * theY;
}

// specifies simple boundary conditions.
kernel void setBoundary1(
	global float * theData, 
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % (n + 2);
	unsigned int theY = myIndex / (n + 2);
	
	if(theX == 0){
		if(theY == 0){
			theData[I(0, 0, n)] = 0.5f * (theData[I(1, 0, n)] + theData[I(0, 1, n)]);
		}else if(theY == n + 1){
			theData[I(0, n + 1, n)] = 0.5f * (theData[I(1, n + 1, n)] + theData[I(0, n, n)]);
		}else{
			theData[I(0, theY, n)] = theData[I(1, theY, n)];
		}
	}else if(theX == n + 1){
		if(theY == 0){
			theData[I(n + 1, 0, n)] = 0.5f * (theData[I(n, 0, n)] + theData[I(n + 1, 1, n)]);
		}else if(theY == n + 1){
			theData[I(n + 1, n + 1, n)] = 0.5f * (theData[I(n, n + 1, n)] + theData[I(n + 1, n, n)]);
		}else{
			theData[I(n + 1, theY, n)] = theData[I(n, theY, n)];
		}
	}else if(theY == 0){
		theData[I(theX, 0, n)] = theData[I(theX, 1, n)];
	}else if(theY == n + 1){
		theData[I(theX, n + 1, n)] =  theData[I(theX, n, n)];
	}
}

// specifies simple boundary conditions.
kernel void setBoundary2(
	global float2 * theData, 
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % (n + 2);
	unsigned int theY = myIndex / (n + 2);
	
	if(theX == 0){
		if(theY == 0){
			theData[I(0, 0, n)] = 0.5f * (theData[I(1, 0, n)] + theData[I(0, 1, n)]);
		}else if(theY == n + 1){
			theData[I(0, n + 1, n)] = 0.5f * (theData[I(1, n + 1, n)] + theData[I(0, n, n)]);
		}else{
			theData[I(0, theY, n)] = theData[I(1, theY, n)];
		}
	}else if(theX == n + 1){
		if(theY == 0){
			theData[I(n + 1, 0, n)] = 0.5f * (theData[I(n, 0, n)] + theData[I(n + 1, 1, n)]);
		}else if(theY == n + 1){
			theData[I(n + 1, n + 1, n)] = 0.5f * (theData[I(n, n + 1, n)] + theData[I(n + 1, n, n)]);
		}else{
			theData[I(n + 1, theY, n)] = theData[I(n, theY, n)];
		}
	}else if(theY == 0){
		theData[I(theX, 0, n)] = theData[I(theX, 1, n)];
	}else if(theY == n + 1){
		theData[I(theX, n + 1, n)] =  theData[I(theX, n, n)];
	}

}

kernel void projectBoundary(
	global float2 * theData, 
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % (n + 2);
	unsigned int theY = myIndex / (n + 2);
	
	if(theX == 0){
		if(theY == 0){
			theData[I(0, 0, n)] = 0.5f * (theData[I(1, 0, n)] + theData[I(0, 1, n)]);
		}else if(theY == n + 1){
			theData[I(0, n + 1, n)] = 0.5f * (theData[I(1, n + 1, n)] + theData[I(0, n, n)]);
		}else{
			theData[I(0, theY, n)] = theData[I(1, theY, n)];
			theData[I(0, theY, n)].x *= -1;
		}
	}else if(theX == n + 1){
		if(theY == 0){
			theData[I(n + 1, 0, n)] = 0.5f * (theData[I(n, 0, n)] + theData[I(n + 1, 1, n)]);
		}else if(theY == n + 1){
			theData[I(n + 1, n + 1, n)] = 0.5f * (theData[I(n, n + 1, n)] + theData[I(n + 1, n, n)]);
		}else{
			theData[I(n + 1, theY, n)] = theData[I(n, theY, n)];
			theData[I(n + 1, theY, n)].x *= -1;
		}
	}else if(theY == 0){
		theData[I(theX, 0, n)] = theData[I(theX, 1, n)];
		theData[I(theX, 0, n)].y *= -1;
	}else if(theY == n + 1){
		theData[I(theX, n + 1, n)] = theData[I(theX, n, n)];
		theData[I(theX, n + 1, n)].y *= -1;
	}
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
	int n
) {

	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % (n + 2);
	unsigned int theY = myIndex / (n + 2);
	
	if(theX > 0 && theX < n + 1 && theY > 0 && theY < n + 1){
		x[I(theX, theY, n)] = (a * (
			x[I(theX - 1, theY, n)] + 
			x[I(theX + 1, theY, n)] + 
			x[I(theX, theY - 1, n)] + 
			x[I(theX, theY + 1, n)]
		) + x0[I(theX, theY, n)]) / c;	
	}
}

kernel void linearSolver2(
	global float2 * x, 
	global float2 * x0, 
	float a, 
	float c, 
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int theX = myIndex % (n + 2);
	unsigned int theY = myIndex / (n + 2);
	
	if(theX > 0 && theX < n + 1 && theY > 0 && theY < n + 1){
		x[I(theX, theY, n)] = (a * (
			x[I(theX - 1, theY, n)] + 
			x[I(theX + 1, theY, n)] + 
			x[I(theX, theY - 1, n)] + 
			x[I(theX, theY + 1, n)]
		) + x0[I(theX, theY, n)]) / c;	
	}
}

kernel void diffuse2(
	global float2 * c, 
	global float2 * c0, 
	float theDiffusion, 
	float theDeltaTime,
	int n
) {
	const float a = theDeltaTime * theDiffusion * n * n;
	linearSolver2(c, c0, a, 1 + 4 * a, n);
	
	barrier(CLK_GLOBAL_MEM_FENCE);
	
	setBoundary2(c, n);
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
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
	
	if(myX > 0 && myX < n + 1 && myY > 0 && myY < n + 1){

		float dt0 = theDeltaTime * n;
	
		// go backwards through velocity field
		float2 pos = (float2)(myX, myY) - dt0 * theVelocities[I(myX, myY, n)];
		float x = pos.x;
		float y = pos.y;
	
		// interpolate results
		if (x > n + 0.5)
			x = n + 0.5f;
		if (x < 0.5)
			x = 0.5f;
	
		int i0 = (int) x;
		int i1 = i0 + 1;
	
		if (y > n + 0.5)
			y = n + 0.5f;
		if (y < 0.5)
			y = 0.5f;

		int j0 = (int) y;
		int j1 = j0 + 1;
	
		float s1 = x - i0;
		float s0 = 1 - s1;
		float t1 = y - j0;
		float t0 = 1 - t1;
	
		d[I(myX, myY, n)] = 
			s0 * (t0 * d0[I(i0, j0, n)] + t1 * d0[I(i0, j1, n)]) +
			s1 * (t0 * d0[I(i1, j0, n)] + t1 * d0[I(i1, j1, n)]);
	}
}

kernel void advect2(
	global float2 * d, 
	global float2 * d0, 
	global float2 * theVelocities,
	float theDeltaTime,
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
	
	if(myX > 0 && myX < n + 1 && myY > 0 && myY < n + 1){

		float dt0 = theDeltaTime * n;
	
		// go backwards through velocity field
		float2 pos = (float2)(myX, myY) - dt0 * theVelocities[I(myX, myY, n)];
		float x = pos.x;
		float y = pos.y;
	
		// interpolate results
		if (x > n + 0.5)
			x = n + 0.5f;
		if (x < 0.5)
			x = 0.5f;
	
		int i0 = (int) x;
		int i1 = i0 + 1;
	
		if (y > n + 0.5)
			y = n + 0.5f;
		if (y < 0.5)
			y = 0.5f;

		int j0 = (int) y;
		int j1 = j0 + 1;
	
		float s1 = x - i0;
		float s0 = 1 - s1;
		float t1 = y - j0;
		float t0 = 1 - t1;
	
		d[I(myX, myY, n)] = 
			s0 * (t0 * d0[I(i0, j0, n)] + t1 * d0[I(i0, j1, n)]) +
			s1 * (t0 * d0[I(i1, j0, n)] + t1 * d0[I(i1, j1, n)]);
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
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
	
	if(myX > 0 && myX < n + 1 && myY > 0 && myY < n + 1){	
		float du_dy = (theVelocities[I(myX, myY + 1, n)].x - theVelocities[I(myX, myY - 1, n)].x) * 0.5f;
		float dv_dx = (theVelocities[I(myX + 1, myY, n)].y - theVelocities[I(myX - 1, myY, n)].y) * 0.5f;

		theCurl[I(myX, myY, n)] = fabs( du_dy - dv_dx);
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
	int n
) {
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
	
	if(myX > 1 && myX < n && myY > 1 && myY < n){
		// Find derivative of the magnitude (n = del |w|)
		float dw_dx = (theCurl[I(myX + 1, myY, n)] - theCurl[I(myX - 1, myY, n)]) * 0.5f;
		float dw_dy = (theCurl[I(myX, myY + 1, n)] - theCurl[I(myX, myY - 1, n)]) * 0.5f;

		// Calculate vector length. (|n|)
		// Add small factor to prevent divide by zeros.
		float length = sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;

		// N = ( n/|n| )
		dw_dx /= length;
		dw_dy /= length;
	
		float v = theCurl[I(myX, myY, n)];
	
		// N x w
		Fvc[I(myX, myY, n)] += (float2)(theDeltaTime * dw_dy * -v, theDeltaTime * dw_dx * v);
	}
}

kernel void divergency(
	global float * div,
	global float2 * theVelocities,
	int n
){
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
			
	if(myX > 0 && myX < n + 1 && myY > 0 && myY < n + 1){	
		div[I(myX, myY, n)] = (
			theVelocities[I(myX + 1, myY, n)].x - 
			theVelocities[I(myX - 1, myY, n)].x + 
			theVelocities[I(myX, myY + 1, n)].y - 
			theVelocities[I(myX, myY - 1, n)].y
		) * -0.5f / n;
	}
}

kernel void massConserve(
	global float2 * theVelocities,
	global float * p,
	int n
){
	unsigned int myIndex = get_global_id(0);
	unsigned int myX = myIndex % (n + 2);
	unsigned int myY = myIndex / (n + 2);
	
	if(myX > 0 && myX < n + 1 && myY > 0 && myY < n + 1){	
		theVelocities[I(myX, myY, n)].x -= 0.5f * n * (p[I(myX + 1, myY, n)] - p[I(myX - 1, myY, n)]);
		theVelocities[I(myX, myY, n)].y -= 0.5f * n * (p[I(myX, myY + 1, n)] - p[I(myX, myY - 1, n)]);
	}
}

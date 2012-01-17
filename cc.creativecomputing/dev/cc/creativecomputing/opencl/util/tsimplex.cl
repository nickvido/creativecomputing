// This is (sqrt(3.0)-1.0)/2.0
#define F2 0.366025403784
	
// This is (3.0-sqrt(3.0))/6.0
#define G2 0.211324865405

float gradient1d(int i, float v){
	int index = (lattice1d(i) & G_MASK) * G_VECSIZE;
	float g = G[index + 0];
	return (v * g);
}

float2 gradient2d(int2 i){
	int index = (lattice2d(i) & G_MASK) * G_VECSIZE;
	return (float2)(G[index + 0], G[index + 1]);
}

float gradient3d(int4 i, float4 v){
	int index = (lattice3d(i) & G_MASK) * G_VECSIZE;
	float4 g = (float4)(G[index + 0], G[index + 1], G[index + 2], 1.0f);
	return dot(v, g);
}

/*
 * 2D simplex noise. Somewhat slower but much better looking than classic noise.
 */
float noise2(float2 P) {

	// Skew and unskew factors are a bit hairy for 2D, so define them as constants
	// Skew the (x,y) space to determine which cell of 2 simplices we're in
	float s = (P.x + P.y) * F2;   // Hairy factor for 2D skewing
  	float2 pf = floor(P + s);
  	int2 ip = (int2)((int)pf.x, (int)pf.y);
  	
	float t = (pf.x + pf.y) * G2; // Hairy factor for unskewing
	float2 P0 = pf - t; // Unskew the cell origin back to (x,y) space
	
	float2 Pf0 = P - P0;  // The x,y distances from the cell origin

	// For the 2D case, the simplex shape is an equilateral triangle.
	// Find out whether we are above or below the x = y diagonal to
	// determine which of the two triangles we're in.
	int2 o1;
	if(Pf0.x > Pf0.y) o1 = (int2)(1, 0);  // +x, +y traversal order
	else o1 = (int2)(0, 1);               // +y, +x traversal order

	// Noise contribution from simplex origin
  	float2 grad0 = gradient2d(ip);
  	float t0 = 0.5 - dot(Pf0, Pf0);
  	float n0;
  	if (t0 < 0.0) n0 = 0.0;
  	else {
    	t0 *= t0;
    	n0 = t0 * t0 * dot(grad0, Pf0);
  	}

  	// Noise contribution from middle corner
  	float2 Pf1 = Pf0 - (float2)o1 + G2;
  	float2 grad1 = gradient2d(ip + o1);
  	float t1 = 0.5 - dot(Pf1, Pf1);
  	float n1;
  	if (t1 < 0.0) n1 = 0.0;
  	else {
    t1 *= t1;
    n1 = t1 * t1 * dot(grad1, Pf1);
  }
  
  // Noise contribution from last corner
  float2 Pf2 = Pf0 - (float2)(1.0-2.0*G2);
  float2 grad2 = gradient2d(ip + (int2)(1,1));
  float t2 = 0.5 - dot(Pf2, Pf2);
  float n2;
  if(t2 < 0.0) n2 = 0.0;
  else {
    t2 *= t2;
    n2 = t2 * t2 * dot(grad2, Pf2);
  }

  // Sum up and scale the result to cover the range [-1,1]
  return 70.0 * (n0 + n1 + n2);
}
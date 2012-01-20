//
// Description : Array and textureless GLSL 2D/3D/4D simplex 
//               noise functions.
//      Author : Ian McEwan, Ashima Arts.
//  Maintainer : ijm
//     Lastmod : 20110410 (stegu)
//     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
//               Distributed under the MIT License. See LICENSE file.
//

float4 permute4f(float4 x){
	return fmod(((x * 34.0f) + 1.0f) * x, (float4)(289.0f));
}

float permute1f(float x){
	float myResult = fmod(((x * 34.0f) + 1.0f) * x, 289.0f);
	return floor(myResult);
}

float4 taylorInvSqrt4f(float4 r){
	return 1.79284291400159f - 0.8537347209531f * r;
}

float taylorInvSqrt1f(float r){
	return 1.79284291400159f - 0.85373472095314f * r;
}

float4 grad4(float j, float4 ip){
	const float4 ones = (float4)(1.0f, 1.0f, 1.0f, -1.0f);
	
	float4 foo;
	float4 p = floor( fract ((float4)(j) * ip, &foo) * 7.0f) * ip.z - 1.0f;
	p.w = 1.5 - dot(fabs((float4)(p.xyz,0)), (float4)(ones.xyz,0));
	float4 s = (float4)(
		p.x < 0, 
		p.y < 0, 
		p.z < 0, 
		p.w < 0
	);
	p.xyz = p.xyz + (s.xyz*2.0 - 1.0) * s.www; 
	return p;
}

float2 fade(float2 t) {
  return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
}

float noise2(float2 thePosition){
	const float4 C = (float4)(
		0.211324865405187,  // (3.0-sqrt(3.0))/6.0
		0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
		-0.577350269189626, // -1.0 + 2.0 * C.x
		0.024390243902439	// 1.0 / 41.0
	); 

	// First corner
	float2 i  = floor(thePosition + dot(thePosition, C.yy) );
	float2 x0 = thePosition -   i + dot(i, C.xx);

	// Other corners
	float2 i1 = (x0.x > x0.y) ? (float2)(1.0, 0.0) : (float2)(0.0, 1.0);
  	float4 x12 = x0.xyxy + C.xxzz;
  	x12.xy -= i1;

	// Permutations
  	i = fmod(i, (float2)(289.0)); // Avoid truncation effects in permutation
  	float4 p = permute4f( permute4f( i.y + (float4)(0.0, i1.y, 1.0,0 )) + i.x + (float4)(0.0, i1.x, 1.0, 0));

	float4 m = max(0.5f - (float4)(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw), 0.0f), 0.0f);
	m = m * m ;
	m = m * m ;

	// Gradients: 41 points uniformly over a line, mapped onto a diamond.
	// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)
	float4 foo;
	float4 x = 2.0f * fract(p * C.wwww, &foo) - 1.0f;
	float4 h = fabs(x) - 0.5f;
	float4 ox = floor(x + 0.5f);
	float4 a0 = x - ox;

	// Normalise gradients implicitly by scaling m
	// Inlined for speed: m *= taylorInvSqrt( a0*a0 + h*h );
	m *= 1.79284291400159f - 0.85373472095314f * ( a0*a0 + h*h );

	// Compute final noise value at P
	float4 g;
	g.x  = a0.x  * x0.x  + h.x  * x0.y;
	g.yz = a0.yz * x12.xz + h.yz * x12.yw;
	return 130.0 * dot(m, g);
}

// Unsigned simplex noise 2d
float unoise2(float2 position){
    return (0.5f - 0.5f * noise2(position));
}

float noise3(float4 v){ 
	const float2  C = (float2)(1.0/6.0, 1.0/3.0) ;
	const float4  D = (float4)(0.0, 0.5, 1.0, 2.0);

	// First corner
	float4 i  = floor(v + dot(v, (float4)(C.yyy, 0)));
	float4 x0 =   v - i + dot(i, (float4)(C.xxx, 0)) ;

	// Other corners
	float4 g = step(x0.yzxw, x0.xyzw);
	float4 l = 1.0f - g;
	float4 i1 = min( g.xyzw, l.zxyw);
	float4 i2 = max( g.xyzw, l.zxyw);

	float4 x1 = x0 - i1 + (float4)(C.xxx,0);
	float4 x2 = x0 - i2 + (float4)(C.yyy,0); 
	float4 x3 = x0 - (float4)(D.yyy,0);

	// Permutations
	i = fmod(i, (float4)(289.0f)); 
	float4 p = permute4f( permute4f( permute4f(
		i.z + (float4)(0.0, i1.z, i2.z, 1.0)) + 
		i.y + (float4)(0.0, i1.y, i2.y, 1.0)) + 
		i.x + (float4)(0.0, i1.x, i2.x, 1.0)
	);

	// Gradients: 7x7 points over a square, mapped onto an octahedron.
	// The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
	float n_ = 0.142857142857f; // 1.0/7.0
	float4 ns = n_ * (float4)(D.wyz,0) - (float4)(D.xzx,0);

	float4 j = p - 49.0f * floor(p * ns.z * ns.z);  //  fmod(p,7*7)

	float4 x_ = floor(j * ns.z);
	float4 y_ = floor(j - 7.0f * x_);    // fmod(j,N)

	float4 x = x_ *ns.x + ns.yyyy;
	float4 y = y_ *ns.x + ns.yyyy;
	float4 h = 1.0f - fabs(x) - fabs(y);

	float4 b0 = (float4)(x.xy, y.xy);
	float4 b1 = (float4)(x.zw, y.zw);

	float4 s0 = floor(b0)*2.0 + 1.0;
	float4 s1 = floor(b1)*2.0 + 1.0;
	float4 sh = -step(h, (float4)(0.0));

	float4 a0 = b0.xzyw + s0.xzyw * sh.xxyy ;
	float4 a1 = b1.xzyw + s1.xzyw * sh.zzww ;

	float4 p0 = (float4)(a0.xy,h.x, 0);
	float4 p1 = (float4)(a0.zw,h.y, 0);
	float4 p2 = (float4)(a1.xy,h.z, 0);
	float4 p3 = (float4)(a1.zw,h.w, 0);

	//Normalise gradients
	float4 norm = taylorInvSqrt4f((float4)(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
	p0 *= norm.x;
	p1 *= norm.y;
	p2 *= norm.z;
	p3 *= norm.w;

	// Mix final noise value
	float4 m = fmax(0.6f - (float4)(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0f);
	m = m * m;
	return 42.0f * dot( m*m, (float4)( dot(p0,x0), dot(p1,x1), dot(p2,x2), dot(p3,x3) ) );
}
/*
float snoise(float theX, float theY, float theZ, float theW){
	float4 v = (float4)(theX, theY, theZ, theW);
	const float4  C = (float4)(
  		0.138196601125011,  // (5 - sqrt(5))/20  G4
		0.276393202250021,  // 2 * G4
		0.414589803375032,  // 3 * G4
		-0.447213595499958	// -1 + 4 * G4
	); 

	// (sqrt(5) - 1)/4 = F4, used once below
	#define F4 0.309016994374947451

	// First corner
	float4 i  = floor(v + dot(v, float4(F4)) );
	float4 x0 = v - i + dot(i, C.xxxx);

	// Other corners

	// Rank sorting originally contributed by Bill Licea-Kane, AMD (formerly ATI)
	float4 i0;
	float4 isX = (float4)(step(x0.yzw, x0.xxx),0);
	float4 isYZ = (float4)(step(x0.zww, x0.yyz),0);
	
	i0.x = isX.x + isX.y + isX.z;
	i0.yzw = 1.0 - isX;
	i0.y += isYZ.x + isYZ.y;
	i0.zw += 1.0 - isYZ.xy;
	i0.z += isYZ.z;
	i0.w += 1.0 - isYZ.z;

	// i0 now contains the unique values 0,1,2,3 in each channel
	float4 i3 = clamp( i0, 0.0, 1.0 );
	float4 i2 = clamp( i0-1.0, 0.0, 1.0 );
	float4 i1 = clamp( i0-2.0, 0.0, 1.0 );

	//  x0 = x0 - 0.0 + 0.0 * C.xxxx
	//  x1 = x0 - i1  + 0.0 * C.xxxx
	//  x2 = x0 - i2  + 0.0 * C.xxxx
	//  x3 = x0 - i3  + 0.0 * C.xxxx
	//  x4 = x0 - 1.0 + 4.0 * C.xxxx
	float4 x1 = x0 - i1 + C.xxxx;
	float4 x2 = x0 - i2 + C.yyyy;
	float4 x3 = x0 - i3 + C.zzzz;
	float4 x4 = x0 + C.wwww;

	// Permutations
	i = fmod(i, 289.0); 
	float j0 = permute( permute( permute( permute(i.w) + i.z) + i.y) + i.x);
	float4 j1 = permute( permute( permute( permute (
		i.w + float4(i1.w, i2.w, i3.w, 1.0 )) + 
		i.z + float4(i1.z, i2.z, i3.z, 1.0 )) + 
		i.y + float4(i1.y, i2.y, i3.y, 1.0 )) + 
		i.x + float4(i1.x, i2.x, i3.x, 1.0 ));

	// Gradients: 7x7x6 points over a cube, mapped onto a 4-cross polytope
	// 7*7*6 = 294, which is close to the ring size 17*17 = 289.
	float4 ip = float4(1.0/294.0, 1.0/49.0, 1.0/7.0, 0.0) ;

	float4 p0 = grad4(j0,   ip);
	float4 p1 = grad4(j1.x, ip);
	float4 p2 = grad4(j1.y, ip);
	float4 p3 = grad4(j1.z, ip);
	float4 p4 = grad4(j1.w, ip);

	// Normalise gradients
	float4 norm = taylorInvSqrt(float4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
	p0 *= norm.x;
	p1 *= norm.y;
	p2 *= norm.z;
	p3 *= norm.w;
	p4 *= taylorInvSqrt(dot(p4,p4));

	// Mix contributions from the five corners
	float4 m0 = max(0.6f - (float4)(dot(x0,x0), dot(x1,x1), dot(x2,x2),0), 0.0);
	float2 m1 = max(0.6f - (float2)(dot(x3,x3), dot(x4,x4)));
	m0 = m0 * m0;
	m1 = m1 * m1;
  	return 49.0 * (
  		dot(
  			m0 * m0, 
  			(float4)(
  				dot( p0, x0 ), 
  				dot( p1, x1 ), 
  				dot( p2, x2 ),
  				0
  			)
  		) + 
  		dot(
  			m1 * m1, 
  			(float2)(
  				dot( p3, x3 ), 
  				dot( p4, x4 ) 
  			)
  		)
  	);
}*/
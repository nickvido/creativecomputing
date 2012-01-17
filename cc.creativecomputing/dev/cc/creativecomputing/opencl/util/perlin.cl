float gradient1d(int i, float v){
	int index = (lattice1d(i) & G_MASK) * G_VECSIZE;
	float g = G[index + 0];
	return (v * g);
}

float gradient2d(int2 i, float2 v){
	int index = (lattice2d(i) & G_MASK) * G_VECSIZE;
	float2 g = (float2)(G[index + 0], G[index + 1]);
	return dot(v, g);
}

float gradient3d(int4 i, float4 v){
	int index = (lattice3d(i) & G_MASK) * G_VECSIZE;
	float4 g = (float4)(G[index + 0], G[index + 1], G[index + 2], 1.0f);
	return dot(v, g);
}

////////////////////////////////////////////////////////////////////////////////////////////////////

// Signed gradient noise 1d
float noise1(float position){
	float p = position;
	float pf = floor(p);
	int ip = (int)pf;
	float fp = p - pf;        
    ip &= P_MASK;
	
	float n0 = gradient1d(ip + 0, fp - 0.0f);
	float n1 = gradient1d(ip + 1, fp - 1.0f);

	float n = mix1d(n0, n1, smooth(fp));
	return n * (1.0f / 0.7f);
}

// Signed gradient noise 2d
float noise2(float2 position){
	float2 p = position;
	float2 pf = floor(p);
	int2 ip = (int2)((int)pf.x, (int)pf.y);
	float2 fp = p - pf;        
    ip &= P_MASK;
	
	const int2 I00 = (int2)(0, 0);
	const int2 I01 = (int2)(0, 1);
	const int2 I10 = (int2)(1, 0);
	const int2 I11 = (int2)(1, 1);
	
	const float2 F00 = (float2)(0.0f, 0.0f);
	const float2 F01 = (float2)(0.0f, 1.0f);
	const float2 F10 = (float2)(1.0f, 0.0f);
	const float2 F11 = (float2)(1.0f, 1.0f);

	float n00 = gradient2d(ip + I00, fp - F00);
	float n10 = gradient2d(ip + I10, fp - F10);
	float n01 = gradient2d(ip + I01, fp - F01);
	float n11 = gradient2d(ip + I11, fp - F11);

	const float2 n0001 = (float2)(n00, n01);
	const float2 n1011 = (float2)(n10, n11);

	float2 n2 = mix2d(n0001, n1011, smooth(fp.x));
	float n = mix1d(n2.x, n2.y, smooth(fp.y));
	return n * (1.0f / 0.7f);
}

// Signed gradient noise 3d
float noise3(float4 position){

	float4 p = position;
	float4 pf = floor(p);
	int4 ip = (int4)((int)pf.x, (int)pf.y, (int)pf.z, 0.0);
	float4 fp = p - pf;        
    ip &= P_MASK;

    int4 I000 = (int4)(0, 0, 0, 0);
    int4 I001 = (int4)(0, 0, 1, 0);  
    int4 I010 = (int4)(0, 1, 0, 0);
    int4 I011 = (int4)(0, 1, 1, 0);
    int4 I100 = (int4)(1, 0, 0, 0);
    int4 I101 = (int4)(1, 0, 1, 0);
    int4 I110 = (int4)(1, 1, 0, 0);
    int4 I111 = (int4)(1, 1, 1, 0);
	
    float4 F000 = (float4)(0.0f, 0.0f, 0.0f, 0.0f);
    float4 F001 = (float4)(0.0f, 0.0f, 1.0f, 0.0f);
    float4 F010 = (float4)(0.0f, 1.0f, 0.0f, 0.0f);
    float4 F011 = (float4)(0.0f, 1.0f, 1.0f, 0.0f);
    float4 F100 = (float4)(1.0f, 0.0f, 0.0f, 0.0f);
    float4 F101 = (float4)(1.0f, 0.0f, 1.0f, 0.0f);
    float4 F110 = (float4)(1.0f, 1.0f, 0.0f, 0.0f);
    float4 F111 = (float4)(1.0f, 1.0f, 1.0f, 0.0f);
	
	float n000 = gradient3d(ip + I000, fp - F000);
	float n001 = gradient3d(ip + I001, fp - F001);
	
	float n010 = gradient3d(ip + I010, fp - F010);
	float n011 = gradient3d(ip + I011, fp - F011);
	
	float n100 = gradient3d(ip + I100, fp - F100);
	float n101 = gradient3d(ip + I101, fp - F101);

	float n110 = gradient3d(ip + I110, fp - F110);
	float n111 = gradient3d(ip + I111, fp - F111);

	float4 n40 = (float4)(n000, n001, n010, n011);
	float4 n41 = (float4)(n100, n101, n110, n111);

	float4 n4 = mix3d(n40, n41, smooth(fp.x));
	float2 n2 = mix2d(n4.xy, n4.zw, smooth(fp.y));
	float n = mix1d(n2.x, n2.y, smooth(fp.z));
	return n * (1.0f / 0.7f);
}

////////////////////////////////////////////////////////////////////////////////////////////////////


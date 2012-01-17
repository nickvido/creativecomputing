float noiseX(float4 p) {
	return noise3(p);
}

float noiseY(float4 p) {
	return noise3((float4)(p.y + 31.416, p.z - 47.853, p.x + 12.793,0));
}

float noiseZ(float4 p) {
	return noise3((float4)(p.z - 233.145, p.x - 113.408, p.y - 185.31, 0));
}

constant float noiseLengthScales[] = {0.4f,0.23f,0.11f};
constant float noiseGains[] = {1.0f,0.5f,0.25f};

kernel void field(
	global float4 * theAccelerations,
	global float4 * theVelocities,
	global float4 * thePositions,
	
	const float4 theScale,
	const float4 theOffset,
	const float theDeltaTime,
	
	const float theStrength
){
	unsigned int myIndex = get_global_id(0);
	
	float4 myFuturePosition = thePositions[myIndex] + theVelocities[myIndex] * theDeltaTime;
	float4 myNoisePosition = myFuturePosition * theScale + theOffset;
	
	float4 result = (float4)(0);
		
	//float d=distance_and_normal(x, y, z, normal);
	// add turbulence octaves that respect boundaries, increasing upwards
	for (int i = 0; i < 1; i++) {
		result += (float4)(noiseX(myNoisePosition), noiseY(myNoisePosition), noiseZ(myNoisePosition), 0) * noiseGains[i];
		myNoisePosition *= noiseLengthScales[i];
	}
		
	theAccelerations[myIndex] += result * theStrength;
}
/*
struct NoiseForceField : Force{
	float noiseScale;
	float strength;
	float3 noiseOffset;
	
	float noiseLengthScales[3];
	float noiseGains[3];
	
	float noise0(float3 p) {
		return snoise(p);
	}

	float noise1(float3 p) {
		return snoise(float3(p.y + 31.416, p.z - 47.853, p.x + 12.793));
	}

	float noise2(float3 p) {
		return snoise(float3(p.z - 233.145, p.x - 113.408, p.y - 185.31));
	}
	
	float3 force(float3 thePosition, float3 theVelocity, float2 theTexID, float theDeltaTime){
		float3 noisePosition = (thePosition + theVelocity) * noiseScale + noiseOffset;
		float3 result = float3(0,0,0);
		
		 //float d=distance_and_normal(x, y, z, normal);
		// add turbulence octaves that respect boundaries, increasing upwards
		for (int i = 0; i < noiseLengthScales.length; i++) {
			result += float3(noise0(noisePosition), noise1(noisePosition), noise2(noisePosition)) * noiseGains[i];
		}

		return result * strength;
	}
};*/
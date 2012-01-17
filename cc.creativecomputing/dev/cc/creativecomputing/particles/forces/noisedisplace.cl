kernel void displace(
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
	float2 myNoisePosition = myFuturePosition.xz * theScale.xz + theOffset.xz;
	
	float height = noise2(myNoisePosition);
			
	float myDisplacement = myFuturePosition.y - height * theScale.y + theOffset.y + theVelocities[myIndex].y;
		
	theAccelerations[myIndex].y += -myDisplacement * theStrength;
}
/*
struct NoiseHeightmapForce : Force{
	float noiseScale;
	float strength;
	float height;
	float3 noiseOffset;
	
	float3 force(float3 thePosition, float3 theVelocity, float2 theTexID, float theDeltaTime){
		float3 fPosition = thePosition + theVelocity * theDeltaTime;
		float3 noisePosition = (fPosition) * noiseScale + noiseOffset;
		
		float displacement =  fPosition.y - noise(noisePosition.xz) * height +  + theVelocity.y;
		return float3(0,clamp(-displacement,-1,1),0) * strength;
	}
};*/
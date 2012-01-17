kernel void displace(
	global float4 * theAccelerations,
	global float4 * theVelocities,
	global float4 * thePositions,
	
	read_only image2d_t theDisplacement, 
	
	const float4 theScale,
	const float4 theOffset,
	const float theDeltaTime,
	const float theStrength
){
	unsigned int myIndex = get_global_id(0);
	
	float4 myFuturePosition = thePositions[myIndex] + theVelocities[myIndex] * theDeltaTime;
	float2 myTexturePosition = myFuturePosition.xz / theScale.xz + theOffset.xz;
	
	const sampler_t mySampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP | CLK_FILTER_LINEAR; 
	float4 height = read_imagef(theDisplacement, mySampler, myTexturePosition);
			
	float myDisplacement = myFuturePosition.y - height.x * theScale.y + theOffset.y + theVelocities[myIndex].y;
		
	theAccelerations[myIndex].y += -myDisplacement * theStrength;
}
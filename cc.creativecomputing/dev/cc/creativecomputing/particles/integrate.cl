
kernel void clearBuffer(global float4 * theBuffer){
	theBuffer[get_global_id(0)] = (float4)(0);
}

kernel void integrate(
	global float4 * theAccelerations,
	global float4 * theVelocities,
	global float4 * thePositions,
	global float4 * theInfos,
	float theDeltaTime
){
	unsigned int myIndex = get_global_id(0);
	
	theInfos[myIndex].z -= (1 - theInfos[myIndex].x) * theDeltaTime;
	
	theVelocities[myIndex] += theAccelerations[myIndex] * theDeltaTime;
	thePositions[myIndex] += theVelocities[myIndex] * theDeltaTime;
	if(theInfos[myIndex].z <= 0)thePositions[myIndex].x += 1000000;
	thePositions[myIndex].w = 1.0;
}
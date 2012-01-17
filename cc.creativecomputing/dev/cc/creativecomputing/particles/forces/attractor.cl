kernel void attractor(
	global float4 * theAccelerations,
	global float4 * theVelocities,
	global float4 * thePositions,
	const float4 thePosition,
	const float theRadius,
	const float theStrength
){
	unsigned int myIndex = get_global_id(0);
	
	float4 myForce = thePosition - thePositions[myIndex];
	float myDistance = length(myForce);
		
	if (myDistance > theRadius) return;
	
	float myFallOff = 1.0f - myDistance / theRadius;
	float myFactor = myFallOff * myFallOff;
	myForce -= theVelocities[myIndex];
	//myForce *= myFactor / myDistance;
        
	theAccelerations[myIndex] += myForce * theStrength;
}
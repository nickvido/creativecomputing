kernel void viscousDrag(
	global float4 * theAccelerations,
	global float4 * theVelocities,
	float theCoefficient,
	float theStrength
){
	unsigned int myIndex = get_global_id(0);
	
	theAccelerations[myIndex] += theVelocities[myIndex] * -theCoefficient * theStrength;
}
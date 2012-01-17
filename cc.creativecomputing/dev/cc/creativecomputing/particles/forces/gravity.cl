kernel void gravity(
	global float4 * theAccelerations,
	const float4 theDirection,
	float theStrength
){
	unsigned int myIndex = get_global_id(0);
	
	theAccelerations[myIndex] += theDirection * theStrength;
}
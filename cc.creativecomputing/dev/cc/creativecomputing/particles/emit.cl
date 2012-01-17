kernel void emit(
	global int * theIndices,
	
	global float4 * theInputVelocities,
	global float4 * theInputPositions,
	global float4 * theInputInfos,
	
	global float4 * theVelocities,
	global float4 * thePositions,
	global float4 * theInfos
){
	unsigned int myIndex = get_global_id(0);
	int myParticleIndex = theIndices[myIndex];
	
	theVelocities[myParticleIndex] = theInputVelocities[myIndex];
	thePositions[myParticleIndex] = theInputPositions[myIndex];
	theInfos[myParticleIndex] = theInputInfos[myIndex];
}
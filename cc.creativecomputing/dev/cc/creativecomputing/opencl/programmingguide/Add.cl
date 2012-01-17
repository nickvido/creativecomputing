kernel void add(
	global const float * a, 
	global float * b
){
	int gid = get_global_id(0);
	b[gid] += a[gid];
}

kernel void multiply(
	global const float *a, 
	global const float *b,
	global float *result
){
	int gid = get_global_id(0);
	result[gid] = a[gid] * b[gid];
}
kernel void histogram(
	read_only image2d_t input,
	global uint *histogram
){
	int	x = get_global_id(0); 
	int	y = get_global_id(1);
	
	float4 clr = read_imagef(
		input, 
		CLK_NORMALIZED_COORDS_FALSE |
		CLK_ADDRESS_CLAMP_TO_EDGE | 
		CLK_FILTER_NEAREST, 
		(float2)(x, y)
	);
	uchar	indx_x, indx_y, indx_z; 
	indx_x = convert_uchar_sat(clr.x); 
	indx_y = convert_uchar_sat(clr.y); 
	indx_z = convert_uchar_sat(clr.z); 
		
	histogram[indx_x]++; 
	histogram[256+(uint)indx_y]++; 
	histogram[512+(uint)indx_z]++;
}
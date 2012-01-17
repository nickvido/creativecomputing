//********************************************************************
// This kernel takes a RGBA 8-bit / channel input image and produces a
// partial histogram for R, G and B. Each work-group represents an
// image tile and computes the histogram for that tile.
// 
// partial_histogram is an array of num_groups * (256 * 3) entries. 
// Each entry is a 32-bit unsigned integer value. 
// 
// We store 256 R bins, followed by 256 G bins and then the 256 B bins. 
//********************************************************************

kernel void histogramPartialImageRGBAUnorm8(
	read_only image2d_t img,
	global uint *histogram
){
	int local_size = (int)get_local_size(0) * (int)get_local_size(1);
	int image_width = get_image_width(img);
	int image_height = get_image_height(img);
	int group_indx = (get_group_id(1) * get_num_groups(0) + get_group_id(0)) * 256 * 3;
	
	int	x = get_global_id(0); 
	int	y = get_global_id(1);
	
	local uint tmp_histogram[256 * 3];
	
	int	tid = get_local_id(1) * get_local_size(0) + get_local_id(0);
	int j=256*3; 
	int	indx = 0;
	
	// clear the local buffer that will generate the partial histogram
	do {
		if (tid < j)
			tmp_histogram[indx+tid] = 0;
		j -= local_size;
		indx += local_size; 
	} while (j > 0);
	
	barrier(CLK_LOCAL_MEM_FENCE);
	
	if ((x < image_width) && (y < image_height)){
		float4 clr = read_imagef(
			img, 
			CLK_NORMALIZED_COORDS_FALSE |
			CLK_ADDRESS_CLAMP_TO_EDGE | 
			CLK_FILTER_NEAREST, 
			(float2)(x, y)
		);
		uchar	indx_x, indx_y, indx_z; 
		indx_x = convert_uchar_sat(clr.x); 
		indx_y = convert_uchar_sat(clr.y); 
		indx_z = convert_uchar_sat(clr.z); 
		
		tmp_histogram[indx_x]++; 
		tmp_histogram[256+(uint)indx_y]++; 
		tmp_histogram[512+(uint)indx_z]++;
	}
	
	barrier(CLK_LOCAL_MEM_FENCE);
	// copy the partial histogram to appropriate location in 
	// histogram given by group_indx
	if (local_size >= (256 * 3)){
		if (tid < (256 * 3)) 
			histogram[group_indx + tid] = tmp_histogram[tid];
	}else{
		j = 256 * 3; 
		indx = 0; 
		do {
			if (tid < j)
				histogram[group_indx + indx + tid] = tmp_histogram[indx + tid];
			
			j -= local_size;
			indx += local_size;
		} while (j > 0);
	}
}

//******************************************************************** 
// This kernel sums partial histogram results into a final histogram result
// num_groups is the number of work-groups used to compute partial histograms
// partial_histogram is an array of num_groups * (256 * 3) entries.
// we store 256 R bins, followed by 256 G bins and then the 256 B bins.
// The final summed results are returned in histogram.
//********************************************************************
kernel void histogram_sum_partial_results_unorm8(
	global uint *partial_histogram,
	int num_groups,
	global uint *histogram
){
	int tid = (int)get_global_id(0);
	int group_indx; 
	int	n = num_groups; 
	local uint tmp_histogram[256 * 3];

	tmp_histogram[tid] = partial_histogram[tid];
	group_indx = 256 * 3; 
	while (--n > 0) {
		tmp_histogram[tid] += partial_histogram[group_indx + tid]; 
		group_indx += 256*3;
	}
	histogram[tid] = tmp_histogram[tid];
}
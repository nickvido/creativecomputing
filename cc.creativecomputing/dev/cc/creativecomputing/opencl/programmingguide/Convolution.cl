kernel void convolve(
	const global uint * const input, 
	constant uint * const mask,
	global uint * const output, 
	const int inputWidth, 
	const int maskWidth
){
	const int x = get_global_id(0); 
	const int y = get_global_id(1);

	uint sum = 0; 
	
	for (int r = 0; r < maskWidth; r++) {
		const int idxIntmp = (y + r) * inputWidth + x;
		for (int c = 0; c < maskWidth; c++) {
			sum += mask[(r * maskWidth) + c] * input[idxIntmp + c];
		} 
	}	
	output[y * get_global_size(0) + x] = sum;
}
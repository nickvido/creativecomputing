//
// File:       noise_kernel.cl
//
// Abstract:   This example shows how OpenCL can be used for procedural texture synthesis
//             and intermix with existing OpenGL textures for display.  Several compute
//             kernels are provided which generate a variety of procedural functions,
//             including gradient noise (aka Perlin Noise), turbulence and other
//             fractals.
//
// Version:    <1.0>
//
// Disclaimer: IMPORTANT:  This Apple software is supplied to you by Apple Inc. ("Apple")
//             in consideration of your agreement to the following terms, and your use,
//             installation, modification or redistribution of this Apple software
//             constitutes acceptance of these terms.  If you do not agree with these
//             terms, please do not use, install, modify or redistribute this Apple
//             software.
//
//             In consideration of your agreement to abide by the following terms, and
//             subject to these terms, Apple grants you a personal, non - exclusive
//             license, under Apple's copyrights in this original Apple software ( the
//             "Apple Software" ), to use, reproduce, modify and redistribute the Apple
//             Software, with or without modifications, in source and / or binary forms;
//             provided that if you redistribute the Apple Software in its entirety and
//             without modifications, you must retain this notice and the following text
//             and disclaimers in all such redistributions of the Apple Software. Neither
//             the name, trademarks, service marks or logos of Apple Inc. may be used to
//             endorse or promote products derived from the Apple Software without specific
//             prior written permission from Apple.  Except as expressly stated in this
//             notice, no other rights or licenses, express or implied, are granted by
//             Apple herein, including but not limited to any patent rights that may be
//             infringed by your derivative works or by other works in which the Apple
//             Software may be incorporated.
//
//             The Apple Software is provided by Apple on an "AS IS" basis.  APPLE MAKES NO
//             WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
//             WARRANTIES OF NON - INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A
//             PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION
//             ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
//
//             IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
//             CONSEQUENTIAL DAMAGES ( INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
//             SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//             INTERRUPTION ) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
//             AND / OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER
//             UNDER THEORY OF CONTRACT, TORT ( INCLUDING NEGLIGENCE ), STRICT LIABILITY OR
//             OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// Copyright ( C ) 2008 Apple Inc. All Rights Reserved.
//
////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////

uchar4
tonemap(float4 color)
{
    uchar4 result = convert_uchar4_sat_rte(color * 255.0f);
    return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////////////////////////////////







////////////////////////////////////////////////////////////////////////////////////////////////////

#if USE_IMAGES_FOR_RESULTS

////////////////////////////////////////////////////////////////////////////////////////////////////

__kernel void 
MonoFractalImage2d(
	write_only image2d_t output,
	const float2 bias, 
	const float2 scale,
	const float lacunarity, 
	const float increment, 
	const float octaves,	
	const float amplitude)
{
	int2 coord = (int2)(get_global_id(0), get_global_id(1));

	int2 size = (int2)(get_global_size(0), get_global_size(1));

	float2 position = (float2)(coord.x / (float)size.x, 
	                              coord.y / (float)size.y);
		
    float2 sample = (position + bias);
   
	float value = monofractal2d(sample, scale.x, lacunarity, increment, octaves);

	float4 color = (float4)(value, value, value, 1.0f) * amplitude;
    color.w = 1.0f;
    
	write_imagef(output, coord, color);
}



__kernel void 
RidgedMultiFractalImage2d(	
	write_only image2d_t output,
	const float2 bias, 
	const float2 scale,
	const float lacunarity, 
	const float increment, 
	const float octaves,	
	const float amplitude) 
{
	int2 coord = (int2)(get_global_id(0), get_global_id(1));

	int2 size = (int2)(get_global_size(0), get_global_size(1));

	float2 position = (float2)(coord.x / (float)size.x, 
	                              coord.y / (float)size.y);
		
    float2 sample = (position + bias);

	float value = ridgedmultifractal2d(sample, scale.x, lacunarity, increment, octaves);

	float4 color = (float4)(value, value, value, 1.0f) * amplitude;
    color.w = 1.0f;

    write_imagef(output, coord, color);
}



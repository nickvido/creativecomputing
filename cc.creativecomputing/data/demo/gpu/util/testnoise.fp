#include "shader/util/simplex.fp"

uniform samplerRECT input;  
uniform float noiseScale = 1;
uniform float2 noiseOffset = float2(0,0);
	
void main(
	in 		float2 		coords	: WPOS,
	out 	float4 		output0 : COLOR0
) { 
	float4 value = texRECT(input, coords);
	output0 = (noise((value.xy) * noiseScale + noiseOffset) + 1)/2;
}     
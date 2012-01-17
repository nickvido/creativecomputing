#include "shader/util/simplex.fp"

float gain(float a, float b) {
	const float LOG_HALF = log(0.5);
	float p;

	if (a < .001)
			return 0.;
	else if (a > .999)
		return 1;

	b = (b < .001) ? .0001 : (b > .999) ? .999 : b;
	p = log(1. - b) / LOG_HALF;
		
	if (a < 0.5)
		return pow(2 * a, p) / 2;
	else
		1. - pow(2 * (1. - a), p) / 2;
}

float bias(float a, float b) {
	const float LOG_HALF = log(0.5);
		if (a < .001)
			return 0.;
		else if (a > .999)
			return 1.;
		else if (b < .001)
			return 0.;
		else if (b > .999)
			return 1.;
		else
			return pow(a, log(b) / LOG_HALF);
	}

float filter(float t) {
	t = bias(t, .67);
	if (t < .5)
		t = gain(t, .86);
	t = bias(t, .35);
	return t;
}

uniform sampler2D cloudTexture : TEXUNIT0;

uniform float noiseScale = 1;
uniform float noiseAmp1 = 1;
uniform float noiseAmp2 = 1;
uniform float3 noiseOffset = float3(0,0,0);

void main(
	in float4 iColor : COLOR,
	in float4 iTextureCoords : TEXCOORD0,
	out float4 oColor : COLOR
){
	float4 color = tex2D( cloudTexture, iTextureCoords.xy);
	float filterredCol = color.x;
	float myNoise1 = snoise(float3( iTextureCoords.xy,0) * noiseScale + noiseOffset);
	filterredCol += myNoise1 * noiseAmp1;
	float myNoise2 = snoise(float3( iTextureCoords.xy,0) * noiseScale * 2 + noiseOffset);
	filterredCol += myNoise2 * noiseAmp2;
	filterredCol = filter(filterredCol);
	oColor = float4(filterredCol, filterredCol, filterredCol, 1);
	//oColor = float4(1,1,1,1);
}
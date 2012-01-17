uniform float depthRange;

void main(
	in 	float4 	iColor 	: COLOR,
	in 	float 	iDepth 	: TEXCOORD0,
	in 	float3 	iNormal : TEXCOORD1,
	
	out float4 	oColor 	: COLOR0,
	out float3 	oNormal : COLOR1,
	out float 	oDepth 	: DEPTH
){
	oColor = iColor;
	oDepth = iDepth / depthRange;
	oNormal = normalize(iNormal);
  	oNormal += float4(1,1,1,0);
  	oNormal *= 0.5;
}
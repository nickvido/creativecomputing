void main(
	in float4 value0 : TEXCOORD0,
	in float4 value1 : TEXCOORD1,
	out float4 color0 : COLOR0,
	out float4 color1 : COLOR1 
){
	color0 = value0;
	color1 = value1;
}

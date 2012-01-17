uniform samplerRECT positions :  TEXUNIT0;

void main(
	in float4 value1 : TEXCOORD1,
	out float4 color1 : COLOR0 
){
	color1 = value1;
}

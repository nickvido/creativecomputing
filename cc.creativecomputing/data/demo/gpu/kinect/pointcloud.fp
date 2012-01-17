uniform samplerRECT colorMap : TEXUNIT1;

void main(
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR
){
	oColor = texRECT(colorMap,iTexCoord);
}
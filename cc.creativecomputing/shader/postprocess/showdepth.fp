sampler2D depthBuffer;

void main(
	in float2 texCoord : TEXCOORD0,
	out float4 color : COLOR
){
	float depth = tex2D(depthBuffer, texCoord);
	color = float4(depth,depth,depth,1);
}
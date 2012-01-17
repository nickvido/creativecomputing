uniform samplerRECT positionTexture;
uniform samplerRECT forwardTexture;
uniform samplerRECT sideTexture;
uniform samplerRECT upTexture;
uniform float		scale;
	
void main (
	in 	float2 coords : WPOS,
	out float4 quadPosition : COLOR0,
	out float3 quadNormal : COLOR1
){
	int index = floor(coords.x / 4);
	float2 indexCoords = float2(index,coords.y);
	
	float3 position = texRECT (positionTexture, indexCoords);
	float3 up = texRECT (upTexture, indexCoords);
	
	float3 side = texRECT (sideTexture, indexCoords);
	
	index = coords.x - index * 4;
	
	if(index == 0)quadPosition = float4(position - side * scale - up * scale,1);
	if(index == 1)quadPosition = float4(position + side * scale - up * scale,1);
	if(index == 2)quadPosition = float4(position + side * scale + up * scale,1);
	if(index == 3)quadPosition = float4(position - side * scale + up * scale,1);
	quadNormal = normalize(texRECT (forwardTexture, indexCoords));
	
}
	           
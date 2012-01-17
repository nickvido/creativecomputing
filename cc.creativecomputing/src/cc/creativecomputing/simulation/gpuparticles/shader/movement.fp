uniform float appWidth;
uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform float 		deltaTime;
uniform float		make2D = 1;

void main (
	in 	float2 coords : WPOS,
	out float4 newPosition : COLOR0,
	out float4 newInfo : COLOR1
){
	float4 lastInfo = texRECT(infoTexture, coords);
	newInfo = float4(
		lastInfo.x + deltaTime * (1 - lastInfo.z),
		lastInfo.y,
		lastInfo.z,
		lastInfo.w + deltaTime
	);
	
	float3 position = texRECT (positionTexture, coords);
	if(lastInfo.x >= lastInfo.y - 0.2)position = float3(1000000,0,0);
	float3 velocity = texRECT (velocityTexture, coords);
	newPosition = float4(position + deltaTime * velocity,1); 
	newPosition.z *= make2D;
	
	
}
	           
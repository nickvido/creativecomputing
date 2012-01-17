uniform samplerRECT upTexture;

uniform float deltaTime;

void main (
	in 		float2 		texID : WPOS,
	out 	float3 		newVelocity : COLOR0,  
	out 	float3 		up : COLOR1,    
	out 	float3 		side : COLOR2,    
	out 	float3 		forward : COLOR3
){
	float3 position = (float3)texRECT (positionTexture, texID);
	float3 velocity = texRECT (velocityTexture, texID);
	
	float3 acceleration = float3(0,0,0);
	
	for(int i = 0; i < forces.length;i++){
		acceleration = acceleration + forces[i].force(position,velocity,texID,deltaTime);
	}
	
	newVelocity = velocity + acceleration;
	
	//acceleration = acceleration * 0.5;
	
	float3 bankUp = normalize(texRECT (upTexture, texID) + acceleration + float3(0,1,0));
	float speed = length(newVelocity);
		
	if (speed > 0){
		forward = newVelocity / speed;
		side = cross(forward,bankUp);
		up = cross(side,forward);
	}
}
	           
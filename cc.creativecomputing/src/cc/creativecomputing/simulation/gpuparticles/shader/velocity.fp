uniform float deltaTime;

void main (
	in 		float2 texID : WPOS,
	out 	float3 newVelocity : COLOR0
){
	float3 position =(float3) texRECT (positionTexture, texID);
	float3 velocity = texRECT (velocityTexture, texID);
	float3 acceleration = float3(0,0,0);
	
	for(int i = 0; i < forces.length;i++){
		acceleration = acceleration + forces[i].force(position,velocity,texID,deltaTime);
	}
	
	velocity = velocity + acceleration * (deltaTime * 60);
	
	for(int i = 0; i < constraints.length;i++){
		velocity = constraints[i].constraint(velocity, position,texID, deltaTime);
	}
	
	newVelocity = velocity;
}
	           
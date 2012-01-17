float turbulence2d(
	float2 position, 
	float frequency,
	float lacunarity, 
	float increment, 
	float octaves
){
	int i = 0;
	float fi = 0.0f;
	float remainder = 0.0f;	
	float sample = 0.0f;	
	float value = 0.0f;
	int iterations = (int)octaves;
	
	for (i = 0; i < iterations; i++){
		fi = (float)i;
		sample = noise2(position * frequency);
		sample *= pow( lacunarity, -fi * increment );
		value += fabs(sample);
		frequency *= lacunarity;
	}
	
	remainder = octaves - (float)iterations;
	if ( remainder > 0.0f ){
		sample = remainder * noise2(position * frequency);
		sample *= pow( lacunarity, -fi * increment );
		value += fabs(sample);
	}
		
	return value;	
}

float turbulence3d(
	float4 position, 
	float frequency,
	float lacunarity, 
	float increment, 
	float octaves
){
	float fi = 0.0f;
	float remainder = 0.0f;	
	float sample = 0.0f;	
	float value = 0.0f;
	int iterations = (int)octaves;
	
	for (int i = 0; i < iterations; i++){
		fi = (float)i;
		sample = (1.0f - 2.0f * noise3(position * frequency));
		sample *= pow( lacunarity, -fi * increment );
		value += fabs(sample);
		frequency *= lacunarity;
	}
	
	remainder = octaves - (float)iterations;
	if ( remainder > 0.0f ){
		sample = remainder * (1.0f - 2.0f * noise3(position * frequency));
		sample *= pow( lacunarity, -fi * increment );
		value += fabs(sample);
	}
		
	return value;	
}
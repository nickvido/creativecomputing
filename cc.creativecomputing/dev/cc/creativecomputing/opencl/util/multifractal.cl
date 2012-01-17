float multifractal2d(
	float2 position, 
	float frequency,
	float lacunarity, 
	float increment, 
	float octaves)
{
	int i = 0;
	float fi = 0.0f;
	float remainder = 0.0f;	
	float sample = 0.0f;	
	float value = 1.0f;
	int iterations = (int)octaves;
	
	for (i = 0; i < iterations; i++)
	{
		fi = (float)i;
		sample = sgnoise2d(position * frequency) + 1.0f;
		sample *= pow( lacunarity, -fi * increment );
		value *= sample;
		frequency *= lacunarity;
	}
	
	remainder = octaves - (float)iterations;
	if ( remainder > 0.0f )
	{
		sample = remainder * (sgnoise2d(position * frequency) + 1.0f);
		sample *= pow( lacunarity, -fi * increment );
		value *= sample;
	}
		
	return value;	
}

float multifractal3d(
	float4 position, 
	float frequency,
	float lacunarity, 
	float increment, 
	float octaves)
{
	int i = 0;
	float fi = 0.0f;
	float remainder = 0.0f;	
	float sample = 0.0f;	
	float value = 1.0f;
	int iterations = (int)octaves;
	
	for (i = 0; i < iterations; i++)
	{
		fi = (float)i;
		sample = (1.0f - 2.0f * sgnoise3d(position * frequency)) + 1.0f;
		sample *= pow( lacunarity, -fi * increment );
		value *= sample;
		frequency *= lacunarity;
	}
	
	remainder = octaves - (float)iterations;
	if ( remainder > 0.0f )
	{
		sample = remainder * (1.0f - 2.0f * sgnoise3d(position * frequency)) + 1.0f;
		sample *= pow( lacunarity, -fi * increment );
		value *= sample;
	}
		
	return value;	
}
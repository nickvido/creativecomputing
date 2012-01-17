float monofractal2d(
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
	float value = 0.0f;
	int iterations = (int)octaves;
	
	for (i = 0; i < iterations; i++)
	{
		fi = (float)i;
		sample = sgnoise2d(position * frequency);
		sample *= pow( lacunarity, -fi * increment );
		value += sample;
		frequency *= lacunarity;
	}
	
	remainder = octaves - (float)iterations;
	if ( remainder > 0.0f )
	{
		sample = remainder * sgnoise2d(position * frequency);
		sample *= pow( lacunarity, -fi * increment );
		value += sample;
	}
		
	return value;	
}

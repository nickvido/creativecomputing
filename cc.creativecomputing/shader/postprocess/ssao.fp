/**
 * ssao shader based on the screen space ambient occlusion article in shaderx7
 */
 
 // 4x4 texture containing 16 random vectors
 sampler2D sRotSampler4x4;
 
 // scene depth target containing  normalized from 0 to 1 depth
 sampler2D sSceneDepthSampler;
 
 // dimension of the screen
 float2 screenSize;
 
 float blendBreak;
 float blendPower;
 
 float farClipDist = 1;
 
 //parameters affecting offset points number and distribution
 int nSamplesNum = 32; //maybe 8, 16, 24
 float offsetScale = 0.01;
 float offsetScaleStep = 1 + 2.4 / 24;
 
 float Accessibility = 0;
 
 // vPlane should be normalized
// the returned vector has the same length as vDir
float3 mirror( float3 vDir, float3 vPlane ) {
  return vDir - 2 * vPlane * dot(vPlane,vDir);
}
 
 void main(
 	in float2 screenTC : TEXCOORD0,
 	out float4 oColor : COLOR
 ){
 	// get rotation vector, rotation is tiled every 4 screen pixels
 	float2 rotationTC = screenTC * screenSize / 4;
 	float3 vRotation = 2 * tex2D(sRotSampler4x4, rotationTC).rgb-1;
 	
 	// get depth of current pixel and convert it to meters
 	float fSceneDepthP = tex2D(sSceneDepthSampler, screenTC).r * farClipDist;
 	
 	// sample area and accumulate accessibility
 	for(int i = 0; i < (nSamplesNum/8);i++)
 	for(int x = -1; x <= 1; x+=2)
 	for(int y = -1; y <= 1; y+=2)
 	for(int z = -1; z <= 1; z+=2){
 		// generate offset vector (this code is executed only at shader compile stage)
 		
 		//here we use cube corners and give it different length
 		float3 vOffset = normalize(float3(x,y,z)) * (offsetScale *= offsetScaleStep);
 		
 		//rotate offset vector by rotation matrix
 		float3 vRotatedOffset = mirror(vOffset, vRotation);
 		
 		// get center pixel 3d coordinates in screen space
 		float3 vSamplePos = float3(screenTC, fSceneDepthP);
 		
 		//shift coordinates by offset vector (range convert and with depth value)
 		vSamplePos += float3(vRotatedOffset.xy,vRotatedOffset.z * fSceneDepthP * 2);
 		
 		//read scene depth at sampling point and convert into meters
 		float fSceneDepthS = tex2D(sSceneDepthSampler, vSamplePos.xy) * farClipDist;
 		
 		// check if depths of both pixels are close enough and sampling point should affect our center pixel
 		float fRangeIsValid = saturate(((fSceneDepthP - fSceneDepthS) / fSceneDepthS));
 		
 		//acumulate accessibility use default value of 0.5 if right computations are not possible
 		Accessibility += lerp(fSceneDepthS > vSamplePos.z, 0.5, fRangeIsValid);
 	}
 	
 	// get Average value 
 	Accessibility /= nSamplesNum;
 	
 	//amplify if necessary
 	float ssao = saturate(Accessibility * Accessibility + Accessibility);
 	ssao = (saturate(ssao + 0.25) - 0.25) / (1 - 0.25);
 	
 	/*
 	float blendStep = step(0.5, Accessibility);
 	float ssao = 
 		blendBreak * pow(2 * Accessibility,blendPower) * (1 - blendStep)  + 
		(1 - (1 - blendBreak) * pow(2 * (1 - Accessibility),blendPower)) * blendStep;
 	*/
 	oColor = float4(ssao,ssao,ssao,1);
 	
 }
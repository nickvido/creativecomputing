// scene depth target containing  normalized from 0 to 1 depth
sampler2D sSceneDepthSampler;

// scene depth target containing  normalized from 0 to 1 depth
sampler2D sSSAOSampler;
sampler2D sColorSampler;

float2 screenSize;
float saturation;

void main(
	in float2 screenTC : TEXCOORD0,
	out float4 oColor : COLOR
){
 	float fSceneDepthP = 0;
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2(-1 / screenSize.x, -1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 0,                -1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 1 / screenSize.x, -1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 2 / screenSize.x, -1 / screenSize.y));
 	
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2(-1 / screenSize.x,  0));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 0,                 0));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 1 / screenSize.x,  0));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 2 / screenSize.x,  0));
 	
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2(-1 / screenSize.x,  1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 0,                 1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 1 / screenSize.x,  1 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 2 / screenSize.x,  1 / screenSize.y));
 	
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2(-1 / screenSize.x,  2 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 0,                 2 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 1 / screenSize.x,  2 / screenSize.y));
 	fSceneDepthP += tex2D(sSSAOSampler, screenTC + float2( 2 / screenSize.x,  2 / screenSize.y));
 	fSceneDepthP /= 16;
 	fSceneDepthP = (saturate(fSceneDepthP + saturation)-saturation) / (1 - saturation);
 	
 	float4 color = tex2D(sColorSampler, screenTC) * fSceneDepthP;
 	oColor = color;//float4(fSceneDepthP,fSceneDepthP,fSceneDepthP,1);
 	//oColor = tex2D(sSSAOSampler,screenTC);
}
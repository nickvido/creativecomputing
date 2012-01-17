const float PI = 3.14159265;
const float TWOPI = 6.28318531;

uniform sampler2D colorMap;
uniform float radius1;
uniform float radius2;
uniform float noiseScale;
uniform float noiseOffset;

vec4 sphere(in vec2 uv) {

	uv.x *= TWOPI;
	uv.y *= PI;
	vec4 pSphere;
	pSphere.x = cos(uv.y) * sin(uv.x);
	pSphere.y = sin(uv.y) * sin(uv.x);
	pSphere.z = cos(uv.x);
	pSphere.w = 1.0;
	
	
	float height = noise1(vec4(pSphere.xyz,noiseOffset) * noiseScale);
	float radius = mix(radius1, radius2,height);
	pSphere.xyz *= radius;
	
	return pSphere;
}

void main(){
	vec2 uv = gl_Vertex.xy;
	vec4 spherePos = sphere(uv);
	
	gl_Position = gl_ModelViewProjectionMatrix * spherePos; 
	
	gl_TexCoord[0] = vec4(uv.xy,0,0); 
}
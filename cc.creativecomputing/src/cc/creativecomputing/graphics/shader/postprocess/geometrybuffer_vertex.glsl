#version 120

varying vec4 pos;
varying vec3 normal;
varying float depth;

varying vec4 albedo;
varying vec4 diffuse;
varying vec4 specular;
   
uniform float near;
uniform float far;

void main(){
	pos	= gl_ModelViewMatrix * gl_Vertex;
	normal = normalize(gl_NormalMatrix * gl_Normal);
	depth = 1-(-pos.z - near) / (far - near);

	albedo = gl_FrontMaterial.diffuse;
	diffuse	= gl_FrontMaterial.diffuse;
	specular = gl_FrontMaterial.specular;
	specular.a = gl_FrontMaterial.shininess;
	//albedo = gl_FrontMaterial.emission + (gl_LightModel.ambient * gl_FrontMaterial.ambient);  \n"

	gl_Position  = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_FrontColor = gl_Color;
}
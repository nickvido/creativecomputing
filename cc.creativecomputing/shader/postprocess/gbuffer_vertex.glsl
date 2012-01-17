varying vec4 position;
varying vec3 normal;
varying float depth;

uniform float near;
uniform float far;

void main(){
    position      = gl_ModelViewMatrix * gl_Vertex;
    normal        = normalize( gl_NormalMatrix * gl_Normal );
    depth         = (-position.z-near)/(far-near);
    gl_Position   = position;
    gl_FrontColor = gl_Color;
}
varying vec4 position;
varying vec3 normal;
varying float depth;

void main(){
    gl_FragData[0] = gl_Color;
    gl_FragData[1] = position;
    gl_FragData[2] = vec4(normal * 0.5f + 0.5f, depth);
}
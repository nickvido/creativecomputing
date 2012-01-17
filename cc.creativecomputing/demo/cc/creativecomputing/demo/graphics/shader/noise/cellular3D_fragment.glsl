
varying vec3 vTexCoord3D;

void main(void) {
	vec2 F = cellular(vTexCoord3D.xyz);
	float n = F.y-F.x;
	gl_FragColor = vec4(n, n, n, 1.0);
}

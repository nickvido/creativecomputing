
varying vec2 vTexCoord2D;

void main(void) {
	vec2 F = cellular2x2(vTexCoord2D.xy);	
	float n = 1.0-1.5*F.x;
	gl_FragColor = vec4(n, n, n, 1.0);
}

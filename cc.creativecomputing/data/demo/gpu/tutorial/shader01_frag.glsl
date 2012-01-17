uniform sampler2D colorMap;
uniform sampler2D alphaMap;

void main(){
	vec4 alpha = texture2D(alphaMap, gl_TexCoord[1].xy);
	gl_FragColor = texture2D(colorMap, gl_TexCoord[1].xy);
	gl_FragColor.w = alpha.r;
}
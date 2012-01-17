uniform sampler2DRect data;
uniform sampler2DRect indices;
uniform float width;
uniform float height;

vec2 indexToTexCoord(float theIndex){
	return vec2(
		floor(mod(theIndex, width)),
		floor(theIndex / width)
	);
}

void main(){
    // get self
    float index = texture2DRect(indices, gl_TexCoord[0].xy).x;
    float sortKey = texture2DRect(data, indexToTexCoord(index)).x;
    
    gl_FragColor = vec4(sortKey,sortKey,sortKey,1);
}

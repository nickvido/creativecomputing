uniform sampler2DRect data;
uniform sampler2DRect indices;
uniform float oddPass;
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
    
    bool isOddIndex = bool(mod(index,2.0));

    float compare;

    // invert the compare if we're on the "odd" sorting pass
    if (isOddIndex){
		// self is odd -> compare with right key
		compare = oddPass;
    } else {
		// self is even -> compare with left key
		compare = -oddPass;
	}
    
    // correct the special case that the "odd" pass copies the first and the last key
    if (oddPass > 0.0 && ( index == 0.0 ||  index == (width * height)-1.0)){
		// must copy -> compare with self
		compare = 0.0;
	}

    // get the partner
    float partnerIndex = texture2DRect(indices, indexToTexCoord(index + compare)).x;
    float partnerSortKey = texture2DRect(data, indexToTexCoord(partnerIndex)).x;

    // on the left its a < operation, on the right its a >= operation
    float resultIndex = (sortKey * compare < partnerSortKey * compare) ? index : partnerIndex;
    
    gl_FragColor = vec4(resultIndex,0,0,0);
    
    //gl_FragColor = sortKey;
}

// $Id: solid.glshader,v 1.1 2007/03/23 13:50:50 dyken Exp $

sampler3D noise_tex;

varying float life;
varying vec2 tp;

void main()
{
	float r = max(1.0-dot(tp,tp),0.0)*(1.0-abs(texture3D(noise_tex, vec3(0.2*tp,0.2*life))));


//	gl_FragColor = vec4(1.0);
//gl_SecondaryColor.rgb-vec3(0.5)
	gl_FragColor = vec4(1.0, 0.2+ 0.3*clamp(life-2.0,0.0,1.0), 0.1, min(life,0.1*r) );
}

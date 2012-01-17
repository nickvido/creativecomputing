/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */

sampler3D noise_tex;

varying in vec3 indir[1];
varying in vec2 ininfo[1];

varying out vec3 pos;
varying out vec3 dir;
varying out vec2 info;

void main()
{
	pos = gl_PositionIn[0]+0.005*indir[0] + 0.0008*max(ininfo[0].x,0.1)*texture3D( noise_tex, gl_PositionIn[0].xyz);
	dir = 0.98*indir[0] + vec3(0.0,0.03,0.0);
	info = ininfo[0]-vec2(0.05,0.0);
	if(info.x > 0.0) {
		EmitVertex();
	}
}

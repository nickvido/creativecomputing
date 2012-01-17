/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */

sampler3D noise_tex;

varying in vec3 indir[1];
varying in vec2 ininfo[1];

varying out float life;
varying out vec2 tp;

void main()
{
	life = ininfo[0].x;
	
	float size = 0.01 + 0.01*max(2.0-2.0*ininfo[0].x,0.0);
	float ysize = max(5.0-life,1.0)*size;



	tp = vec2(-1,-1);
	gl_Position = gl_ProjectionMatrix*vec4( gl_PositionIn[0]+vec3(-size,-ysize,0.0),1.0 );
	EmitVertex();
	tp = vec2(-1, 1);
	gl_Position = gl_ProjectionMatrix*vec4( gl_PositionIn[0]+vec3(-size, ysize,0.0),1.0 );
	EmitVertex();
	tp = vec2( 1,-1);
	gl_Position = gl_ProjectionMatrix*vec4( gl_PositionIn[0]+vec3( size,-ysize,0.0),1.0 );
	EmitVertex();
	tp = vec2( 1, 1);
	gl_Position = gl_ProjectionMatrix*vec4( gl_PositionIn[0]+vec3( size, ysize,0.0),1.0 );
	EmitVertex();
}

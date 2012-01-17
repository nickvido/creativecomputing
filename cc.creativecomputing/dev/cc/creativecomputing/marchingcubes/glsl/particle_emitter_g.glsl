/* $Id: vertexpassthrough.glshader,v 1.1 2007/03/08 21:23:34 dyken Exp $ */
//#extension GL_EXT_geometry_shader4 : enable
//#version 120

sampler3D noise_tex;
uniform int frame_rand;
uniform float threshold;

varying in vec3 indir[3];

varying out vec3 pos;
varying out vec3 dir;
varying out vec2 info;



void main()
{
	unsigned int key = gl_PrimitiveIDIn + frame_rand;
	
	vec4 mynoise = texelFetch3D( noise_tex, ivec3( key&0xf, (key>>4)&0xf, (key>>8)&0xf ), 0 );
	
	float area = (mynoise.z+1.2);//*length( cross( gl_PositionIn[1].xyz-gl_PositionIn[0].xyz,gl_PositionIn[2].xyz-gl_PositionIn[0].xyz ) );

	if(area > threshold ) {
		float baryx = 0.5*(mynoise.x+1.0);
		float baryy = 0.5*(mynoise.y+1.0);
		float baryz = 1.0-baryx-baryy;
		gl_FrontColor = vec4(1.0, 1.0, 1.0,1.0);
		pos = baryx*gl_PositionIn[0].xyz+
			  baryy*gl_PositionIn[1].xyz+
			  baryz*gl_PositionIn[2].xyz;
		dir = 0.2*normalize( baryx*indir[0]+
			 			 baryy*indir[1]+
						 baryz*indir[2] );
		info = vec2(1.5 + 3.0*(mynoise.w+1.0), 0.0);
		gl_Position = gl_ProjectionMatrix*(vec4(pos,1.0));
		EmitVertex();
	}
}

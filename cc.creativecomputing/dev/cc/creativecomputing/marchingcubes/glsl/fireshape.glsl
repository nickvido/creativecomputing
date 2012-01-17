/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */

uniform sampler3D noise_tex;


#ifndef FLAT_SHADING
varying vec3 Normal;
#endif

varying in vec3 ms_normal;
varying in vec3 ms_position;
varying in vec3 cs_normal;
varying in vec3 cs_position;

float
bnoise( vec3 p )
{
	return 1.00*texture3D( noise_tex, 1.00*p ).r+
		   0.65*texture3D( noise_tex, randrot0*2.11*p ).r+
		   0.25*texture3D( noise_tex, randrot1*4.97*p ).r;
}


float
noise( vec3 p )
{
	return 1.00*texture3D( noise_tex, 1.00*p ).r+
		   0.65*texture3D( noise_tex, randrot0*2.11*p ).r+
		   0.35*texture3D( noise_tex, randrot1*4.97*p ).r+
		   0.25*texture3D( noise_tex, randrot2*8.11*p ).r+
		   0.10*texture3D( noise_tex, randrot3*16.11*p ).r;
}

vec4
noise4( vec3 p )
{
	return 1.00*texture3D( noise_tex, 1.00*p ).rgba+
		   0.65*texture3D( noise_tex, randrot0*2.11*p ).rgba+
		   0.25*texture3D( noise_tex, randrot1*4.97*p ).rgba+
		   0.15*texture3D( noise_tex, randrot2*8.11*p ).rgba;
}

vec4
turb4( vec3 p )
{
	return 1.00*abs(texture3D( noise_tex, 1.00*p )).rgba+
		   0.65*abs(texture3D( noise_tex, randrot0*2.11*p )).rgba+
		   0.25*abs(texture3D( noise_tex, randrot1*4.97*p )).rgba+
		   0.15*abs(texture3D( noise_tex, randrot2*8.97*p )).rgba+
		   0.10*abs(texture3D( noise_tex, randrot3*16.11*p )).rgba;
}

float
turb( vec3 p )
{
	return 
	1.00*abs(texture3D( noise_tex, 1.00*p ).r)+
    0.65*abs(texture3D( noise_tex, randrot0*2.11*p ).r)+
    0.25*abs(texture3D( noise_tex, randrot1*4.97*p ).r)+
    0.15*abs(texture3D( noise_tex, randrot2*8.11*p ).r);
}


float
heightfield( vec3 p )
{
	return clamp(1.0 + 1.4*bnoise(1.1*p)- abs( noise(3.1*p) ), 0.0, 1.0);
}

void main()
{
	vec3 pos = ms_position.xyz;
	vec3 nor = normalize(cs_normal);

	float away = 1.0-abs( nor.z )+0.00001*length(nor.xy)+0.01*length(cs_position)+0.01*length(ms_normal);

	vec4 mynoise = turb4(1.3*pos) + 0.3*turb4(7*pos);

	gl_FragColor.rgb = vec3( 0.2*mynoise.x + 0.3+pow(away,1.2),
							 0.1*mynoise.y + pow(away,1.8),
							 0.1*mynoise.z + pow(away,7.0) );


}

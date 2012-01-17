/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */

#ifndef FLAT_SHADING
varying vec3 cs_normal;
#endif

void main()
{
	vec3 l1 = normalize(vec3(1,1,3));
	vec3 h1 = normalize( l1+vec3(0,0,1) );
	vec3 l2 = normalize(vec3(-1,-1,-2.0));
	vec3 h2 = normalize( l2+vec3(0,0,1) );


#ifdef FLAT_SHADING
	vec3 n = 2.0*( gl_SecondaryColor.rgb-vec3(0.5) );
#else
	vec3 n = normalize( cs_normal ); // - (1.0-dir)*Normal );
#endif
	vec4 diffuse = max( dot(l1, n), 0.0) * vec4(0.5,0.5,1.0,0.0) +
				   max( dot(l2, n), 0.0) * vec4(1.0,0.0,0.0,0.0);
	vec4 specular = pow(max( dot(h1, n), 0.0), 20.0) *	vec4(0.8, 0.9, 1.0, 0.0) +
					pow(max( dot(h2, n), 0.0), 40.0) *	vec4(1.0, 0.5, 0.0, 0.0) ;
	gl_FragColor = diffuse + specular;
}

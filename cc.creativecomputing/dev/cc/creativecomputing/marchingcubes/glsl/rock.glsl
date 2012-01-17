// $Id: phong.glshader,v 1.1 2007/03/23 13:50:50 dyken Exp $
uniform sampler3D noise_tex;

varying vec3 ms_normal;
varying vec3 ms_position;
varying vec3 ms_tangent;
varying vec3 ms_cotangent;


varying vec3 cs_nor;
varying vec3 cs_pos;

uniform vec3 cs_msx;
uniform vec3 cs_msy;
uniform vec3 cs_msz;
uniform vec3 ms_eye;

uniform mat3 normal_matrix;

#if 1

vec4
turb4( vec3 p )
{
	return 
	1.00*abs(texture3D( noise_tex, 1.00*p ))+
    0.65*abs(texture3D( noise_tex, randrot2*2.11*p ))+
    0.25*abs(texture3D( noise_tex, randrot1*4.97*p ))+
    0.15*abs(texture3D( noise_tex, randrot2*8.11*p ));
}


float
depthField( vec3 p )
{
	vec3 _p = fract( 20.0*p +
					 0.5*texture3D(noise_tex, 1.13*p ) +
					 0.25*texture3D( noise_tex, randrot0*(2.3*p) ) )-
					 vec3(0.50)
					 ;
	return smoothstep(0.0,1.0,1.0-3.0*dot(_p,_p) );
}


void main()
{
	float bumpsize = 0.015;
	float texdelta = 0.005;
	float midlevel = 0.15;
	vec3 l1 = normalize(vec3(1,1,2));
	vec3 h1 = normalize( l1+vec3(0,0,1) );
	





	// --- create orthonormal tangent-space basis
	vec3 normal    = normalize(ms_normal);
	vec3 tangent   = normalize(ms_tangent   - dot (ms_tangent,normal)*normal);
	vec3 cotangent = normalize(ms_cotangent - dot(ms_cotangent,normal)*normal);


	vec3 viewdir = normalize( ms_position - ms_eye );
	float desc = dot(viewdir, normal );

	// --- ray enters into material (otherwise normal is wrong way...)

	vec3 texpos = ms_position;
	float h = depthField( texpos );

#ifdef DISPLACEMENT_MAPPING
	if( desc < 0.0 ) {

		// view direction projected onto the surface

		vec3 texdir = viewdir - desc*normal;
		vec2 foo = mix( vec2(0.0001,150), vec2(0.01,10), desc*desc);

//		float scale = 0.001;


		desc *= foo.x/bumpsize;
		texdir *= foo.x;
		float depth = 1.0;//bumpsize;
		for(int i=0; i<foo.y && depth > h; i++) {
			texpos += texdir;
			depth += desc;
			h = depthField( texpos );
		}
		texdir *= 0.1;
		desc *= 0.1;
		for(int i=0; i<foo.y && depth < h; i++) {
			texpos -= texdir;
			depth -= desc;
			h = depthField( texpos );
		}
	}

#endif

	float du = (bumpsize/texdelta)*(depthField( texpos + texdelta*tangent )-h);
	float dv = (bumpsize/texdelta)*(depthField( texpos + texdelta*cotangent )-h);

	vec3 n = normalize( normal_matrix * cross(tangent+du*normal, cotangent+dv*normal) );

	float wb = max(1.0-(1.0/midlevel)*h,0.0);
	float wh = max((1.0/(1.0-midlevel))*h-midlevel,0.0);
	float wm = 1.0-wb-wh;

	vec3 difcolb = vec3(0.5,0.5,0.4)+0.1*turb4(3.1*texpos).rgb;
	vec3 difcolm = vec3(0.7,0.5,0.4);
	vec3 difcolh = vec3(0.8,0.8,0.6);

	vec3 diffusecol = wb*difcolb
					+ wm*difcolm
					+ wh*difcolh;

	vec3 speccol = mix(diffusecol, vec3(1,1,1), 0.7);

//	diffusecol = vec3(0.8,0.8,0.8);
//	diffusecol.r = h;



	vec3 lightdir = normalize( l1 - ms_eye );




//	gl_FragColor.rgb = vec3( 100.0*h, 0.5, 0.5 );

	gl_FragColor.rgb = diffusecol*max(dot(n,l1),0.3)
					 + (0.5*wm+wh)*speccol*pow(max(dot(n,h1),0.0),20.0);
}


#else





#ifndef FLAT_SHADING
varying vec3 Normal;
#endif


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
	vec3 pos = ms_pos.xyz;
	vec3 nor = cs_nor;

	vec3 l1 = normalize(vec3(1,1,2));
	vec3 h1 = normalize( l1+vec3(0,0,1) );
	vec3 l2 = normalize(vec3(-1,-1,0));
	vec3 h2 = normalize( l2+vec3(0,0,1) );

	
//	float dir = float( dot(Normal, vec3(0,0,1) ) >= 0 );

#ifdef FLAT_SHADING
	vec3 n = 2.0*( gl_SecondaryColor.rgb-vec3(0.5) );
#else
	vec3 n = normalize( cs_nor ); // - (1.0-dir)*Normal );
#endif



	vec3 ms_pnor = normalize(ms_nor);
	vec3 ms_viewvec = normalize(ms_pos - ms_eye);
	float ms_desc = dot(ms_viewvec, ms_pnor);
	vec3 ms_planevec = ms_viewvec - ms_desc*ms_pnor;

	vec3 hfpos = ms_pos.xyz;

	float height = heightfield( hfpos );
	float rayh = 0.00;
	if( ms_desc > 0.0 ) {
		for(int i=0; i<20 && rayh < 0.02*height; i++) {
			hfpos -= 0.001*ms_planevec;
			rayh  += 0.001*ms_desc;
			height = heightfield( hfpos );
		}
		for(int i=0; i<20 && height < 0.02*rayh; i++) {
			hfpos += 0.0001*ms_planevec;
			rayh  -= 0.0001*ms_desc;
			height = heightfield( hfpos );
		}

	}

	vec3 dheight = (0.45)*(vec3( heightfield( hfpos + vec3(0.01, 0.0, 0.0) ),
								 heightfield( hfpos + vec3(0.0, 0.01, 0.0) ),
								 heightfield( hfpos + vec3(0.0, 0.0, 0.01) ) )- vec3(height));

	vec3 projaxis = vec3( dot(n, cs_msx), dot(n, cs_msy), dot(n,cs_msz) );
	projaxis = normalize( projaxis*projaxis );


/*

	n = normalize(n - cs_msx*dheight.x*(projaxis.y+projaxis.z)
					- cs_msy*dheight.y*(projaxis.x+projaxis.z)
					- cs_msz*dheight.z*(projaxis.x+projaxis.y) );

*/
	vec3 chooser = normalize(nor*nor);

	n = normalize( n+ chooser.x*vec3(0.0,dheight.y,dheight.z)
				    + chooser.y*vec3(dheight.x,0.0,dheight.z)
				    + chooser.z*vec3(dheight.x,dheight.y,0.0) );

	float plateu = min(10.0*max(height-0.9,0.0),1.0);
	
	vec4 somenoise = noise4( 0.81*pos );
	
	
	
	vec3 diffcolor = mix(vec3(0.7, 0.7, 0.8 ),vec3(0.8,0.8,0.8)+0.05*somenoise.xyz,plateu);
	vec3 speccolor = vec3(1.0, 1.0, 1.0 );

//	diffcolor.r = height;

	float grace = 1.5*pow(1.0-max(n.z,0.0),1.2);

	vec3 diffuse  = (max(0.3x,dot(n,l1))*vec3(1.0,1.0,0.7)/*+
					 max(0.0,dot(n,l2))*vec3(0.3,0.3,0.8)*/)*diffcolor;
	vec3 specular = ((0.5+grace)*pow(max(0.0,dot(n,h1)),30.0+20.0*(plateu))*vec3(1.0,1.0,1.0) /*+
					 pow(max(0.0,dot(n,h2)),40)*vec3(0.7,0.7,1.0)*/ )*speccolor;


	gl_FragColor.rgb = diffuse + specular;

/*

	vec2 tp = n.x*pos.yz + n.y*pos.xz + n.z*pos.xy;



	float rock = rocksample( 3.3*pos );
	float rockx= rocksample( 3.3*pos+vec3(0.005,0.0,0.0) )-rock;
	float rocky= rocksample( 3.3*pos+vec3(0.0,0.005,0.0) )-rock;
	float rockz= rocksample( 3.3*pos+vec3(0.0,0.0,0.005) )-rock;


	float rockyness = clamp( 4.0*noise(1.3*pos), 0.0, 1.0);

	vec3 nx = n - rockyness*vec3(0.0, rocky, rockz );
	vec3 ny = n - rockyness*vec3(rockx, 0.0, rockz );
	vec3 nz = n - rockyness*vec3(rockx, rocky , 0.0);

	n = normalize(chooser.x*nx + chooser.y*ny + chooser.z*nz);

	float ridge = rockyness*clamp(rock-0.8, 0.0, 1.0);
	float deep = rockyness*(1.0-clamp(1.6*rock,0.0, 1.0));
	float mid = 1.0 - deep - ridge;

	vec3 diffcol = vec3(0.5,0.55+0.1*noise(8.0*pos),0.5)*deep
				 + vec3(0.5,0.5,0.5)*mid
				 + vec3(0.6,0.5,0.5)*ridge;

	diffcol += (1.0-rockyness)*0.05*noise3(1.1*pos);

	vec3 speccol = vec3(0.5,0.55,0.5)*deep
				 + vec3(0.5,0.5,0.5)*mid
				 + vec3(1.0,1.0,0.9)*ridge;


	float spec = 10*deep
			   + 20*mid
			   + 40*ridge;


	vec3 diffuse = max( dot(l1, n), 0.2)*diffcol;
	
//	vec4 diffuse = max( dot(l1, n), 0.2) * mix(vec4(0.0,0.7,0.7,1.0),vec4(0.8,0.8,0.8,1.0), tp.x );
//				   max( dot(l2, n), 0.0) * vec4(1.0,0.0,0.0,0.0);
//	vec4 diffuse = max( dot(l1, n), 0.0) * vec4(0.5,0.5,1.0,0.0) +
//				   max( dot(l2, n), 0.0) * vec4(1.0,0.0,0.0,0.0);
	vec3 specular = pow(max( dot(h1, n), 0.0), 20+50*rockyness) *speccol;
*/

}

#endif
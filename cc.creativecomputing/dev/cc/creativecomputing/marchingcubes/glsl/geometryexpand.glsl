/* $Id: expand_gs.glshader,v 1.9 2007/02/16 15:25:39 dyken Exp $ */
#extension GL_EXT_gpu_shader4 : enable
#extension GL_EXT_geometry_shader4 : enable
#version 120

#ifdef USE_TEX3D
uniform sampler3D function_tex;
#else
uniform sampler2D function_tex;
#endif
uniform sampler2D hp_tex;
uniform sampler2D tritable_tex;
uniform float key_off;
uniform float threshold;

varying vec3 ms_position;
varying vec3 ms_normal;
varying vec3 ms_tangent;
varying vec3 ms_cotangent;

varying vec3 cs_position;
varying vec3 cs_normal;


float sample( vec3 p )
{
#ifdef USE_TEX3D
	p.z = (p.z+0.5)*(1.0/FUNC_SLICES);
	return texture3D( function_tex, p ).a;
#else
	vec2 tp = vec2(1.0/FUNC_COLS, 1.0/FUNC_ROWS)*fract(p.xy);
	float slice = p.z;
	
	float col = fract((1.0/FUNC_COLS)*slice);
	float row = (1.0/FUNC_ROWS)*floor((1.0/FUNC_COLS)*slice);
	return texture2D( function_tex, tp + vec2(col,row) ).a;
#endif
}

void
main()
{
	float key_ix = gl_PositionIn[0].x + key_off;
	gl_FrontColor = gl_FrontColorIn[0];

/*
	vec2 texpos = vec2(0.5);
	vec4 delta_x = vec4( -0.5,  0.5, -0.5, 0.25 );
	vec4 delta_y = vec4(  0.0, -0.5,  0.0, 0.25 );
	for(int i=HP_MIPS; i>0; i--) {
		vec4 sums = texture2DLod( hp_tex, texpos, i );
		vec4 hist = sums;
		hist.w   += hist.z;
		hist.zw  += hist.yy;
		hist.yzw += hist.xxx;
		vec4 mask = vec4( lessThan( vec4(key_ix), hist ) );
		texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
		key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
		delta_x  *= 0.5;
		delta_y  *= 0.5;
	}

*/
	vec2 texpos = vec2(0.5);
	vec4 delta_x = vec4( -0.5,  0.5, -0.5, 0.25 );
	vec4 delta_y = vec4(  0.0, -0.5,  0.0, 0.25 );

	vec4 sums,hist,mask;
#ifdef FORCE_UNROLL
#ifdef HP_MIPS_11
	sums = texture2DLod( hp_tex, texpos, 11 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_10
	sums = texture2DLod( hp_tex, texpos, 10 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_9
	sums = texture2DLod( hp_tex, texpos, 9 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_8
	sums = texture2DLod( hp_tex, texpos, 8 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_7
	sums = texture2DLod( hp_tex, texpos, 7 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_6
	sums = texture2DLod( hp_tex, texpos, 6 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_5
	sums = texture2DLod( hp_tex, texpos, 5 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_4
	sums = texture2DLod( hp_tex, texpos, 4 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_3
	sums = texture2DLod( hp_tex, texpos, 3 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_2
	sums = texture2DLod( hp_tex, texpos, 2 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#ifdef HP_MIPS_1
	sums = texture2DLod( hp_tex, texpos, 1 );
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	delta_x  *= 0.5;
	delta_y  *= 0.5;
#endif
#else
	for(int i=HP_MIPS; i>0; i--) {
		vec4 sums = texture2DLod( hp_tex, texpos, i );
		vec4 hist = sums;
		hist.w   += hist.z;
		hist.zw  += hist.yy;
		hist.yzw += hist.xxx;
		vec4 mask = vec4( lessThan( vec4(key_ix), hist ) );
		texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
		key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
		delta_x  *= 0.5;
		delta_y  *= 0.5;
	}
#endif
	vec4 raw  = texture2DLod( hp_tex, texpos, 0 );
	sums = floor(raw);
	hist = sums;
	hist.w   += hist.z;
	hist.zw  += hist.yy;
	hist.yzw += hist.xxx;
	mask = vec4( lessThan( vec4(key_ix), hist ) );
	float nib = dot(vec4(mask), vec4(-1.0,-1.0,-1.0, 3.0));
	texpos   += vec2( dot( mask, delta_x ), dot( mask, delta_y ) );
	key_ix   -= dot( sums.xyz, vec3(1.0)-mask.xyz );
	float val = fract( dot( raw, vec4(equal(vec4(nib),vec4(0,1,2,3))) ) );


	vec2 foo = vec2(HP_COLS,HP_ROWS)*texpos;
	vec2 tp = fract(foo);
	float slice = dot( vec2(1.0,HP_COLS), floor(foo));

	int count = int(texture2D( tritable_tex, vec2( 0.5/16.0, val ) ).a);
	int foobar = 0;
	for(int i=1; i<=count; i++) {
		vec4 edge = texture2D( tritable_tex, vec2((1.0/16.0)*(i+0.5), val ) );

#ifdef PACK_TABLE
		float foo = abs(edge.w);

		vec3 shift = vec3(greaterThanEqual(fract(foo*vec3(1.0/8.0,1.0/4.0,1.0/2.0)), vec3(0.5)));
		vec3 axis = vec3(equal(vec3(4/8.0,2/8.0,1/8.0),vec3(fract(foo))));
		vec3 p = vec3(tp, slice)
			   + vec3(1.0/FUNC_TSIZE, 1.0/FUNC_TSIZE, 1.0)*shift;
			   + 0.5*vec3(1.0/FUNC_TSIZE, 1.0/FUNC_TSIZE, 1.0)*axis;
		vec3 n = edge.xyz;
#else
		vec3 shift = edge.xyz;
		vec3 axis = vec3(equal(vec3(1.0, 2.0, 3.0), vec3(abs(edge.w))));

		vec3 pa = vec3(tp, slice)
				+ vec3(1.0/FUNC_TSIZE, 1.0/FUNC_TSIZE, 1.0)*shift;
		vec3 pb = pa
				+ vec3(1.0/FUNC_TSIZE, 1.0/FUNC_TSIZE, 1.0)*axis;

		vec4 slices = (1.0/FUNC_COLS)*(vec4(slice)+vec4(shift.z)+vec4(0,1,axis.z,1+axis.z));
		vec4 slicex = fract( slices );
		vec4 slicey = (1.0/FUNC_ROWS)*floor(slices);
	
		vec4 tpx = (1.0/FUNC_COLS)*(vec4(pa.xx,pb.xx) + vec4(0.0,1.0/FUNC_TSIZE,0.0,1.0/FUNC_TSIZE));
		vec4 tpy = (1.0/FUNC_ROWS)*(vec4(pa.yy,pb.yy) + vec4(0.0,1.0/FUNC_TSIZE,0.0,1.0/FUNC_TSIZE));

		vec3 texposx = slicex.xxy + tpx.xyx;
		vec3 texposy = slicey.xxy + tpy.xyx;
		float va  = texture2D( function_tex, vec2(texposx.x,texposy.x) ).a;
		vec3 na = vec3( texture2D( function_tex, vec2(texposx.y,texposy.x) ).a,
						texture2D( function_tex, vec2(texposx.x,texposy.y) ).a,
						texture2D( function_tex, vec2(texposx.z,texposy.z) ).a );

		texposx = slicex.zzw + tpx.zwz;
		texposy = slicey.zzw + tpy.zwz;
		vec3 nb = vec3( texture2D( function_tex, vec2(texposx.y,texposy.x) ).a,
						texture2D( function_tex, vec2(texposx.x,texposy.y) ).a,
						texture2D( function_tex, vec2(texposx.z,texposy.z) ).a );

		float t = (va-threshold)/(va-dot(na,axis));
		vec3 p = mix(pa, pb, t );
		vec3 n = mix(na, nb,t)-vec3(threshold);
#endif
		p.z *= 1.0/FUNC_SLICES;
		n = normalize( -n );
		// --- make approximate tangent space basis (will be made orthonormal in fragment shader)
		vec3 w = n*(1.0/(dot(vec3(1.0),abs(n))));
		ms_tangent   = abs(w.x) * vec3(0,0,-1) + abs(w.y) * vec3(0,0,-1) + abs(w.z) * vec3(1,0,0);
		ms_cotangent =     w.x  * vec3(0,1, 0) +     w.y  * vec3(-1,0,0) +     w.z  * vec3(0,1,0);
		ms_normal = n;
		ms_position = p;
		cs_normal = gl_NormalMatrix * n;
		cs_position = (gl_ModelViewMatrix * vec4(p,1.0)).xyz;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(p,1.0);
#ifdef FLAT_SHADING
		gl_FrontSecondaryColor.rgb = 0.5*normalize(-gl_NormalMatrix * n)+vec3(0.5);//gl_Normal
#endif
		EmitVertex();
		if(edge.w < 0)
			EndPrimitive();

	}
}

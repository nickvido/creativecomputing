/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#include <iostream>
#include <cmath>
#include <sstream>
#include <string>
#include <vector>
#include <algorithm>
#include <boost/lexical_cast.hpp>
#include <Util/Timer.h>
#include <GL/glew.h>
#include <IO/SnarfFile.h>
#include <GfxMath/VecOperations.h>
#include <GfxMath/MatOperations.h>
#include <GfxMath/MatTransforms.h>
#include "GLMarcher.h"
#include "CheckedGLCalls.h"
#include "TriangleTable.h"

namespace dyken {
	namespace Marching {
		using std::cerr;
//		using std::copy_n;
		using std::vector;
		using std::string;
		using std::stringstream;
		using boost::lexical_cast;
		using GLUtil::getSingleInteger;
		using GLUtil::checkFramebufferStatus;
		using GLUtil::compileShader;
		using GLUtil::linkProgram;
		using GLUtil::getUniformLocation;
		using IO::snarfFile;

		using GfxMath::Mat4f;
		using GfxMath::Vec3f;
		using GfxMath::randomRotationMatrix;

#define EDGEPACK(A,B,C,D,E,F) ((((A)<<5)|((B)<<4)|((C)<<3)|(D)<<2|(E)<<1|(F)))
static unsigned int edgemasks[12] = {
	EDGEPACK(0,0,0,1,0,0), EDGEPACK(1,0,0,0,0,1),
	EDGEPACK(0,0,1,1,0,0), EDGEPACK(0,0,0,0,0,1),
	EDGEPACK(0,1,0,1,0,0), EDGEPACK(1,1,0,0,0,1),
	EDGEPACK(0,1,1,1,0,0), EDGEPACK(0,1,0,0,0,1),
	EDGEPACK(0,0,0,0,1,0), EDGEPACK(1,0,0,0,1,0),
	EDGEPACK(1,0,1,0,1,0), EDGEPACK(0,0,1,0,1,0)
};
#undef EDGEPACK

static GLfloat edges[12][4] = {
	{ 0.0, 0.0, 0.0, 0.0f }, { 1.0, 0.0, 0.0, 2.0f },
	{ 0.0, 0.0, 1.0, 0.0f }, { 0.0, 0.0, 0.0, 2.0f },
	{ 0.0, 1.0, 0.0, 0.0f }, { 1.0, 1.0, 0.0, 2.0f },
	{ 0.0, 1.0, 1.0, 0.0f }, { 0.0, 1.0, 0.0, 2.0f },
	{ 0.0, 0.0, 0.0, 1.0f }, { 1.0, 0.0, 0.0, 1.0f },
	{ 1.0, 0.0, 1.0, 1.0f }, { 0.0, 0.0, 1.0, 1.0f }
};



GLMarcher::GLMarcher( Config& config, bool use_geometry_shader )
: Technique( config ),
  m_use_geometry_shader( use_geometry_shader ),
  m_no_triangles( 0 ),
  m_no_frames( 0 )
{
	bool pack_table = false;

	int cols_log2 = (int)ceilf(log2(sqrt((double)m_config.m_function_slices)));
	int cols = 1<<cols_log2;
	int rows = (m_config.m_function_slices+cols+1)/cols;
	int tsize = 1<<m_config.m_function_tsize_log2;
	int func_w = tsize*cols;
	int func_h = tsize*rows;
	
	cerr << __func__ << ": Flat3D func tile size = [ " << tsize << " x " << tsize << " ]\n";
	cerr << __func__ << ": Flat3D func tiling = [ " << cols << " x " << rows << " ]\n";
	cerr << __func__ << ": Flat3D func size = [ " << func_w << " x " << func_h << " ]\n";


	glGenTextures( 1, &m_tex_function );

	GLint iformat;
	GLenum format;
	GLenum type;
	int stride;
	switch( m_config.m_function_format )
	{
	case Config::FUNC_UNSIGNED_BYTE:
		iformat = GL_ALPHA;
		format = GL_ALPHA;
		type = GL_UNSIGNED_BYTE;
		stride = sizeof(unsigned char);
		m_isovalue_scale = 1.0f/0xff;
		cerr << __func__ << ": Using unsigned byte function texture.\n";
		break;
	case Config::FUNC_UNSIGNED_SHORT:
		if(! GLEW_EXT_texture_integer ) {
			cerr << __func__ << ": EXT_texture_integer not present.\n";
			exit( EXIT_FAILURE );
		}
		iformat = GL_ALPHA16;//16UI_EXT;
		format = GL_ALPHA;//_INTEGER_EXT;
		type = GL_UNSIGNED_SHORT;
		stride = sizeof(unsigned short);
		m_isovalue_scale = 1.0f/0xffff;
		cerr << __func__ << ": Using unsigned short function texture.\n";
		break;
	case Config::FUNC_FLOAT:
		if(! GLEW_ARB_texture_float ) {
			cerr << __func__ << ": ARB_texture_float not present.\n";
			exit( EXIT_FAILURE );
		}
		if( m_config.m_field_update_enable ) {
			// seems like that we only can render to an RGBA tex...?
			iformat = GL_RGBA32F_ARB;
			format = GL_RGBA;
		} else {
			iformat = GL_ALPHA32F_ARB;
			format = GL_ALPHA;
		}
		type = GL_FLOAT;
		stride = sizeof(float);
		m_isovalue_scale = 1.0f;
		cerr << __func__ << ": Using float function texture.\n";
		break;
	default:
		cerr << __func__ << ": Illegal format.\n";
		exit( EXIT_FAILURE );
	}
	cerr << __func__ << ": Function texture mem = " << (func_w*func_h*stride+1024*1024-1)/(1024*1024) << " Mb.\n";

	stringstream defines;


	// --- upload texture
	if(m_config.m_gl_use_tex3d) {
		defines << "#define USE_TEX3D\n";
		
		glBindTexture( GL_TEXTURE_3D, m_tex_function );
		glTexImage3D( GL_TEXTURE_3D, 0, iformat, 
					  m_config.m_function_tsize,
					  m_config.m_function_tsize,
					  m_config.m_function_slices,
					  0,
					  format, type, m_config.m_function_data_ );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glBindTexture( GL_TEXTURE_3D, 0 );
	} else {
		std::vector< unsigned char > zerobuf( func_w*func_h*4*4 );

		glBindTexture( GL_TEXTURE_2D, m_tex_function );
		glTexImage2D( GL_TEXTURE_2D, 0, iformat, func_w, func_h, 0, format, type, &zerobuf[0] );
		for(int s=0; s<m_config.m_function_slices; s++) {
			int ti = s % cols;
			int tj = s / cols;
			glTexSubImage2D( GL_TEXTURE_2D, 0,
							 tsize*ti, tsize*tj,
							 tsize, tsize,
							 format, type,
							 (unsigned char*)m_config.m_function_data_ + s*stride*tsize*tsize );

		}
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glBindTexture( GL_TEXTURE_2D, 0 );
	}
	CHECK_GL;

	// --- create Flat 3D tile table
	if(m_config.m_gl_vs_tile_table || m_config.m_gl_fs_tile_table || m_config.m_gl_gs_tile_table) {
		std::cerr << __func__ << ": Creating tile table.\n";
		std::vector<GLfloat> tile_table(4*(m_config.m_function_slices));
		for(int s=0; s<m_config.m_function_slices; s++) {
			tile_table[4*s+0] = (float)(s % cols)/(float)cols;
			tile_table[4*s+1] = (float)(s / cols)/(float)rows;
			tile_table[4*s+2] = (float)((s+1) % cols)/(float)cols;
			tile_table[4*s+3] = (float)((s+1) / cols)/(float)rows;
		}
		glGenTextures( 1, &m_tex_tile_table );
		glBindTexture( GL_TEXTURE_1D, m_tex_tile_table );
		glTexImage1D( GL_TEXTURE_1D, 0, GL_RGBA32F_ARB, m_config.m_function_slices, 0, GL_RGBA, GL_FLOAT, &tile_table[0] );
		glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glBindTexture( GL_TEXTURE_1D, 0 );
		if(m_config.m_gl_vs_tile_table)
			defines << "#define VS_TILE_TABLE 1\n";
		if(m_config.m_gl_fs_tile_table)
			defines << "#define FS_TILE_TABLE 1\n";
		if(m_config.m_gl_gs_tile_table)
			defines << "#define GS_TILE_TABLE 1\n";
	}	

	// --- create lookup tables
	std::vector<GLfloat> tricount(256);
	std::vector<GLfloat> edge_decode( 256*16*4 );
	for( int j=0; j<256; j++ ) {
		int count;
		for(count=0; count<16; count++) {
			if( triangle_table[j][count] == -1 ) {
				break;
			}
		}
		for(int i=0; i<16; i++) {
			for(int k=0; k<4; k++)
				edge_decode[4*16*remapCode(j) + 4*i+k ] = edges[ triangle_table[j][i] != -1 ? triangle_table[j][i] : 0 ][k];
		}
		tricount[ remapCode(j) ] = count;
	}

	glGenTextures( 1, &m_tex_tricount );
	glBindTexture( GL_TEXTURE_1D, m_tex_tricount );
	glTexImage1D( GL_TEXTURE_1D, 0, GL_ALPHA32F_ARB, 256, 0, GL_ALPHA, GL_FLOAT, &tricount[0] );	
	glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
	glTexParameteri( GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
	glBindTexture( GL_TEXTURE_1D, 0 );

	glGenTextures( 1, &m_tex_tritable );
	glBindTexture( GL_TEXTURE_2D, m_tex_tritable );

	if(m_use_geometry_shader) {
		if(m_config.m_extraction_fitp)
			createPackedStripTable( edge_decode );
		else {
			createPackedStripTableWithNormals( edge_decode );
			defines << "#define PACK_TABLE\n";
			defines << "#define FLAT_SHADING 1\n";
		}
		glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
	} else {
		if(m_config.m_extraction_fitp) {
			glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
		} else {
			createPackedTriangleTableWithNormals( edge_decode );
			glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
			defines << "#define PACK_TABLE\n";
		}
	}

	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
	glBindTexture( GL_TEXTURE_2D, 0 );

	// --- create histopyramid texture
	m_hp_size_log2 = cols_log2+m_config.m_function_tsize_log2-1;
	int hp_size = 1<<m_hp_size_log2;
	cerr << __func__ << ": HP size = [ " << hp_size << " x " << hp_size << " ]\n";

	glGenTextures( 1, &m_hp_tex );
	glBindTexture( GL_TEXTURE_2D, m_hp_tex );
	std::vector<GLfloat> zerobuf( hp_size*hp_size*4 );
//	std::vector<GLfloat> zerobuf( (func_w*func_h) );
	glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, hp_size, hp_size, 0, GL_RGBA, GL_FLOAT, &zerobuf[0] );
	glGenerateMipmapEXT( GL_TEXTURE_2D );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0 );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, m_hp_size_log2 );
	cerr << __func__ << " checkpoint " << __LINE__ << "\n";

	CHECK_GL;

	// --- create histopyramid framebuffers
	m_hp_framebuffers.resize( m_hp_size_log2+1 );
	glGenFramebuffersEXT( m_hp_size_log2+1, &m_hp_framebuffers[0] );
	for(int m=0; m<=m_hp_size_log2; m++) {
		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, m_hp_framebuffers[m] );
		glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT,
								   GL_COLOR_ATTACHMENT0_EXT,
								   GL_TEXTURE_2D,
								   m_hp_tex,
								   m );
		GLenum drawbuffers[1] = { GL_COLOR_ATTACHMENT0_EXT };
		glDrawBuffers( 1, &drawbuffers[0] );
		checkFramebufferStatus( __func__ );
	}
	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0 );

	// --- generate extraction vbo
	m_enumerate_vbo_size = 3*((1024*1024)/3);
	std::vector<GLfloat> dummy( m_enumerate_vbo_size*3 );
	for(int i=0; i<m_enumerate_vbo_size; i++)
		dummy[3*i] = i;
	glGenBuffers( 1, &m_enumerate_vbo );
	glBindBuffer( GL_ARRAY_BUFFER, m_enumerate_vbo );
	glBufferData( GL_ARRAY_BUFFER, sizeof(GLfloat)*dummy.size(), &dummy[0], GL_DYNAMIC_DRAW );
	glBindBuffer( GL_ARRAY_BUFFER, 0 );
	CHECK_GL;

	cerr << __func__ << ": enumerate vbo mem = " << (sizeof(GLfloat)*dummy.size()+1024*1024-1)/(1024*1024) << " Mb.\n";


	// --- create a string of commonly used defines
	if(m_use_geometry_shader)
		defines << "#define USE_GEOMETRY_SHADER 1 \n";
	if(m_config.m_gl_unroll) {
		defines << "#define FORCE_UNROLL 1\n";
		for(int i=0; i<=m_hp_size_log2; i++) {
			defines << "#define HP_MIPS_"<<i<<" 1\n";
		}
	}
	
	defines << "#define FUNC_COLS " << cols << "\n";
	defines << "#define FUNC_ROWS " << rows << "\n";
	defines << "#define FUNC_WIDTH " << func_w << "\n";
	defines << "#define FUNC_HEIGHT " << func_h << "\n";
	defines << "#define FUNC_TSIZE " << tsize << "\n";
	defines << "#define FUNC_SLICES " << m_config.m_function_slices << "\n";
	defines << "#define HP_COLS " << cols << "\n";
	defines << "#define HP_ROWS " << cols << "\n";


	defines << "#define HP_MIPS " << m_hp_size_log2 << "\n";



	m_gpgpu_passthru_v = compileShader( snarfFile( "glsl/vertexpassthrough.glshader" ), GL_VERTEX_SHADER );

	
	// --- create baselevel shader
	m_baselevel_f = compileShader( defines.str() + snarfFile( "glsl/hpbaselevel.glshader" ), GL_FRAGMENT_SHADER );
	m_baselevel_p = glCreateProgram();
	glAttachShader( m_baselevel_p, m_gpgpu_passthru_v );
	glAttachShader( m_baselevel_p, m_baselevel_f );
	linkProgram( m_baselevel_p );
	CHECK_GL;

	glUseProgram( m_baselevel_p );
	m_uniform_baselevel_threshold = getUniformLocation( m_baselevel_p, "threshold" );
	glUniform1i( getUniformLocation( m_baselevel_p, "tricount_tex" ), 2 );
	glUniform1i( getUniformLocation( m_baselevel_p, "function_tex" ), 1 );
	if(m_config.m_gl_fs_tile_table)
		glUniform1i( getUniformLocation( m_baselevel_p, "tiletable_tex" ), 3 );


	// --- first reduction shader
	m_reduce_base_f = compileShader( string("#define PRE_OP floor(\n") +
									 string("#define POST_OP )\n") +
									 snarfFile( "glsl/hpreduction.glshader" ),
									 GL_FRAGMENT_SHADER );
	m_reduce_base_p = glCreateProgram();
	glAttachShader( m_reduce_base_p, m_gpgpu_passthru_v );
	glAttachShader( m_reduce_base_p, m_reduce_base_f );
	linkProgram( m_reduce_base_p );
	CHECK_GL;

	glUseProgram( m_reduce_base_p );
	glUniform1i( getUniformLocation( m_reduce_base_p, "hp_tex" ), 0 );
	m_reduce_base_uniform_delta = getUniformLocation( m_reduce_base_p, "delta" );
	

	// --- rest of reduction shader	
	m_reduce_f = compileShader( snarfFile( "glsl/hpreduction.glshader" ),
								GL_FRAGMENT_SHADER );
	m_reduce_p = glCreateProgram();
	glAttachShader( m_reduce_p, m_gpgpu_passthru_v );
	glAttachShader( m_reduce_p, m_reduce_f );
	linkProgram( m_reduce_p );
	CHECK_GL;

	glUseProgram( m_reduce_base_p );
	glUniform1i( getUniformLocation( m_reduce_base_p, "hp_tex" ), 0 );
	m_reduce_uniform_delta = getUniformLocation( m_reduce_p, "delta" );

	// --- onscreen shader

	
	for(int k=0; k<5; k++) {
		Mat4f m = GfxMath::randomRotationMatrix( Vec3f( drand48(), drand48(), drand48()) );

		defines << "mat3 randrot"<<k<<" = { \n";
		for(int j=0; j<3; j++) {
			defines << "\t{ ";
			for(int i=0; i<3; i++) {
				defines << m.at(j,i) << (i==2 ? "" : ", ");
			}
			defines << "}" << (j==2 ? "" : ",") << "\n";
		}
		defines << "};\n";
	}
	m_defines = defines.str();
	std::cerr << m_defines << "\n";
	buildNoise();
	initialize();

	if(m_config.m_field_update_enable) {
		if(m_config.m_gl_use_tex3d)
			throw std::runtime_error( "Texture3D unsupported here." );

		if( m_config.m_field_update_type == Config::FIELD_UPDATE_ALGEBRAIC_MORPH ) {
			m_field_update_f = compileShader( m_defines + snarfFile( "glsl/amorph.glshader" ), GL_FRAGMENT_SHADER );
		} else if( m_config.m_field_update_type == Config::FIELD_UPDATE_METABALLS ) {
			m_field_update_f = compileShader( m_defines + snarfFile( "glsl/metaballs.glshader" ), GL_FRAGMENT_SHADER );
		} else {
			throw std::runtime_error( __func__ );
		}
		m_field_update_p = glCreateProgram();
		glAttachShader( m_field_update_p, m_gpgpu_passthru_v );
		glAttachShader( m_field_update_p, m_field_update_f );
		linkProgram( m_field_update_p );
		CHECK_GL;

		glGenFramebuffersEXT( 1, &m_field_update_fb );
		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, m_field_update_fb );

		std::cerr << m_tex_function << "\n";

		glFramebufferTexture2DEXT( GL_FRAMEBUFFER_EXT,
								   GL_COLOR_ATTACHMENT0_EXT,
								   GL_TEXTURE_2D, m_tex_function, 0 );
/*
		GLenum drawbuffers[1] = { GL_COLOR_ATTACHMENT0_EXT };
		glDrawBuffers( 1, &drawbuffers[0] );
*/
		checkFramebufferStatus( __func__ );
		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0 );
	}

	m_profile = config.m_profile;
}

void
GLMarcher::fieldInit()
{
}

void
GLMarcher::fieldUpdate()
{
}

void
GLMarcher::buildNoise()
{
	std::vector<GLfloat> mem(16*16*16*4);
	for(int i=0; i<16*16*16*4; i++) {
		mem[i] = static_cast<GLfloat>(rand())*(2.0/(RAND_MAX+1.0))-1.0;
	}

	glGenTextures( 1, &m_tex_noise );
	glBindTexture( GL_TEXTURE_3D, m_tex_noise );
	glTexImage3D( GL_TEXTURE_3D, 0, GL_RGBA16F_ARB,
				  16, 16, 16, 0,
				  GL_RGBA, GL_FLOAT, &mem[0] );
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT); 
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT); 
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
	glBindTexture( GL_TEXTURE_3D, 0 );
}

void
GLMarcher::initialize()
{
	m_onscreen_v = 0;
	m_onscreen_g = 0;
	m_onscreen_f = 0;
	m_onscreen_p = 0;

	m_particle_emitter_v = 0;
	m_particle_emitter_g = 0;
	m_particle_emitter_f = 0;
	m_particle_emitter_p = 0;
	m_particle_emitter_threshold_value = 0.5;

	m_particle_animate_v = 0;
	m_particle_animate_g = 0;
	m_particle_animate_f = 0;
	m_particle_animate_p = 0;

	m_particle_render_v = 0;
	m_particle_render_g = 0;
	m_particle_render_f = 0;
	m_particle_render_p = 0;

	m_particle_buffer[0] = 0;
	m_particle_query = 0;
	m_feedback_buffer = 0;
	m_feedback_query = 0;

	
	if(m_profile) {
		glGenQueries( 3, &m_timers[0] );
	}
}

void
GLMarcher::cleanup()
{
	if(m_onscreen_p != 0) 		glDeleteProgram( m_onscreen_p );
	if(m_onscreen_v != 0) 		glDeleteShader( m_onscreen_v );
	if(m_onscreen_g != 0) 		glDeleteShader( m_onscreen_g );
	if(m_onscreen_f != 0) 		glDeleteShader( m_onscreen_f );
	if(m_feedback_buffer != 0) 	glDeleteBuffers( 1, &m_feedback_buffer );
	if(m_feedback_query  != 0) 	glDeleteQueries( 1, &m_feedback_query );

	if(m_profile) {
		glDeleteQueries( 3, &m_timers[0] );
	}
}




GLMarcher::~GLMarcher()
{
//	glDeleteTextures( 1, &m_tex_function );
}

std::string
GLMarcher::info()
{
	std::string ret(m_use_geometry_shader ?"[gs] [": "[vs] [");
	ret += boost::lexical_cast<std::string>(m_config.m_function_tsize*m_config.m_function_tsize*m_config.m_function_slices) + " voxels] [";

	if(m_no_frames)
		ret += boost::lexical_cast<std::string>(m_no_triangles/m_no_frames) + (m_use_geometry_shader ? " occ. cells]": " tris] ");
	m_no_triangles = 0;
	m_no_frames = 0;
	return ret;
}

void
GLMarcher::render()
{
	CHECK_GL;

	static double animation_time;
	if(1) {
		static Util::Timer rolex;
		static bool first = true;
		if(first) {
			animation_time = 0.0;
			first = false;
		} else if(!m_config.m_anim_pause) {
			animation_time += m_config.m_anim_timewarp*rolex.elapsed();			
/*
	m_anim_timewarp = 1.0;
	m_anim_pause = false;
	m_anim_hesitancy = 1.0;
*/
		}
		rolex.restart();
	}

	if(m_config.m_field_update_enable) {
		glPushAttrib( GL_VIEWPORT_BIT );
		glUseProgram( m_field_update_p );

		if(m_config.m_field_update_type == Config::FIELD_UPDATE_ALGEBRAIC_MORPH) {
			static GLfloat C[7][12] = {
//			 x^5,	x^4,	y^4,	z^4,	x^2y^2,	x^2z^2,	y^2z^2,	xyz,	x^2,	y^2,	z^2,	1
				{ 0.0,	-2.0,	0.0,	0.0,	0.0,	0.0,	-1.0,	0.0,	6.0,	0.0,	0.0,	0.0 }, // helix
				{ 0.0,	8.0,	0.5,	0.5,	4.0,	4.0,	-1.4,	0.0,	0.0,	0.0,	0.0,	0.0 }, // in-between
				{ 0.0,  16.0,	1.0,	1.0,	8.0,	8.0,	-2.0,	0.0,	-6.0,	0.0,	0.0,	0.0 },
				{ 0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	1.0,	1.0,	0.3,	-0.95 }, // daddel
				{ 0.0,	1.0,	1.0,	1.0,	2.0,	2.0,	2.0,	0.0,	-1.01125, -1.01125, 0.94875, 0.225032 }, // torus
				{ -0.5,	-0.5,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	1.0,	1.0,	0.0 }, // kiss
				{ 0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	16.0,	4.0,	4.0,	4.0,	-1.0 }, // cayley
			};

			double st = animation_time/(1.0+m_config.m_anim_hesitancy);
			int ix = (int)(floor(st));
			double ut = animation_time - ix*(1.0+m_config.m_anim_hesitancy);
			double t = std::min( ut, 1.0 );
//			double t = std::min( animation_time-2.0*ix, 1.0 );
			std::vector<GLfloat> CC(12);
			for(int i=0; i<12; i++)
				CC[i] = (1.0-t)*C[ix%7][i] + t*C[(ix+1)%7][i];
			glUniform1fv( glGetUniformLocation( m_field_update_p, "shape" ), 12, &CC[0] );
		} else if( m_config.m_field_update_type == Config::FIELD_UPDATE_METABALLS ) {
			std::vector<GLfloat> pos(8*3);
			for(int i=0; i<8; i++) {
				double spread = sin(0.312*animation_time);
				double tt = animation_time + (0.1+spread)*i;
				pos[3*i+0] = 0.75*cos( tt );
				pos[3*i+1] = 0.75*sin( 1.71*tt );
				pos[3*i+2] = 0.75*cos( 2.3*tt );
			}
			glUniform3fv( glGetUniformLocation( m_field_update_p, "metaballs" ), 8, &pos[0] );
		}

		

		glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, m_field_update_fb );

		int cols_log2 = (int)ceilf(log2(sqrt((double)m_config.m_function_slices)));
		int cols = 1<<cols_log2;
		int rows = (m_config.m_function_slices+cols+1)/cols;
		int tsize = 1<<m_config.m_function_tsize_log2;
		int func_w = tsize*cols;
		int func_h = tsize*rows;


		glViewport( 0, 0, func_w, func_h );
		glBegin( GL_QUADS );
		glVertex2f( -1.0f, -1.0f );
		glVertex2f(  1.0f, -1.0f );
		glVertex2f(  1.0f,  1.0f );
		glVertex2f( -1.0f,  1.0f );
		glEnd();

		glPopAttrib();
	}


	int N = buildHistopyramid();


	glBindFramebufferEXT( GL_FRAMEBUFFER_EXT, 0 );

	glActiveTextureARB( GL_TEXTURE3_ARB );
	glBindTexture( GL_TEXTURE_3D, m_tex_noise );
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT); 
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT); 
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
	glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
	glActiveTextureARB( GL_TEXTURE0_ARB );

	


	// --- extract

	glPushAttrib( GL_ENABLE_BIT );
	glEnable( GL_DEPTH_TEST );


//	glScalef( 1.0f/*m_config.m_function_xscale*/,
//			  1.0f/*m_config.m_function_yscale*/,
//			  m_config.m_function_zscale/m_config.m_function_xscale );


//	if(m_config.m_extraction_fitp)
	glShadeModel( GL_FLAT );

	glActiveTextureARB( GL_TEXTURE2_ARB );
	glBindTexture( GL_TEXTURE_2D, m_tex_tritable );
	glActiveTextureARB( GL_TEXTURE0_ARB );

	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0 );
	glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, m_hp_size_log2 );

	glPushClientAttrib( GL_CLIENT_VERTEX_ARRAY_BIT );

	if( m_config.m_mode == Config::MODE_WIREFRAME ) {
		glEnable( GL_POLYGON_OFFSET_FILL );
		glPolygonOffset( 1.0, 1.0 );
		glColor3f( 0.0, 0.0, 0.2 );
		glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
	}

	if( m_feedback_buffer_inuse ) {
		int M = N* (m_use_geometry_shader ? 15 : 1 );
		if( m_feedback_buffer_size < M ) {
			std::cerr << __func__ << ": feedback buffer resized.\n";
			m_feedback_buffer_size = M + M/10;
			glBindBuffer( GL_ARRAY_BUFFER, m_feedback_buffer );
			glBufferData( GL_ARRAY_BUFFER, sizeof(GLfloat)*7*m_feedback_buffer_size, NULL, GL_STATIC_DRAW );
			glBindBuffer( GL_ARRAY_BUFFER, 0 );
			CHECK_GL;
		}
	}

	glBindBuffer( GL_ARRAY_BUFFER, m_enumerate_vbo );
	glVertexPointer( 3, GL_FLOAT, 0, NULL );
	glEnableClientState( GL_VERTEX_ARRAY );
	glUseProgram( m_onscreen_p );
	glUniform1f( m_uniform_onscreen_threshold, m_isovalue_scale*m_config.m_function_isovalue );

	// --- assumes only rotation + scale in modelview, should be inversetranspose
	std::vector<GLfloat> normal_matrix(9);
	for(int j=0; j<3; j++) {
		for(int i=0; i<3; i++) {
			normal_matrix[3*j+i] = *(m_config.m_modelview_matrix.c_ptr()+4*j+i);
		}
	}
	glUniformMatrix3fv( m_onscreen_normal_matrix, 1, false, &normal_matrix[0] );


	Vec3f cs_mso = m_config.m_modelview_matrix * Vec3f(0,0,0);
	Vec3f cs_msx = (m_config.m_modelview_matrix * Vec3f(1,0,0)) - cs_mso;
	Vec3f cs_msy = (m_config.m_modelview_matrix * Vec3f(0,1,0)) - cs_mso;
	Vec3f cs_msz = (m_config.m_modelview_matrix * Vec3f(0,0,1)) - cs_mso;

	glUniform3fv( m_onscreen_cs_msx, 1, cs_msx.c_ptr() );
	glUniform3fv( m_onscreen_cs_msy, 1, cs_msy.c_ptr() );
	glUniform3fv( m_onscreen_cs_msz, 1, cs_msz.c_ptr() );
	glUniform3fv( m_onscreen_ms_eye, 1, m_config.m_modelspace_eye.c_ptr() );

	// -- begin capturing of geometry
	if( m_feedback_buffer_inuse ) {
		glBindBufferBaseNV( GL_TRANSFORM_FEEDBACK_BUFFER_NV, 0, m_feedback_buffer );
		glBeginTransformFeedbackNV( GL_TRIANGLES );
		glBeginQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV, m_feedback_query );
	}
	CHECK_GL;

//	if( m_config.m_mode == Config::MODE_FIRE )
//		glColor3f(0.3,0.0,0.0);

//	if( m_config.m_mode == Config::MODE_FIRE )
//		glEnable( GL_RASTERIZER_DISCARD_NV );

	if(m_profile) {
		glBeginQuery( GL_TIME_ELAPSED_EXT, m_timers[2] );
	}

	for(unsigned int i=0; i<N; i+=m_enumerate_vbo_size) {
		unsigned int elements = std::min(N-i, m_enumerate_vbo_size);
		glUniform1f( m_uniform_onscreen_key_off, i );
		glDrawArrays( m_use_geometry_shader ? GL_POINTS : GL_TRIANGLES, 0, elements );
	}

	if(m_profile) {
		glEndQuery( GL_TIME_ELAPSED_EXT );
	}

//	if( m_config.m_mode == Config::MODE_FIRE )
//		glDisable( GL_RASTERIZER_DISCARD_NV );

	// --- finish capturing of geometry

	int feedback_size;
	if( m_feedback_buffer_inuse ) {
		glEndTransformFeedbackNV();
		glEndQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV );
		GLuint foo;
		glGetQueryObjectuiv( m_feedback_query, GL_QUERY_RESULT, &foo );
		feedback_size = 3*foo;
//		std::cerr << "\n" << N << " <-> " << 3*foo << "\n";
	}


	if( m_config.m_mode == Config::MODE_WIREFRAME ) {
		glDisable( GL_POLYGON_OFFSET_FILL );
		glColor3f( 1.0, 1.0, 1.0 );
		glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		// --- if we have transform feedback, use the feedback
		if( m_feedback_buffer_inuse ) {
			glPushClientAttrib( GL_CLIENT_VERTEX_ARRAY_BIT );

			glUseProgram( 0 );
			glMatrixMode( GL_MODELVIEW );
			glPushMatrix();
			glLoadIdentity();
			glBindBuffer( GL_ARRAY_BUFFER, m_feedback_buffer );
			glInterleavedArrays( GL_N3F_V3F, 0, NULL );
			glDrawArrays( GL_TRIANGLES, 0, feedback_size );
			glPopMatrix();
			glPopClientAttrib();
		}
		// --- re-extract geometry
		else {
			glBindBuffer( GL_ARRAY_BUFFER, m_enumerate_vbo );
			glVertexPointer( 3, GL_FLOAT, 0, NULL );
			glEnableClientState( GL_VERTEX_ARRAY );
			glUseProgram( m_onscreen_p );
			glUniform1f( m_uniform_onscreen_threshold, m_isovalue_scale*m_config.m_function_isovalue );
			for(unsigned int i=0; i<N; i+=m_enumerate_vbo_size) {
				unsigned int elements = std::min(N-i, m_enumerate_vbo_size);
				glUniform1f( m_uniform_onscreen_key_off, i );
				glDrawArrays( m_use_geometry_shader ? GL_POINTS : GL_TRIANGLES, 0, elements );
			}
		}
		glPolygonOffset( 0.0, 0.0 );
		glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
	}

	if( m_config.m_mode == Config::MODE_FIRE && m_particle_emitter_p != 0 ) {
		glActiveTextureARB( GL_TEXTURE0_ARB );
		glBindTexture( GL_TEXTURE_3D, m_tex_noise );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );

		glColor3f( 1.0, 1.0, 1.0 );
		glUseProgram( m_particle_emitter_p );
//		glUniform1i( m_particle_emitter_frame_rand, 0 );
		glUniform1i( m_particle_emitter_frame_rand, rand() );
		glUniform1f( m_particle_emitter_threshold, m_particle_emitter_threshold_value );
		glPushClientAttrib( GL_CLIENT_VERTEX_ARRAY_BIT );

		glBindBuffer( GL_ARRAY_BUFFER, m_feedback_buffer );
		glInterleavedArrays( GL_N3F_V3F, 0, NULL );

		int pbuf = (m_no_frames) & 0x1;
		int cbuf = (m_no_frames+1) & 0x1;

		glBindBufferBaseNV( GL_TRANSFORM_FEEDBACK_BUFFER_NV, 0, m_particle_buffer[cbuf] );
		glBeginTransformFeedbackNV( GL_POINTS );
		glBeginQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV, m_particle_query );
		glEnable( GL_RASTERIZER_DISCARD_NV );
		glDrawArrays( GL_TRIANGLES, 0, feedback_size );
		glDisable( GL_RASTERIZER_DISCARD_NV );

		glEndTransformFeedbackNV();
		glEndQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV );
		glGetQueryObjectuiv( m_particle_query, GL_QUERY_RESULT, &m_particle_buffer_fill[cbuf] );

		glActiveTextureARB( GL_TEXTURE0_ARB );
		glBindTexture( GL_TEXTURE_3D, m_tex_noise );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT); 
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
		glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );


		// --- animate old particles

		glUseProgram( m_particle_animate_p );
		glBindBuffer( GL_ARRAY_BUFFER, m_particle_buffer[pbuf] );
		glInterleavedArrays( GL_T2F_N3F_V3F, 0, NULL );
		glBindBufferOffsetNV( GL_TRANSFORM_FEEDBACK_BUFFER_NV, 0, m_particle_buffer[cbuf], sizeof(GLfloat)*8*m_particle_buffer_fill[cbuf] );

		glBeginTransformFeedbackNV( GL_POINTS );
		glBeginQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV, m_particle_query );
		glEnable( GL_RASTERIZER_DISCARD_NV );
		glDrawArrays( GL_POINTS, 0, m_particle_buffer_fill[pbuf] );
		glDisable( GL_RASTERIZER_DISCARD_NV );

		glEndTransformFeedbackNV();
		glEndQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV );

		GLuint foo;
		glGetQueryObjectuiv( m_particle_query, GL_QUERY_RESULT, &foo );
		std::cerr << m_particle_buffer_fill[cbuf] << " foo: " << foo << "\n";


		// --- adjust threshold
		if( m_particle_buffer_fill[cbuf] < 10 )
			m_particle_emitter_threshold_value *= 0.995;
		else if( m_particle_buffer_fill[cbuf] < 600 )
			m_particle_emitter_threshold_value *= 0.9995;
		else if( m_particle_buffer_fill[cbuf] > 1000 )
			m_particle_emitter_threshold_value *= 1.005;
		else if( m_particle_buffer_fill[cbuf] > 800 )
			m_particle_emitter_threshold_value *= 1.0005;

		m_particle_buffer_fill[cbuf] += foo;




		// --- render particles
		glMatrixMode( GL_MODELVIEW );
		glPushMatrix();
		glLoadIdentity();
		glUseProgram( m_particle_render_p );
		glDepthMask( GL_FALSE );
		glEnable( GL_BLEND );
		glBlendFunc( GL_SRC_ALPHA, GL_ONE );

		glBindBuffer( GL_ARRAY_BUFFER, m_particle_buffer[cbuf] );
		glInterleavedArrays( GL_T2F_N3F_V3F, 0, NULL );
		glDrawArrays( GL_POINTS, 0, m_particle_buffer_fill[cbuf] );
		glDepthMask( GL_TRUE );

		glDisable( GL_BLEND );

		glPopMatrix();
		glPopClientAttrib();
	}
	
	glPopClientAttrib();
	glBindBuffer( GL_ARRAY_BUFFER, 0 );
	CHECK_GL;
	glPopAttrib();
	glUseProgram( 0 );

	if(m_profile) {
		GLuint64EXT a, b, c;
		glGetQueryObjectui64vEXT( m_timers[0], GL_QUERY_RESULT, &a );
		glGetQueryObjectui64vEXT( m_timers[1], GL_QUERY_RESULT, &b );
		glGetQueryObjectui64vEXT( m_timers[2], GL_QUERY_RESULT, &c );

		std::cerr << "base=" << 1e-6*(double)a << "ms, "
		          << "reduce=" << 1e-6*(double)b << "ms, "
		          << "extract=" << 1e-6*(double)c << "ms\n";
	}
}


	} // namespace Marching
} // namespace dyken

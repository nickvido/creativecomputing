/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.marchingcubes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCMarcher {
	
	static int[][] edges = new int[][]{
		{0,0,0,1,0,0}, {1,0,0,0,0,1},
		{0,0,1,1,0,0}, {0,0,0,0,0,1},
		{0,1,0,1,0,0}, {1,1,0,0,0,1},
		{0,1,1,1,0,0}, {0,1,0,0,0,1},
		{0,0,0,0,1,0}, {1,0,0,0,1,0},
		{1,0,1,0,1,0}, {0,0,1,0,1,0}
	};

	public static int remapCode(int code ) {
		return
		(((code >> 0) & 0x1) << 0) | 
		(((code >> 1) & 0x1) << 1) | 
		(((code >> 4) & 0x1) << 2) | 
		(((code >> 5) & 0x1) << 3) | 
		(((code >> 3) & 0x1) << 4) | 
		(((code >> 2) & 0x1) << 5) | 
		(((code >> 7) & 0x1) << 6) | 
		(((code >> 6) & 0x1) << 7) ;
	}
	
	private CCMarcherConfg _myConfig;
	protected String m_defines;

	protected boolean m_use_geometry_shader;
	protected boolean m_feedback_buffer_inuse;
	protected int[]	m_feedback_buffer = new int[1];
	protected int m_feedback_buffer_size;
	protected int[]	m_feedback_query = new int[1];

	protected int[]	m_tex_function = new int[1];

	protected float				m_isovalue_scale;

	private CCGLSLShader _myOnScreenShader;
	private int m_onscreen_cs_msx;
	private int m_onscreen_cs_msy;
	private int m_onscreen_cs_msz;
	private int m_onscreen_ms_eye;
	private int m_onscreen_normal_matrix;
	private int	m_uniform_onscreen_key_off;
	private int	m_uniform_onscreen_threshold;
	
	protected CCGLSLShader	_myParticleEmitterShader;
	protected int m_particle_emitter_frame_rand;
	protected int m_particle_emitter_threshold;
	protected float	_particle_emitter_threshold_value;
	
	protected CCGLSLShader _myParticleAnimateShader;
	
	protected CCGLSLShader _myParticleRenderShader;
	
	int[] m_particle_query = new int[1];
	int[] m_particle_buffer = new int[2];
	int[] m_particle_buffer_fill = new int[2];
	
	private CCGraphics g;

	public CCMarcher(CCGraphics g, CCMarcherConfg theConfig, boolean theUseGeometry) {
		_myConfig = theConfig;
		m_use_geometry_shader = theUseGeometry;
		this.g = g;
		
		boolean pack_table = false;

		int cols_log2 = CCMath.ceil(CCMath.log2(CCMath.sqrt((double)_myConfig.m_function_slices)));
		int cols = 1 << cols_log2;
		int rows = (_myConfig.m_function_slices+cols+1)/cols;
		int tsize = 1<<_myConfig.m_function_tsize_log2;
		int func_w = tsize*cols;
		int func_h = tsize*rows;
		
		System.err.println(": Flat3D func tile size = [ " + tsize + " x " + tsize + " ]");
		System.err.println(": Flat3D func tiling = [ " + cols + " x " + rows + " ]");
		System.err.println(": Flat3D func size = [ " + func_w + " x " + func_h + " ]");


		g.gl.glGenTextures( 1, m_tex_function, 0);

		int iformat;
		int format;
		int type;
		int stride;
		switch( _myConfig.m_function_format ){
		case FUNC_UNSIGNED_BYTE:
			iformat = GL2.GL_ALPHA;
			format = GL2.GL_ALPHA;
			type = GL2.GL_UNSIGNED_BYTE;
			stride = Character.SIZE;
			m_isovalue_scale = 1.0f/0xff;
			System.err.println(": Using unsigned byte function texture.");
			break;
		case FUNC_UNSIGNED_SHORT:
			iformat = GL2.GL_ALPHA16;//16UI_EXT;
			format = GL2.GL_ALPHA;//_INTEGER_EXT;
			type = GL2.GL_UNSIGNED_SHORT;
			stride = Short.SIZE;
			m_isovalue_scale = 1.0f/0xffff;
			System.err.println(": Using unsigned short function texture.");
			break;
		case FUNC_FLOAT:
			if( _myConfig.m_field_update_enable ) {
				// seems like that we only can render to an RGBA tex...?
				iformat = GL2.GL_RGBA32F;
				format = GL2.GL_RGBA;
			} else {
				iformat = GL2.GL_ALPHA32F;
				format = GL2.GL_ALPHA;
			}
			type = GL2.GL_FLOAT;
			stride = Float.SIZE;
			m_isovalue_scale = 1.0f;
			System.err.println(": Using float function texture.");
			break;
		default:
			System.err.println(": Illegal format.");
		}
		System.err.println(": Function texture mem = " + (func_w*func_h*stride+1024*1024-1)/(1024*1024) + " Mb.");

		StringBuilder defines = new StringBuilder();


		// --- upload texture
		if(_myConfig.m_gl_use_tex3d) {
			defines.append("#define USE_TEX3D\n");
			
			g.gl.glBindTexture( GL2.GL_TEXTURE_3D, m_tex_function[0] );
			g.gl.glTexImage3D( GL2.GL_TEXTURE_3D, 0, iformat, 
						  _myConfig.m_function_tsize,
						  _myConfig.m_function_tsize,
						  _myConfig.m_function_slices,
						  0,
						  format, type, _myConfig.m_function_data );
			g.gl.glTexParameteri( GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			g.gl.glTexParameteri( GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP); 
			g.gl.glTexParameteri( GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP); 
			g.gl.glTexParameteri( GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST );
			g.gl.glTexParameteri( GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST );
			g.gl.glBindTexture( GL2.GL_TEXTURE_3D, 0 );
		} else {
//			std::vector< unsigned char > zerobuf( func_w*func_h*4*4 );

			g.gl.glBindTexture( GL2.GL_TEXTURE_2D, m_tex_function[0] );
			g.gl.glTexImage2D( GL2.GL_TEXTURE_2D, 0, iformat, func_w, func_h, 0, format, type, &zerobuf[0] );
			for(int s=0; s<_myConfig.m_function_slices; s++) {
				int ti = s % cols;
				int tj = s / cols;
				g.gl.glTexSubImage2D( GL2.GL_TEXTURE_2D, 0,
								 tsize*ti, tsize*tj,
								 tsize, tsize,
								 format, type,
								 (unsigned char*)_myConfig.m_function_data. + s*stride*tsize*tsize );

			}
			g.gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			g.gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP); 
			g.gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST );
			g.gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST );
			g.gl.glBindTexture( GL2.GL_TEXTURE_2D, 0 );
		}

		// --- create Flat 3D tile table
		if(_myConfig.m_gl_vs_tile_table || _myConfig.m_gl_fs_tile_table || _myConfig.m_gl_gs_tile_table) {
			System.err.println( ": Creating tile table.");
			float[] tile_table = new float[4*(_myConfig.m_function_slices)];
			for(int s=0; s<_myConfig.m_function_slices; s++) {
				tile_table[4*s+0] = (float)(s % cols)/(float)cols;
				tile_table[4*s+1] = (float)(s / cols)/(float)rows;
				tile_table[4*s+2] = (float)((s+1) % cols)/(float)cols;
				tile_table[4*s+3] = (float)((s+1) / cols)/(float)rows;
			}
			g.gl.glGenTextures( 1, &m_tex_tile_table );
			g.gl.glBindTexture( GL2.GL_TEXTURE_1D, m_tex_tile_table );
			g.gl.glTexImage1D( GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA32F, _myConfig.m_function_slices, 0, GL2.GL_RGBA, GL2.GL_FLOAT, tile_table[0] );
			g.gl.glTexParameteri( GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			g.gl.glTexParameteri( GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST );
			g.gl.glTexParameteri( GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST );
			g.gl.glBindTexture( GL2.GL_TEXTURE_1D, 0 );
			if(_myConfig.m_gl_vs_tile_table)
				defines.append("#define VS_TILE_TABLE 1\n");
			if(_myConfig.m_gl_fs_tile_table)
				defines.append("#define FS_TILE_TABLE 1\n");
			if(_myConfig.m_gl_gs_tile_table)
				defines.append("#define GS_TILE_TABLE 1\n");
		}	

		// --- create lookup tables
		float[] tricount = new float[256];
		float[] edge_decode = new float[256*16*4];
		for( int j=0; j<256; j++ ) {
			int count;
			for(count=0; count<16; count++) {
				if( TriangleTable.triangle_table[j][count] == -1 ) {
					break;
				}
			}
			for(int i=0; i<16; i++) {
				for(int k=0; k<4; k++)
					edge_decode[4*16*remapCode(j) + 4*i+k ] = edges[ TriangleTable.triangle_table[j][i] != -1 ? TriangleTable.triangle_table[j][i] : 0 ][k];
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
			if(_myConfig.m_extraction_fitp)
				createPackedStripTable( edge_decode );
			else {
				createPackedStripTableWithNormals( edge_decode );
				defines.append("#define PACK_TABLE\n";
				defines.append("#define FLAT_SHADING 1\n";
			}
			glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
		} else {
			if(_myConfig.m_extraction_fitp) {
				glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
			} else {
				createPackedTriangleTableWithNormals( edge_decode );
				glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, 16, 256,0, GL_RGBA, GL_FLOAT, &edge_decode[0] );
				defines.append("#define PACK_TABLE\n";
			}
		}

		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glBindTexture( GL_TEXTURE_2D, 0 );

		// --- create histopyramid texture
		m_hp_size_log2 = cols_log2+_myConfig.m_function_tsize_log2-1;
		int hp_size = 1<<m_hp_size_log2;
		System.err.println(__func__ << ": HP size = [ " << hp_size << " x " << hp_size << " ]\n";

		glGenTextures( 1, &m_hp_tex );
		glBindTexture( GL_TEXTURE_2D, m_hp_tex );
		std::vector<GLfloat> zerobuf( hp_size*hp_size*4 );
//		std::vector<GLfloat> zerobuf( (func_w*func_h) );
		glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA32F_ARB, hp_size, hp_size, 0, GL_RGBA, GL_FLOAT, &zerobuf[0] );
		glGenerateMipmapEXT( GL_TEXTURE_2D );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); 
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0 );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, m_hp_size_log2 );
		System.err.println(__func__ << " checkpoint " << __LINE__ << "\n";

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

		System.err.println(__func__ << ": enumerate vbo mem = " << (sizeof(GLfloat)*dummy.size()+1024*1024-1)/(1024*1024) << " Mb.\n";


		// --- create a string of commonly used defines
		if(m_use_geometry_shader)
			defines.append("#define USE_GEOMETRY_SHADER 1 \n";
		if(_myConfig.m_gl_unroll) {
			defines.append("#define FORCE_UNROLL 1\n";
			for(int i=0; i<=m_hp_size_log2; i++) {
				defines.append("#define HP_MIPS_"<<i<<" 1\n";
			}
		}
		
		defines.append("#define FUNC_COLS " << cols << "\n";
		defines.append("#define FUNC_ROWS " << rows << "\n";
		defines.append("#define FUNC_WIDTH " << func_w << "\n";
		defines.append("#define FUNC_HEIGHT " << func_h << "\n";
		defines.append("#define FUNC_TSIZE " << tsize << "\n";
		defines.append("#define FUNC_SLICES " << _myConfig.m_function_slices << "\n";
		defines.append("#define HP_COLS " << cols << "\n";
		defines.append("#define HP_ROWS " << cols << "\n";


		defines.append("#define HP_MIPS " << m_hp_size_log2 << "\n";



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
		if(_myConfig.m_gl_fs_tile_table)
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

			defines.append("mat3 randrot"<<k<<" = { \n";
			for(int j=0; j<3; j++) {
				defines.append("\t{ ";
				for(int i=0; i<3; i++) {
					defines.append(m.at(j,i) << (i==2 ? "" : ", ");
				}
				defines.append("}" << (j==2 ? "" : ",") << "\n";
			}
			defines.append("};\n";
		}
		m_defines = defines.str();
		std::System.err.println(m_defines << "\n";
		buildNoise();
		initialize();

		if(_myConfig.m_field_update_enable) {
			if(_myConfig.m_gl_use_tex3d)
				throw std::runtime_error( "Texture3D unsupported here." );

			if( _myConfig.m_field_update_type == Config::FIELD_UPDATE_ALGEBRAIC_MORPH ) {
				m_field_update_f = compileShader( m_defines + snarfFile( "glsl/amorph.glshader" ), GL_FRAGMENT_SHADER );
			} else if( _myConfig.m_field_update_type == Config::FIELD_UPDATE_METABALLS ) {
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

			std::System.err.println(m_tex_function << "\n";

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
	
	public void reconfigure() {
		cleanup();

		GL2 gl = CCGraphics.currentGL();
		String defines = m_defines;

		if(_myConfig.m_gl_displace)
			defines += "#define DISPLACEMENT_MAPPING 1\n";

		String myFragmentShader = "";
		
		switch(_myConfig.m_mode) {
		case MODE_PHONG:
			m_feedback_buffer_inuse = false;
			myFragmentShader = "phong.glsl";
			break;
		case MODE_WIREFRAME:
			m_feedback_buffer_inuse = g.isExtensionSupported("GL_NV_transform_feedback");
			myFragmentShader = "solid.glsl";
			break;
		case MODE_ROCK:
			m_feedback_buffer_inuse = false;
			myFragmentShader = "rock.glsl";
			break;
		case MODE_FIRE:
			m_feedback_buffer_inuse = g.isExtensionSupported("GL_NV_transform_feedback");
			myFragmentShader = "fireshape.glsl";
			break;
		case MODE_FUR:
			m_feedback_buffer_inuse = g.isExtensionSupported("GL_NV_transform_feedback");
			myFragmentShader = "furbase.glsl";
			break;
		}
		
		if(m_use_geometry_shader) {
			_myOnScreenShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "glsl/geometryexpandvs.glsl"), 
				CCIOUtil.classPath(this, "glsl/geometryexpand.glsl"),
				CCIOUtil.classPath(this, "glsl/" + myFragmentShader)
			);
			_myOnScreenShader.geometryInputType(CCGeometryInputType.POINTS);
			_myOnScreenShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
			_myOnScreenShader.geometryVerticesOut(12);
		} else {
			_myOnScreenShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "glsl/vertexexpand.glsl"), 
				CCIOUtil.classPath(this, "glsl/" + myFragmentShader)
			);
		}

		// --- set up feedback buffer for extracted geometry
		if(m_feedback_buffer_inuse) {
			gl.glGenBuffers( 1, m_feedback_buffer, 0);
			gl.glGenQueries( 1, m_feedback_query ,0);

			// --- force activiation of varyings we will record
			String names[] = { "cs_normal", "cs_position" };
//			for(int i=0; i<2; i++)
//				gl.glActiveVaryi(_myOnScreenShader.glProgram(), names[i] );
			// --- link
			_myOnScreenShader.load();

			int[] locations = new int[2];
//			for(int i=0; i<2; i++)
//				locations[i] = gl.glGetVaryin( _myOnScreenShader.glProgram(), names[i] );
			gl.glTransformFeedbackVaryings( _myOnScreenShader.glProgram(), 2, names, GL2.GL_INTERLEAVED_ATTRIBS );
			m_feedback_buffer_size = 0;
		}
		else {
			_myOnScreenShader.load();
		}
		
		_myOnScreenShader.start();
		m_onscreen_cs_msx = _myOnScreenShader.uniformLocation("cs_msx");
		m_onscreen_cs_msy = _myOnScreenShader.uniformLocation("cs_msy" );
		m_onscreen_cs_msz = _myOnScreenShader.uniformLocation("cs_msz" );
		m_onscreen_ms_eye = _myOnScreenShader.uniformLocation("ms_eye" );
		m_onscreen_normal_matrix = _myOnScreenShader.uniformLocation("normal_matrix" );

		_myOnScreenShader.uniform1i("hp_tex", 0);
		_myOnScreenShader.uniform1i("function_tex", 1);
		_myOnScreenShader.uniform1i("tritable_tex", 2);
		if(!m_use_geometry_shader && _myConfig.m_gl_vs_tile_table)
			_myOnScreenShader.uniform1i( "tiletable_tex", 3 );

		_myOnScreenShader.uniform1i("noise_tex", 3);

		m_uniform_onscreen_threshold = _myOnScreenShader.uniformLocation("threshold" );
		m_uniform_onscreen_key_off = _myOnScreenShader.uniformLocation("key_off" );
		_myOnScreenShader.end();

		// --- initialize particle emitter
		if( /*GLEW_EXT_geometry_shader4 &&*/ _myConfig.m_mode == CCMarcherConfg.CCMarchMode.MODE_FIRE ) {
			String[] names = { "info", "dir", "pos" };
			
			System.err.println(": initializing particle emitter");

			_myParticleEmitterShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "glsl/particle_emitter_v.glsl"), 
				CCIOUtil.classPath(this, "glsl/particle_emitter_g.glsl"), 
				CCIOUtil.classPath(this, "glsl/solid.glsl")
			);
			
			_myParticleEmitterShader.geometryInputType(CCGeometryInputType.TRIANGLES);
			_myParticleEmitterShader.geometryOutputType(CCGeometryOutputType.POINTS);
			_myParticleEmitterShader.geometryVerticesOut(1);

			// --- tag varyings as active
//			for(int i=0; i<3; i++)
//				gl.glActiveVaryingNV( m_particle_emitter_p, names[i] );
			_myParticleEmitterShader.load();

			_myParticleEmitterShader.start();
			_myParticleEmitterShader.uniform1i("noise_tex", 0 );
			m_particle_emitter_frame_rand = _myParticleEmitterShader.uniformLocation("frame_rand");
			m_particle_emitter_threshold = _myParticleEmitterShader.uniformLocation("threshold" );

			// --- select varyings for feedback
//			int[] locations[3];
//			for(int i=0; i<3; i++)
//				locations[i] = getVaryingLocationNV( m_particle_emitter_p, names[i] );
			gl.glTransformFeedbackVaryings( _myParticleEmitterShader.glProgram(), 3, names, GL2.GL_INTERLEAVED_ATTRIBS);

			// --- particle animation shader
			_myParticleAnimateShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "glsl/particle_animate_v.glsl"), 
				CCIOUtil.classPath(this, "glsl/particle_animate_g.glsl"), 
				CCIOUtil.classPath(this, "glsl/solid.glsl")
			);
			_myParticleAnimateShader.geometryInputType(CCGeometryInputType.POINTS);
			_myParticleAnimateShader.geometryOutputType(CCGeometryOutputType.POINTS);
			_myParticleAnimateShader.geometryVerticesOut(1);

//			for(int i=0; i<3; i++)
//				gl.glActiveVaryingNV( m_particle_animate_p, names[i] );
			
			_myParticleAnimateShader.load();
			_myParticleAnimateShader.uniform1i("noise_tex", 0 );
//			for(int i=0; i<3; i++) {
//				locations[i] = gl.glGetVaryingLocationNV( m_particle_animate_p, names[i] );
//				if(locations[i] == -1) {
//					std::System.err.println(__func__ << ": failed to select varying " << names[i] << " for readback.\n";
//					exit( EXIT_FAILURE );
//				}
//			}
			gl.glTransformFeedbackVaryings(_myParticleAnimateShader.glProgram(), 3, names, GL2.GL_INTERLEAVED_ATTRIBS);

			// --- particle visualization shader
			_myParticleRenderShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "glsl/particle_animate_v.glsl"), 
				CCIOUtil.classPath(this, "glsl/particle_render_g.glsl"), 
				CCIOUtil.classPath(this, "glsl/particle_render_f.glsl")
			);
			_myParticleRenderShader.geometryInputType(CCGeometryInputType.POINTS);
			_myParticleRenderShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
			_myParticleRenderShader.geometryVerticesOut(4);

			_myParticleRenderShader.load();
			_myParticleRenderShader.start();
			_myParticleRenderShader.uniform1i("noise_tex", 0);
			_myParticleRenderShader.end();

			// --- create particle buffers
			if(m_particle_query[0] == 0)
				gl.glGenQueries(1, m_particle_query,1);
			if(m_particle_buffer[0] == 0)
				gl.glGenBuffers(2, m_particle_buffer,0);
			for(int i=0; i<2; i++) {
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, m_particle_buffer[i] );
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, Float.SIZE*8*100000, null, GL2.GL_STATIC_DRAW );
			}

			gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
		}

		gl.glUseProgram( 0 );
	}
	
	public void render() {	

		if(_myConfig.m_field_update_enable) {
			glPushAttrib( GL_VIEWPORT_BIT );
			glUseProgram( m_field_update_p );

			if(_myConfig.m_field_update_type == Config::FIELD_UPDATE_ALGEBRAIC_MORPH) {
				static GLfloat C[7][12] = {
//				 x^5,	x^4,	y^4,	z^4,	x^2y^2,	x^2z^2,	y^2z^2,	xyz,	x^2,	y^2,	z^2,	1
					{ 0.0,	-2.0,	0.0,	0.0,	0.0,	0.0,	-1.0,	0.0,	6.0,	0.0,	0.0,	0.0 }, // helix
					{ 0.0,	8.0,	0.5,	0.5,	4.0,	4.0,	-1.4,	0.0,	0.0,	0.0,	0.0,	0.0 }, // in-between
					{ 0.0,  16.0,	1.0,	1.0,	8.0,	8.0,	-2.0,	0.0,	-6.0,	0.0,	0.0,	0.0 },
					{ 0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	1.0,	1.0,	0.3,	-0.95 }, // daddel
					{ 0.0,	1.0,	1.0,	1.0,	2.0,	2.0,	2.0,	0.0,	-1.01125, -1.01125, 0.94875, 0.225032 }, // torus
					{ -0.5,	-0.5,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	1.0,	1.0,	0.0 }, // kiss
					{ 0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	0.0,	16.0,	4.0,	4.0,	4.0,	-1.0 }, // cayley
				};

				double st = animation_time/(1.0+_myConfig.m_anim_hesitancy);
				int ix = (int)(floor(st));
				double ut = animation_time - ix*(1.0+_myConfig.m_anim_hesitancy);
				double t = CCMath.min( ut, 1.0 );
//				double t = std::min( animation_time-2.0*ix, 1.0 );
				std::vector<GLfloat> CC(12);
				for(int i=0; i<12; i++)
					CC[i] = (1.0-t)*C[ix%7][i] + t*C[(ix+1)%7][i];
				glUniform1fv( glGetUniformLocation( m_field_update_p, "shape" ), 12, &CC[0] );
			} else if( _myConfig.m_field_update_type == Config::FIELD_UPDATE_METABALLS ) {
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

			int cols_log2 = (int)ceilf(log2(sqrt((double)_myConfig.m_function_slices)));
			int cols = 1<<cols_log2;
			int rows = (_myConfig.m_function_slices+cols+1)/cols;
			int tsize = 1<<_myConfig.m_function_tsize_log2;
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


//		glScalef( 1.0f/*_myConfig.m_function_xscale*/,
//				  1.0f/*_myConfig.m_function_yscale*/,
//				  _myConfig.m_function_zscale/_myConfig.m_function_xscale );


//		if(_myConfig.m_extraction_fitp)
		glShadeModel( GL_FLAT );

		glActiveTextureARB( GL_TEXTURE2_ARB );
		glBindTexture( GL_TEXTURE_2D, m_tex_tritable );
		glActiveTextureARB( GL_TEXTURE0_ARB );

		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0 );
		glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, m_hp_size_log2 );

		glPushClientAttrib( GL_CLIENT_VERTEX_ARRAY_BIT );

		if( _myConfig.m_mode == Config::MODE_WIREFRAME ) {
			glEnable( GL_POLYGON_OFFSET_FILL );
			glPolygonOffset( 1.0, 1.0 );
			glColor3f( 0.0, 0.0, 0.2 );
			glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		}

		if( m_feedback_buffer_inuse ) {
			int M = N* (m_use_geometry_shader ? 15 : 1 );
			if( m_feedback_buffer_size < M ) {
				std::System.err.println(__func__ << ": feedback buffer resized.\n";
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
		glUniform1f( m_uniform_onscreen_threshold, m_isovalue_scale*_myConfig.m_function_isovalue );

		// --- assumes only rotation + scale in modelview, should be inversetranspose
		std::vector<GLfloat> normal_matrix(9);
		for(int j=0; j<3; j++) {
			for(int i=0; i<3; i++) {
				normal_matrix[3*j+i] = *(_myConfig.m_modelview_matrix.c_ptr()+4*j+i);
			}
		}
		glUniformMatrix3fv( m_onscreen_normal_matrix, 1, false, &normal_matrix[0] );


		Vec3f cs_mso = _myConfig.m_modelview_matrix * Vec3f(0,0,0);
		Vec3f cs_msx = (_myConfig.m_modelview_matrix * Vec3f(1,0,0)) - cs_mso;
		Vec3f cs_msy = (_myConfig.m_modelview_matrix * Vec3f(0,1,0)) - cs_mso;
		Vec3f cs_msz = (_myConfig.m_modelview_matrix * Vec3f(0,0,1)) - cs_mso;

		glUniform3fv( m_onscreen_cs_msx, 1, cs_msx.c_ptr() );
		glUniform3fv( m_onscreen_cs_msy, 1, cs_msy.c_ptr() );
		glUniform3fv( m_onscreen_cs_msz, 1, cs_msz.c_ptr() );
		glUniform3fv( m_onscreen_ms_eye, 1, _myConfig.m_modelspace_eye.c_ptr() );

		// -- begin capturing of geometry
		if( m_feedback_buffer_inuse ) {
			glBindBufferBaseNV( GL_TRANSFORM_FEEDBACK_BUFFER_NV, 0, m_feedback_buffer );
			glBeginTransformFeedbackNV( GL_TRIANGLES );
			glBeginQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV, m_feedback_query );
		}
		CHECK_GL;

//		if( _myConfig.m_mode == Config::MODE_FIRE )
//			glColor3f(0.3,0.0,0.0);

//		if( _myConfig.m_mode == Config::MODE_FIRE )
//			glEnable( GL_RASTERIZER_DISCARD_NV );

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

//		if( _myConfig.m_mode == Config::MODE_FIRE )
//			glDisable( GL_RASTERIZER_DISCARD_NV );

		// --- finish capturing of geometry

		int feedback_size;
		if( m_feedback_buffer_inuse ) {
			glEndTransformFeedbackNV();
			glEndQuery( GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV );
			GLuint foo;
			glGetQueryObjectuiv( m_feedback_query, GL_QUERY_RESULT, &foo );
			feedback_size = 3*foo;
//			std::System.err.println("\n" << N << " <-> " << 3*foo << "\n";
		}


		if( _myConfig.m_mode == Config::MODE_WIREFRAME ) {
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
				glUniform1f( m_uniform_onscreen_threshold, m_isovalue_scale*_myConfig.m_function_isovalue );
				for(unsigned int i=0; i<N; i+=m_enumerate_vbo_size) {
					unsigned int elements = std::min(N-i, m_enumerate_vbo_size);
					glUniform1f( m_uniform_onscreen_key_off, i );
					glDrawArrays( m_use_geometry_shader ? GL_POINTS : GL_TRIANGLES, 0, elements );
				}
			}
			glPolygonOffset( 0.0, 0.0 );
			glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		}

		if( _myConfig.m_mode == Config::MODE_FIRE && m_particle_emitter_p != 0 ) {
			glActiveTextureARB( GL_TEXTURE0_ARB );
			glBindTexture( GL_TEXTURE_3D, m_tex_noise );
			glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT); 
			glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT); 
			glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
			glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );

			glColor3f( 1.0, 1.0, 1.0 );
			glUseProgram( m_particle_emitter_p );
//			glUniform1i( m_particle_emitter_frame_rand, 0 );
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
			std::System.err.println(m_particle_buffer_fill[cbuf] << " foo: " << foo << "\n";


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

			std::System.err.println("base=" << 1e-6*(double)a << "ms, "
			          << "reduce=" << 1e-6*(double)b << "ms, "
			          << "extract=" << 1e-6*(double)c << "ms\n";
		}
	}
	
	public void cleanup()
	{
		
	}
	
	protected FloatBuffer createPackedTriangleTableWithNormals() {
		FloatBuffer myResult = FloatBuffer.allocate( 4*256 );
		for( int j=0; j<256; j++ ) {
			for(int t=0; t<5; t++) {
				CCVector3f[] v = new CCVector3f[3];
				for(int i=0; i<3; i++) {
					int ix = TriangleTable.triangle_table[j][3*t+i];
					if(ix == -1)  ix = 0;
					v[i] = new CCVector3f( 2*edges[ ix ][0] + edges[ ix ][3],
									  2*edges[ ix ][1] + edges[ ix ][4],
									  2*edges[ ix ][2] + edges[ ix ][5] ).scale(0.5f);
				}
				CCVector3f n = CCVecMath.normal(v[0], v[1], v[2]);
				for(int i=0; i<3; i++) {
					int ix = TriangleTable.triangle_table[j][3*t+i];
					if(ix == -1)  ix = 0;
					myResult.put(4*16*remapCode(j) + 4*3*t + 4*i + 0, n.x);
					myResult.put(4*16*remapCode(j) + 4*3*t + 4*i + 1, n.y);
					myResult.put(4*16*remapCode(j) + 4*3*t + 4*i + 2, n.z);
					myResult.put(4*16*remapCode(j) + 4*3*t + 4*i + 3, (1.0f/8.0f)*( (edges[ix][0]<<5)
																		  + (edges[ix][1]<<4)
																		  + (edges[ix][2]<<3)
																		  + (edges[ix][3]<<2)
																		  + (edges[ix][4]<<1)
																		  + (edges[ix][5]) ));
				}	
			}
		}
		return myResult;
	}
	
	protected FloatBuffer createPackedStripTable() {
		FloatBuffer myResult = FloatBuffer.allocate(256*16*4);
		for(int j=0; j<256; j++) {
			int N = TriStrip.mc_tri_strips[j][0];
			myResult.put(16*4*remapCode(j) + 3 ,N);
			for(int i=0; i<N; i++) {
				int ix = TriStrip.mc_tri_strips[j][i+1];
				boolean stop = (ix & 0x10) > 0; // TODO check this
				ix &= 0xf;
				myResult.put(16*4*remapCode(j)+4*(i+1)+0, edges[ ix ][0]);
				myResult.put(16*4*remapCode(j)+4*(i+1)+1, edges[ ix ][1]);
				myResult.put(16*4*remapCode(j)+4*(i+1)+2, edges[ ix ][2]);
				myResult.put(16*4*remapCode(j)+4*(i+1)+3, (stop ? -1 : 1 )*( 1*edges[ ix ][3] + 2*edges[ ix ][4] +  3*edges[ ix ][5] ));
			}
		}
		return myResult;
	}
	
	protected FloatBuffer createPackedStripTableWithNormals() {
		FloatBuffer myResult = FloatBuffer.allocate(256*16*4);
		for(int j=0; j<256; j++) {
			int N = TriStrip.mc_tri_strips[j][0];
			myResult.put(16*4*remapCode(j) + 3, N);
			int primitive_start = 0;
			
			for(int i=0; i<N; i++) {
				int ix = TriStrip.mc_tri_strips[j][i+1];
				boolean stop = (ix & 0x10) > 0; // TODO check this
				
				CCVector3f[] v = new CCVector3f[3];
				for(int k=0; k<3; k++) {
					ix = TriStrip.mc_tri_strips[j][CCMath.max(primitive_start,i-2)+k+1] & 0xf;
					v[k] = new CCVector3f(
						2*edges[ ix ][0] + edges[ ix ][3],
						2*edges[ ix ][1] + edges[ ix ][4],
						2*edges[ ix ][2] + edges[ ix ][5]
					).scale(0.5f);
				}
				CCVector3f n = CCVecMath.normal(v[0], v[1], v[2]);
				if( (CCMath.max(0,(i-primitive_start))%2) != 0 )
					n.scale(-1);

				if( primitive_start != 0 )
					n.scale(-1);

				ix &= 0xf;

				myResult.put(16*4*remapCode(j)+4*(i+1) + 0, n.x);
				myResult.put(16*4*remapCode(j)+4*(i+1) + 1, n.y);
				myResult.put(16*4*remapCode(j)+4*(i+1) + 2, n.z);
				myResult.put(16*4*remapCode(j)+4*(i+1) + 3, 
					(stop ? -1.0f : 1.0f ) * (1.0f/8.0f) * (
						(edges[ix][0]<<5) + 
						(edges[ix][1]<<4) + 
						(edges[ix][2]<<3) + 
						(edges[ix][3]<<2) + 
						(edges[ix][4]<<1) + 
						(edges[ix][5]) 
					)
				);
				if(stop)
					primitive_start = i;
			}
		}
		return myResult;
	}
	
	protected IntBuffer createTritabInt4() {
		IntBuffer myResult = IntBuffer.allocate(4*256*16 );
		for( int j=0; j<256; j++ ) {
			int N = 0;
			for(int i=0; i<16; i++) {
				if(TriangleTable.triangle_table[j][i] == -1) {
					N = i;
					break;
				}
			}
			for( int i=0; i<N; i++) {
				int ix = TriangleTable.triangle_table[j][i];
				myResult.put(4*(16*remapCode(j) + i) + 0, edges[ix][0]);
				myResult.put(4*(16*remapCode(j) + i) + 1, edges[ix][1]);
				myResult.put(4*(16*remapCode(j) + i) + 2, edges[ix][2]);
				myResult.put(4*(16*remapCode(j) + i) + 3, edges[ix][3]<<2 + (edges[ix][4]<<1) + (edges[ix][5]));
			}
		}
		return myResult;
	}
	
	protected IntBuffer createTricntInt(){
		IntBuffer myResult = IntBuffer.allocate(256);
			for( int j=0; j<256; j++ ) {
				int N = 0;
				for(int i=0; i<16; i++) {
					if(TriangleTable.triangle_table[j][i] == -1) {
						N = i;
						break;
					}
				}
				myResult.put(remapCode(j), N);
			}
			return myResult;
		}
	
	protected void initializeEmptyGLBuffer( int name, int size ) {
		ShortBuffer myShortBuffer = ShortBuffer.allocate(size);
		
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, name );
		gl.glBufferData( GL2.GL_ARRAY_BUFFER, size, myShortBuffer, GL2.GL_DYNAMIC_DRAW );
		gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
	}
	
	protected void initializeEnumVec4fGLBuffer( int name, int size ) {
		FloatBuffer myFloatBuffer = FloatBuffer.allocate(4 * size);
		for(int i=0; i<size; i++) {
			myFloatBuffer.put(4*i+0, i);
		}
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, name );
		gl.glBufferData( GL2.GL_ARRAY_BUFFER, Float.SIZE * 4 * size, myFloatBuffer, GL2.GL_DYNAMIC_DRAW );
		gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
	}
}

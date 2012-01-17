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

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.marchingcubes.Config.mode_t;

/**
 * @author christianriekoff
 *
 */

public class GLMarcher extends Technique{
	
	public GLMarcher( Config config, boolean use_geometry_shader ) {
		super(config);
	}

	public void render() {
	}
	
	public String info() {
		return "";
	}

	public void reconfigure() {
		cleanup();

		GL2 gl = CCGraphics.currentGL();
		String defines = m_defines;

		if(m_config.m_gl_displace)
			defines += "#define DISPLACEMENT_MAPPING 1\n";

		if(m_use_geometry_shader) {
			m_onscreen_v = compileShader( defines + snarfFile( "glsl/geometryexpandvs.glshader" ), GL2.GL_VERTEX_SHADER );
			m_onscreen_g = compileShader( defines + snarfFile( "glsl/geometryexpand.glshader" ), GL2.GL_GEOMETRY_SHADER_ARB);
		} else {
			m_onscreen_v = compileShader( defines + snarfFile( "glsl/vertexexpand.glshader" ), GL2.GL_VERTEX_SHADER );
		}

		if( m_config.m_mode == mode_t.MODE_PHONG ) {
			m_feedback_buffer_inuse = false;
			m_onscreen_f = compileShader( defines + snarfFile( "glsl/phong.glshader" ), GL2.GL_FRAGMENT_SHADER );
		}
		else if( m_config.m_mode == mode_t.MODE_WIREFRAME ) {
			m_feedback_buffer_inuse = GLEW_NV_transform_feedback;
			m_onscreen_f = compileShader( defines + snarfFile( "glsl/solid.glshader" ), GL2.GL_FRAGMENT_SHADER );
		}
		else if( m_config.m_mode == mode_t.MODE_ROCK ) {
			m_feedback_buffer_inuse = false;
			m_onscreen_f = compileShader( defines + snarfFile( "glsl/rock.glshader" ), GL2.GL_FRAGMENT_SHADER );
		}
		else if( m_config.m_mode == mode_t.MODE_FIRE ) {
			m_feedback_buffer_inuse = GLEW_NV_transform_feedback;
			m_onscreen_f = compileShader( defines + snarfFile( "glsl/fireshape.glshader" ), GL2.GL_FRAGMENT_SHADER );
		}
		else if( m_config.m_mode == mode_t.MODE_FUR ) {
			m_feedback_buffer_inuse = GLEW_NV_transform_feedback;
			m_onscreen_f = compileShader( defines + snarfFile( "glsl/furbase.glshader" ), GL2.GL_FRAGMENT_SHADER );
		}

		m_onscreen_p = gl.glCreateProgram();
		gl.glAttachShader( m_onscreen_p, m_onscreen_v );
		if(m_use_geometry_shader)
			gl.glAttachShader( m_onscreen_p, m_onscreen_g );
		gl.glAttachShader( m_onscreen_p, m_onscreen_f );
		if(m_use_geometry_shader) {
			gl.glProgramParameteri( m_onscreen_p, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, 12 );
			gl.glProgramParameteri( m_onscreen_p, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, GL2.GL_POINTS );
			gl.glProgramParameteri( m_onscreen_p, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL2.GL_TRIANGLE_STRIP );
		}

		// --- set up feedback buffer for extracted geometry
		if(m_feedback_buffer_inuse) {
			gl.glGenBuffers( 1, m_feedback_buffer, 0);
			gl.glGenQueries( 1, m_feedback_query ,0);

			// --- force activiation of varyings we will record
			String names[] = { "cs_normal", "cs_position" };
			for(int i=0; i<2; i++)
				gl.glActiveVaryingNV( m_onscreen_p, names[i] );
			// --- link
			linkProgram( m_onscreen_p );

			GLint locations[2];
			for(int i=0; i<2; i++)
				locations[i] = getVaryingLocationNV( m_onscreen_p, names[i] );
			gl.glTransformFeedbackVaryingsNV( m_onscreen_p, 2, &locations[0], GL2.GL_INTERLEAVED_ATTRIBS_NV );
			m_feedback_buffer_size = 0;
		}
		else {
			linkProgram( m_onscreen_p );
		}
		
		gl.glUseProgram( m_onscreen_p );
		m_onscreen_cs_msx = getUniformLocation( m_onscreen_p, "cs_msx" );
		m_onscreen_cs_msy = getUniformLocation( m_onscreen_p, "cs_msy" );
		m_onscreen_cs_msz = getUniformLocation( m_onscreen_p, "cs_msz" );
		m_onscreen_ms_eye = getUniformLocation( m_onscreen_p, "ms_eye" );
		m_onscreen_normal_matrix = getUniformLocation( m_onscreen_p, "normal_matrix" );

		gl.glUniform1i( getUniformLocation( m_onscreen_p, "hp_tex" ), 0 );
		gl.glUniform1i( getUniformLocation( m_onscreen_p, "function_tex" ), 1 );
		gl.glUniform1i( getUniformLocation( m_onscreen_p, "tritable_tex" ), 2 );
		if(!m_use_geometry_shader && m_config.m_gl.gl_vs_tile_table)
			gl.glUniform1i( getUniformLocation( m_onscreen_p, "tiletable_tex" ), 3 );

		gl.glUniform1i( getUniformLocation( m_onscreen_p, "noise_tex"), 3 );

		m_uniform_onscreen_threshold = getUniformLocation( m_onscreen_p, "threshold" );
		m_uniform_onscreen_key_off = getUniformLocation( m_onscreen_p, "key_off" );

		// --- initialize particle emitter
		if( GLEW_EXT_geometry_shader4 && m_config.m_mode == Config::MODE_FIRE ) {
			const char* names[3] = { "info", "dir", "pos" };
			
			std::cerr << __func__ << ": initializing particle emitter\n";
			if(m_particle_emitter_p != 0) gl.glDeleteProgram( m_particle_emitter_p );
			if(m_particle_emitter_v != 0) gl.glDeleteShader( m_particle_emitter_v );
			if(m_particle_emitter_f != 0) gl.glDeleteShader( m_particle_emitter_f );
			if(m_particle_emitter_g != 0) gl.glDeleteShader( m_particle_emitter_g );

			m_particle_emitter_v = compileShader( snarfFile( "glsl/particle_emitter_v.glshader" ), GL2.GL_VERTEX_SHADER );
			m_particle_emitter_g = compileShader( snarfFile( "glsl/particle_emitter_g.glshader" ), GL2.GL_GEOMETRY_SHADER_EXT );
			m_particle_emitter_f = compileShader( snarfFile( "glsl/solid.glshader" ), GL2.GL_FRAGMENT_SHADER );

			m_particle_emitter_p = gl.glCreateProgram();
			gl.glAttachShader( m_particle_emitter_p, m_particle_emitter_v );
			gl.glAttachShader( m_particle_emitter_p, m_particle_emitter_g );
			gl.glAttachShader( m_particle_emitter_p, m_particle_emitter_f );
			gl.glProgramParameteriEXT( m_particle_emitter_p, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, 1 );
			gl.glProgramParameteriEXT( m_particle_emitter_p, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, GL2.GL_TRIANGLES );
			gl.glProgramParameteriEXT( m_particle_emitter_p, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL2.GL_POINTS );

			// --- tag varyings as active
			for(int i=0; i<3; i++)
				gl.glActiveVaryingNV( m_particle_emitter_p, names[i] );
			linkProgram( m_particle_emitter_p );

			gl.glUseProgram( m_particle_emitter_p );
			gl.glUniform1i( getUniformLocation( m_particle_emitter_p, "noise_tex"), 0 );
			m_particle_emitter_frame_rand = getUniformLocation( m_particle_emitter_p, "frame_rand");
			m_particle_emitter_threshold = getUniformLocation( m_particle_emitter_p, "threshold" );

			// --- select varyings for feedback
			GLint locations[3];
			for(int i=0; i<3; i++)
				locations[i] = getVaryingLocationNV( m_particle_emitter_p, names[i] );
			gl.glTransformFeedbackVaryingsNV( m_particle_emitter_p, 3, &locations[0], GL2.GL_INTERLEAVED_ATTRIBS_NV );

			// --- particle animation shader
			if(m_particle_animate_p != 0) gl.glDeleteProgram( m_particle_animate_p );
			if(m_particle_animate_v != 0) gl.glDeleteShader( m_particle_animate_v );
			if(m_particle_animate_f != 0) gl.glDeleteShader( m_particle_animate_f );
			if(m_particle_animate_g != 0) gl.glDeleteShader( m_particle_animate_g );

			m_particle_animate_v = compileShader( snarfFile( "glsl/particle_animate_v.glshader" ), GL2.GL_VERTEX_SHADER );
			m_particle_animate_g = compileShader( snarfFile( "glsl/particle_animate_g.glshader" ), GL2.GL_GEOMETRY_SHADER_EXT );
			m_particle_animate_f = compileShader( snarfFile( "glsl/solid.glshader" ), GL2.GL_FRAGMENT_SHADER );

			m_particle_animate_p = gl.glCreateProgram();
			gl.glAttachShader( m_particle_animate_p, m_particle_animate_v );
			gl.glAttachShader( m_particle_animate_p, m_particle_animate_g );
			gl.glAttachShader( m_particle_animate_p, m_particle_animate_f );
			gl.glProgramParameteriEXT( m_particle_animate_p, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, 1 );
			gl.glProgramParameteriEXT( m_particle_animate_p, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, GL2.GL_POINTS );
			gl.glProgramParameteriEXT( m_particle_animate_p, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL2.GL_POINTS );
			for(int i=0; i<3; i++)
				gl.glActiveVaryingNV( m_particle_animate_p, names[i] );
			linkProgram( m_particle_animate_p );
			gl.glUniform1i( getUniformLocation( m_particle_animate_p, "noise_tex"), 0 );
			for(int i=0; i<3; i++) {
				locations[i] = gl.glGetVaryingLocationNV( m_particle_animate_p, names[i] );
				if(locations[i] == -1) {
					std::cerr << __func__ << ": failed to select varying " << names[i] << " for readback.\n";
					exit( EXIT_FAILURE );
				}
			}
			gl.glTransformFeedbackVaryingsNV( m_particle_animate_p, 3, &locations[0], GL2.GL_INTERLEAVED_ATTRIBS_NV );

			// --- particle visualization shader
			if(m_particle_render_p != 0) gl.glDeleteProgram( m_particle_render_p );
			if(m_particle_render_v != 0) gl.glDeleteShader( m_particle_render_v );
			if(m_particle_render_f != 0) gl.glDeleteShader( m_particle_render_f );
			if(m_particle_render_g != 0) gl.glDeleteShader( m_particle_render_g );

			m_particle_render_v = compileShader( snarfFile( "glsl/particle_animate_v.glshader" ), GL2.GL_VERTEX_SHADER );
			m_particle_render_g = compileShader( snarfFile( "glsl/particle_render_g.glshader" ), GL2.GL_GEOMETRY_SHADER_EXT );
			m_particle_render_f = compileShader( snarfFile( "glsl/particle_render_f.glshader" ), GL2.GL_FRAGMENT_SHADER );

			m_particle_render_p = gl.glCreateProgram();
			gl.glAttachShader( m_particle_render_p, m_particle_render_v );
			gl.glAttachShader( m_particle_render_p, m_particle_render_g );
			gl.glAttachShader( m_particle_render_p, m_particle_render_f );
			gl.glProgramParameteriEXT( m_particle_render_p, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, 4 );
			gl.glProgramParameteriEXT( m_particle_render_p, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, GL2.GL_POINTS );
			gl.glProgramParameteriEXT( m_particle_render_p, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL2.GL_TRIANGLE_STRIP );
			linkProgram( m_particle_render_p );
			gl.glUniform1i( getUniformLocation( m_particle_render_p, "noise_tex"), 0 );


			// --- create particle buffers
			if(m_particle_query == 0)
				gl.glGenQueries(1, &m_particle_query );
			if(m_particle_buffer[0] == 0)
				gl.glGenBuffers(2, &m_particle_buffer[0] );
			for(int i=0; i<2; i++) {
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, m_particle_buffer[i] );
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, sizeof(GLfloat)*8*100000, NULL, GL2.GL_STATIC_DRAW );
			}

			gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
		}

		gl.glUseProgram( 0 );
	}



	protected boolean				m_profile;

	protected long long int		m_no_triangles;
	protected int				m_no_frames;

	protected float				m_isovalue_scale;

	protected int[]				m_timers = new int[3];

	protected boolean				m_use_geometry_shader;
	protected boolean				m_use_tex3d;
	protected boolean				m_integer_texture;
	protected int[]				m_feedback_buffer = new int[0];
	protected int		m_feedback_buffer_size;
	protected int[]				m_feedback_query = new int[0];
	protected int				m_tex_function;
	protected int				m_tex_tricount;
	protected int				m_tex_tritable;
	protected int				m_tex_tile_table;

	protected int				m_tex_noise;

	protected int				m_gpgpu_passthru_v;
	protected int				m_baselevel_f;
	protected int				m_baselevel_p;
	protected int				m_reduce_base_f;
	protected int				m_reduce_base_p;
	protected int				m_reduce_f;
	protected int				m_reduce_p;
	protected CCGLSLShader		m_onscreen;

	protected int				m_particle_emitter_v;
	protected int				m_particle_emitter_g;
	protected int				m_particle_emitter_f;
	protected int				m_particle_emitter_p;
	protected int				m_particle_emitter_frame_rand;
	protected int				m_particle_emitter_threshold;
	protected float				m_particle_emitter_threshold_value;

	protected int				m_particle_animate_v;
	protected int				m_particle_animate_g;
	protected int				m_particle_animate_f;
	protected int				m_particle_animate_p;

	protected int				m_particle_render_v;
	protected int				m_particle_render_g;
	protected int				m_particle_render_f;
	protected int				m_particle_render_p;


	int				m_particle_query;
	int				m_particle_buffer[2];
	int				m_particle_buffer_fill[2];

	int				m_field_update_f;
	int				m_field_update_p;
	int				m_field_update_fb;

	protected int				m_enumerate_vbo;
	protected int		m_enumerate_vbo_size;

	protected int				m_reduce_base_uniform_delta;
	protected int				m_reduce_uniform_delta;
	protected int				m_uniform_onscreen_key_off;
	protected int				m_uniform_onscreen_threshold;
	protected int				m_uniform_baselevel_threshold;




	protected int				m_hp_tex;
	protected int		m_hp_size_log2;
	protected std::vector<int>	m_hp_framebuffers;

	protected void fieldInit();
	protected void fieldUpdate();


	protected void buildNoise();
	protected void initialize();
	protected void cleanup();

	protected int buildHistopyramid() {
		GL2 gl = CCGraphics.currentGL();
		if(m_profile) {
			glBeginQuery( GL2.GL_TIME_ELAPSED_EXT, m_timers[0] );
		}

		gl.glPushAttrib( GL2.GL_VIEWPORT_BIT );

		if(m_config.m_gl_vs_tile_table || m_config.m_gl_fs_tile_table || m_config.m_gl_gs_tile_table) {
			gl.glActiveTexture( GL2.GL_TEXTURE3);
			gl.glBindTexture( GL2.GL_TEXTURE_1D, m_tex_tile_table );
		}

		gl.glActiveTexture( GL2.GL_TEXTURE2);
		gl.glBindTexture( GL2.GL_TEXTURE_1D, m_tex_tricount );

		gl.glActiveTexture( GL2.GL_TEXTURE1);
		if(m_config.m_gl_use_tex3d)
			gl.glBindTexture( GL2.GL_TEXTURE_3D, m_tex_function );
		else
			gl.glBindTexture( GL2.GL_TEXTURE_2D, m_tex_function );

		gl.glActiveTexture( GL2.GL_TEXTURE0 );
		gl.glBindTexture( GL2.GL_TEXTURE_2D, m_hp_tex );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL, 0 );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 0);


		// --- base level
		gl.glUseProgram( m_baselevel_p );
		gl.glUniform1f( m_uniform_baselevel_threshold, m_isovalue_scale*m_config.m_function_isovalue );

		gl.glBindFramebufferEXT( GL2.GL_FRAMEBUFFER, m_hp_framebuffers[0] );
		gl.glViewport( 0, 0, 1<<m_hp_size_log2, 1<<m_hp_size_log2 );
		gl.glBegin( GL2.GL_QUADS );
		gl.glVertex2f( -1.0f, -1.0f );
		gl.glVertex2f(  1.0f, -1.0f );
		gl.glVertex2f(  1.0f,  1.0f );
		gl.glVertex2f( -1.0f,  1.0f );
		gl.glEnd();

		if(m_profile) {
			gl.glEndQuery( GL2.GL_TIME_ELAPSED_EXT );
			gl.glBeginQuery( GL2.GL_TIME_ELAPSED_EXT, m_timers[1] );
		}

		gl.glBindTexture( GL2.GL_TEXTURE_2D, m_hp_tex );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL, 0 );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 0);
		// --- first reduction
		gl.glUseProgram( m_reduce_base_p );
		gl.glUniform2f( m_reduce_base_uniform_delta, -0.5/(1<<m_hp_size_log2), 0.5/(1<<m_hp_size_log2) );
		gl.glBindFramebufferEXT( GL2.GL_FRAMEBUFFER, m_hp_framebuffers[1] );
		gl.glViewport( 0, 0, 1<<(m_hp_size_log2-1), 1<<(m_hp_size_log2-1) );
		gl.glBegin( GL2.GL_QUADS );
		gl.glVertex2f( -1.0f, -1.0f );
		gl.glVertex2f(  1.0f, -1.0f );
		gl.glVertex2f(  1.0f,  1.0f );
		gl.glVertex2f( -1.0f,  1.0f );
		gl.glEnd();

		// --- rest of reductions
		gl.glUseProgram( m_reduce_p );
		for(int m=2; m<=m_hp_size_log2; m++) {
			gl.glBindTexture( GL2.GL_TEXTURE_2D, m_hp_tex );
			gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL, m-1 );
			gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, m-1 );

			gl.glUniform2f( m_reduce_uniform_delta, -0.5/(1<<(m_hp_size_log2+1-m)), 0.5/(1<<(m_hp_size_log2+1-m)) );
			gl.glBindFramebufferEXT( GL2.GL_FRAMEBUFFER, m_hp_framebuffers[m] );
			gl.glViewport( 0, 0, 1<<(m_hp_size_log2-m), 1<<(m_hp_size_log2-m) );
			gl.glBegin( GL2.GL_QUADS );
			gl.glVertex2f( -1.0f, -1.0f );
			gl.glVertex2f(  1.0f, -1.0f );
			gl.glVertex2f(  1.0f,  1.0f );
			gl.glVertex2f( -1.0f,  1.0f );
			gl.glEnd();
		}

		float mem[4];
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL, m_hp_size_log2 );
		gl.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, m_hp_size_log2 );
		gl.glGetTexImage( GL2.GL_TEXTURE_2D, m_hp_size_log2, GL2.GL_RGBA, GL2.GL_FLOAT, &mem[0] );

		int N = (int)(mem[0]+mem[1]+mem[2]+mem[3]);
		m_no_triangles += N;
		m_no_frames++;


		gl.glPopAttrib();

		if(m_profile) {
			gl.glEndQuery( GL2.GL_TIME_ELAPSED_EXT );
		}
		return N;
	}
} 


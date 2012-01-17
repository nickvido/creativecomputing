/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#include <vector>
#include <boost/lexical_cast.hpp>
#include <boost/shared_ptr.hpp>
#include <GL/glew.h>
#include <GL/glut.h>
#include <GLUtil/GlutApplication3D.h>

#include "Config.h"
#include "GLMarcher.h"
#include "CheckedGLCalls.h"


namespace dyken {
	namespace Marching {
		using std::vector;
		using std::string;
		using boost::shared_ptr; 
		using GfxMath::Vec4f;
		using GfxMath::Vec3f;
		using GfxMath::Vec3i;
		using GfxMath::BBox3f;


class Marcher : public GLUtil::GlutApplication3D
{
public:
	Marcher( int *argc, char **argv )
	: GlutApplication3D( argc, argv )
	{
		setFpsVisibility( true );
		init( BBox3f(Vec3f(0.0f), Vec3f(1.0f)) );
		m_config.init( argc, argv );
		m_rolex.restart();
		m_fps = 0;


		m_gv->setOrientation( GfxMath::Quat4f( 0.006571141,-0.8595775,0.3637625,-0.3588316
		/*
		0.8658687,-0.4463991,0.1160223,0.1937471*/ ) );


	switch( m_config.m_method ) {
		case Config::METHOD_CUDA:
#ifdef HAS_CUDA
			m_technique = new CudaMarcher( m_config, CudaMarcher::CUDA_EXTRACT );
#else
			std::cerr << argv[0] << " compiled without cuda support.\n";
			exit( EXIT_FAILURE );
#endif
			break;
		case Config::METHOD_CUDA_VS:
#ifdef HAS_CUDA
			m_technique = new CudaMarcher( m_config, CudaMarcher::VS_EXTRACT );
#else
			std::cerr << argv[0] << " compiled without cuda support.\n";
			exit( EXIT_FAILURE );
#endif
			break;
		case Config::METHOD_VERTEX_SHADER:
			m_technique = new GLMarcher( m_config, false );
			break;
		case Config::METHOD_GEOMETRY_SHADER:
			m_technique = new GLMarcher( m_config, true );
			break;
		default:
			abort();
		}

		m_technique->reconfigure();
	}

	~Marcher()
	{
		delete m_technique;
	}

protected:
	Util::Timer		m_rolex;
	unsigned int	m_fps;
	Technique*	m_technique;

	Config	m_config;

	void
	keyboard( unsigned char key )
	{
		float delta = glutGetModifiers() == GLUT_ACTIVE_SHIFT ? 0.001 : 0.01;

		switch(key) {
		case 'p':
		case 'P':
			m_config.m_anim_pause = !m_config.m_anim_pause;
			std::cerr << "animation pause " << (m_config.m_anim_pause?"enabled":"disabled") << "\n";
			break;
		case 'w':
			m_config.m_anim_timewarp = std::max(0.1, m_config.m_anim_timewarp-0.1);
			std::cerr << "animation time warp " << m_config.m_anim_timewarp << "\n";
			break;
		case 'W':
			m_config.m_anim_timewarp = std::min(4.00, m_config.m_anim_timewarp+0.1);
			std::cerr << "animation time warp " << m_config.m_anim_timewarp << "\n";
			break;
		case 'h':
			m_config.m_anim_hesitancy = std::max(0.0, m_config.m_anim_hesitancy-0.1);
			std::cerr << "animation hesitancy " << m_config.m_anim_hesitancy << "\n";
			break;
		case 'H':
			m_config.m_anim_hesitancy = std::min(4.00, m_config.m_anim_hesitancy+0.1);
			std::cerr << "animation hesitancy " << m_config.m_anim_hesitancy << "\n";
			break;

		case ' ':
			m_config.m_mode = (Config::mode_t)( ((int)m_config.m_mode+1)%(int)Config::MODE_NO);
			m_technique->reconfigure();
			break;
		case '+':
			m_config.m_function_isovalue =
				std::min( m_config.m_function_iso_max,
						  m_config.m_function_isovalue
						  + delta*(m_config.m_function_iso_max-m_config.m_function_iso_min) );
			break;
		case 'd':
			if(m_config.m_mode == Config::MODE_ROCK ) {
				m_config.m_gl_displace = !m_config.m_gl_displace;
				m_technique->reconfigure();
			}
			break;
		case '-':
			m_config.m_function_isovalue =
				std::max( m_config.m_function_iso_min,
						  m_config.m_function_isovalue
						  - delta*(m_config.m_function_iso_max-m_config.m_function_iso_min) );
			break;			
		}

	}
	
	std::string
	info()
	{
		return
			std::string("[iso=") + boost::lexical_cast<std::string>(m_config.m_function_isovalue) +"] " +
			m_technique->info();
	}

	void
	idle()
	{
		glutPostRedisplay();
	}

	void
	render()
	{
		while( glGetError() != GL_NO_ERROR ) {}

		m_config.m_modelview_matrix = m_gv->getModelviewMatrix();
		m_config.m_projection_matrix = m_gv->getProjectionMatrix();
		m_config.m_modelview_projection_matrix = m_gv->getModelviewProjectionMatrix();
		m_config.m_modelspace_eye = m_gv->getCurrentViewPoint();


//		std::cerr << m_gv->getOrientationString() << "\n";

		m_technique->render();
		m_fps++;
		if( m_rolex.elapsed() > 2.0 ) {
			double fps = (double)m_fps/m_rolex.elapsed();
			double vps = (m_config.m_function_tsize-1)*
						 (m_config.m_function_tsize-1)*
						 (m_config.m_function_slices-1)*fps;
						 
			std::cerr << fps << " fps, " << vps/1000000.0 << " mvps\n";
			m_fps = 0;
			m_rolex.restart();
		}
	}

};

	} // namespace Marching
} // namespace dyken

int main(int argc, char **argv)
{
#ifndef _WIN32
	std::set_terminate(__gnu_cxx::__verbose_terminate_handler);
#endif
	dyken::Marching::Marcher marcher(&argc, argv);
	marcher.run();
	return EXIT_SUCCESS;
}



/* -*- mode: C++; tab-width:4; c-basic-offset: 4; indent-tabs-mode:nil -*-
 *
 * (C) 2007 Christopher Dyken, <erikd@ifi.uio.no>
 * 
 * Distributed under the GNU GPL
 *
 */
#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include <cmath>
#include <boost/program_options.hpp>
#include <boost/lexical_cast.hpp>
#include "Config.h"
#include "TriangleTable.h"
#include <GfxMath/Vec.h>
#include <GfxMath/VecOperations.h>

#ifdef HAS_IL
#include <IL/il.h>
#include <IL/ilu.h>
#endif

namespace dyken {
	namespace Marching {
		using std::max;
		using std::min;
		using std::cerr;
		using std::cout;
		using std::string;
		using std::vector;
		using std::ifstream;
		using std::fill_n;
		using boost::lexical_cast;
		using GfxMath::Vec3f;
		using GfxMath::triangleNormal;
		namespace po = boost::program_options;



void
Config::init( int *argc, char **argv )
{
	GLenum error = glewInit();
	if( error != GLEW_OK ) {
		cerr << "Glew init failed: " << glewGetErrorString( error ) << "\n";
		exit( EXIT_FAILURE );
	}

#ifdef HAS_IL
	ilInit();
	iluInit();
#endif

	// --- parse command line
	po::options_description desc( "General options" );
	desc.add_options()
		("help,h", "Print help message" )
		("export,e", po::value<string>(), "export filename")
		("statistics", po::value<bool>()->default_value(true), "calculate statistics for volume")
		("pow2,p", po::value<bool>()->default_value(true),
		 "Force that number of slides is a power of 2.")
#ifdef HAS_CUDA
		("method,m", po::value<string>()->default_value( "vs" ),
#else
		("method,m", po::value<string>()->default_value( "vs" ),
#endif
		"Method to use:\n"
		"\tcuda\n"
		"\tcudavs\n"
		"\ttetra\n"
		"\tvs\n"
		"\tgs")
		("format,f", po::value<string>()->default_value( "float" ),
//			GLEW_EXT_texture_integer ? "ushort" : (GLEW_ARB_texture_float ? "float" : "ubyte")),
		"Data format of function values:\n"
		"ubyte\n"
		"ushort\n"
		"float")
		("fitp", po::value<bool>()->default_value(true),
		"Interpolate field to get linear precision intersection in extraction.")
		("profile", po::value<bool>()->default_value(false),
		 "Enable profile mode.")
		("debug", po::value<bool>()->default_value(false),
		"Enable debug mode.")
		("visualization,v", po::value<string>()->default_value( "phong" ),
		 "Visualization mode:\tphong\n\twireframe\n\trock\n\tfire\n\tfur")
	;

	po::options_description animdesc( "Options governing animation" );
	animdesc.add_options()
		("apause", po::value<bool>()->default_value(false),
		 "Initial animation pause state, runtime key: p.")
		("awarp", po::value<double>()->default_value(1.0),
		 "Initial animation time warp (=speed), runtime keys: w/W")
		("ahesitancy", po::value<double>()->default_value(1.0),
		 "Initial animation hesitancy (=the amount of hesitation before morphing to next shape), runtime keys: h/H.")
	;


	

	po::options_description gldesc( "Options for OpenGL" );
	gldesc.add_options()
		("tex3d", po::value<bool>()->default_value(false), "Use texture3D.")
		("unroll", po::value<bool>()->default_value(true), "Force unrolling of histopyramid traversal in VS.")
		("vstt", po::value<bool>()->default_value(false), "Use tile table texture in vertex shader.")
		("fstt", po::value<bool>()->default_value(false), "Use tile table texture in fragment shader.")
		("gstt", po::value<bool>()->default_value(false), "NOT IMPLEMENTED. Use tile table texture in geometry shader.")
	;

	po::options_description parload( "Options for loading of DICOMM files" );
	parload.add_options()
		("downsample,d", po::value<unsigned int>()->default_value(0), "Log2 downsample factor of volume" )
	;

	po::options_description mfuncs( "Options for mathematically defined volumes" );
	mfuncs.add_options()
		("size,s", po::value<unsigned int>()->default_value(7), "Log2 of dimension of volume")
	;

	po::options_description hidden("Hidden options");
	hidden.add_options()
		("input-file", po::value< vector<string> >(), "Model to visualize" )
	;

	desc.add( mfuncs ).add( parload ).add( gldesc ).add(cudadesc).add(animdesc);
	
	po::positional_options_description pdesc;
	pdesc.add( "input-file", -1 );

	po::options_description cmdline;
	cmdline.add(desc).add(hidden);


	po::variables_map vm;
	po::store(po::command_line_parser(*argc, argv).options(cmdline).positional(pdesc).run(), vm);
	po::notify(vm);    
	if (vm.count("help")) {
		cout << "\nUsage: " << argv[0] << " input [options]\n\n";
		cout << "Input is either a file name or the name of one of the predefined\n";
		cout << "mathematically defined shapes:\n";
		cout << "    cayley anim-morph anim-metaballs\n\n";
		cout << "Keys:\n";
        cout << "    <space> switch between visualization modes.\n";
		cout << "    +/- increases/decreases isovalue by 1% of range.\n";
		cout << "    shift and +/- increases/decreases isovalue by 0.1% of range.\n\n";
   		cout << desc << "\n";
   		exit( EXIT_SUCCESS );
	}

	m_anim_timewarp = std::min(4.0, std::max(0.1, vm["awarp"].as<double>()));
	m_anim_pause = vm["apause"].as<bool>();
	m_anim_hesitancy = std::min(4.0, std::max(0.0, vm["ahesitancy"].as<double>()));

	m_debug = vm["debug"].as<bool>();
	m_profile = vm["profile"].as<bool>();
	m_force_pow2 = vm["pow2"].as<bool>();

	m_gl_use_tex3d = vm["tex3d"].as<bool>();
	m_gl_unroll = vm["unroll"].as<bool>();
	m_gl_vs_tile_table = vm["vstt"].as<bool>();
	m_gl_fs_tile_table = vm["fstt"].as<bool>();
	m_gl_gs_tile_table = vm["gstt"].as<bool>();
	m_gl_displace = true;

	m_extraction_fitp = vm["fitp"].as<bool>();


	if( vm["visualization"].as<string>() == "phong" )
		m_mode = MODE_PHONG;
	else if( vm["visualization"].as<string>() == "wireframe" )
		m_mode = MODE_WIREFRAME;
	else if( vm["visualization"].as<string>() == "rock" )
		m_mode = MODE_ROCK;
	else if( vm["visualization"].as<string>() == "fire" )
		m_mode = MODE_FIRE;
	else if( vm["visualization"].as<string>() == "fur" )
		m_mode = MODE_FUR;
	else {
		std::cerr << "Illegal visualization mode: " << vm["visualization"].as<string>() << "\n";
		exit( EXIT_FAILURE );
	}

	// --- determine method to use
	if( vm["method"].as<string>() == "cuda" )
		m_method = METHOD_CUDA;
	else if( vm["method"].as<string>() == "cudavs" )
		m_method = METHOD_CUDA_VS;
	else if( vm["method"].as<string>() == "vs" )
		m_method = METHOD_VERTEX_SHADER;
	else if( vm["method"].as<string>() == "gs" )
		m_method = METHOD_GEOMETRY_SHADER;
	else if( vm["method"].as<string>() == "tetra" )
		m_method = METHOD_GSTETRA;
	else {
		cerr << "Unknown method: \"" << vm["method"].as<string>() << "\"\n";
		exit( EXIT_FAILURE );
	}

	

	if( vm["format"].as<string>() == "float" ) {
		if( !GLEW_ARB_texture_float ) {
			std::cerr << "GL_ARB_texture_format not available.\n";
			exit( EXIT_FAILURE );
		}
		m_function_format = FUNC_FLOAT;
	} else if ( vm["format"].as<string>() == "ubyte" ) {
		m_function_format = FUNC_UNSIGNED_BYTE;
	} else if ( vm["format"].as<string>() == "ushort" ) {
		if( !GLEW_EXT_texture_integer ) {
			std::cerr << "GL_EXT_texture_integer not available.\n";
			exit( EXIT_FAILURE );
		}
		m_function_format = FUNC_UNSIGNED_SHORT;
	} else {
		std::cerr << "Illegal format: \""<< vm["format"].as<string>() << "\"\n";
		exit( EXIT_FAILURE );
	}

	m_cuda_threads_log2 = vm["threads"].as<unsigned int>();
	m_field_update_enable = false;

//	m_downsample_log2 = vm["downsample"].as<unsigned int>();
//	m_downsample = 1<<m_downsample_log2;

	vector< string > inputfiles;
	
	if(vm.count("input-file")) {
		inputfiles = vm["input-file"].as< vector<string> >();
	}

	if( inputfiles.size() == 0 )
		//inputfiles.push_back( string( "/work/data/CThead/load.par" ) );
		inputfiles.push_back( string( "anim-metaballs" ) );

	if( inputfiles.size() > 1 ) {
		readSetOfSlices( inputfiles,
						 1<<vm["downsample"].as<unsigned int>(),
						 1<<vm["downsample"].as<unsigned int>(),
						 1<<vm["downsample"].as<unsigned int>() );
	}
	else {
		string inputfile = inputfiles[0];
		if( 4 <= inputfile.length() && inputfile.substr(inputfile.length()-4, inputfile.length()) == ".par" ) {
			switch( m_function_format )
			{
			case FUNC_FLOAT:
				m_function_iso_min = 0;
				m_function_iso_max = 0xfff;
				if( inputfile == "/work/data/mrbrain/load.par" )
					m_function_isovalue = 1539.0;
				else
					m_function_isovalue = 0x200;
				break;
			case FUNC_UNSIGNED_BYTE:
				m_function_iso_min = 0;
				m_function_iso_max = 0xff;
				m_function_isovalue = 0x20;
				break;
			case FUNC_UNSIGNED_SHORT:
				m_function_iso_min = 0;
				m_function_iso_max = 0xfff;
				m_function_isovalue = 0x200;
				break;
			}
			parseParFile( inputfile, vm["downsample"].as<unsigned int>() );
		}
		else if( 4 <= inputfile.length() && inputfile.substr(inputfile.length()-4, inputfile.length()) == ".raw" ) {
			switch( m_function_format )
			{
			case FUNC_FLOAT:
				m_function_iso_min = -2.0;
				m_function_iso_max = 2.0;
				if( inputfile.substr(0,17) == "/work/data/bonsai" )
					m_function_isovalue = -0.72;
				else if( inputfile.substr(0,19) == "/work/data/aneurism" )
					m_function_isovalue = -0.92;
				else
					m_function_isovalue = 0.0;
				break;
			case FUNC_UNSIGNED_BYTE:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xff;
				m_function_isovalue = 0x7f;
				break;
			case FUNC_UNSIGNED_SHORT:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xffff;
				m_function_isovalue = 0x7fff;
				break;
			}
			snarfRAW( inputfile, vm["size"].as<unsigned int>() );
		}
		else if (6 <= inputfile.length() && inputfile.substr(0, 6) == "cayley") {
			switch( m_function_format )
			{
			case FUNC_FLOAT:
				m_function_iso_min = -2.0;
				m_function_iso_max = 2.0;
				m_function_isovalue = 0.0;
				break;
			case FUNC_UNSIGNED_BYTE:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xff;
				m_function_isovalue = 0x7f;
				break;
			case FUNC_UNSIGNED_SHORT:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xffff;
				m_function_isovalue = 0x7fff;
				break;
			}
			cayley( vm["size"].as<unsigned int>() );
		}
		else if (5 <= inputfile.length() && inputfile.substr(0, 5) == "anim-") {
			m_field_update_enable = true;
			if( 10 <= inputfile.length() && inputfile.substr(5, 5) == "morph") {
				m_field_update_type = FIELD_UPDATE_ALGEBRAIC_MORPH;
			} else if( 14 <= inputfile.length() && inputfile.substr(5, 9) == "metaballs") {
				m_field_update_type = FIELD_UPDATE_METABALLS;
			} else {
				std::cerr << "Unknown animation mode.\n";
				abort();
			}

			switch( m_function_format )
			{
			case FUNC_FLOAT:
				m_function_iso_min = -2.0;
				m_function_iso_max = 2.0;
				m_function_isovalue = 0.0;
				break;
			case FUNC_UNSIGNED_BYTE:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xff;
				m_function_isovalue = 0x7f;
				break;
			case FUNC_UNSIGNED_SHORT:
				m_function_iso_min = 0.0;
				m_function_iso_max = 0xffff;
				m_function_isovalue = 0x7fff;
				break;
			}
			m_function_tsize_log2 = vm["size"].as<unsigned int>();
			m_function_tsize = 1<<m_function_tsize_log2;
			m_function_slices = m_function_tsize;
			allocFuncMem();
		}
		else {
			std::cerr << "Unknown data source.\n";
			exit( EXIT_FAILURE );
		}
	}

	if(vm["statistics"].as<bool>()) {
		calcStatistics();
	}

	if(vm.count("export") && m_function_format == FUNC_FLOAT ) {
		int S = 1<<(int)ceilf(log2(m_function_slices));
		int X = m_function_tsize;
		std::string fname = vm["export"].as<string>()
						  + "-" + boost::lexical_cast<std::string>(m_function_slices)
						  + "-" + boost::lexical_cast<std::string>(S)
						  + "-" + boost::lexical_cast<std::string>(X)
						  + "-" + boost::lexical_cast<std::string>(X)
						  + ".f32";

		float* data = new float[S*X*X];
		for(int s=0; s<m_function_slices; s++) {

			for(int j=0; j<X; j++) {
				for(int i=0; i<X; i++) {
					data[s*X*X + j*X + i] = ((float*)m_function_data_)[s*X*X + j*X + i]-m_function_isovalue;
				}
			}
		}
		std::cerr << "Writing to " << fname << "\n";

		std::ofstream dump( fname.c_str(), std::ofstream::binary );
		dump.write( (char*)&data[0], sizeof(float)*X*X*S );
		delete [] data;

		



		std::cerr << "export : " << vm["export"].as<std::string>() << "\n";		
	}
}

Config::~Config()
{
}

void
Config::calcStatistics()
{
	if(m_function_format != FUNC_FLOAT)
		return;

	// --- count triangles for the different cases
	std::vector<int> mc_counts(256);
	for(int j=0; j<256; j++) {
		int i;
		for(i=0; i<16; i++) {
			if(triangle_table[j][i] == -1)
				break;
		}
		mc_counts[j] = i/3;
	}

	static const int mt_counts[256*8] = {
		0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 1, 4, 1, 4, 4, 1, 1, 4, 4, 1, 4, 1, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 1, 4, 4, 1, 4, 1, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 2, 8, 8, 2, 8, 2, 2, 8, 6, 7, 7, 6, 7, 6, 6, 7, 4, 1, 1, 4, 1, 4, 4, 1, 8, 2, 2, 8, 2, 8, 8, 2, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 6, 7, 7, 6, 7, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 1, 4, 4, 1, 4, 1, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 2, 8, 8, 2, 8, 2, 2, 8, 6, 7, 7, 6, 7, 6, 6, 7, 2, 8, 8, 2, 8, 2, 2, 8, 6, 7, 7, 6, 7, 6, 6, 7, 3, 8, 8, 3, 8, 3, 3, 8, 7, 7, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 4, 1, 1, 4, 1, 4, 4, 1, 8, 2, 2, 8, 2, 8, 8, 2, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 2, 2, 8, 2, 8, 8, 2, 8, 3, 3, 8, 3, 8, 8, 3, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 6, 7, 7, 6, 7, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 10, 10, 10, 10, 10, 10, 10, 10, 6, 9, 9, 6, 9, 6, 6, 9, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 4, 1, 1, 4, 1, 4, 4, 1, 8, 2, 2, 8, 2, 8, 8, 2, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 2, 2, 8, 2, 8, 8, 2, 8, 3, 3, 8, 3, 8, 8, 3, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 10, 10, 10, 10, 10, 10, 10, 10, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 8, 2, 2, 8, 2, 8, 8, 2, 8, 3, 3, 8, 3, 8, 8, 3, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 10, 10, 10, 10, 10, 10, 10, 10, 6, 9, 9, 6, 9, 6, 6, 9, 8, 3, 3, 8, 3, 8, 8, 3, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7, 7, 7, 7, 7, 7, 7, 3, 8, 8, 3, 8, 3, 3, 8, 7, 7, 7, 7, 7, 7, 7, 7, 3, 8, 8, 3, 8, 3, 3, 8, 6, 9, 9, 6, 9, 6, 6, 9, 2, 8, 8, 2, 8, 2, 2, 8, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 7, 7, 7, 7, 7, 3, 8, 8, 3, 8, 3, 3, 8, 6, 9, 9, 6, 9, 6, 6, 9, 2, 8, 8, 2, 8, 2, 2, 8, 6, 9, 9, 6, 9, 6, 6, 9, 2, 8, 8, 2, 8, 2, 2, 8, 5, 5, 5, 5, 5, 5, 5, 5, 1, 4, 4, 1, 4, 1, 1, 4, 1, 4, 4, 1, 4, 1, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 2, 8, 8, 2, 8, 2, 2, 8, 6, 9, 9, 6, 9, 6, 6, 9, 2, 8, 8, 2, 8, 2, 2, 8, 6, 9, 9, 6, 9, 6, 6, 9, 3, 8, 8, 3, 8, 3, 3, 8, 7, 7, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 2, 8, 8, 2, 8, 2, 2, 8, 6, 9, 9, 6, 9, 6, 6, 9, 3, 8, 8, 3, 8, 3, 3, 8, 7, 7, 7, 7, 7, 7, 7, 7, 3, 8, 8, 3, 8, 3, 3, 8, 7, 7, 7, 7, 7, 7, 7, 7, 4, 4, 4, 4, 4, 4, 4, 4, 8, 3, 3, 8, 3, 8, 8, 3, 6, 9, 9, 6, 9, 6, 6, 9, 10, 10, 10, 10, 10, 10, 10, 10, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 8, 3, 3, 8, 3, 8, 8, 3, 8, 2, 2, 8, 2, 8, 8, 2, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 10, 10, 10, 10, 10, 10, 10, 10, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 8, 3, 3, 8, 3, 8, 8, 3, 8, 2, 2, 8, 2, 8, 8, 2, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 8, 2, 2, 8, 2, 8, 8, 2, 4, 1, 1, 4, 1, 4, 4, 1, 5, 5, 5, 5, 5, 5, 5, 5, 9, 6, 6, 9, 6, 9, 9, 6, 6, 9, 9, 6, 9, 6, 6, 9, 10, 10, 10, 10, 10, 10, 10, 10, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 6, 7, 6, 6, 7, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 6, 7, 7, 6, 7, 6, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 9, 6, 6, 9, 6, 9, 9, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 7, 6, 7, 7, 6, 8, 3, 3, 8, 3, 8, 8, 3, 8, 2, 2, 8, 2, 8, 8, 2, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 8, 2, 2, 8, 2, 8, 8, 2, 4, 1, 1, 4, 1, 4, 4, 1, 7, 6, 6, 7, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 8, 8, 8, 8, 8, 8, 8, 8, 6, 9, 9, 6, 9, 6, 6, 9, 9, 6, 6, 9, 6, 9, 9, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 7, 7, 7, 7, 7, 3, 8, 8, 3, 8, 3, 3, 8, 6, 7, 7, 6, 7, 6, 6, 7, 2, 8, 8, 2, 8, 2, 2, 8, 6, 7, 7, 6, 7, 6, 6, 7, 2, 8, 8, 2, 8, 2, 2, 8, 5, 5, 5, 5, 5, 5, 5, 5, 1, 4, 4, 1, 4, 1, 1, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 6, 7, 6, 6, 7, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 7, 6, 6, 7, 6, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 8, 2, 2, 8, 2, 8, 8, 2, 4, 1, 1, 4, 1, 4, 4, 1, 6, 7, 7, 6, 7, 6, 6, 7, 2, 8, 8, 2, 8, 2, 2, 8, 5, 5, 5, 5, 5, 5, 5, 5, 1, 4, 4, 1, 4, 1, 1, 4, 5, 5, 5, 5, 5, 5, 5, 5, 1, 4, 4, 1, 4, 1, 1, 4, 4, 1, 1, 4, 1, 4, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
	};


	float* ptr = (float*)m_function_data_;
	int M = m_function_tsize;
	int non_empty = 0;
	int mc_tris = 0;
	int mt_tris = 0;
	for(int s=0; s<m_function_slices-1; s++) {
		for(int j=0; j<M-1; j++) {
			for(int i=0; i<M-1; i++) {
				int code = (ptr[(s+0)*M*M+(j+0)*M+(i+0)]<m_function_isovalue ?   1 : 0 )
						 + (ptr[(s+0)*M*M+(j+0)*M+(i+1)]<m_function_isovalue ?   2 : 0 )
						 + (ptr[(s+0)*M*M+(j+1)*M+(i+0)]<m_function_isovalue ?   4 : 0 )
						 + (ptr[(s+0)*M*M+(j+1)*M+(i+1)]<m_function_isovalue ?   8 : 0 )
						 + (ptr[(s+1)*M*M+(j+0)*M+(i+0)]<m_function_isovalue ?  16 : 0 )
						 + (ptr[(s+1)*M*M+(j+0)*M+(i+1)]<m_function_isovalue ?  32 : 0 )
						 + (ptr[(s+1)*M*M+(j+1)*M+(i+0)]<m_function_isovalue ?  64 : 0 )
						 + (ptr[(s+1)*M*M+(j+1)*M+(i+1)]<m_function_isovalue ? 128 : 0 );
				if( code != 0 && code != 255 ) {
					int tetrashift = ((s&0x1)<<2)+((j&0x1)<<1)+(i&0x1);
					non_empty++;
					mc_tris += mc_counts[ code ];
					mt_tris += mt_counts[ 8*code + tetrashift ];
				}
			}
		}
	}
	std::cerr << __func__ << ": " << (m_function_slices-1)*(M-1)*(M-1) << " voxels.\n";
	std::cerr << __func__ << ": " << non_empty << " geometry producing voxels ( " << non_empty/(float)((m_function_slices-1)*(M-1)*(M-1)) << ").\n";
	std::cerr << __func__ << ": " << mc_tris << " mc triangles ( avg " << (float)mc_tris/(float)non_empty << " ).\n";
	std::cerr << __func__ << ": " << mt_tris << " mt triangles ( avg " << (float)mt_tris/(float)non_empty << " ).\n";
}


std::string
Config::getPath( const std::string& filename )
{
	string::size_type sep = filename.rfind('/', filename.size() );
	if( sep != string::npos ) {
		return filename.substr( 0, sep+1 );
	} else {
		return string();
	}
}

void
Config::allocFuncMem()
{
	switch( m_function_format )
	{
	case FUNC_UNSIGNED_BYTE:
		m_function_data_ = new unsigned char[m_function_tsize*m_function_tsize*m_function_slices];
		break;
	case FUNC_UNSIGNED_SHORT:
		m_function_data_ = new unsigned short[m_function_tsize*m_function_tsize*m_function_slices];
		break;
	case FUNC_FLOAT:
		m_function_data_ = new float[m_function_tsize*m_function_tsize*m_function_slices];
		break;
	}
}

void
Config::snarfRAW( std::string& filename, unsigned int dst_log2 )
{
	int spec_r = filename.rfind('.');
	int spec_l = filename.rfind('.', spec_r-1);

	string spec = filename.substr(spec_l+1, spec_r-spec_l-1);
	if(spec.size() != 2) {
		std::cerr << "illegal spec string.\n";
		abort();
	}

	int src_log2 = -1;
	if('0' <= spec[1] && spec[1] <= '9') {
		src_log2 = spec[1]-'0';
	}
	
	if( src_log2 < 0 || 9 < src_log2 ) {
		std::cerr << "hmmm.\n";
		abort();
	}
	if( src_log2 < dst_log2 ) {
		std::cerr << "we don't support upsampling.\n";
	}

	std::cerr << __func__ << ": source file=\""<<filename<<"\", src_log2=" << src_log2 << ", dst_log2="<<dst_log2<<".\n";

	int src_size = 1<<src_log2;
	int dst_size = 1<<dst_log2;
	int scale = 1<<(src_log2-dst_log2);
	
	m_function_tsize_log2 = dst_log2;
	m_function_tsize = dst_size;
	m_function_slices = dst_size;
	allocFuncMem();

	// --- read raw data
	int chunksize = src_size*src_size*src_size;
	unsigned char* data = new unsigned char[chunksize];
	std::ifstream file( filename.c_str(), std::ios::binary );
	if(!file.good()) {
		std::cerr << "File is not good.\n";
		abort();
	}
	file.read( (char*)data, chunksize );
	file.close();

	// --- downsample
	for(int k=0; k<dst_size; k++) {
		for(int j=0; j<dst_size; j++) {
			for(int i=0; i<dst_size; i++) {

				// --- sum over volume
				unsigned long int value = 0;
				for(int kk=0; kk<scale; kk++) {
					for(int jj=0; jj<scale; jj++) {
						for(int ii=0; ii<scale; ii++) {
							value += data[ (k*scale+kk)*src_size*src_size + (j*scale+jj)*src_size + (i*scale+ii) ];
						}
					}
				}

				// --- and store
				switch( m_function_format )
				{
				case FUNC_UNSIGNED_BYTE:
					((unsigned char*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] =
						value/(scale*scale*scale);
					break;
				case FUNC_UNSIGNED_SHORT:
					((unsigned short*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] =
						(256*value)/(scale*scale*scale);
					break;
				case FUNC_FLOAT:
					((float*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] =
						(value)/(128.0*scale*scale*scale) - 1.0;
					break;
				default:
					exit( EXIT_FAILURE );
				}

			}
		}
	}
	delete [] data;
}

void
Config::cayley( unsigned int size_log2 )
{
	m_function_tsize_log2 = size_log2;
	m_function_tsize = 1<<m_function_tsize_log2;
	m_function_slices = m_function_tsize;
	allocFuncMem();

	std::cerr << __func__ << ": sampling function ";
	for(int k=0; k<m_function_tsize; k++) {
		for(int j=0; j<m_function_tsize; j++) {
			for(int i=0; i<m_function_tsize; i++) {
				double x = 2.0*i/m_function_tsize-1.0;
				double y = 2.0*j/m_function_tsize-1.0;
				double z = 2.0*k/m_function_tsize-1.0;


#if 0
				// octdong
				x *= 1.1;
				y *= 1.1;
				z *= 1.1;
				float f = x*x*x + x*x*z*z - y*y;
#else	
				// cayley
				float f = 1.0 - 16*x*y*z - 4*x*x - 4*y*y - 4*z*z;
#endif
				


				switch( m_function_format )
				{
				case FUNC_UNSIGNED_BYTE:
					((unsigned char*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] =
						(unsigned char)std::min(255.0f,std::max(0.0f,100.0f*f+127.0f) );
					break;
				case FUNC_UNSIGNED_SHORT:
					((unsigned short*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] =
						(unsigned short)std::min(65535.0f,std::max(0.0f,20000.0f*f+32767.0f) );
					break;
				case FUNC_FLOAT:
					((float*)m_function_data_)[k*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] = f;
					break;
				default:
					exit( EXIT_FAILURE );
				}
			}
		}
		if( (40*k)/m_function_tsize != (40*(k+1))/m_function_tsize )
			std::cerr << '.';
	}
	std::cerr << "\n";

}


#ifdef HAS_IL
void
Config::readSetOfSlices( const std::vector<std::string>& files,
						 unsigned int downx,
						 unsigned int downy,
						 unsigned int downz )
{
	ILuint image;
	ilGenImages( 1, &image );
	ilBindImage( image);

	ILenum type, bpp;
	switch( m_function_format )
	{
	case FUNC_FLOAT:
		m_function_iso_min = 0;
		m_function_iso_max = 0xfff;
		m_function_isovalue = 0x200;
		type = IL_FLOAT;
		bpp = 4;
		break;
	case FUNC_UNSIGNED_BYTE:
		m_function_iso_min = 0;
		m_function_iso_max = 0xff;
		m_function_isovalue = 0x20;
		type = IL_UNSIGNED_BYTE;
		bpp = 1;
		break;
	case FUNC_UNSIGNED_SHORT:
		m_function_iso_min = 0;
		m_function_iso_max = 0xfff;
		m_function_isovalue = 0x200;
		type = IL_UNSIGNED_SHORT;
		bpp = 2;
		break;
	}

	int w, h;
	for(int i=0; i<files.size(); i++) {
		std::vector<char> filename(files[i].begin(), files[i].end());
		filename.push_back('\0');

		if( ilLoadImage( &filename[0] ) == IL_FALSE ) {
			std::cerr << __func__ << ": failed to read image " << files[i] << "\n";
			exit( EXIT_FAILURE );
		}
		std::cerr << __func__ << ": read " << files[i] << "\n";		

		if( i==0 ) {
			w = ilGetInteger( IL_IMAGE_WIDTH );
			h = ilGetInteger( IL_IMAGE_HEIGHT );
			std::cerr << __func__ << ": input = [ " << w << " x " << h << "]\n";
			std::cerr << __func__ << ": type = " << type << "\n";
			std::cerr << __func__ << ": bpp = " << bpp << "\n";
			m_function_tsize_log2 = (unsigned int)ceilf(log2(max(w,h)));
			m_function_tsize = 1<<m_function_tsize_log2;
			m_function_slices = files.size();
			allocFuncMem();
		}

		ilCopyPixels(0, 0, 0, m_function_tsize, m_function_tsize, 1, IL_LUMINANCE, type, (unsigned char*)m_function_data_ + bpp*m_function_tsize*m_function_tsize*i );
	}
	ilDeleteImages( 1, &image );
}
#else
void
Config::readSetOfSlices( const std::vector<std::string>& files,
						 unsigned int downx,
						 unsigned int downy,
						 unsigned int downz )
{
	std::cerr << __func__ << ": Compiled without IL.\n";
	exit( EXIT_FAILURE );
}
#endif


void
Config::parseParFile( const std::string& filename, unsigned int downsample_log2 )
{
	unsigned int downsample = 1u<<downsample_log2;


	string path = getPath( filename );

	ifstream load_par( filename.c_str() );
	if(!load_par.good()) {
		cerr << __func__ << ": Failed to open \"" << filename << "\" for reading.\n";
		exit( EXIT_FAILURE );
	}

	int function_real_width  = 0;
	int function_real_height = 0;
	int function_real_slices = 0;

	m_function_xscale = 1.0;
	m_function_yscale = 1.0;
	m_function_zscale = 1.0;


	string pattern;
	while( !load_par.eof() ) {
		string line;
		getline( load_par, line );
		
		string::size_type sp = line.find(' ');
		if( sp != string::npos ) {
			string key = line.substr(0, sp);
			string val = line.substr(sp+1, line.size() );
			if(key == "ImageWidth")
				function_real_width = lexical_cast<unsigned int>( val );
			else if( key == "ImageHeight" )
				function_real_height = lexical_cast<unsigned int>( val );
			else if( key == "TotalSlices" )
				function_real_slices = lexical_cast<unsigned int>( val );
			else if( key == "FilenamePattern" )
				pattern = val;
			else if( key == "XScale" )
				m_function_xscale = lexical_cast<float>( val );
			else if( key == "YScale" )
				m_function_yscale = lexical_cast<float>( val );
			else if( key == "ZScale" )
				m_function_zscale = lexical_cast<float>( val );
		}
	}


	// --- sanity checks
	cerr << __func__ << ": Data size of file: ["<<function_real_width<< " x "<<function_real_height<< " x "<<function_real_slices<<"]\n";
	if( function_real_width == 0 || function_real_height == 0 || function_real_slices == 0 ) {
		exit( EXIT_FAILURE );		
	}

	m_function_tsize_log2 = (unsigned int)ceilf(log2(max(function_real_width,function_real_height)));

	if(m_function_tsize_log2 < downsample_log2+3) {
		cerr << __func__ << ": Downsampled image too small.\n";	
		exit( EXIT_FAILURE );
	}

	m_function_tsize_log2 -= downsample_log2;
	m_function_tsize = 1<<m_function_tsize_log2;
	if(m_force_pow2) {
		m_function_slices = 1<<((unsigned int)ceilf(log2(ceilf( function_real_slices / downsample ))));
	} else {
		m_function_slices = (unsigned int)ceilf( function_real_slices / downsample );
	}

	cerr << __func__ << ": Data size in mem:  [" << m_function_tsize << " x " << m_function_tsize << " x " << m_function_slices << "]\n";
/*
	if( m_function_tsize < 16 || m_function_slices < 16 ) {
		std::cerr << __func__  << ": Dataset is way too small.\n";
		exit( EXIT_FAILURE );
	}
*/
	if( m_function_slices == 0 )
		exit( EXIT_FAILURE );
	
	string::size_type prefix_sp = pattern.find('#');
	if( prefix_sp == string::npos ) {
		cerr << __func__ << ": pattern doesn't contain a '#'.\n";
		exit( EXIT_FAILURE );
	}
	string prefix = getPath( filename ) + pattern.substr(0, prefix_sp);
	string postfix = pattern.substr(prefix_sp+1, pattern.size() );
	cerr << __func__ << ": pattern prefix = '" << prefix << "'\n";
	cerr << __func__ << ": pattern postfix = '" << postfix << "'\n";
	cerr << __func__ << ": reading slices ";

	// --- read model

	allocFuncMem();
	
	{
		std::vector<unsigned short> raw_slices( function_real_width*function_real_height*downsample );
		for(int s=0; s<m_function_slices; s++) {

			// --- read a set of slices
			for(int ss = 0; ss<downsample; ss++) {
				int slice = s*downsample + ss;
				if( slice < function_real_slices ) {
					string slicename = prefix + lexical_cast<string>(slice+1) + postfix;
					ifstream slicefile( slicename.c_str() );
					if(!slicefile.good() ) {
						cerr << __func__ << "Error opening " << slicename << " for reading.\n";
						exit( EXIT_FAILURE );
					}
					slicefile.read( (char*)(&raw_slices[function_real_width*function_real_height*ss]),
									sizeof(unsigned short)*function_real_width*function_real_height );
				} else {
					fill_n( (char*)(&raw_slices[function_real_width*function_real_height*ss]),
							sizeof(unsigned short)*function_real_width*function_real_height, 0 );
				}
			}
			
			// --- fix endianess
			for(int i=0; i<raw_slices.size(); i++) {
				unsigned int hi = *((unsigned char*)&raw_slices[i]+0);
				unsigned int lo = *((unsigned char*)&raw_slices[i]+1);
				unsigned int val = (hi<<8) + lo;
				if( val == 0xf830 )
					val = 0;
				raw_slices[i] = val;
			}

			// --- downsample
			for(int j=0; j<m_function_tsize; j++) {
				for(int i=0; i<m_function_tsize; i++) {
					unsigned long long int value = 0ull;

					for(int ss=0; ss<downsample; ss++) {
						for(int jj=0; jj<downsample; jj++) {
							for(int ii=0; ii<downsample; ii++) {
								int iii = i*downsample+ii;
								int jjj = j*downsample+jj;
								if( iii < function_real_width && jjj < function_real_height ) {
									value += raw_slices[ ss*function_real_width*function_real_width
												 	   + jjj*function_real_width
												 	   + iii ];
								}
							}
						}
					}
					value /= downsample*downsample*downsample;
					switch( m_function_format )
					{
					case FUNC_UNSIGNED_BYTE:
						((unsigned char*)m_function_data_)[s*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] = value>>4;
						break;
					case FUNC_UNSIGNED_SHORT:
						((unsigned short*)m_function_data_)[s*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] = value;
						break;
					case FUNC_FLOAT:
						((float*)m_function_data_)[s*m_function_tsize*m_function_tsize + j*m_function_tsize + i ] = value;
						break;
					default:
						exit( EXIT_FAILURE );
					}
				}
			}
		if( (40*(s))/m_function_slices != (40*(s+1))/m_function_slices )
			cerr << '.';
		}
	}
	cerr << '\n';
	
}




	} // namespace Marching
} // namespace dyken

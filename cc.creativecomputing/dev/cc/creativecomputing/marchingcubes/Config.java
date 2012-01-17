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

/**
 * @author christianriekoff
 *
 */
public class Config {
	
	
	boolean	m_debug;
	boolean	m_profile;
	boolean m_field_update_enable;
	field_update_t					m_field_update_type;

	double							m_anim_timewarp;
	boolean							m_anim_pause;
	double							m_anim_hesitancy;


	mode_t							m_mode;
	method_t						m_method;
	boolean							m_force_pow2;
	boolean							m_gl_displace;
	boolean							m_gl_use_gs;
	boolean							m_gl_use_tex3d;
	boolean							m_gl_vs_tile_table;
	boolean							m_gl_fs_tile_table;
	boolean							m_gl_gs_tile_table;
	boolean							m_gl_unroll;
	int					m_cuda_threads_log2;
	boolean							m_extraction_fitp;
	int					m_blocksize_log2;
	int					m_blocksize;
	int					m_function_slices;
	int					m_function_tsize_log2;
	int					m_function_tsize;

	

//	void*							m_function_data_;
//	format_t						m_function_format;

	float							m_function_iso_min;
	float							m_function_iso_max;
	float							m_function_isovalue;

	float							m_function_xscale;
	float							m_function_yscale;
	float							m_function_zscale;


	std::vector<short>	m_function_data;

	GfxMath::Mat4f					m_modelview_matrix;
	GfxMath::Mat4f					m_projection_matrix;
	GfxMath::Mat4f					m_modelview_projection_matrix;
	GfxMath::Vec3f					m_modelspace_eye;

	void storeSlice( void* mem, int slice, format_t format );
	public Config(){
	}
//
//	void
//	init( int *argc, char **argv );
//
//	~Config();
//
//	void
//	readSetOfSlices( const std::vector<std::string>& files, int downx, int downy, int downz );
//
//	void
//	parseParFile( const std::string& filename, int downsample );
//
//	static std::string
//	getPath( const std::string& filename );
//
//	void
//	allocFuncMem();
//
//	void
//	cayley( int size_log2 );
//
//	void
//	calcStatistics();
//
//	void
//	snarfRAW( std::string& filename, int size_log2 );


	
}

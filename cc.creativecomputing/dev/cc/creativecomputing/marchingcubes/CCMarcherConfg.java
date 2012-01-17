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

import java.nio.Buffer;

/**
 * @author christianriekoff
 * 
 */
public class CCMarcherConfg {

	public static enum CCMarchMode {
		MODE_PHONG, MODE_WIREFRAME, MODE_ROCK, MODE_FIRE, MODE_FUR, MODE_NO
	};

	public static enum CCMarchMethod {
		METHOD_VERTEX_SHADER, METHOD_GEOMETRY_SHADER, METHOD_GSTETRA
	};
	
	public static enum CCMarchFormat
	{	FUNC_UNSIGNED_BYTE,
		FUNC_UNSIGNED_SHORT,
		FUNC_FLOAT
	};


	enum CCFieldUpdate {
		FIELD_UPDATE_ALGEBRAIC_MORPH,
		FIELD_UPDATE_METABALLS
	};

	public CCMarchMode m_mode;
	public CCMarchMethod m_method;

	double m_anim_timewarp;
	boolean m_anim_pause;
	double m_anim_hesitancy;
	
	public CCMarchFormat m_function_format;
	Buffer m_function_data;
	boolean m_field_update_enable;
	int	m_function_slices;
	int	m_function_tsize_log2;
	int	m_function_tsize;

	boolean	m_gl_use_tex3d;
	boolean m_gl_displace;
	boolean	m_gl_vs_tile_table;
	boolean	m_gl_fs_tile_table;
	boolean	m_gl_gs_tile_table;

	float m_function_iso_min;
	float m_function_iso_max;
	float m_function_isovalue;
}

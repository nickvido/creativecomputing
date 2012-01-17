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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;

public class CCMarcherDemo extends CCApp {

	CCMarcher m_technique;
	CCMarcherConfg	m_config;
	
	@Override
	public void setup() {
		m_config = new CCMarcherConfg();
		m_config.init( argc, argv );
//		m_rolex.restart();
//		m_fps = 0;
//
//
//		m_gv->setOrientation( GfxMath::Quat4f( 0.006571141,-0.8595775,0.3637625,-0.3588316
//		/*
//		0.8658687,-0.4463991,0.1160223,0.1937471*/ ) );


	switch( m_config.m_method ) {
		
		case METHOD_VERTEX_SHADER:
			m_technique = new CCMarcher(g, m_config, false );
			break;
		case METHOD_GEOMETRY_SHADER:
			m_technique = new CCMarcher(g, m_config, true );
			break;
		}

		m_technique.reconfigure();

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		m_technique.render();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		float delta = theKeyEvent.isShiftDown() ? 0.001f : 0.01f;
		
		switch(theKeyEvent.getKeyChar()) {
		case 'p':
		case 'P':
			m_config.m_anim_pause = !m_config.m_anim_pause;
			System.out.println("animation pause " + (m_config.m_anim_pause?"enabled":"disabled"));
			break;
		case 'w':
			m_config.m_anim_timewarp = CCMath.max(0.1f, m_config.m_anim_timewarp-0.1);
			System.out.println("animation time warp " + m_config.m_anim_timewarp);
			break;
		case 'W':
			m_config.m_anim_timewarp = CCMath.min(4.00f, m_config.m_anim_timewarp+0.1);
			System.out.println("animation time warp " + m_config.m_anim_timewarp);
			break;
		case 'h':
			m_config.m_anim_hesitancy = CCMath.max(0.0f, m_config.m_anim_hesitancy-0.1);
			System.out.println("animation hesitancy " + m_config.m_anim_hesitancy);
			break;
		case 'H':
			m_config.m_anim_hesitancy = CCMath.min(4.00f, m_config.m_anim_hesitancy+0.1);
			System.out.println("animation hesitancy " + m_config.m_anim_hesitancy);
			break;
		case ' ':
			m_config.m_mode = CCMarcherConfg.CCMarchMode.values()[(m_config.m_mode.ordinal() + 1) % CCMarcherConfg.CCMarchMode.values().length];
			break;
		case '+':
			m_config.m_function_isovalue =
				CCMath.min( m_config.m_function_iso_max,
						  m_config.m_function_isovalue
						  + delta*(m_config.m_function_iso_max-m_config.m_function_iso_min) );
			break;
		case 'd':
			if(m_config.m_mode == CCMarcherConfg.CCMarchMode.MODE_ROCK ) {
				m_config.m_gl_displace = !m_config.m_gl_displace;
				m_technique.reconfigure();
			}
			break;
		case '-':
			m_config.m_function_isovalue =
				CCMath.max( m_config.m_function_iso_min,
						  m_config.m_function_isovalue
						  - delta*(m_config.m_function_iso_max-m_config.m_function_iso_min) );
			break;			
		}

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMarcherDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


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
package cc.creativecomputing.scene;

import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCTransformation {
	public static final CCTransformation IDENTITY = new CCTransformation();
	
	private CCMatrix32f m_kRotate; 
	private CCVector3f m_kTranslate; 
	private CCVector3f m_kScale; 
	private boolean m_bIsIdentity, m_bIsUniformScale;
	
	public CCTransformation() {
		m_kRotate = new CCMatrix32f();
		
	}
}

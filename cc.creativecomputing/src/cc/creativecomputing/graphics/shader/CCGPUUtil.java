/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.graphics.shader;

import java.io.File;

import cc.creativecomputing.util.logging.CCLog;

/**
 * @author info
 *
 */
public class CCGPUUtil {
	public static String shaderPath(final String theShader) {
		File myFile = new File(CCGPUUtil.class.getResource("").getPath());
		String myFileName = myFile.getParentFile().getParentFile().getParentFile().getParentFile().getParent() + File.separator + "shader"+ File.separator +theShader;
		return myFileName.replaceAll("%20", " ");
	}
	
	public static void main(String[] args) {
		File myFile = new File(CCGPUUtil.class.getResource("").getPath());
		String myFileName = myFile.getParentFile().getParentFile().getParentFile().getParentFile().getParent() + File.separator + "shader/bla.fp";
		CCLog.info(myFileName);
	}
}

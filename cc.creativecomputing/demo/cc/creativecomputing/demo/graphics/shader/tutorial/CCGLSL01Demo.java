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
package cc.creativecomputing.demo.graphics.shader.tutorial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;

public class CCGLSL01Demo extends CCApp {
	
	private CCGLSLShader _myShader;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this,"vertex.glsl"),
			CCIOUtil.classPath(this,"fragment01.glsl")
		);
		_myShader.load();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

		g.clear();
		g.color(0,255,0);
		_myShader.start();
		g.rect(0,0,100,100);
		_myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGLSL01Demo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


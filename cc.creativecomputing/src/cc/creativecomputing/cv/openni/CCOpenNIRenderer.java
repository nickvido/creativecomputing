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
package cc.creativecomputing.cv.openni;

import javax.media.opengl.GL;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNIRenderer {

	private CCVBOMesh _myMesh;
	
	private CCGLSLShader _myDepthmapShader;
	private CCGLSLShader _mySceneDepthmapShader;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	
	@CCControl(name = "depth lod", min = 0, max = 10)
	private float _cDepthLod = 0;
	
	public CCOpenNIRenderer(CCOpenNI theOpenNI) {
		_myOpenNI = theOpenNI;
		
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_mySceneAnalyzer = _myOpenNI.createSceneAnalyzer();
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myDepthGenerator.width() * _myDepthGenerator.height());
		for (float x = 0; x < _myDepthGenerator.width(); x ++) {
			for (float y = 0; y < _myDepthGenerator.height(); y ++) {
				_myMesh.addVertex((x + 0.5f) / _myDepthGenerator.width(),(y + 0.5f) / _myDepthGenerator.height());	
			}
		}
		
		_myDepthmapShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/drawDepthMap_vert.glsl"),
			CCIOUtil.classPath(this, "shader/drawDepthMap_frag.glsl")
		);
		_myDepthmapShader.load();
		
		_mySceneDepthmapShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/drawDepthMap_vert.glsl"),
			CCIOUtil.classPath(this, "shader/drawSceneMap_frag.glsl")
		);
		_mySceneDepthmapShader.load();
		
	}
	
	public void drawDepthMesh(CCGraphics g) {
		g.texture(0,_myDepthGenerator.texture());
		_myDepthmapShader.start();
		_myDepthmapShader.uniform1i("depthData", 0);
		_myDepthmapShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_myDepthmapShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_myDepthmapShader.uniform2f("depthDimension", _myDepthGenerator.width(), _myDepthGenerator.height());
		_myDepthmapShader.uniform1f("res", 640 / _myDepthGenerator.width());
		_myDepthmapShader.uniform1f("depthLod", _cDepthLod);
		_myMesh.draw(g);
		_myDepthmapShader.end();
		g.noTexture();
	}
	
	public void drawSceneMesh(CCGraphics g) {
		g.texture(0,_myDepthGenerator.texture());
		g.texture(1,_mySceneAnalyzer.texture());
		_mySceneDepthmapShader.start();
		_mySceneDepthmapShader.uniform1i("depthData", 0);
		_mySceneDepthmapShader.uniform1i("sceneData", 1);
		_mySceneDepthmapShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_mySceneDepthmapShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_mySceneDepthmapShader.uniform2f("depthDimension", _myDepthGenerator.width(), _myDepthGenerator.height());
		_mySceneDepthmapShader.uniform1f("res", 640 / _myDepthGenerator.width());
		_mySceneDepthmapShader.uniform1f("depthLod", _cDepthLod);
		_myMesh.draw(g);
		_mySceneDepthmapShader.end();
		g.noTexture();
	}
}

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
package cc.creativecomputing.simulation.gpuparticles.render;

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

/**
 * @author christianriekoff
 *
 */
public class CCGPUIndexedParticleRenderer extends CCGPUParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	private CCGPUParticles _myParticles;
	
	protected CCShaderTexture _myIndexTexture;
	
	private CCGLSLShader _myShader;
	
	private CCGraphics g;
	
	public CCGPUIndexedParticleRenderer(CCGraphics theGraphics) {
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/indexed/indexed_display_vertex.glsl"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/indexed/indexed_display_fragment.glsl")
		);
		_myShader.load();
		g = theGraphics;
	}
	
	public void setup(CCGPUParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
		
		_myIndexTexture = new CCShaderTexture(_myParticles.width(), _myParticles.height());
		
		_myIndexTexture.beginDraw();
		g.clear();
		_myParticles.initValueShader().start();
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < _myParticles.width();x++) {
			for(int y = 0; y < _myParticles.height();y++) {
				g.textureCoords(0, (float)x, (float)y);
				g.vertex(x,y);
				_myMesh.addVertex(x,y,0);
			}
		}
		g.endShape();
		
		_myParticles.initValueShader().end();
		_myIndexTexture.endDraw();
		
		
		
	}
	
	public void update(final float theDeltaTime) {
//		_myMesh.vertices(_myParticles.positions());
	}

	public void draw(CCGraphics g){
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.positions(),0);
		g.texture(1, _myParticles.positions(),1);
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSize(float thePointSize) {
//		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
//		_myDisplayShader.pointSize(thePointSize);
	}
	
	public void pointSizeClamp(final float theMinPointSize, final float theMaxPointSize) {
//		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
//		_myDisplayShader.minPointSize(theMinPointSize);
//		_myDisplayShader.maxPointSize(theMinPointSize);
	}
}

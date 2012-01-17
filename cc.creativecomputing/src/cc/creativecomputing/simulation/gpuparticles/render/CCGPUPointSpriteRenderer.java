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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

/**
 * @author christianriekoff
 *
 */
public class CCGPUPointSpriteRenderer extends CCGPUParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	private CCGPUParticles _myParticles;
	
	protected CCShaderTexture _myIndexTexture;
	
	private CCGLSLShader _myShader;
	
	private CCGraphics g;
	
	private CCVector2f _myPointSize;
	
	private boolean _myFadeOut = false;
	
	private CCTexture2D _myPointSpriteTexture;
	private int _myXSplits;
	private int _myYSplits;
	
	public CCGPUPointSpriteRenderer(CCGraphics theGraphics, CCTexture2D thePointSpriteTexture, int theXSplits, int theYSplits) {
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this,"shader/pointsprite/pointsprite_vertex.glsl"),
			CCIOUtil.classPath(this,"shader/pointsprite/pointsprite_fragment.glsl")
		);
		_myShader.load();
		
		_myPointSpriteTexture = thePointSpriteTexture;
		_myXSplits = theXSplits;
		_myYSplits = theYSplits;
		g = theGraphics;
		
		_myPointSize = new CCVector2f(1, 1 * g.width / (float)g.height);
	}
	
	public void setup(CCGPUParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * 4);
		_myMesh.prepareVertexData(4);
		_myMesh.prepareTextureCoordData(0, 2);
		_myIndexTexture = new CCShaderTexture(_myParticles.width(), _myParticles.height());
		
		_myIndexTexture.beginDraw();
		g.clear();
		_myParticles.initValueShader().start();
		g.beginShape(CCDrawMode.POINTS);
		
		float myXtexSize = 1f / _myXSplits;
		float myYtexSize = 1f / _myYSplits;
		
		for(int x = 0; x < _myParticles.width();x++) {
			for(int y = 0; y < _myParticles.height();y++) {
				g.textureCoords(0, (float)x, (float)y);
				g.vertex(x,y);
				_myMesh.addVertex(x,y,-1,-1);
				_myMesh.addVertex(x,y, 1,-1);
				_myMesh.addVertex(x,y, 1, 1);
				_myMesh.addVertex(x,y,-1, 1);
				
				int xIndex = x % _myXSplits;
				int yIndex = y % _myYSplits;
				
				_myMesh.addTextureCoords(xIndex * myXtexSize, (yIndex + 1) * myYtexSize);
				_myMesh.addTextureCoords((xIndex + 1) * myXtexSize, (yIndex + 1) * myYtexSize);
				_myMesh.addTextureCoords((xIndex + 1) * myXtexSize, yIndex * myYtexSize);
				_myMesh.addTextureCoords(xIndex * myXtexSize, yIndex * myYtexSize);
			}
		}
		g.endShape();
		
		_myParticles.initValueShader().end();
		_myIndexTexture.endDraw();
	}
	
	public void update(final float theDeltaTime) {
//		_myMesh.vertices(_myParticles.positions());
	}
	
	public void fadeOut(boolean theFadeOut) {
		_myFadeOut = theFadeOut;
	}
	
	public void pointSize(final float thePointSize) {
		_myPointSize = new CCVector2f(thePointSize, thePointSize * g.width / (float)g.height);
	}

	public void draw(CCGraphics g){
//		g.gl.glEnable(GL.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.positions(),0);
		g.texture(1, _myParticles.positions(),1);
		g.texture(2, _myPointSpriteTexture);
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
		_myShader.uniform2f("pointSize", _myPointSize);
		_myShader.uniform1f("alpha", _myFadeOut ? 0 : 1);
		_myShader.uniform1i("pointSprite", 2);
		_myMesh.draw(g);
//		g.point(0,0);
//		g.point(100,0);
//		g.point(200,0);
		g.noTexture();
		_myShader.end();
//		g.gl.glDisable(GL.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
}

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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;

/**
 * @author christianriekoff
 *
 */
public class CCGPUParticlePointRenderer extends CCGPUParticleRenderer{

	protected CCVBOMesh _myMesh;
	
	private CCGPUParticles _myParticles;
	private CCGPUDisplayShader _myDisplayShader;
	
	public CCGPUParticlePointRenderer() {
		_myDisplayShader = new CCGPUDisplayShader();
	}
	
	public void setup(CCGPUParticles theParticles) {
		_myParticles = theParticles;
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticles.size());
	}
	
	public void update(final float theDeltaTime) {
		_myMesh.vertices(_myParticles.positions());
	}

	public void draw(CCGraphics g){
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
		_myParticles.positions().bind(1);
		_myDisplayShader.start();
		_myDisplayShader.tangHalfFov(CCMath.tan(g.camera().fov()) * g.height);
		_myMesh.draw(g);
		_myDisplayShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public void pointSize(float thePointSize) {
		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
		_myDisplayShader.pointSize(thePointSize);
	}
	
	public void pointSizeClamp(final float theMinPointSize, final float theMaxPointSize) {
		if(_myDisplayShader == null)_myDisplayShader = new CCGPUDisplayShader();
		_myDisplayShader.minPointSize(theMinPointSize);
		_myDisplayShader.maxPointSize(theMinPointSize);
	}
}

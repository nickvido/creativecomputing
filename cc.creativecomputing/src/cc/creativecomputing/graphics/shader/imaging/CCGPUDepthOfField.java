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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGPUUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 * 
 */
public class CCGPUDepthOfField {

	private final CCGraphics _myGraphics;
	private final int _myWidth;
	private final int _myHeight;

	private CCGLSLShader _myThirdShader;
	private CCGLSLShader _myFourthShader;
	private CCGLSLShader _myFifthShader;
	
	private CCCGShader _mySceneShader;
	private CGparameter _myFocalDistanceParameter;
	private CGparameter _myFocalRangeParameter;
	
	private CCCGShader _myDownSampleShader;

	private CCGPUSeperateGaussianBlur _myBlur;

	private CCRenderTexture _mySceneTexture;
	private CCRenderTexture _myFBO0;
	private CCRenderTexture _myFBO1;
	private CCRenderTexture _myFBO2;

	private float _myFocalDistance;
	private float _myFocalRange;
	
	private int _myDevider = 2;

	public CCGPUDepthOfField(final CCGraphics g, final int theWidth, final int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_mySceneShader = new CCCGShader(CCGPUUtil.shaderPath("imaging/dof/scene.vp"), CCGPUUtil.shaderPath("imaging/dof/scene.fp"));
		_myFocalDistanceParameter = _mySceneShader.vertexParameter("focalDistance");
		_myFocalRangeParameter = _mySceneShader.vertexParameter("focalRange");
		_mySceneShader.load();
		
		_myDownSampleShader = new CCCGShader(
			CCGPUUtil.shaderPath("imaging/dof/downsample.vp"), 
			CCGPUUtil.shaderPath("imaging/dof/downsample.fp")
		);
		_myDownSampleShader.load();
		
		_myThirdShader = new CCGLSLShader(
			CCGPUUtil.shaderPath("imaging/dof/third.vert"), 
			CCGPUUtil.shaderPath("imaging/dof/third.frag")
		);
		_myThirdShader.load();
		_myFourthShader = new CCGLSLShader(
			CCGPUUtil.shaderPath("imaging/dof/fourth.vert"), 
			CCGPUUtil.shaderPath("imaging/dof/fourth.frag")
		);
		_myFourthShader.load();
		_myFifthShader = new CCGLSLShader(
			CCGPUUtil.shaderPath("imaging/dof/fifth.vert"), 
			CCGPUUtil.shaderPath("imaging/dof/fifth.frag")
		);
		_myFifthShader.load();

		_myThirdShader.start();
		_myThirdShader.uniform1i("Width", _myWidth * 2);
		_myThirdShader.end();

		_myFourthShader.start();
		_myFourthShader.uniform1i("Height", _myHeight * 2);
		_myFourthShader.end();

		_myFifthShader.start();
		_myFifthShader.uniform1i("Tex0", 0);
		_myFifthShader.uniform1i("Tex1", 1);
		_myFifthShader.uniform1i("Tex2", 2);
		_myFifthShader.end();

		_mySceneTexture = new CCRenderTexture(g, 2, _myWidth, _myHeight);
		_myFBO0 = new CCRenderTexture(g, _myWidth / _myDevider, _myHeight / _myDevider);
		_myFBO1 = new CCRenderTexture(g, _myWidth / _myDevider, _myHeight / _myDevider);
		_myFBO2 = new CCRenderTexture(g, _myWidth / _myDevider, _myHeight / _myDevider);
		
		_myBlur = new CCGPUSeperateGaussianBlur(g,5);
		_myBlur.texture(_myFBO0);
	}
	
	public void focalDistance(final float theFocalDistance) {
		_myFocalDistance = theFocalDistance;
	}
	
	public void focalRange(final float theFocalRange) {
		_myFocalRange = theFocalRange;
	}

	public void begin() {

		/* First pass: scene rendering */
		_mySceneTexture.beginDraw();
		
		_mySceneShader.start();
		_mySceneShader.parameter(_myFocalDistanceParameter, _myFocalDistance);
		_mySceneShader.parameter(_myFocalRangeParameter, _myFocalRange);
	}

	public void end() {
		_mySceneShader.end();
		_mySceneTexture.endDraw();

		/* Second pass: downsampling */
		_myFBO0.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_mySceneTexture);
		_myDownSampleShader.start();
		_myGraphics.gl.glViewport(0, 0, _myWidth /_myDevider, _myHeight / _myDevider);
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
		_myDownSampleShader.end();
		_myGraphics.noTexture();
		_myFBO0.endDraw();

		/* Third pass: Gaussian filtering along the X axis */
		_myFBO1.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_myFBO0);
		_myThirdShader.start();
////		_myBlur.start();
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
		_myThirdShader.end();
////		_myBlur.end();
////		_myBlur.flipKernel();
		_myGraphics.noTexture();
		_myFBO1.endDraw();

		/* Fourth pass: Gaussian filtering along the Y axis */
		_myFBO2.beginDraw();
		_myGraphics.clear();
		_myGraphics.texture(_myFBO1);
		_myFourthShader.start();
//		_myBlur.start();
		drawQuad(_myWidth / _myDevider, _myHeight / _myDevider);
		_myFourthShader.end();
//		_myBlur.end();
//		_myBlur.flipKernel();
		_myGraphics.noTexture();
		_myFBO2.endDraw();
		

		/* Fifth pass: final compositing */
		_myGraphics.clear();
		_myGraphics.texture(0, _mySceneTexture);
		_myGraphics.texture(1, _myFBO2);
		_myGraphics.texture(2, _mySceneTexture,1);
		_myFifthShader.start();

		_myGraphics.beginShape(CCDrawMode.QUADS);
		_myGraphics.textureCoords(0, 0.0f, 0.0f);
		_myGraphics.textureCoords(1, 0.0f, 0.0f);
		_myGraphics.vertex(-_myWidth / 2, -_myHeight / 2);
		_myGraphics.textureCoords(0, 1.0f, 0.0f);
		_myGraphics.textureCoords(1, 1.0f, 0.0f);
		_myGraphics.vertex(_myWidth / 2, -_myHeight / 2);
		_myGraphics.textureCoords(0, 1.0f, 1.0f);
		_myGraphics.textureCoords(1, 1.0f, 1.0f);
		_myGraphics.vertex(_myWidth / 2, _myHeight / 2);
		_myGraphics.textureCoords(0, 0.0f, 1.0f);
		_myGraphics.textureCoords(1, 0.0f, 1.0f);
		_myGraphics.vertex(-_myWidth / 2, _myHeight / 2);
		_myGraphics.endShape();

		_myFifthShader.end();
		_myGraphics.noTexture();
	}

	private void drawQuad(final int theWidth, final int theHeight) {
		_myGraphics.beginShape(CCDrawMode.QUADS);
		_myGraphics.textureCoords(0.0f, 0.0f);
		_myGraphics.vertex(-theWidth / 2, -theHeight / 2);
		_myGraphics.textureCoords(1.0f, 0.0f);
		_myGraphics.vertex(theWidth / 2, -theHeight / 2);
		_myGraphics.textureCoords(1.0f, 1.0f);
		_myGraphics.vertex(theWidth / 2, theHeight / 2);
		_myGraphics.textureCoords(0.0f, 1.0f);
		_myGraphics.vertex(-theWidth / 2, theHeight / 2);
		_myGraphics.endShape();
	}
}

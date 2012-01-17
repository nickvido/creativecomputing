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
package cc.creativecomputing.demo.graphics.shader.simulation.dla;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 * 
 */
public class CCGPUDLA {

	@CCControl(name = "particle replacement", min = 1, max = 20)
	private float _cParticleReplacement = 0;

	@CCControl(name = "particle speed", min = 10, max = 1000)
	private float _cParticleSpeed = 0;

	private CCGraphics g;

	/* USED FOR PARTICLES */

	private CCCGShader _myParticleShader;
	private CGparameter _myRandomTextureParameter;
	private CGparameter _myPositionTextureParameter;
	private CGparameter _myParticleCrystalTextureParameter;

	private CGparameter _myTextureOffsetParameter;
	private CGparameter _myBoundaryParameter;
	private CGparameter _mySpeedParameter;
	private CGparameter _myReplacementParameter;

	private CCTexture2D _myRandomTexture;

	private CCShaderTexture _myInputTexture;
	private CCShaderTexture _myOutputTexture;

	private CCCGShader _myInitValueShader;

	private int _myParticlesSizeX;
	private int _myParticlesSizeY;

	/* USED FOR CRYSTALIZATION */

	private CCCGShader _myCrystalShader;
	private CGparameter _myCrystalTextureParameter;
	private CGparameter _myParticleTextureParameter;

	private CCShaderTexture _myParticlesTexture;
	private CCShaderTexture _myCrystalInputTexture;
	private CCShaderTexture _myCrystalOutputTexture;

	private int _myWidth;
	private int _myHeight;

	private CCVBOMesh _myParticlesMesh;

	public CCGPUDLA(CCGraphics theGraphics, final int theParticlesSizeX, final int theParticlesSizeY, final int theWidth, final int theHeight) {
		g = theGraphics;
		_myParticlesSizeX = theParticlesSizeX;
		_myParticlesSizeY = theParticlesSizeY;

		_myWidth = theWidth;
		_myHeight = theHeight;

		_myParticleShader = new CCCGShader(null, CCIOUtil.classPath(this,"particles.fp"));
		_myRandomTextureParameter = _myParticleShader.fragmentParameter("randomTexture");
		_myPositionTextureParameter = _myParticleShader.fragmentParameter("positionTexture");
		_myParticleCrystalTextureParameter = _myParticleShader.fragmentParameter("crystalTexture");

		_myTextureOffsetParameter = _myParticleShader.fragmentParameter("texOffset");
		_myBoundaryParameter = _myParticleShader.fragmentParameter("boundary");
		_mySpeedParameter = _myParticleShader.fragmentParameter("speed");
		_myReplacementParameter = _myParticleShader.fragmentParameter("replacement");
		_myParticleShader.load();

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(this,"initvalue.fp"));
		_myInitValueShader.load();

		_myRandomTexture = new CCTexture2D(CCTextureIO.newTextureData(CCIOUtil.classPath(this,"random.png")), CCTextureTarget.TEXTURE_RECT);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);

		_myInputTexture = new CCShaderTexture(32, 3, _myParticlesSizeX, _myParticlesSizeY);
		_myOutputTexture = new CCShaderTexture(32, 3, _myParticlesSizeX, _myParticlesSizeY);

		_myParticlesMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticlesSizeX * _myParticlesSizeY);

		initializeParticles();

		_myCrystalShader = new CCCGShader(null, CCIOUtil.classPath(this,"crystal.fp"));
		_myCrystalTextureParameter = _myCrystalShader.fragmentParameter("crystalTexture");
		_myParticleTextureParameter = _myCrystalShader.fragmentParameter("particleTexture");
		_myCrystalShader.load();

		_myParticlesTexture = new CCShaderTexture(32, 3, _myWidth, _myHeight);
		_myCrystalInputTexture = new CCShaderTexture(32, 3, _myWidth, _myHeight);
		_myCrystalOutputTexture = new CCShaderTexture(32, 3, _myWidth, _myHeight);
		// initializeCrystal();
	}

	private void initializeParticles() {
		// Render velocity.
		_myInputTexture.beginDraw();
		_myInitValueShader.start();

		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < _myParticlesSizeX; x++) {
			for (int y = 0; y < _myParticlesSizeY; y++) {
				g.textureCoords(0, new CCVector3f(CCMath.random(_myWidth), CCMath.random(_myHeight)));
				g.vertex(x, y);// ) + offsetY /* + offsetY shouldn't be */
			}
		}
		g.endShape();

		_myInitValueShader.end();
		_myInputTexture.endDraw();
	}

	public void beginCrystal() {
		_myCrystalInputTexture.beginDraw();
	}

	public void endCrystal() {
		_myCrystalInputTexture.endDraw();
	}

	private void initializeCrystal() {
		// Render velocity.
		_myCrystalInputTexture.beginDraw();
		// _myInitValueShader.start();

		g.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < 10; i++) {
			g.textureCoords(0, new CCVector3f(1, 1, 1));
			g.vertex(CCMath.random(_myWidth), CCMath.random(_myHeight));// ) + offsetY /* + offsetY shouldn't be */
		}
		g.endShape();

		// _myInitValueShader.end();
		_myCrystalInputTexture.endDraw();
	}

	public void reset() {
		_myCrystalInputTexture.clear();
		// initializeCrystal();
		_myCrystalOutputTexture.clear();
	}

	public void update(final float theDeltaTime) {
		/* UPDATE PARTICLES */
		_myParticleShader.texture(_myRandomTextureParameter, _myRandomTexture.id());
		_myParticleShader.texture(_myPositionTextureParameter, _myInputTexture.id());
		_myParticleShader.texture(_myParticleCrystalTextureParameter, _myCrystalInputTexture.id());
		_myParticleShader.start();
		_myParticleShader.parameter(_myTextureOffsetParameter, new CCVector2f((int) CCMath.random(500), (int) CCMath.random(500)));

		_myParticleShader.parameter(_mySpeedParameter, _cParticleSpeed * theDeltaTime);
		_myParticleShader.parameter(_myReplacementParameter, _cParticleReplacement * theDeltaTime);
		_myParticleShader.parameter(_myBoundaryParameter, new CCVector2f(_myWidth, _myHeight));

		_myOutputTexture.draw();
		_myParticleShader.end();

		_myParticlesMesh.vertices(_myOutputTexture, _myOutputTexture.id(0));

		CCShaderTexture myTemp = _myInputTexture;
		_myInputTexture = _myOutputTexture;
		_myOutputTexture = myTemp;

		/* UPDATE CRYSTAL */
		_myParticlesTexture.beginDraw();
		g.clear();
		_myParticlesMesh.draw(g);
		_myParticlesTexture.endDraw();

		_myCrystalShader.texture(_myParticleTextureParameter, _myParticlesTexture.id());
		_myCrystalShader.texture(_myCrystalTextureParameter, _myCrystalInputTexture.id());
		_myCrystalShader.start();

		_myCrystalOutputTexture.draw();

		_myCrystalShader.end();

		myTemp = _myCrystalInputTexture;
		_myCrystalInputTexture = _myCrystalOutputTexture;
		_myCrystalOutputTexture = myTemp;
	}

	public void draw(CCGraphics g) {
		g.image(_myCrystalOutputTexture, -_myWidth / 2, -_myHeight / 2);
		// _myParticlesMesh.draw(g);
	}

	public CCTexture2D dlaTexture() {
		return _myCrystalOutputTexture;
	}
}

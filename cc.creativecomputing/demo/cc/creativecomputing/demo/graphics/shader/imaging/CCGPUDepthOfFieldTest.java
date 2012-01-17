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
package cc.creativecomputing.demo.graphics.shader.imaging;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.imaging.CCGPUDepthOfField;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

/**
 * @author christianriekoff
 * 
 */
public class CCGPUDepthOfFieldTest extends CCApp {

	@CCControlClass(name = "dof")
	private static class CCGPUDoFControls {
		@CCControl(name = "focalDistance", min = 0, max = 1000)
		private static float focalDistance = 0;
		@CCControl(name = "focalRange", min = -0, max = 500)
		private static float focalRange = 0;
	}

	public static float DEFAULT_DISTANCE = 9.0f;
	public static float DEFAULT_RANGE = 6.0f;

	int i;
	int interval = 1;
	float _myMoveZ = 0.0f, _myRotateY = 0.0f;
	boolean _myIsMoving = false, _myIsRotating = true, bg = true;
	float focalDistance = DEFAULT_DISTANCE;
	float focalRange = DEFAULT_RANGE;

	private CCTexture2D _myTexture0;
	private CCTexture2D _myTexture1;
	
	private CCGPUDepthOfField _myDepthOfField;
	
	private CCArcball _myArcball;
	
	private CCMesh _myMesh;

	public void setup() {
		frameRate(40);
		addControls(CCGPUDepthOfFieldTest.class);
		_myUI.drawBackground(false);

		_myTexture0 = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/tex0.jpg"));
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/tex1.png"));
		
		_myDepthOfField = new CCGPUDepthOfField(g, width, height);
		
		_myArcball = new CCArcball(this);
		
		_myMesh = new CCVBOMesh(CCDrawMode.LINES);
		
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for(int i = 0; i < 1000;i++) {
			CCVector3f myVector = CCVecMath.random3f(CCMath.random(2000));
//			myVector.z(-2000 + 4f * i);
			myVertices.add(myVector);
		}
		_myMesh.vertices(myVertices);
		g.pointSize(5);
	}
	
	public void update(final float theDeltaTime) {
		if (_myIsMoving)
			_myMoveZ += theDeltaTime * interval;
		if (_myIsRotating)
			_myRotateY += interval * theDeltaTime * 10;
	}

	public void draw() {
		_myDepthOfField.begin();
		_myDepthOfField.focalDistance(CCGPUDoFControls.focalDistance);
		_myDepthOfField.focalRange(CCGPUDoFControls.focalRange);
		
		g.clear();
		g.clearColor(0);
		_myArcball.draw(g);
		g.translate(0, -80, 100);
		g.rotateX(15);
		g.noBlend();

		
		
		/* Brick room */
		g.texture(_myTexture0);
		if (bg) {
			DrawCube(640, true);
		}
		g.noTexture();

		/* Rotating cubes */
		g.texture(_myTexture1);
		for (i = 0; i < 6; i++) {
			g.pushMatrix();
			g.translate(0.0f, 0.0f, 130.0f * CCMath.sin(_myMoveZ)+150);
			g.translate(-200.0f, 0.0f, 0.0f);
			g.translate(i * 100.0f, 0.0f, i * (-100.0f));
			g.rotate(_myRotateY, 0.0f, 1.0f, 0.0f);
			DrawCube(100, false);
			g.popMatrix();
		}
		g.noTexture();

		_myDepthOfField.end();
		
//		System.out.println(frameRate);
	}

	void DrawCube(final float theSize, boolean open) {
		g.beginShape(CCDrawMode.QUADS);
		if (!open) {
			/* Front Face */
			g.textureCoords(1.0f, 1.0f);
			g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 1.0f);
			g.vertex(theSize / 2, -theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 0.0f);
			g.vertex(theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(1.0f, 0.0f);
			g.vertex(-theSize / 2, theSize / 2, theSize / 2);
		}
		/* Back Face */
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		if (!open) {
			/* Top Face */
			g.textureCoords(1.0f, 0.0f);
			g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
			g.textureCoords(1.0f, 1.0f);
			g.vertex(-theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 1.0f);
			g.vertex(theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 0.0f);
			g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		}
		/* Bottom Face */
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, theSize / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
		// Right face
		g.textureCoords(0.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, theSize / 2);
		// Left Face
		g.textureCoords(1.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
		g.endShape();
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case CCKeyEvent.VK_P:
			_myIsMoving = !_myIsMoving;
			break;
		case CCKeyEvent.VK_R:
			_myIsRotating = !_myIsRotating;
			break;
		case CCKeyEvent.VK_B:
			bg = !bg;
			break;
		case CCKeyEvent.VK_F:
			System.out.printf("focalDistance = %f\n", focalDistance);
			System.out.printf("focalRange = %f\n", focalRange);
			break;
		case CCKeyEvent.VK_Y:
			focalDistance -= 5f;
			break;
		case CCKeyEvent.VK_Q:
			focalDistance = DEFAULT_DISTANCE;
			break;
		case CCKeyEvent.VK_A:
			focalDistance += 5f;
			break;
		case CCKeyEvent.VK_X:
			focalRange -= 5f;
			break;
		case CCKeyEvent.VK_W:
			focalRange = DEFAULT_RANGE;
			break;
		case CCKeyEvent.VK_S:
			focalRange += 5f;
			break;
		case CCKeyEvent.VK_E:
			focalDistance = DEFAULT_DISTANCE;
			focalRange = DEFAULT_RANGE;
			_myMoveZ = 0.0f;
			_myRotateY = 0.0f;
			break;
		case CCKeyEvent.VK_1:
			focalDistance = 9.0f;
			break;
		case CCKeyEvent.VK_2:
			focalDistance = 12.5f;
			break;
		case CCKeyEvent.VK_3:
			focalDistance = 17.5f;
			break;
		case CCKeyEvent.VK_4:
			focalDistance = 21.0f;
			break;
		case CCKeyEvent.VK_5:
			focalDistance = 25.0f;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUDepthOfFieldTest.class);
		myManager.settings().size(640, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

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
package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.util.CCArcball;

public class CCOpenNIRenderDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIRenderer _myRenderer;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myRenderer = new CCOpenNIRenderer(_myOpenNI); 
		
		_myOpenNI.start();
		g.perspective(95, width / (float) height, 10, 150000);
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", _myRenderer);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		
		
		g.clear();
		_myArcball.draw(g);
		g.translate(0, 0, -1000);
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myRenderer.drawSceneMesh(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIRenderDemo.class);
		myManager.settings().size(1400, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


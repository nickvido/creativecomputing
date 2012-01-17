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
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIGestureGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIGestureListener;
import cc.creativecomputing.cv.openni.CCOpenNIHand;
import cc.creativecomputing.cv.openni.CCOpenNIHandGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIHandListener;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

/**
 * This demos shows how to use the gesture/hand generator. 
 * It's not the most reliable yet, a two hands example will follow
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIGestureGeneratorDemo extends CCApp {
	
	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIHandGenerator _myHandGenerator;
	private CCOpenNIGestureGenerator _myGestureGenerator;
	private CCOpenNIRenderer _myRenderer;

	public void setup() {
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.mirror(false);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myRenderer = new CCOpenNIRenderer(_myOpenNI); 

		_myDepthGenerator = _myOpenNI.createDepthGenerator();

		// enable hands + gesture generation
		_myHandGenerator = _myOpenNI.createHandGenerator();
		_myHandGenerator.historySize(30);
		_myHandGenerator.events().add(new CCOpenNIHandListener() {
			
			@Override
			public void onUpdateHands(int theID, CCVector3f thePosition, float theTime) {}
			
			public void onDestroyHands(int theID, float theTime) {
				_myGestureGenerator.addGestures(CCOpenNIGestureGenerator.RAISE_HAND);
			}
			
			public void onCreateHands(int theID, CCVector3f thePosition, float theTime) {}
		});

		_myGestureGenerator = _myOpenNI.createGestureGenerator();
		_myGestureGenerator.addGestures(
			CCOpenNIGestureGenerator.WAVE,
			CCOpenNIGestureGenerator.CLICK,
			CCOpenNIGestureGenerator.RAISE_HAND
		);
		
		_myGestureGenerator.events().add(new CCOpenNIGestureListener() {
			
			@Override
			public void onRecognizeGesture(String theGesture, CCVector3f theIdPosition, CCVector3f theEndPosition) {
				System.out.println(
					"onRecognizeGesture - " +
					"strGesture: " + theGesture + ", " +
					"idPosition: " + theIdPosition + ", " +
					"endPosition:" + theEndPosition
				);
				_myGestureGenerator.removeGestures(theGesture);
				_myHandGenerator.startTracking(theEndPosition);
			}
			
			public void onProgressGesture(String theGesture, CCVector3f thePosition, float theProgress) {}
		});
		_myOpenNI.start();
		g.perspective(95.0f, (float) width / height, 10.0f, 150000.0f);
	}

	public void draw() {
		g.clear();

		_myArcball.draw(g);

		g.translate(0, 0, -1000);
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myRenderer.drawSceneMesh(g);
		
		g.color(255, 0, 0, 200);
		
		for(CCOpenNIHand myHand:_myHandGenerator.hands()) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (CCVector3f myPosition : myHand.history()) {
				g.vertex(myPosition);
			}
			g.endShape();
			
			g.color(255, 0, 0);
			g.strokeWeight(4);
			g.point(myHand.position());
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIGestureGeneratorDemo.class);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}

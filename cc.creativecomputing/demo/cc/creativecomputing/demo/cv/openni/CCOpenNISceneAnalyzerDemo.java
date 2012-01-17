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
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;

/**
 * @author Max Rheiner
 * @author christianriekoff
 * 
 */
public class CCOpenNISceneAnalyzerDemo extends CCApp {

	private CCOpenNI _myOpenNI;

	private CCOpenNISceneAnalyzer _mySceneGenerator;

	public void setup() {
		
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");

		// mirror is by default enabled

		// enable depthMap generation
		_mySceneGenerator = _myOpenNI.createSceneAnalyzer();
		_myOpenNI.start();
	}

	public void draw() {

		g.clear();
		g.image(_mySceneGenerator.texture(), 0, 0);
		
		System.out.println(frameRate+":"+_mySceneGenerator.rawData());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISceneAnalyzerDemo.class);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}

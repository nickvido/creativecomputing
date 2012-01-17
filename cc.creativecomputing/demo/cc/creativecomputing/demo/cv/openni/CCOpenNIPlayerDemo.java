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

/**
 * @author christianriekoff
 *
 */
package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIPlayer;

public class CCOpenNIPlayerDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIPlayer _myPlayer;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myPlayer = _myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		System.out.println("number of frames:" + _myPlayer.numberOfFrames(_myDepthGenerator));
		System.out.println("format:          " + _myPlayer.format());
		System.out.println("source:          " + _myPlayer.source());
		_myOpenNI.start();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.image(_myDepthGenerator.texture(),-320,-240);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIPlayerDemo.class);
		myManager.settings().size(640, 480);
		myManager.start();
	}
}


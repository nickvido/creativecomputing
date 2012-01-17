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
package cc.creativecomputing.demo.cv;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.CCBlobTracker;
import cc.creativecomputing.cv.CCPixelRaster;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCBlobtrackerDemo extends CCApp {
	
	private CCBlobTracker _myBlobTracker;
	private CCPixelRaster _myRaster;
	private CCTextureData _myTextureData;

	@Override
	public void setup() {
		_myBlobTracker = new CCBlobTracker(this,400,400,width,height);
		_myTextureData = CCTextureIO.newTextureData("textures/noisetexture.png");
		_myRaster = new CCPixelRaster(_myTextureData);
		frameRate(1);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		System.out.println(frameRate);
		_myBlobTracker.onUpdateRaster(new CCPixelRaster(_myTextureData));
		_myBlobTracker.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlobtrackerDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

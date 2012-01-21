/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCMovieFramesDemo extends CCApp {

	private CCGStreamerMovie _myData;
	private CCVideoTexture _myTexture;
	
	private int _myNewFrame;
	
	private CCText _myText;
	
	@Override
	public void setup() {
		_myText = new CCText(CCFontIO.createVectorFont("arial", 24));
		
		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/station.mov"));
		_myTexture = new CCVideoTexture(_myData);
		
		_myData.start();
		_myData.goToBeginning();
		_myData.pause();
	}

	

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.image(_myTexture, -width/2, -height/2, width, height);
		
		g.color(240, 20, 30);
		_myText.position(-width/2 + 10, -height/2 + 30);
		_myText.text(_myData.frame() + " / " + (_myData.numberOfFrames() - 1));
		_myText.draw(g);
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case CCKeyEvent.VK_LEFT:
			if (_myNewFrame > 0)
				_myNewFrame--;
			break;
		case CCKeyEvent.VK_RIGHT:
			if (_myNewFrame < _myData.numberOfFrames() - 1)
				_myNewFrame++;
			break;
		}

		_myData.frame(_myNewFrame);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMovieFramesDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


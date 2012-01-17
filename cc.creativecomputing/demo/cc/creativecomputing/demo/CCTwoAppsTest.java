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
package cc.creativecomputing.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings.CCCloseOperation;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

/**
 * This demonstrates how to open one app from another app and set the close operation to hide.
 * @author christianriekoff
 *
 */
public class CCTwoAppsTest extends CCApp {
	
	public static class CCInnerApp extends CCApp{
		private CCTexture2D _myTexture;
		
		public void texture(CCTexture2D theTexture) {
			_myTexture = theTexture;
		}
		
		@Override
		public void setup() {
		}
		
		@Override
		public void draw() {
			g.clear();
			
			g.image(_myTexture, -_myTexture.width() / 2, -_myTexture.height() / 2);
		}
	}
	
	private CCInnerApp _myInnerApp;
	
	private CCTexture2D _myTexture;

	@Override
	public void setup() {

		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("textures/waltz.png"));
		
		CCApplicationManager myManager = new CCApplicationManager(CCInnerApp.class);
		myManager.settings().size(500, 500);
		myManager.settings().closeOperation(CCCloseOperation.HIDE_ON_CLOSE);
		myManager.settings().location(600,200);
		myManager.start();
		
		_myInnerApp = (CCInnerApp)myManager.app();
//		frameRate(1);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture, -_myTexture.width() / 2, -_myTexture.height() / 2);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case CCKeyEvent.VK_S:
			_myInnerApp.show();
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTwoAppsTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().location(100,200);
		myManager.start();
	}
}


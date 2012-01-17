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
package cc.creativecomputing.demo.io;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCClipboard;

public class CCClipboardImageDataDemo extends CCApp {
	
	CCClipboard _myClipboard;
	CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myClipboard = CCClipboard.instance();
		_myTexture = new CCTexture2D(100, 100);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.image(_myTexture, 0,0);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		if((theKeyEvent.isMetaDown() || theKeyEvent.isControlDown()) && theKeyEvent.keyCode() == CCKeyEvent.VK_V) {
			_myTexture.data(_myClipboard.getTextureData());
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCClipboardImageDataDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


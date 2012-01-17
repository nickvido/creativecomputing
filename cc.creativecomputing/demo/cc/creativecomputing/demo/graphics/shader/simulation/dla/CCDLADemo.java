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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCDLADemo extends CCApp {
	
	private CCGPUDLA _myDLA;
	private CCText _myText;
	
	public void setup() {
		_myDLA = new CCGPUDLA(g, 150, 150, width, height);
		_myText = new CCText(CCFontIO.createVectorFont("arial", 360));
		_myText.position(50,250);
		_myText.text("DLA");
		addControls("dla", "dla", _myDLA);
		CCFontIO.printFontList();
		
		_myDLA.beginCrystal();
		_myText.draw(g);
		_myDLA.endCrystal();
	}
	
	public void update(final float theDeltaTime) {
		_myDLA.update(theDeltaTime);
	}

	public void draw() {
		g.clear();
		g.blend(CCBlendMode.LIGHTEST);
		_myDLA.draw(g);
		g.blend();
		System.out.println(frameRate);
	}
	
	public void keyPressed(final CCKeyEvent theEvent) {
		_myDLA.reset();
		_myDLA.beginCrystal();
		_myText.draw(g);
		_myDLA.endCrystal();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDLADemo.class);
		myManager.settings().size(800, 800);
		myManager.start();
	}
}


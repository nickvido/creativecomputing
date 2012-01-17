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
package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.modulators.CCEnvelope;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;

public class CCUIEnvelopeTest extends CCApp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private static class Circle{
		static final float DEFAULT_RADIUS = 50;
		
		public Circle() {
		}
		
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2)
		private float _myX = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2)
		private float _myY = 0;
		
		@CCControl(name = "radius", min = 1, max = 50)
		private float _myRadius = 0;
		
		@CCControl(name = "envelope", min = 0, max = 100, length=4, numberOfEnvelopes=4)
		public CCEnvelope _myEnvelope = new CCEnvelope();
		
		public void draw(CCGraphics g) {
			g.ellipse(_myX, _myY, _myEnvelope.value());
		}
	}
	
	private Circle _myCircle1;
	private Circle _myCircle2;

	@Override
	public void setup() {
		frameRate(30);
		
		_myCircle1 = new Circle();
		_myCircle2 = new Circle();
		
		addControls("circles", "circle1", 0, _myCircle1);
		addControls("circles", "circle2", 1, _myCircle2);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myCircle1.draw(g);
		_myCircle2.draw(g);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	int index = 0;
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		_myCircle1._myEnvelope.play(index++);
		index%=4;
		_myCircle2._myEnvelope.play();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIEnvelopeTest.class);
		myManager.settings().size(APP_WIDTH, APP_HEIGHT);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


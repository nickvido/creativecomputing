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
package cc.creativecomputing.demo.protocol.udp;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.udp.CCUDPByteOutputStream;
import cc.creativecomputing.protocol.udp.CCUDPOut;

public class CCUDPOutDemo extends CCApp {
	
	@CCControl(name = "variation", min = 0, max = 20)
	private float _cVariation = 0;
	
	@CCControl(name = "loop")
	private boolean _cLoop = false;
	
	@CCControl(name = "loop end", min = 10, max = 1000)
	private int _cLoopEnd = 100;
	
	private CCUDPOut _myUDPOut;

	@Override
	public void setup() {
		_myUDPOut = new CCUDPOut(53551);
		
		addControls("app", "app", this);
	}
	
	int myCounter = 0;

	long _myTimeInMillis = 0;
	long _myMillisFrom = 0;
	long _myLastMillis = 0;
	
	float _myTimeBreak = 200;
	
	@Override
	public void update(final float theDeltaTime) {
		
		if(_myLastMillis == 0) {
			_myLastMillis = System.currentTimeMillis();
		}
		long myMillis = System.currentTimeMillis();
		
		_myTimeInMillis += myMillis - _myLastMillis;
		_myMillisFrom += myMillis - _myLastMillis;
		_myLastMillis = myMillis;
		
		if(_myMillisFrom > _myTimeBreak) {
			_myMillisFrom -= _myTimeBreak;
			
			_myTimeBreak = CCMath.random(200 - _cVariation,200 + _cVariation);
			
			CCUDPByteOutputStream myOutputStream = new CCUDPByteOutputStream();
			myOutputStream.write("play"+myCounter);
			_myUDPOut.send(myOutputStream);
			
			myCounter++;
			
			if(_cLoop && myCounter > _cLoopEnd){
				myCounter = 0;
			}
		}
		
	}

	@Override
	public void draw() {
		g.clear();

		g.text("myCounter:" + myCounter,0,20);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		myCounter = 0;
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUDPOutDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


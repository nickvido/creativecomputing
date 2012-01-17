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
import cc.creativecomputing.protocol.udp.CCUDPByteInputStream;
import cc.creativecomputing.protocol.udp.CCUDPIn;
import cc.creativecomputing.protocol.udp.CCUDPListener;

public class CCUPDInDemo extends CCApp {

	private CCUDPIn _myUDPIn;
	
	private long _myLastTime = System.currentTimeMillis();
	private long _myOffset = 0;
	private long _myLastDifference = 0;
	
	@Override
	public void setup() {
		_myUDPIn = new CCUDPIn(53551);
		
		
		_myUDPIn.addListener(new CCUDPListener() {
			
			@Override
			public void onUDPInput(CCUDPByteInputStream theInput) {
				while(theInput.hasBytes()) {
					long myTime = System.currentTimeMillis();
					_myOffset += myTime - _myLastTime - _myLastDifference;
					System.out.println(theInput.readString()+":"+(myTime - _myLastTime)+":"+_myOffset);
					_myLastDifference = myTime - _myLastTime;
					_myLastTime = myTime;
				}
			}
		});
		
		_myUDPIn.startListening();
		
		
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUPDInDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


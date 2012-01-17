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
package cc.creativecomputing.demo.protocol.osc;

import java.net.SocketAddress;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.osc.CCOSCChannel;
import cc.creativecomputing.protocol.osc.CCOSCChannelAttributes;
import cc.creativecomputing.protocol.osc.CCOSCIn;
import cc.creativecomputing.protocol.osc.CCOSCListener;
import cc.creativecomputing.protocol.osc.CCOSCMessage;
import cc.creativecomputing.protocol.osc.CCOSCOut;
import cc.creativecomputing.protocol.osc.CCOSCProtocol;

public class CCOSCByteStreamDemo extends CCApp {
	
	private CCOSCOut _myOut;
	private CCOSCIn _myIn;
	
	@CCControl(name = "out", min = 0, max = 1)
	private float _cOut = 0;
	
	private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
	
	float myColor2 = 0;

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myOut = CCOSCChannel.createOut(new CCOSCChannelAttributes(CCOSCProtocol.UDP),"127.0.0.1",9000);
		_myOut.connect();
		
		_myIn = CCOSCChannel.createIn(new CCOSCChannelAttributes(CCOSCProtocol.UDP, 9000));
		_myIn.startListening();
		_myIn.addOSCListener(new CCOSCListener() {
			
			@Override
			public void messageReceived(CCOSCMessage theMessage, SocketAddress theSender, long theTime) {
				byte[] myBytes = theMessage.blobArgument(0);
				for(byte myByte:myBytes) {
					myColor2 = unsignedByteToInt(myByte) / 255f;
//					System.out.println(unsignedByteToInt(myByte) / 255f);
				}
			}
			
		});
		
		frameRate(20);
	}
	
	

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 20;i++){
			CCOSCMessage myMessage = new CCOSCMessage("/test");
			byte[] myByte = new byte[70];
			for(int j = 0; j < 70;j++){
				float number = CCMath.random();
				myByte[j] = (byte)(_cOut * 255);
			}
			myMessage.add(myByte);
			_myOut.send(myMessage);
		}
	}

	@Override
	public void draw() {
		g.clear();
		g.color(_cOut);
		g.rect(0,0,100,100);
		g.color(myColor2);
		g.rect(100,0,100,100);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCByteStreamDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


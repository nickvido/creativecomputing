package cc.creativecomputing.demo.protocol.osc;


import java.net.SocketAddress;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.protocol.osc.CCOSCChannel;
import cc.creativecomputing.protocol.osc.CCOSCChannelAttributes;
import cc.creativecomputing.protocol.osc.CCOSCIn;
import cc.creativecomputing.protocol.osc.CCOSCListener;
import cc.creativecomputing.protocol.osc.CCOSCMessage;
import cc.creativecomputing.protocol.osc.CCOSCProtocol;

public class CCOSCInDemo extends CCApp {
	
	private CCOSCIn _myIn;

	@Override
	public void setup() {
		_myIn = CCOSCChannel.createIn(new CCOSCChannelAttributes(CCOSCProtocol.UDP, 9000));
		_myIn.startListening();
		_myIn.addOSCListener(new CCOSCListener() {
			
			@Override
			public void messageReceived(CCOSCMessage theMessage, SocketAddress theSender, long theTime) {
				System.out.println("RECEIVE:"+theMessage);
			}
			
		});
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCInDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}

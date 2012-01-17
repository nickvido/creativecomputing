package cc.creativecomputing.demo.protocol.osc;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.osc.CCOSCChannel;
import cc.creativecomputing.protocol.osc.CCOSCChannelAttributes;
import cc.creativecomputing.protocol.osc.CCOSCMessage;
import cc.creativecomputing.protocol.osc.CCOSCOut;
import cc.creativecomputing.protocol.osc.CCOSCProtocol;

public class CCOSCOutDemo extends CCApp {
	
	private CCOSCOut _myOut;

	@Override
	public void setup() {
		_myOut = CCOSCChannel.createOut(new CCOSCChannelAttributes(CCOSCProtocol.UDP),"127.0.0.1",9000);
		_myOut.connect();
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 20;i++){
			CCOSCMessage myMessage = new CCOSCMessage("/test");
			for(int j = 0; j < 70;j++){
				float number = CCMath.random(500);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
			}
			_myOut.send(myMessage);
		}
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCOutDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}

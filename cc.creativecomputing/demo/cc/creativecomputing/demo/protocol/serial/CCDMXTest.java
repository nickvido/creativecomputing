package cc.creativecomputing.demo.protocol.serial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;
import cc.creativecomputing.protocol.serial.dmx.CCDMXListener;
import cc.creativecomputing.protocol.serial.dmx.CCDMXMessage;

public class CCDMXTest extends CCApp implements CCDMXListener {

	private CCDMX _myDMX;
	
	private CCTextureMapFont _myFont;

	public void setup() {
		
		_myFont = CCFontIO.createTextureMapFont("Arial", 10);
		g.textFont(_myFont);

		_myDMX = new CCDMX(this, "/dev/cu.usbserial-ENQWD1SM");
		_myDMX.getInterfaceData();
		_myDMX.getSerial();
		_myDMX.addDMXListener(this);
	}

	int[] theData = new int[0];

	public void draw() {
		g.clear();
		int y = -200;
		int x = -200;
		for (int i = 0; i < theData.length; i++) {

			g.color(theData[i]);
			g.rect(x,y-22, 19, 21);
			g.color(255,0,0);
			g.text(theData[i],x,y);
			x+=20;
			if(i%27==0){
				x = -200;
				y +=22;
			}
		}
		// System.out.println();
		// System.out.println(_mySerial.firmwareVersion);
		// System.out.println(_mySerial.breakTime);
		// System.out.println(_mySerial.markAfterBreakTime);
		// System.out.println(_mySerial.refreshRate);
		// System.out.println(_mySerial.serial);
		//		
		// _mySerial.setDMXChannel(0, mouseX);
		// _mySerial.send();
		
		System.out.println(frameRate);
	}

	@Override
	public void mousePressed(CCMouseEvent theEvent) {

	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCDMXTest.class);
		myManager.settings().size(800, 500);
		myManager.start();
	}

	public void onDMXIn(CCDMXMessage theMessage) {
		theData = theMessage.data();
		for (int i = 0; i < theMessage.data().length; i++) {

		}
	}
}

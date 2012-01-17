package cc.creativecomputing.demo.protocol.serial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.protocol.serial.CCISerialListener;
import cc.creativecomputing.protocol.serial.CCSerial;

public class CCSerialTest extends CCApp implements CCISerialListener{

	private CCSerial _mySerial;
	
	public void setup(){
		for(String myPort:CCSerial.list()){
			System.out.println(myPort);
		}
		
		_mySerial = new CCSerial(this, this, "COM1",57600);
	}
	
	public void draw(){
		
	}
	
	private StringBuffer _myMessageBuffer = new StringBuffer();

	public void onSerialEvent(CCSerial theSerial) {
		char myChar = theSerial.readChar();
		if(myChar == '\n'){
			System.out.println(_myMessageBuffer.toString().trim());
			_myMessageBuffer = new StringBuffer();
			return;
		}
		_myMessageBuffer.append(myChar);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myApplicationManager = new CCApplicationManager(CCSerialTest.class);
		myApplicationManager.start();
	}
}

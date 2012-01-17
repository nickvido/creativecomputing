package cc.creativecomputing.protocol.serial.dmx;


public class CCDMXMessage {
	private int[] _myData;
	public CCDMXMessage(final int[] theData){
		_myData = theData;
	}
	
	public int[] data(){
		return _myData;
	}
}

package cc.creativecomputing.protocol.serial;

/**
 * Implement this interface and pass an object of the class to the CCSerial to listen serial events. This is called when
 * data is available. Use one of the read() methods inside the passed CCSerial instance to capture this data. The
 * onSerialEvent() can be set with buffer() inside CCSerial to only trigger after a certain number of data elements are
 * read and can be set with bufferUntil() inside CCSerial to only trigger after a specific character is read.
 * 
 * @shortdesc interface to listen to serial events
 * @author christian riekoff
 * 
 */
public interface CCISerialListener {

	/**
	 * Implement this method and pass an object of the class to the CCSerial to listen serial events. This is called
	 * when data is available. Use one of the read() methods inside the passed CCSerial instance to capture this data.
	 * The onSerialEvent() can be set with buffer() inside CCSerial to only trigger after a certain number of data
	 * elements are read and can be set with bufferUntil() inside CCSerial to only trigger after a specific character is
	 * read. 
	 * 
	 * @shortdesc Called when data is available.
	 * 
	 * @param theSerial the serial port that received data
	 */
	public void onSerialEvent(final CCSerial theSerial);

}

package cc.creativecomputing.protocol.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.util.logging.CCLog;


/**
 * <p>
 * 
 * <pre>
 
	CCUDPIn receiver = new CCUDPIn(7000);
	CCUDPListener listener = new CCUDPListener() {
		public void onUDPInput(CCUDPByteInputStream theInput) {
			System.out.println("Message received!");
		}
	};
	receiver.addListener(listener);

 * </pre>
 * <p>
 */
public class CCUDPIn implements Runnable {
	
	/**
	 * Socket to listen to incoming UDP messages
	 */
	private DatagramSocket _mySocket;
	
	/**
	 * to this port the socket is listening
	 */
	private final int _myPort;

	/**
	 * true if UDPin is listening to incoming events
	 */
	private boolean _myIsListening;
	private boolean _myClose = false;
	
	//Map of listeners reacting on all events
	private List<CCUDPListener> _myListeners = new ArrayList<CCUDPListener>();
	
	/**
	 * Create an OSCPort that listens on the specified port.
	 * @param thePort UDP port to listen on.
	 * @see CCUDPOut
	 */
	public CCUDPIn(final int thePort) {
		try {
			_myPort = thePort;
			_mySocket = new DatagramSocket(_myPort);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public CCUDPIn(final DatagramSocket theSocket) {
		_myPort = theSocket.getLocalPort();
		_mySocket = theSocket;
	}
	
	/**
	 * Close the socket if this hasn't already happened.
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * Run the loop that listens for OSC on a socket until isListening becomes false.
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		byte[] buffer = new byte[1536];
		DatagramPacket packet = new DatagramPacket(buffer, 1536);
	
		while (_myIsListening && !_mySocket.isClosed()) {
			
			try {
				_mySocket.receive(packet);
				for(CCUDPListener myUDPListener:_myListeners) {
					myUDPListener.onUDPInput(new CCUDPByteInputStream(packet.getAddress(), buffer,packet.getLength()));
				}
				
				if(_myClose){
					_mySocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}catch (ArrayIndexOutOfBoundsException e) {
				CCLog.error("To many arguments");
			}
		}
	}
	
	/**
	 * Start listening for incoming OSCPackets
	 */
	public void startListening() {
		_myIsListening = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Stop listening for incoming OSCPackets
	 */
	public void stopListening() {
		_myIsListening = false;
	}
	
	/**
	 * Am I listening for packets?
	 */
	public boolean isListening() {
		return _myIsListening;
	}
	
	/**
	 * Register the listener for incoming UDPPackets addressed to an address
	 * @param theListener   the object to invoke when a message comes in
	 */
	public void addListener(final CCUDPListener theListener){
		_myListeners.add(theListener);
	}
	
	/**
	 * Close the socket and free-up resources. It's recommended that clients call
	 * this when they are done with the port.
	 */
	public void close() {
		if(_myIsListening){
			_myClose = true;
			_myIsListening = false;
		}else{
			_mySocket.close();
		}
	}
}

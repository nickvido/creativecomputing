/*
 * 
 */
package cc.creativecomputing.protocol.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Use this class to send out UDP messages. The UDP data
 * will be send to the receiver defined by the net address and port 
 * passed to the constructor. 
 */
public class CCUDPOut{

	private InetAddress _myAddress;
	private DatagramSocket _mySocket;
	private int _myPort;
	private int _myLocalPort;
	
	protected CCUDPOut(final InetAddress theAddress, final int thePort) {
		try {
			_mySocket = new DatagramSocket();
			_myLocalPort = _mySocket.getLocalPort();
			_myAddress = theAddress;
		} catch (SocketException e) {
			throw new RuntimeException("Host "+theAddress+" is unknown!",e);
		}
		_myPort = thePort;
	}

	/**
	 * Create a new UDP out object that sends to the specified receiver. You can
	 * specify the net address and port. If you only specify the port the data
	 * will be send to the local host.
	 * @param theAddress address of the receiver
	 * @param thePort port of the receiver
	 * @see #send(CCOSCBundle)
	 */
	public CCUDPOut(final String theAddress, final int thePort) {
		try {
			_mySocket = new DatagramSocket();
			_myAddress = InetAddress.getByName(theAddress);
			_myLocalPort = _mySocket.getLocalPort();
		} catch (Exception e) {
			throw new CCUDPException("Host "+theAddress+" is unknown!",e);
		}
		_myPort = thePort;
	}

	public CCUDPOut(final int thePort){
		try {
			_mySocket = new DatagramSocket();
			_myLocalPort = _mySocket.getLocalPort();
			_myAddress = InetAddress.getLocalHost();
		} catch (SocketException e) {
			throw new CCUDPException("No local host!",e);
		} catch (UnknownHostException e) {
			throw new CCUDPException("No local host!",e);
		}
		_myPort = thePort;
	}
	
	public void send(final CCUDPByteOutputStream theByteOutputStream){
		/* send objects by socket */
		final byte[] myByteArray = theByteOutputStream.toByteArray();
		final DatagramPacket myPacket = new DatagramPacket(myByteArray, myByteArray.length, _myAddress, _myPort);
		try {
			_mySocket.send(myPacket);
		} catch (IOException e) {
			throw new CCUDPException("Problems sending udp message:", e);
		}
	}
	
	public void address(InetAddress theAddress) {
		_myAddress = theAddress;
	}
	
	public void address(String theAddress) {
		try {
			_myAddress = InetAddress.getByName(theAddress);
		} catch (Exception e) {
			throw new CCUDPException("Host "+theAddress+" is unknown!",e);
		}
	}

	public int getLocalPort() {
		return _myLocalPort;
	}
	
	public DatagramSocket socket() {
		return _mySocket;
	}
	
	/**
	 * Close the socket if this hasn't already happened.
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		_mySocket.close();
	}
	
	/**
	 * Close the socket and free-up resources. It's recommended that clients call
	 * this when they are done with the port.
	 */
	public void close() {
		_mySocket.close();
	}
}

/*
 *  OSCChannel.java
 *  de.sciss.net (NetUtil)
 *
 *  Copyright (c) 2004-2009 Hanns Holger Rutz. All rights reserved.
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCAtom;
import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * A collection of common constants and methods that apply to all kinds of OSC communicators.
 * 
 * @author Hanns Holger Rutz
 * @author Christian Riekoff
 */
public abstract class CCOSCChannel<ChannelType extends SelectableChannel> {

	/**
	 * Creates a new instance of an osc out, using a specific codec and transport protocol and local socket address.
	 * Note that <code>localAddress</code> specifies the local socket, not the remote (or target) socket. This can be
	 * set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param _myLocalAddress a valid address to use for the OSC socket. If the port is <code>0</code>, an arbitrary free
	 *        port is picked when the out is connected. (you can find out the actual port in this case by calling
	 *        <code>getLocalAddress()</code> after the out was connected).
	 * 
	 * @return the newly created out
	 * 
	 * @throws IOException if a networking error occurs while creating the socket
	 * @throws IllegalArgumentException if an illegal protocol is used
	 * 
	 * @since NetUtil 0.33
	 */
	public static CCOSCOut createOut(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		switch (theAttributes.protocol()) {
		case UDP:
			return new CCOSCUDPOut(theAttributes, theTargetAddress, thePort);
		case TCP:
			return new CCOSCTCPOut(theAttributes, theTargetAddress, thePort);
		}

		throw new IllegalArgumentException("Unknown protocol:" + theAttributes.protocol());

	}

	/**
	 * Creates a new instance of an osc out, using default codec and UDP transport on a given channel. The caller should
	 * ensure that the provided channel's socket was bound to a valid address (using
	 * <code>dch.socket().bind( SocketAddress )</code>). Note that <code>dch</code> specifies the local socket, not the
	 * remote (or target) socket. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param dch the <code>DatagramChannel</code> to use as UDP socket.
	 * @return the newly created out
	 * 
	 * @throws IOException if a networking error occurs while configuring the socket
	 */
	public static CCOSCOut createOut(DatagramChannel theChannel) {
		return createOut(CCOSCPacketCodec.getDefaultCodec(), theChannel);
	}

	/**
	 * Creates a new instance of an osc out, using a specific codec and UDP transport on a given channel. The caller
	 * should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>dch.socket().bind( SocketAddress )</code>). Note that <code>dch</code> specifies the local socket, not the
	 * remote (or target) socket. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param dch the <code>DatagramChannel</code> to use as UDP socket.
	 * @return the newly created out
	 * 
	 * @throws IOException if a networking error occurs while configuring the socket
	 * 
	 * @since NetUtil 0.33
	 */
	public static CCOSCOut createOut(CCOSCPacketCodec theCodec, DatagramChannel theChannel) {
		return new CCOSCUDPOut(theCodec, theChannel);
	}

	/**
	 * Creates a new instance of an osc out, using default codec and TCP transport on a given channel. The caller should
	 * ensure that the provided channel's socket was bound to a valid address (using
	 * <code>sch.socket().bind( SocketAddress )</code>). Furthermore, the channel must be connected (using
	 * <code>connect()</code>) before being able to transmit messages. Note that <code>sch</code> specifies the local
	 * socket, not the remote (or target) socket. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param sch the <code>SocketChannel</code> to use as TCP socket.
	 * @return the newly created out
	 * 
	 * @throws IOException if a networking error occurs while configuring the socket
	 */
	public static CCOSCOut createOut(SocketChannel theChannel) {
		return createOut(CCOSCPacketCodec.getDefaultCodec(), theChannel);
	}

	/**
	 * Creates a new instance of an osc out, using a specific codec and TCP transport on a given channel. The caller
	 * should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>sch.socket().bind( SocketAddress )</code>). Furthermore, the channel must be connected (using
	 * <code>connect()</code>) before being able to transmit messages. Note that <code>sch</code> specifies the local
	 * socket, not the remote (or target) socket. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param sch the <code>SocketChannel</code> to use as TCP socket.
	 * @return the newly created out
	 */
	public static CCOSCOut createOut(CCOSCPacketCodec theCodec, SocketChannel theChannel) {
		return new CCOSCTCPOut(theCodec, theChannel);
	}
	
	/**
	 * Creates a new instance of a revivable osc in, using a specific codec and transport protocol and
	 * local socket address. Note that the <code>port</code> specifies the local socket (at which the in listens),
	 * it does not determine the remote sockets from which messages can be received. If you want to filter out a
	 * particular remote (or target) socket, this can be done using the <code>setTarget</code> method!
	 * <P>
	 * <B>TCP</B> receivers are required to be connected to one particular target, so <code>setTarget</code> is must be
	 * called prior to <code>connect</code> or <code>startListening</code>!
	 * 
	 * @param c the codec to use
	 * @param protocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param localAddress a valid address to use for the OSC socket. If the port is <code>0</code>, an arbitrary free
	 *        port is picked when the in is started. (you can find out the actual port in this case by calling
	 *        <code>getLocalAddress()</code> after the in was started).
	 * 
	 * @return the newly created in
	 */
	public static CCOSCIn createIn(CCOSCChannelAttributes theAttributes){
		switch (theAttributes.protocol()) {
		case UDP:
			return new CCOSCUDPIn(theAttributes);
		case TCP:
			return new CCOSCTCPIn(theAttributes);
		}

		throw new IllegalArgumentException("Unknown protocol:" + theAttributes.protocol());
	}

	/**
	 * Creates a new instance of a non-revivable osc in, using default codec and UDP transport on a
	 * given channel. The caller should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>dch.socket().bind( SocketAddress )</code>). Note that <code>dch</code> specifies the local socket (at which
	 * the in listens), it does not determine the remote sockets from which messages can be received. If you want
	 * to filter out a particular remote (or target) socket, this can be done using the <code>setTarget</code> method!
	 * 
	 * @param theChannel the <code>DatagramChannel</code> to use as UDP socket.
	 * @return the newly created in
	 */
	public static CCOSCIn createIn(DatagramChannel theChannel){
		return createIn(CCOSCPacketCodec.getDefaultCodec(), theChannel);
	}

	/**
	 * Creates a new instance of a non-revivable osc in, using a specific codec and UDP transport on a
	 * given channel. The caller should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>dch.socket().bind( SocketAddress )</code>). Note that <code>dch</code> specifies the local socket (at which
	 * the in listens), it does not determine the remote sockets from which messages can be received. If you want
	 * to filter out a particular remote (or target) socket, this can be done using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param theChannel the <code>DatagramChannel</code> to use as UDP socket.
	 * @return the newly created in
	 */
	public static CCOSCIn createIn(CCOSCPacketCodec theCodec, DatagramChannel theChannel){
		return new CCOSCUDPIn(theCodec, theChannel);
	}

	/**
	 * Creates a new instance of a non-revivable osc in, using default codec and TCP transport on a
	 * given channel. The caller should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>sch.socket().bind( SocketAddress )</code>). Furthermore, the channel must be connected (using
	 * <code>connect()</code>) before being able to receive messages. Note that <code>sch</code> specifies the local
	 * socket (at which the in listens), it does not determine the remote sockets from which messages can be
	 * received. The remote (or target) socket must be explicitly specified using <code>setTarget</code> before trying
	 * to connect!
	 * 
	 * @param theChannel the <code>SocketChannel</code> to use as TCP socket.
	 * @return the newly created in
	 */
	public static CCOSCIn createIn(SocketChannel theChannel){
		return createIn(CCOSCPacketCodec.getDefaultCodec(), theChannel);
	}

	/**
	 * Creates a new instance of a non-revivable osc in, using a specific codec and TCP transport on a
	 * given channel. The caller should ensure that the provided channel's socket was bound to a valid address (using
	 * <code>sch.socket().bind( SocketAddress )</code>). Furthermore, the channel must be connected (using
	 * <code>connect()</code>) before being able to receive messages. Note that <code>sch</code> specifies the local
	 * socket (at which the in listens), it does not determine the remote sockets from which messages can be
	 * received. The remote (or target) socket must be explicitly specified using <code>setTarget</code> before trying
	 * to connect!
	 * 
	 * @param theCodec the codec to use
	 * @param theChannel the <code>SocketChannel</code> to use as TCP socket.
	 * @return the newly created in
	 */
	public static CCOSCIn createIn(CCOSCPacketCodec theCodec, SocketChannel theChannel){
		return new CCOSCTCPIn(theCodec, theChannel);
	}

	/**
	 *	Creates a new instance of an <code>OSCClient</code>, using
	 *	a given codec, a specific transport protocol and local socket address.
	 *	Note that <code>localAdress</code> specifies the
	 *	local socket (at which the receiver listens and from which the transmitter sends),
	 *  it does not determine the
	 *	remote sockets to which the client connects. To specify the remote socket,
	 *	use the <code>setTarget</code> method!
	 *	<P>
	 *
	 *	@param	theAttributes the attributes to use
	 */
	public static CCOSCClient createClient(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		return new CCOSCClient(theAttributes, theTargetAddress, thePort);
	}

	/**
	 * Creates a new instance of an <code>OSCServer</code>, using a given codec, a specific transport protocol and local
	 * socket address.
	 * @param theAttributes the attributes to use
	 * @return the newly created server
	 */
	public static CCOSCServer createServer(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		switch (theAttributes.protocol()) {
		case UDP:
			return new CCOSCUDPServer(theAttributes, theTargetAddress, thePort);
		case TCP:
			return new CCOSCTCPServer(theAttributes);
		}

		throw new IllegalArgumentException("Unknown protocol:" + theAttributes.protocol());
	}

	/**
	 * The default buffer size (in bytes) and maximum OSC packet size (8K at the moment).
	 */
	public static final int DEFAULTBUFSIZE = 8192;
	
	protected ChannelType _myChannel;

	protected CCOSCPacketCodec _myCodec;
	protected final CCOSCProtocol _myProtocol;
	protected final InetSocketAddress _myLocalAddress;
	protected final boolean _myIsRevivable;

	protected CCOSCDumpMode _myDumpMode = CCOSCDumpMode.OFF;
	protected PrintStream _myDumpStream = null;
	
	protected boolean _myAllocateBuffer = true;
	protected int _myBufferSize = DEFAULTBUFSIZE;
	protected ByteBuffer _myByteBuffer = null;
	protected final Object _myBufferSyncObject = new Object(); // buffer (re)allocation

	public CCOSCChannel(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress, boolean theIsRevivable) {
		_myCodec = theCodec;
		_myProtocol = theProtocol;
		_myLocalAddress = theLocalAddress;
		_myIsRevivable = theIsRevivable;
	}
	
	public CCOSCChannel(CCOSCChannelAttributes theAttributes, boolean theIsRevivable) {
		_myCodec = theAttributes.codec();
		_myProtocol = theAttributes.protocol();
		_myLocalAddress = theAttributes.localAddress();
		_myIsRevivable = theIsRevivable;
	}

	/**
	 * Specifies which codec is used in packet coding and decoding.
	 * 
	 * @param theCodec the codec to use
	 */
	public void codec(CCOSCPacketCodec theCodec) {
		_myCodec = theCodec;
	}

	/**
	 * Queries the codec used in packet coding and decoding.
	 * 
	 * @return the current codec of this channel
	 * @see CCOSCPacketCodec#getDefaultCodec()
	 */
	public CCOSCPacketCodec codec() {
		return _myCodec;
	}

	/**
	 * Queries the transport protocol used by this communicator.
	 * 
	 * @return the protocol, such as <code>UDP</code> or <code>TCP</code>
	 */
	public CCOSCProtocol protocol() {
		return _myProtocol;
	}

	/**
	 * Queries the communicator's local socket address. You can determine the host and port from the returned address by
	 * calling <code>getHostName()</code> (or for the IP <code>getAddress().getHostAddress()</code>) and
	 * <code>getPort()</code>.
	 * 
	 * @return the address of the communicator's local socket.
	 */
	public InetSocketAddress localAddress() {
		return _myLocalAddress;
	}

	/**
	 * Adjusts the buffer size for OSC messages. This is the maximum size an OSC packet (bundle or message) can grow to.
	 * 
	 * @param theSize the new size in bytes.
	 * 
	 * @see #bufferSize()
	 */
	public void bufferSize(int theSize) {
		synchronized (_myBufferSyncObject) {
			if (_myBufferSize != theSize) {
				_myBufferSize = theSize;
				_myAllocateBuffer = true;
			}
		}
	}

	/**
	 * Queries the buffer size used for coding or decoding OSC messages. This is the maximum size an OSC packet (bundle
	 * or message) can grow to.
	 * 
	 * @return the buffer size in bytes.
	 * 
	 * @see #bufferSize(int )
	 */
	public int bufferSize() {
		synchronized (_myBufferSyncObject) {
			return _myBufferSize;
		}
	}
	
	protected void checkBuffer() {
		synchronized (_myBufferSyncObject) {
			if (_myAllocateBuffer) {
				_myByteBuffer = ByteBuffer.allocateDirect(_myBufferSize);
				_myAllocateBuffer = false;
			}
		}
	}

	/**
	 * Changes the way processed OSC messages are printed to the standard err console. By default messages are not
	 * printed.
	 * 
	 * @param theMode one of <code>kDumpOff</code> (don't dump, default), <code>kDumpText</code> (dump human readable
	 *        string), <code>kDumpHex</code> (hexdump), or <code>kDumpBoth</code> (both text and hex)
	 * @param theStream the stream to print on, or <code>null</code> which is shorthand for <code>System.err</code>
	 */

	public void dumpOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		_myDumpMode = theMode;
		_myDumpStream = theStream == null ? System.err : theStream;
	}
	
	public void dumpOSC(CCOSCDumpMode theMode) {
		dumpOSC(theMode, null);
	}
	
	void dump(CCOSCPacket thePacket) {
		if (_myDumpMode == CCOSCDumpMode.OFF) return;
			
		_myDumpStream.print("r: ");
			
		if (_myDumpMode == CCOSCDumpMode.TEXT || _myDumpMode == CCOSCDumpMode.BOTH)
			CCOSCPacket.printTextOn(_myDumpStream, thePacket);
		
		if (_myDumpMode == CCOSCDumpMode.HEX|| _myDumpMode == CCOSCDumpMode.BOTH) {
			_myByteBuffer.flip();
			CCOSCPacket.printHexOn(_myDumpStream, _myByteBuffer);
		}
	}

	/**
	 * Disposes the resources associated with the OSC communicator. The object should not be used any more after calling
	 * this method.
	 */
	public abstract void dispose();
	
	/**
	 * Establishes connection for transports requiring connectivity (e.g. TCP). For transports that do not require
	 * connectivity (e.g. UDP), this ensures the communication channel is created and bound.
	 * <P>
	 * Having a connected channel without actually listening to incoming messages is usually not making sense. You can
	 * call <code>startListening</code> without explicit prior call to <code>connect</code>, because
	 * <code>startListening</code> will establish the connection if necessary.
	 * <P>
	 * When a <B>UDP</B> channel is created without an explicit <code>DatagramChannel</code> &ndash; say by calling
	 * <code>OSCReceiver.newUsing( &quot;udp&quot; )</code>, calling <code>connect()</code> will actually create and
	 * bind a <code>DatagramChannel</code>. For a <B>UDP</B> receiver which was created with an explicit
	 * <code>DatagramChannel</code>. However, for <B>TCP</B> receivers, this may throw an <code>IOException</code> if
	 * the receiver was already connected, therefore be sure to check <code>isConnected()</code> before.
	 * 
	 * @see #isConnected()
	 * @see #startListening()
	 */
	public abstract void connect();

	/**
	 * Queries the connection state of the channel.
	 * 
	 * @return <code>true</code> if the channel is connected, <code>false</code> otherwise. For transports that do
	 *         not use connectivity (e.g. UDP) this returns <code>false</code>, if the underlying
	 *         <code>DatagramChannel</code> has not yet been created.
	 * 
	 * @see #connect()
	 */
	public abstract boolean isConnected();
}

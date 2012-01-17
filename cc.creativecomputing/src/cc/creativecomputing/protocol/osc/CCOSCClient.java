/*
 *  OSCClient.java
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
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 *
 *
 *  Changelog:
 *		17-Sep-06	created
 *		14-Oct-06	using revivable channels
 *		02-Jul-07	added codec based factory methods
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * This class groups together a transmitter and receiver, allowing bidirectional OSC communication from the perspective
 * of a client. It simplifies the need to use several objects by uniting their functionality. </P>
 * <P>
 * In the following example, a client for UDP to SuperCollider server (scsynth) on the local machine is created. The
 * client starts a synth by sending a <code>/s_new</code> message, and stops the synth by sending a delayed a
 * <code>/n_set</code> message. It waits for the synth to die which is recognized by an incoming <code>/n_end</code>
 * message from scsynth after we've registered using a <code>/notify</code> command.
 * 
 * <pre>
 * final Object        sync = new Object();
 *     final OSCClient     c;
 *     final OSCBundle     bndl1, bndl2;
 *     final Integer       nodeID;
 *     
 *     try {
 *         c = OSCClient.newUsing( OSCClient.UDP );    // create UDP client with any free port number
 *         c.setTarget( new InetSocketAddress( "127.0.0.1", 57110 ));  // talk to scsynth on the same machine
 *         c.start();  // open channel and (in the case of TCP) connect, then start listening for replies
 *     }
 *     catch( IOException e1 ) {
 *         e1.printStackTrace();
 *         return;
 *     }
 *     
 *     // register a listener for incoming osc messages
 *     c.addOSCListener( new OSCListener() {
 *         public void messageReceived( OSCMessage m, SocketAddress addr, long time )
 *         {
 *             // if we get the /n_end message, wake up the main thread
 *             // ; note: we should better also check for the node ID to make sure
 *             // the message corresponds to our synth
 *             if( m.getName().equals( "/n_end" )) {
 *                 synchronized( sync ) {
 *                     sync.notifyAll();
 *                 }
 *             }
 *         }
 *     });
 *     // let's see what's going out and coming in
 *     c.dumpOSC( OSCChannel.kDumpBoth, System.err );
 *     
 *     try {
 *         // the /notify message tells scsynth to send info messages back to us
 *         c.send( new OSCMessage( "/notify", new Object[] { new Integer( 1 )}));
 *         // two bundles, one immediately (with 50ms delay), the other in 1.5 seconds
 *         bndl1   = new OSCBundle( System.currentTimeMillis() + 50 );
 *         bndl2   = new OSCBundle( System.currentTimeMillis() + 1550 );
 *         // this is going to be the node ID of our synth
 *         nodeID  = new Integer( 1001 + i );
 *         // this next messages creates the synth
 *         bndl1.addPacket( new OSCMessage( "/s_new", new Object[] { "default", nodeID, new Integer( 1 ), new Integer( 0 )}));
 *         // this next messages starts to releases the synth in 1.5 seconds (release time is 2 seconds)
 *         bndl2.addPacket( new OSCMessage( "/n_set", new Object[] { nodeID, "gate", new Float( -(2f + 1f) )}));
 *         // send both bundles (scsynth handles their respective timetags)
 *         c.send( bndl1 );
 *         c.send( bndl2 );
 * 
 *         // now wait for the signal from our osc listener (or timeout in 10 seconds)
 *         synchronized( sync ) {
 *             sync.wait( 10000 );
 *         }
 *         catch( InterruptedException e1 ) {}
 *         
 *         // ok, unsubscribe getting info messages
 *         c.send( new OSCMessage( "/notify", new Object[] { new Integer( 0 )}));
 * 
 *         // ok, stop the client
 *         // ; this isn't really necessary as we call dispose soon
 *         c.stop();
 *     }
 *     catch( IOException e11 ) {
 *         e11.printStackTrace();
 *     }
 *     
 *     // dispose the client (it gets stopped if still running)
 *     c.dispose();
 * </pre>
 * 
 * @see CCOSCOut
 * @see CCOSCIn
 * @see CCOSCServer
 * 
 * @author Hanns Holger Rutz
 * @version 0.37, 12-May-09
 * 
 * @since NetUtil 0.30
 */
public class CCOSCClient<ChannelType extends SelectableChannel> extends CCOSCBidirectionalChannel<ChannelType> {
	private final CCOSCIn<ChannelType> _myIn;
	private final CCOSCOut<ChannelType> _myOut;

	public CCOSCClient(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		super(theAttributes);
		_myIn = createIn(theAttributes);
		_myOut = createOut(theAttributes, theTargetAddress, thePort);
	}

	/**
	 * Specifies the client's target address, that is the address of the server to talk to. You should call this method
	 * only once and you must call it before starting the client or sending messages.
	 * 
	 * @param theTarget the address of the server. Usually you construct an appropriate <code>InetSocketAddress</code>
	 * 
	 * @see InetSocketAddress
	 */
	public void setTarget(InetSocketAddress theTarget) {
		_myIn.setTarget(theTarget);
		_myOut.target(theTarget);
	}

	@Override
	public void codec(CCOSCPacketCodec theCodec) {
		_myIn.codec(theCodec);
		_myOut.codec(theCodec);
	}

	@Override
	public CCOSCPacketCodec codec() {
		return _myIn.codec();
	}

	/**
	 * Initializes network channel (if necessary) and establishes connection for transports requiring connectivity (e.g.
	 * TCP). Do not call this method when the client is already connected. Note that <code>start</code> implicitly calls
	 * <code>connect</code> if necessary, so usually you will not need to call <code>connect</code> yourself.
	 * 
	 * @see #isConnected()
	 * @see #start()
	 */
	public void connect()  {
		_myOut.connect();
	}

	/**
	 * Queries the connection state of the client.
	 * 
	 * @return <code>true</code> if the client is connected, <code>false</code> otherwise. For transports that do not
	 *         use connectivity (e.g. UDP) this returns <code>false</code>, if the underlying
	 *         <code>DatagramChannel</code> has not yet been created.
	 * 
	 * @see #connect()
	 */
	public boolean isConnected() {
		return _myOut.isConnected();
	}

	/**
	 * Sends an OSC packet (bundle or message) to the target network address. Make sure that the client's target has
	 * been specified before by calling <code>setTarget()</code>
	 * 
	 * @param thePacket the packet to send
	 * 
	 * @throws IOException if a write error, OSC encoding error, buffer overflow error or network error occurs, for
	 *         example if a TCP client was not connected before.
	 * @throws NullPointerException for a UDP client if the target has not been specified
	 * 
	 * @see #setTarget(SocketAddress )
	 */
	public void send(CCOSCPacket thePacket) {
		_myOut.send(thePacket);
	}

	/**
	 * Registers a listener that gets informed about incoming messages. You can call this both when the client is active
	 * or inactive.
	 * 
	 * @param theListener the listener to register
	 */
	public void addOSCListener(CCOSCListener theListener) {
		_myIn.addOSCListener(theListener);
	}

	/**
	 * Unregisters a listener that gets informed about incoming messages
	 * 
	 * @param theListener the listener to remove from the list of notified objects.
	 */
	public void removeOSCListener(CCOSCListener theListener) {
		_myIn.removeOSCListener(theListener);
	}

	/**
	 * Starts the client. This calls <code>connect</code> if the transport requires connectivity (e.g. TCP) and the
	 * channel is not yet connected. It then tells the underlying OSC receiver to start listening.
	 * 
	 * @warning in the current version, it is not possible to &quot;revive&quot; clients after the server has closed the
	 *          connection. Also it's not possible to start a TCP client more than once. This might be possible in a
	 *          future version.
	 */
	public void start() {
		if (!_myOut.isConnected()) {
			_myOut.connect();
			try {
				_myIn.setChannel(_myOut.channel());
			} catch (IOException e) {
				throw new CCOSCException(e);
			}
		}
		_myIn.startListening();
	}

	/**
	 * Queries whether the client was activated or not. A client is activated by calling its <code>start()</code> method
	 * and deactivated by calling <code>stop()</code>.
	 * 
	 * @return <code>true</code> if the client is active (connected and listening), <code>false</code> otherwise.
	 * 
	 * @see #start()
	 * @see #stop()
	 */
	public boolean isActive() {
		return _myIn.isListening();
	}

	public void stop()  {
		_myIn.stopListening();
	}

	/**
	 * Adjusts the buffer size for OSC messages (both for sending and receiving). This is the maximum size an OSC packet
	 * (bundle or message) can grow to. The initial buffer size is <code>DEFAULTBUFSIZE</code>. Do not call this method
	 * while the client is active!
	 * 
	 * @param theSize the new size in bytes.
	 * 
	 * @see #isActive()
	 * @see #bufferSize()
	 */
	@Override
	public void bufferSize(int theSize) {
		_myBufferSize = theSize;
		_myIn.bufferSize(theSize);
		_myOut.bufferSize(theSize);
	}

	/**
	 * Changes the way incoming and outgoing OSC messages are printed to the standard err console. By default messages
	 * are not printed.
	 * 
	 * @param theMode one of <code>kDumpOff</code> (don't dump, default), <code>kDumpText</code> (dump human readable
	 *        string), <code>kDumpHex</code> (hexdump), or <code>kDumpBoth</code> (both text and hex)
	 * @param theStream the stream to print on, or <code>null</code> which is shorthand for <code>System.err</code>
	 * 
	 * @see #dumpIncomingOSC(int, PrintStream )
	 * @see #dumpOutgoingOSC(int, PrintStream )
	 */
	@Override
	public void dumpOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		dumpIncomingOSC(theMode, theStream);
		dumpOutgoingOSC(theMode, theStream);
	}

	public void dumpIncomingOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		_myIn.dumpOSC(theMode, theStream);
	}

	public void dumpOutgoingOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		_myOut.dumpOSC(theMode, theStream);
	}

	/**
	 * Destroys the client and frees resources associated with it. This automatically stops the client and closes the
	 * networking channel. Do not use this client instance any more after calling <code>dispose.</code>
	 */
	public void dispose() {
		_myIn.dispose();
		_myOut.dispose();
	}
}
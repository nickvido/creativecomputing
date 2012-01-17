/*
 *  OSCServer.java
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
 *		18-Sep-06	created
 *		02-Jul-07	added codec based factory methods
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * This class dynamically groups together a transmitters and receivers, allowing bidirectional OSC communication from
 * the perspective of a server. It simplifies the need to use several objects by uniting their functionality, and by
 * automatically establishing child connections.
 * <P>
 * In <code>UDP</code> mode, simply one receiver and transmitter are handling all the communication. In <code>TCP</code>
 * mode, a <code>ServerSocketChannel</code> is set up to wait for incoming connection requests. Requests are satisfied
 * by opening a new receiver and transmitter for each connection.
 * <P>
 * In the following example, a simple TCP server is created that accepts connections at port 0x5454. The connections
 * understand the OSC commands <code>/pause</code> (disconnect the server for a few seconds), <code>/quit</code> (quit
 * the server), and <code>/dumpOSC</code> (turn on/off printing of message traffic). Each incoming message is replied
 * with a <code>/done</code> message.
 * 
 * <pre>
 * private boolean pause = false; // (must be an instance or static field to be useable
 * // from an anonymous inner class)
 * 
 * final Object sync = new Object();
 * final OSCServer c;
 * try {
 * 	// create TCP server on loopback port 0x5454
 * 	c = OSCServer.newUsing(OSCServer.TCP, 0x5454, true);
 * } catch (IOException e1) {
 * 	e1.printStackTrace();
 * 	return;
 * }
 * 
 * // now add a listener for incoming messages from
 * // any of the active connections
 * c.addOSCListener(new OSCListener() {
 * 	public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
 * 		// first of all, send a reply message (just a demo)
 * 		try {
 * 			c.send(new OSCMessage(&quot;/done&quot;, new Object[] { m.getName() }), addr);
 * 		} catch (IOException e1) {
 * 			e1.printStackTrace();
 * 		}
 * 
 * 		if (m.getName().equals(&quot;/pause&quot;)) {
 * 			// tell the main thread to pause the server,
 * 			// wake up the main thread
 * 			pause = true;
 * 			synchronized (sync) {
 * 				sync.notifyAll();
 * 			}
 * 		} else if (m.getName().equals(&quot;/quit&quot;)) {
 * 			// wake up the main thread
 * 			synchronized (sync) {
 * 				sync.notifyAll();
 * 			}
 * 		} else if (m.getName().equals(&quot;/dumpOSC&quot;)) {
 * 			// change dumping behaviour
 * 			c.dumpOSC(((Number) m.getArg(0)).intValue(), System.err);
 * 		}
 * 	}
 * });
 * try {
 * 	do {
 * 		if (pause) {
 * 			System.out.println(&quot;  waiting four seconds...&quot;);
 * 			try {
 * 				Thread.sleep(4000);
 * 			} catch (InterruptedException e1) {
 * 			}
 * 			pause = false;
 * 		}
 * 		System.out.println(&quot;  start()&quot;);
 * 		// start the server (make it attentive for incoming connection requests)
 * 		c.start();
 * 		try {
 * 			synchronized (sync) {
 * 				sync.wait();
 * 			}
 * 		} catch (InterruptedException e1) {
 * 		}
 * 
 * 		System.out.println(&quot;  stop()&quot;);
 * 		c.stop();
 * 	} while (pause);
 * } catch (IOException e1) {
 * 	e1.printStackTrace();
 * }
 * 
 * // kill the server, free its resources
 * c.dispose();
 * </pre>
 * 
 * Here is an example of sending commands to this server from SuperCollider:
 * 
 * <pre>
 * n = NetAddr( "127.0.0.1", 0x5454 );
 *     r = OSCresponderNode( n, '/done', { arg time, resp, msg;
 *         ("reply : " ++ msg.asString).postln;
 *     }).add;
 *     n.connect;
 *     n.sendMsg( '/dumpOSC', 3 );
 *     n.sendMsg( '/pause' );
 *     n.isConnected;	// --> false
 *     n.connect;
 *     n.sendMsg( '/quit' );
 *     r.remove;
 * </pre>
 * 
 * @see CCOSCClient
 * 
 * @author Hanns Holger Rutz
 * 
 * @todo should provide means to accept or reject connections
 * @todo should provide means to close particular connections
 */
public abstract class CCOSCServer<ChannelType extends SelectableChannel> extends CCOSCBidirectionalChannel<ChannelType> {

	/**
	 * @param theAttributes
	 */
	public CCOSCServer(CCOSCChannelAttributes theAttributes) {
		super(theAttributes);
	}

	/**
	 * Sends an OSC packet (bundle or message) to the given network address. The address should correspond to one of the
	 * connected clients. Particularly, in <code>TCP</code> mode, trying to send to a client which is not connected will
	 * throw an exception. In a future version of NetUtil, there will be an interface to detect clients connecting and
	 * disconnecting. For now, clients can be implicitly detected by a registered <code>OSCListener</code>.
	 * 
	 * @param p the packet to send
	 * @param target the target address to send the packet to
	 */
	public abstract void send(CCOSCPacket p, SocketAddress target);

	/**
	 * Registers a listener that gets informed about incoming messages (from any of the connected clients). You can call
	 * this both when the server is active or inactive.
	 * 
	 * @param listener the listener to register
	 */
	public abstract void addOSCListener(CCOSCListener listener);

	/**
	 * Unregisters a listener that gets informed about incoming messages
	 * 
	 * @param listener the listener to remove from the list of notified objects.
	 */
	public abstract void removeOSCListener(CCOSCListener listener);

	/**
	 * Starts the server. The server becomes attentive to requests for connections from clients, starts to receive OSC
	 * messages and is able to reply back to connected clients.
	 */
	public abstract void start();

	/**
	 * Checks whether the server is active (was started) or not (is stopped).
	 * 
	 * @return <code>true</code> if the server is active, <code>false</code> otherwise
	 */
	public abstract boolean isActive();

	/**
	 * Stops the server. For <code>TCP</code> mode, this implies that all client connections are closed. Stops listening
	 * for incoming OSC traffic.
	 */
	public abstract void stop();

	public abstract void bufferSize(int size);

	

	/**
	 * Specifies which codec is used in packet coding and decoding for a given client socket.
	 * 
	 * @param c the codec to use
	 * @param target the client's address for whom the codec is changed
	 * @throws IOException if a networking error occurs or the client does not exist
	 * 
	 * @since NetUtil 0.33
	 */
	public abstract void setCodec(CCOSCPacketCodec c, SocketAddress target) throws IOException;

	/**
	 * Queries the codec used in packet coding and decoding for a given client socket.
	 * 
	 * @param target the client's address for whom the codec is queried
	 * @return the current codec of this channel
	 * @throws IOException if a networking error occurs or the client does not exist
	 * 
	 * @see CCOSCPacketCodec#getDefaultCodec()
	 * 
	 * @since NetUtil 0.33
	 */
	public abstract CCOSCPacketCodec getCodec(SocketAddress target) throws IOException;

	public final void dumpOSC(CCOSCDumpMode theMode, PrintStream stream) {
		dumpIncomingOSC(theMode, stream);
		dumpOutgoingOSC(theMode, stream);
	}
}
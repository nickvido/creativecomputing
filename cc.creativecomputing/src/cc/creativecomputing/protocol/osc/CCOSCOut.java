/*
 *  OSCTransmitter.java
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
 *		26-Aug-05	created
 *		30-Sep-06	made abstract (unfortunately not backward compatible), finished TCP support
 *		02-Jul-07	uses OSCPacketCodec, bugfix in newUsing( String, int )
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * A class that sends OSC packets (messages or bundles) to a target OSC server. Each instance takes a network channel,
 * either explictly specified by a <code>DatagramChannel</code> (for UDP) or <code>SocketChannel</code> (for TCP), or
 * internally opened when a protocol type is specified.
 * <P>
 * Messages are send by invoking one of the <code>send</code> methods. For an example, please refer to the
 * <code>OSCReceiver</code> document.
 * <P>
 * <B>Note that as of v0.3,</B> you will most likely want to use preferably one of <code>OSCClient</code> or
 * <code>OSCServer</code> over <code>OSCTransmitter</code>. Also note that as of v0.3, <code>OSCTransmitter</code> is
 * abstract, which renders direct instantiation impossible. <B>To update old code,</B> occurences of
 * <code>new OSCTransmitter()</code> must be replaced by one of the <code>OSCTransmitter.newUsing</code> methods!
 * 
 * @author Hanns Holger Rutz
 * @version 0.33, 05-Mar-09
 * 
 * @see CCOSCClient
 * @see CCOSCServer
 * @see CCOSCIn
 * 
 * @synchronization sending messages is thread safe
 * @todo an explicit disconnect method might be useful (this is implicitly done when calling dispose)
 */
public abstract class CCOSCOut<ChannelType extends SelectableChannel> extends CCOSCChannel<ChannelType> {

	protected SocketAddress _myTarget = null;

	protected CCOSCOut(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress, boolean theRevivable) {
		super(theCodec,theProtocol, theLocalAddress, theRevivable);
	}
	
	protected CCOSCOut(CCOSCChannelAttributes theAttributes, boolean revivable, String theTargetAddress, int thePort) {
		super(theAttributes, revivable);
		_myTarget = new InetSocketAddress(theTargetAddress, thePort);
	}

	/**
	 * Specifies the transmitter's target address, that is the address of the remote side to talk to. You should call
	 * this method only once and you must call it before starting to send messages using the shortcut call
	 * <code>send( OSCPacket )</code>.
	 * 
	 * @param theHost
	 * @param thePort
	 */
	public void target(String theHost, int thePort) {
		_myTarget = new InetSocketAddress(theHost, thePort);
	}
	
	void target(SocketAddress theTarget) {
		_myTarget = theTarget;
	}

	/**
	 * Sends an OSC packet (bundle or message) to the given network address, using the current codec.
	 * 
	 * @param thePacket the packet to send
	 * @param theTarget the target address to send the packet to
	 * 
	 * @throws IOException if a write error, OSC encoding error, buffer overflow error or network error occurs
	 */
	public final void send(CCOSCPacket thePacket, SocketAddress theTarget){
		send(_myCodec, thePacket, theTarget);
	}

	/**
	 * Sends an OSC packet (bundle or message) to the given network address, using a particular codec.
	 * 
	 * @param theCodec the codec to use
	 * @param thePacket the packet to send
	 * @param theTarget the target address to send the packet to
	 */
	public abstract void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket, SocketAddress theTarget);

	/**
	 * Sends an OSC packet (bundle or message) to the default network address, using the current codec. The default
	 * address is the one specified using the <code>setTarget</code> method. Therefore this will throw a
	 * <code>NullPointerException</code> if no default address was specified.
	 * 
	 * @param thePacket the packet to send
	 * 
	 * @see #target(SocketAddress )
	 */
	public final void send(CCOSCPacket thePacket) {
		send(thePacket, _myTarget);
	}

	/**
	 * Sends an OSC packet (bundle or message) to the default network address, using a particular codec. The default
	 * address is the one specified using the <code>setTarget</code> method. Therefore this will throw a
	 * <code>NullPointerException</code> if no default address was specified.
	 * 
	 * @param theCodec the codec to use
	 * @param thePacket the packet to send
	 * 
	 * @see #target(SocketAddress )
	 */
	public abstract void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket);

	public void dispose() {
		_myByteBuffer = null;
		if (_myChannel != null) {
			try {
				_myChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			_myChannel = null;
		}
	}

	protected ChannelType channel() {
		synchronized (_myBufferSyncObject) {
			return _myChannel;
		}
	}
}
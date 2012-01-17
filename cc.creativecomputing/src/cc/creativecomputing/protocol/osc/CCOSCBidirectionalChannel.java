/*
 *  OSCBidi.java
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
 *		30-Sep-06	created
 */

package cc.creativecomputing.protocol.osc;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * An interface describing common functionality in bidirectional OSC communicators.
 * 
 * @author Hanns Holger Rutz
 */
public abstract class CCOSCBidirectionalChannel<ChannelType extends SelectableChannel> extends CCOSCChannel<ChannelType> {
	
	public CCOSCBidirectionalChannel(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress) {
		super(theCodec, theProtocol, theLocalAddress, true);
	}
	
	public CCOSCBidirectionalChannel(CCOSCChannelAttributes theAttributes) {
		super(theAttributes, true);
	}
	
	/**
	 * Starts the communicator.
	 */
	public abstract void start();

	/**
	 * Checks whether the communicator is active (was started) or not (is stopped).
	 * 
	 * @return <code>true</code> if the communicator is active, <code>false</code> otherwise
	 */
	public abstract boolean isActive();

	/**
	 * Stops the communicator.
	 */
	public abstract void stop();

	/**
	 * Changes the way incoming messages are dumped to the console. By default incoming messages are not dumped.
	 * Incoming messages are those received by the client from the server, before they get delivered to registered
	 * <code>OSCListener</code>s.
	 * 
	 * @param theMode see <code>dumpOSC( int )</code> for details
	 * @param theStream the stream to print on, or <code>null</code> which is shorthand for <code>System.err</code>
	 * 
	 * @see #dumpOSC(int, PrintStream )
	 * @see #dumpOutgoingOSC(int, PrintStream )
	 */
	public abstract void dumpIncomingOSC(CCOSCDumpMode theMode, PrintStream theStream);

	/**
	 * Changes the way outgoing messages are dumped to the console. By default outgoing messages are not dumped.
	 * Outgoing messages are those send via <code>send</code>.
	 * 
	 * @param theMode see <code>dumpOSC( int )</code> for details
	 * @param theStream the stream to print on, or <code>null</code> which is shorthand for <code>System.err</code>
	 * 
	 * @see #dumpOSC(int, PrintStream )
	 * @see #dumpIncomingOSC(int, PrintStream )
	 */
	public abstract void dumpOutgoingOSC(CCOSCDumpMode theMode, PrintStream theStream);
}

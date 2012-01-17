/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

public class CCOSCUDPOut extends CCOSCOut<DatagramChannel> {

	protected CCOSCUDPOut(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		super(theAttributes, true, theTargetAddress, thePort);
	}

	protected CCOSCUDPOut(CCOSCPacketCodec theCodec, DatagramChannel theChannel) {
		super(theCodec, CCOSCProtocol.UDP, new InetSocketAddress(theChannel.socket().getLocalAddress(), theChannel.socket().getLocalPort()), false);

		_myChannel = theChannel;
	}

	public void connect(){
		synchronized (_myBufferSyncObject) {
			if ((_myChannel != null) && !_myChannel.isOpen()) {
				if (!_myIsRevivable)
					throw new CCOSCException("Channel cannot be revived");
				_myChannel = null;
			}
			if (_myChannel == null) {
				DatagramChannel newCh;
				try {
					newCh = DatagramChannel.open();
					newCh.socket().bind(_myLocalAddress);
					_myChannel = newCh;
				} catch (IOException e) {
					throw new CCOSCException(e);
				}
			}
		}
	}

	public boolean isConnected() {
		synchronized (_myBufferSyncObject) {
			return ((_myChannel != null) && _myChannel.isOpen());
		}
	}

	public void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket){
		send(theCodec, thePacket, _myTarget);
	}

	public void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket, SocketAddress theTarget){
		try {
			synchronized (_myBufferSyncObject) {
				// if( _myChannel == null ) throw new NotYetConnectedException();
				if (_myChannel == null)
					throw new CCOSCException("Channel not connected");

				checkBuffer();
				_myByteBuffer.clear();
				theCodec.encode(thePacket, _myByteBuffer);
				_myByteBuffer.flip();

				dump(thePacket);
				_myChannel.send(_myByteBuffer, theTarget);
			}
		} catch (BufferOverflowException e1) {
			throw new CCOSCException("OSC Buffer Overflow or Underflow" + (thePacket instanceof CCOSCMessage ? ((CCOSCMessage) thePacket).address() : thePacket.getClass().getName()));
		} catch (IOException e) {
			throw new CCOSCException(e);
		}
	}
}
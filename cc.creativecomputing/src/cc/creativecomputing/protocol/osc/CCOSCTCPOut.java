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
import java.nio.channels.SocketChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

public class CCOSCTCPOut extends CCOSCOut<SocketChannel> {

	public CCOSCTCPOut(CCOSCPacketCodec theCodec, InetSocketAddress theLocalAddress) {
		super(theCodec, CCOSCProtocol.TCP, theLocalAddress, true);
	}

	public CCOSCTCPOut(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort) {
		super(theAttributes, true, theTargetAddress, thePort);
	}

	protected CCOSCTCPOut(CCOSCPacketCodec theCodec, SocketChannel theChannel) {
		super(theCodec, CCOSCProtocol.TCP, new InetSocketAddress(theChannel.socket().getLocalAddress(), theChannel.socket().getLocalPort()), false);

		_myChannel = theChannel;

		if (_myChannel.isConnected())
			target(_myChannel.socket().getRemoteSocketAddress());
	}

	public void connect() {
		synchronized (_myBufferSyncObject) {
			if ((_myChannel != null) && !_myChannel.isOpen()) {
				if (!_myIsRevivable)
					throw new CCOSCException("Channel cannot be revived");
				_myChannel = null;
			}
			try {
				if (_myChannel == null) {
					SocketChannel newCh = SocketChannel.open();

					newCh.socket().bind(_myLocalAddress);
					_myChannel = newCh;
				}
				if (!_myChannel.isConnected()) {
					_myChannel.connect(_myTarget);
				}
			} catch (IOException e) {
				throw new CCOSCException(e);
			}
		}
	}

	public boolean isConnected() {
		synchronized (_myBufferSyncObject) {
			return ((_myChannel != null) && _myChannel.isConnected());
		}
	}

	public void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket, SocketAddress theTarget) {
		synchronized (_myBufferSyncObject) {
			if ((theTarget != null) && !theTarget.equals(this._myTarget))
				throw new CCOSCException("Not bound to address : " + theTarget);

			send(theCodec, thePacket);
		}
	}

	public void send(CCOSCPacketCodec theCodec, CCOSCPacket thePacket) {
		final int len;

		try {
			synchronized (_myBufferSyncObject) {
				// if( _myChannel == null ) throw new NotYetConnectedException();
				if (_myChannel == null)
					throw new CCOSCException("Channel not connected");

				checkBuffer();
				_myByteBuffer.clear();
				_myByteBuffer.position(4);
				theCodec.encode(thePacket, _myByteBuffer);
				len = _myByteBuffer.position() - 4;
				_myByteBuffer.flip();
				_myByteBuffer.putInt(0, len);

				dump(thePacket);

				_myChannel.write(_myByteBuffer);
			}
		} catch (BufferOverflowException e1) {
			throw new CCOSCException("OSC Buffer Overflow or Underflow" + (thePacket instanceof CCOSCMessage ? ((CCOSCMessage) thePacket).address() : thePacket.getClass().getName()));
		} catch (IOException e) {
			throw new CCOSCException(e);
		}
	}
}
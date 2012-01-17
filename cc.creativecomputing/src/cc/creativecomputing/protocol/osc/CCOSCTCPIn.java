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
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

class CCOSCTCPIn extends CCOSCIn<SocketChannel> {

	protected CCOSCTCPIn(CCOSCPacketCodec theCodec, InetSocketAddress theLocalAddress) {
		super(theCodec, CCOSCProtocol.TCP, theLocalAddress, true);
	}
	
	protected CCOSCTCPIn(CCOSCChannelAttributes theAttributes) {
		super(theAttributes, true);
	}

	protected CCOSCTCPIn(CCOSCPacketCodec theCodec, SocketChannel theChannel) {
		super(theCodec, CCOSCProtocol.TCP, new InetSocketAddress(theChannel.socket().getLocalAddress(), theChannel.socket().getLocalPort()), false);

		_myChannel = theChannel;
	}

	protected void setChannel(SocketChannel theChannel) throws IOException {
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCOSCException("Cannot be performed while channel is active");

			_myChannel = theChannel;
			if (!_myChannel.isBlocking()) {
				_myChannel.configureBlocking(true);
			}
		}
	}

	public void setTarget(SocketAddress theTarget) {
		synchronized (_myGeneralSync) {
			if (isConnected())
				throw new AlreadyConnectedException();

			this._myTarget = theTarget;
		}
	}

	public void connect(){
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCOSCException("Cannot be performed while channel is active");

			if ((_myChannel != null) && !_myChannel.isOpen()) {
				if (!_myIsRevivable)
					throw new CCOSCException("Channel cannot be revived");
				_myChannel = null;
			}
			try {
				if (_myChannel == null) {
					final SocketChannel newCh = SocketChannel.open();
					newCh.socket().bind(_myLocalAddress);
					_myChannel = newCh;
				}
				if (!_myChannel.isConnected()) {
					_myChannel.connect(_myTarget);
				}
			}catch(Exception e) {
				throw new CCOSCException(e);
			}
		}
	}

	public boolean isConnected() {
		synchronized (_myGeneralSync) {
			return ((_myChannel != null) && _myChannel.isConnected());
		}
	}

	protected void closeChannel() throws IOException {
		if (_myChannel != null) {
			try {
				// System.err.println( "TCPOSCReceiver.closeChannel()" );
				_myChannel.close();
				// System.err.println( "...ok" );
			} finally {
				_myChannel = null;
			}
		}
	}

	public void run() {
		final SocketAddress sender = _myChannel.socket().getRemoteSocketAddress();
		int len, packetSize;

		checkBuffer();

		try {
			listen: while (_myIsListening) {
				try {
					_myByteBuffer.rewind().limit(4); // in TCP mode, first four bytes are packet size in bytes
					do {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1)
							break listen;
					} while (_myByteBuffer.hasRemaining());

					_myByteBuffer.rewind();
					packetSize = _myByteBuffer.getInt();
					_myByteBuffer.rewind().limit(packetSize);

					while (_myByteBuffer.hasRemaining()) {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1)
							break listen;
					}

					flipDecodeDispatch(sender);
					// flipDecodeDispatch( target );
				} catch (IllegalArgumentException e1) { // thrown on illegal byteBuf.limit() calls
					if (_myIsListening) {
						// System.err.println( new OSCException( OSCException.RECEIVE, e1.toString() ));
						final CCOSCException e2 = new CCOSCException("Error while receiving OSC packet " + e1.toString());
						System.err.println("OSCReceiver.run : " + e2.getClass().getName() + " : " + e2.getLocalizedMessage());
					}
				} catch (ClosedChannelException e1) { // bye bye, we have to quit
					if (_myIsListening) {
						System.err.println("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
					}
					return;
				} catch (IOException e1) {
					if (_myIsListening) {
						System.err.println("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
						// System.err.println( new OSCException( OSCException.RECEIVE, e1.toString() ));
					}
				}
			}
		} finally {
			synchronized (_myThreadSync) {
				_myThread = null;
				_myThreadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	/**
	 * @warning this calls socket().shutdownInput() to unblock the listening thread. unfortunately this cannot be
	 *          undone, so it's not possible to revive the receiver in TCP mode ;-( have to check for alternative
	 *          ways
	 */
	protected void sendGuardSignal() throws IOException {
		_myChannel.socket().shutdownInput();
	}
}
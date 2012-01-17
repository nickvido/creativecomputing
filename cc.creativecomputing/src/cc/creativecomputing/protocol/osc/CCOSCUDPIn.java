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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

class CCOSCUDPIn extends CCOSCIn <DatagramChannel>{

	protected CCOSCUDPIn(CCOSCPacketCodec theCodec, InetSocketAddress theLocalAddress){
		super(theCodec, CCOSCProtocol.UDP, theLocalAddress, true);
	}
	
	protected CCOSCUDPIn(CCOSCChannelAttributes theAttributes){
		super(theAttributes, true);
	}

	protected CCOSCUDPIn(CCOSCPacketCodec theCodec, DatagramChannel dch){
		super(theCodec, CCOSCProtocol.UDP, new InetSocketAddress(dch.socket().getLocalAddress(), dch.socket().getLocalPort()), false);

		_myChannel = dch;
	}

	protected void setChannel(DatagramChannel theChannel) throws IOException {
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCOSCException("Cannot be performed while channel is active");

			_myChannel = theChannel;
			if (!_myChannel.isBlocking()) {
				_myChannel.configureBlocking(true);
			}
			if (_myChannel.isConnected())
				throw new CCOSCException("OSCReceiver : channel must not be connected");
		}
	}

	public void setTarget(SocketAddress target) {
		this._myTarget = target;
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
			if (_myChannel == null) {
				try {
				final DatagramChannel newCh = DatagramChannel.open();
				newCh.socket().bind(_myLocalAddress);
				// dch = newCh;
				setChannel(newCh);
				}catch(Exception e) {
					throw new CCOSCException(e);
				}
			}
		}
	}

	public boolean isConnected() {
		synchronized (_myGeneralSync) {
			return ((_myChannel != null) && _myChannel.isOpen());
		}
	}

	protected void closeChannel() throws IOException {
		if (_myChannel != null) {
			try {
				_myChannel.close();
			} finally {
				_myChannel = null;
			}
		}
	}

	/**
	 * This is the body of the listening thread
	 */
	public void run() {
		SocketAddress sender;

		checkBuffer();

		try {
			listen: while (_myIsListening) {
				try {
					_myByteBuffer.clear();
					sender = _myChannel.receive(_myByteBuffer);

					if (!_myIsListening)
						break listen;
					if (sender == null)
						continue listen;
					if ((_myTarget != null) && !_myTarget.equals(sender))
						continue listen;

					flipDecodeDispatch(sender);
				} catch (ClosedChannelException e1) { // bye bye, we have to quit
					if (_myIsListening) {
						// System.err.println( e1 );
						System.err.println("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
					}
					return;
				} catch (IOException e1) {
					if (_myIsListening) {
						System.err.println("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
						// System.err.println( new OSCException( OSCException.RECEIVE, e1.toString() ));
					}
				}
			} // while( isListening )
		} finally {
			synchronized (_myThreadSync) {
				_myThread = null;
				_myThreadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	protected void sendGuardSignal(){
		try {
			final DatagramSocket guard = new DatagramSocket();
			final DatagramPacket guardPacket = new DatagramPacket(new byte[0], 0);
			guardPacket.setSocketAddress(localAddress());
			guard.send(guardPacket);
			guard.close();
		} catch (Exception e) {
			throw new CCOSCException(e);
		}
	}
}
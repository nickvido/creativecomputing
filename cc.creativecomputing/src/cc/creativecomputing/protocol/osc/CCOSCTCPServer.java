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
import java.io.PrintStream;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

public class CCOSCTCPServer extends CCOSCServer<ServerSocketChannel> implements Runnable, CCOSCListener {
	private final Map<SocketAddress, CCOSCIn> _myInMap = new HashMap<SocketAddress, CCOSCIn>();
	private final Map<SocketAddress, CCOSCOut> _myOutMap = new HashMap<SocketAddress, CCOSCOut>(); 

	private final List<CCOSCListener> _myListener = new ArrayList<CCOSCListener>();
	private Thread thread = null;
	private final Object startStopSync = new Object(); // mutual exclusion startListening / stopListening
	private final Object threadSync = new Object(); // communication with receiver thread

	private boolean _myIsListening = false;

	private CCOSCDumpMode _myInDumpMode = CCOSCDumpMode.OFF;
	private PrintStream _myInDumpStream = null;
	private CCOSCDumpMode _myOutDumpMode = CCOSCDumpMode.OFF;
	private PrintStream _myOutDumpStream = null;


	protected CCOSCTCPServer(CCOSCChannelAttributes theAtributes){
		super(theAtributes);

		try {
			_myChannel = ServerSocketChannel.open();
			_myChannel.socket().bind(_myLocalAddress);
		} catch (IOException e) {
			throw new CCOSCException(e);
		}
	}

	public void addOSCListener(CCOSCListener theListener) {
		synchronized (_myListener) {
			_myListener.add(theListener);
		}
	}

	public void removeOSCListener(CCOSCListener theListener) {
		synchronized (_myListener) {
			_myListener.remove(theListener);
		}
	}

	public void codec(CCOSCPacketCodec theCodec) {
		synchronized (_myBufferSyncObject) {
			for (CCOSCOut myOut:_myOutMap.values()) {
				if (myOut.codec() == CCOSCPacketCodec.getDefaultCodec()) {
					myOut.codec(theCodec);
				}
			}
		}
		super.codec(theCodec);
	}

	public void setCodec(CCOSCPacketCodec theCodec, SocketAddress theTarget) {
		final CCOSCOut myOut;

		synchronized (_myBufferSyncObject) {
			myOut =  _myOutMap.get(theTarget);
		}
		if (myOut == null)
			throw new NotYetConnectedException();
	}

	public CCOSCPacketCodec getCodec(SocketAddress theTarget) {
		final CCOSCOut myOut;

		synchronized (_myBufferSyncObject) {
			myOut =  _myOutMap.get(theTarget);
		}
		if (myOut == null)
			throw new NotYetConnectedException();
		return myOut.codec();
	}

	public void start() {
		synchronized (startStopSync) {
			if (Thread.currentThread() == thread)
				throw new IllegalStateException("Cannot call startListening() in the server body thread");

			if (_myIsListening && ((thread == null) || !thread.isAlive())) {
				_myIsListening = false;
			}
			if (!_myIsListening) {
				_myIsListening = true;
				thread = new Thread(this, "TCPServerBody");
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	public void stop() {
		synchronized (startStopSync) {
			if (Thread.currentThread() == thread)
				throw new IllegalStateException("Cannot call stopListening() in the server body thread");

			if (_myIsListening) {
				_myIsListening = false;
				if ((thread != null) && thread.isAlive()) {
					try {
						synchronized (threadSync) {
							final SocketChannel guard;
							guard = SocketChannel.open();
							guard.connect(_myChannel.socket().getLocalSocketAddress());
							guard.close();
							threadSync.wait(5000);
						}
					} catch (InterruptedException e2) {
						System.err.println(e2.getLocalizedMessage());
					} catch (IOException e1) {
						System.err.println("TCPServerBody.stopListening : " + e1);
						throw new CCOSCException(e1);
					} finally {
						// guard = null;
						if ((thread != null) && thread.isAlive()) {
							try {
								System.err.println("TCPServerBody.stopListening : rude task killing (" + this.hashCode() + ")");
								_myChannel.close(); // rude task killing
							} catch (IOException e3) {
								System.err.println("TCPServerBody.stopListening 2: " + e3);
							}
						}
						thread = null;
						stopAll();
					}
				}
			}
		}
	}

	public boolean isActive() {
		return _myIsListening;
	}

	public void send(CCOSCPacket thePacket, SocketAddress theTarget) {
		final CCOSCOut myOut;

		synchronized (_myBufferSyncObject) {
			myOut = _myOutMap.get(theTarget);
		}
		if (myOut == null)
			throw new NotYetConnectedException();

		myOut.send(thePacket);
	}

	public void dispose() {
		stop();

		try {
			_myChannel.close();
		} catch (IOException e1) {
			new CCOSCException(e1);
		}
	}

	private void stopAll() {
		synchronized (_myBufferSyncObject) {
			for (CCOSCIn myIn: _myInMap.values()) {
				myIn.dispose();
			}
			_myInMap.clear();
			for (CCOSCOut myOut:_myOutMap.values()) {
				myOut.dispose();
			}
			_myOutMap.clear();
		}
	}

	@Override
	public void bufferSize(int size) {
		synchronized (_myBufferSyncObject) {
			_myBufferSize = size;

			for (CCOSCIn myIn: _myInMap.values()) {
				myIn.bufferSize(size);
			}
			for (CCOSCOut myOut:_myOutMap.values()) {
				myOut.bufferSize(size);
			}
		}
	}

	public void dumpIncomingOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		synchronized (_myBufferSyncObject) {
			_myInDumpMode = theMode;
			_myInDumpStream = theStream;

			for (CCOSCIn myIn: _myInMap.values()) {
				myIn.dumpOSC(theMode, theStream);
			}
		}
	}

	public void dumpOutgoingOSC(CCOSCDumpMode theMode, PrintStream theStream) {
		synchronized (_myBufferSyncObject) {
			_myOutDumpMode = theMode;
			_myOutDumpStream = theStream;

			for (CCOSCOut myOut:_myOutMap.values()) {
				myOut.dumpOSC(theMode, theStream);
			}
		}
	}

	public void run() {
		SocketAddress mySender;
		SocketChannel myChannel;
		CCOSCIn myIn;
		CCOSCOut myOut;

		try {
			listen: while (_myIsListening) {
				try {
					myChannel = _myChannel.accept();
					if (!_myIsListening)
						break listen;
					if (myChannel == null)
						continue listen;

					mySender = myChannel.socket().getRemoteSocketAddress();

					synchronized (_myBufferSyncObject) {
						myIn = CCOSCChannel.createIn(myChannel);
						myIn.bufferSize(_myBufferSize);
						_myInMap.put(mySender, myIn);
						myOut = CCOSCChannel.createOut(myChannel);
						myOut.bufferSize(_myBufferSize);
						// System.err.println ("put "+mySender );
						_myOutMap.put(mySender, myOut);
						myIn.dumpOSC(_myInDumpMode, _myInDumpStream);
						myOut.dumpOSC(_myOutDumpMode, _myOutDumpStream);
						myIn.addOSCListener(this);
						myIn.startListening();
					}
				} catch (ClosedChannelException e11) { // bye bye, we have to quit
					if (_myIsListening) {
						System.err.println(e11);
					}
					return;
				} catch (IOException e1) {
					if (_myIsListening) {
						System.err.println(new CCOSCException("Error while receiving OSC packet "+ e1.toString()));
					}
				}
			} // while( isListening )
		} finally {
			synchronized (threadSync) {
				thread = null;
				threadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	public void messageReceived(CCOSCMessage msg, SocketAddress mySender, long time) {
		CCOSCListener theListener;

		synchronized (_myListener) {
			for (int i = 0; i < _myListener.size(); i++) {
				theListener = _myListener.get(i);
				theListener.messageReceived(msg, mySender, time);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.sciss.net.CCOSCChannel#connect()
	 */
	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.sciss.net.CCOSCChannel#isConnected()
	 */
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
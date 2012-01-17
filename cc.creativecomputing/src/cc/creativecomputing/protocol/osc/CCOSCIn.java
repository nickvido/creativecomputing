/*
 *  OSCReceiver.java
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
 *		25-Jan-05	created from de.sciss.meloncillo.net.OSCReceiver
 *		26-May-05	moved to de.sciss.net package
 *		21-Jun-05	extended javadoc, set/getDebugDump
 *		25-Jul-05	removed setSocketAddress in stopListening (test wise)
 *		05-Aug-05	added dumpOSC method. new contract regarding connected channels
 *		26-Aug-05	binding an unbound socket in the constructor uses
 *					InetAddress.getLocalHost() or "127.0.0.1" depending on the
 *					loopback status of the filtering address;
 *					; new empty constructor ; corrected synchronized statements
 *					in add/removeOSCListener ; extends AbstractOSCCommunicator
 *		11-Sep-05	the message dispatcher catches runtime exceptions in the listeners ,
 *					therefore making it behave more like the usual awt event dispatchers
 *					and not killing the listening thread
 *		30-Jul-06	fixed a potential sync problem ; throws exceptions in start/stopListening
 *					when calling from an illegal thread
 *		30-Sep-06	made abstract (unfortunately not backward compatible), finished TCP support
 *		14-Oct-06	revivable channels
 *		02-Jul-07	added codec based factory methods
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

//import java.util.Map;

/**
 * An <code>OSCReceiver</code> manages reception of incoming OSC messages. A receiver can be either <B>revivable</B> or
 * <B>non-revivable</B>.
 * <UL>
 * <LI>A non-revivable receiver is bound to one particular network channel (<code>DatagramChannel</code> (for UDP) or
 * <code>SocketChannel</code> (for TCP)). When the channel is closed, the receiver cannot be restarted. The network
 * channel is closed for example when a TCP server shuts down, but also when trying to connect to a TCP server that is
 * not yet reachable.</LI>
 * <LI>It is therefore recommended to use revivable receivers. A revivable receiver is created through one of the
 * <code>newUsing</code> constructors that takes a protocol and address argument. A revivable receiver can be restarted
 * because it recreates the network channel if necessary.</LI>
 * </UL>
 * <P>
 * The receiver launches a listening <code>Thread</code> when <code>startListening</code> is called.
 * <p>
 * The <code>OSCReceiver</code> has methods for registering and unregistering listeners that get informed about incoming
 * messages. Filtering out specific messages must be done by the listeners.
 * <p>
 * The listening thread is stopped using <code>stopListening</code> method.
 * <P>
 * <B>Note that as of v0.3,</B> you will most likely want to use preferably one of <code>OSCClient</code> or
 * <code>OSCServer</code> over <code>OSCReceiver</code>. Also note that as of v0.3, <code>OSCReceiver</code> is
 * abstract, which renders direct instantiation impossible. <B>To update old code,</B> occurrences of
 * <code>new OSCReceiver()</code> must be replaced by one of the <code>OSCReceiver.newUsing</code> methods! The
 * &quot;filter&quot; functionality of NetUtil 0.2 is now implied by calling <code>setTarget( SocketAddress )</code>.
 * <P>
 * Here is an example that also demonstrates message sending using an instance of <code>OSCTransmitter</code>:
 * 
 * <pre>
 * OSCReceiver rcv = null;
 * OSCTransmitter trns;
 * DatagramChannel dch = null;
 * 
 * try {
 * 	final SocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(), 57110);
 * 	final Object notify = new Object();
 * 
 * 	// note: using constructors with SelectableChannel implies the receivers and
 * 	// transmitters cannot be revived. to create revivable channels on the same socket,
 * 	// you must use one of the newUsing methods that take an IP address and/or port
 * 	// number.
 * 	dch = DatagramChannel.open();
 * 	dch.socket().bind(null); // assigns an automatic local socket address
 * 	rcv = OSCReceiver.newUsing(dch);
 * 	trns = OSCTransmitter.newUsing(dch);
 * 
 * 	rcv.addOSCListener(new CCOSCListener() {
 * 		public void messageReceived(CCOSCMessage msg, SocketAddress sender, long time) {
 * 			if (msg.getName().equals(&quot;status.reply&quot;)) {
 * 				System.out.println(&quot;scsynth is running. contains &quot; + msg.getArg(1) + &quot; unit generators, &quot; + msg.getArg(2) + &quot; synths, &quot; + msg.getArg(3) + &quot; groups, &quot;
 * 						+ msg.getArg(4) + &quot; synth defs.\n&quot; + &quot;CPU load is &quot; + msg.getArg(5) + &quot;% (average) / &quot; + msg.getArg(6) + &quot;% (peak)&quot;);
 * 				synchronized (notify) {
 * 					notify.notifyAll();
 * 				}
 * 			}
 * 		}
 * 	});
 * 	rcv.startListening();
 * 	trns.send(new CCOSCMessage(&quot;/status&quot;, CCOSCMessage.NO_ARGS), addr);
 * 
 * 	synchronized (notify) {
 * 		notify.wait(5000);
 * 	}
 * } catch (InterruptedException e1) {
 * } catch (IOException e2) {
 * 	System.err.println(e2.getLocalizedMessage());
 * } finally {
 * 	if (rcv != null) {
 * 		rcv.dispose();
 * 	} else if (dch != null) {
 * 		try {
 * 			dch.close();
 * 		} catch (IOException e4) {
 * 		}
 * 		;
 * 	}
 * }
 * </pre>
 * 
 * Note that the datagram channel needs to be bound to a valid reachable address, because <code>stopListening</code>
 * will be sending a terminating message to this channel. You can bind the channel using
 * <code>dch.socket().bind()</code>, as shown in the example above.
 * <P>
 * Note that someone has reported trouble with the <code>InetAddress.getLocalHost()</code> method on a machine that has
 * no proper IP configuration or DNS problems. In such a case when you need to communicate only on this machine and not
 * a network, use the loopback address &quot;127.0.0.1&quot; as the filtering address or bind the socket to the loop
 * address manually before calling <code>new OSCReceiver()</code>.
 * 
 * @author Hanns Holger Rutz
 * @version 0.37, 12-May-09
 * 
 * @see OSCClient
 * @see OSCServer
 * @see CCOSCOut
 * 
 * @synchronization starting and stopping and listener registration is thread safe. starting and stopping listening is
 *                  thread safe but must not be carried out in the OSC receiver thread.
 * @todo an explicit disconnect method might be useful (this is implicitly done when calling dispose)
 */
public abstract class CCOSCIn<ChannelType extends SelectableChannel> extends CCOSCChannel<ChannelType> implements Runnable {
	private final CCListenerManager<CCOSCListener> _myEvents = CCListenerManager.create(CCOSCListener.class);
	protected Thread _myThread = null;

	// private Map map = null;

	protected final Object _myGeneralSync = new Object(); // mutual exclusion startListening / stopListening
	protected final Object _myThreadSync = new Object(); // communication with receiver thread

	protected boolean _myIsListening = false;

	protected SocketAddress _myTarget = null;

	protected CCOSCIn(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress, boolean revivable) {
		super(theCodec, theProtocol, theLocalAddress, revivable);
	}

	protected CCOSCIn(CCOSCChannelAttributes theAttributes, boolean revivable) {
		super(theAttributes, revivable);
	}

	public abstract void setTarget(SocketAddress target);

	/**
	 * Registers a listener that gets informed about incoming messages. You can call this both when listening was
	 * started and stopped.
	 * 
	 * @param theListener the listener to register
	 */
	public void addOSCListener(CCOSCListener theListener) {
		_myEvents.add(theListener);
	}

	/**
	 * Unregisters a listener that gets informed about incoming messages
	 * 
	 * @param listener the listener to remove from the list of notified objects.
	 */
	public void removeOSCListener(CCOSCListener theListener) {
		_myEvents.remove(theListener);
	}

	/**
	 * Starts to wait for incoming messages. See the class constructor description to learn how connected and
	 * unconnected channels are handled. You should never modify the the channel's setup between the constructor and
	 * calling <code>startListening</code>. This method will check the connection status of the channel, using
	 * <code>isConnected</code> and establish the connection if necessary. Therefore, calling <code>connect</code> prior
	 * to <code>startListening</code> is not necessary.
	 * <p>
	 * To find out at which port we are listening, call <code>getLocalAddress().getPort()</code>.
	 * <p>
	 * If the <code>OSCReceiver</code> is already listening, this method does nothing.
	 */
	public void startListening(){
		synchronized (_myGeneralSync) {
			if (Thread.currentThread() == _myThread)
				throw new CCOSCException("Method call not allowed in this thread");

			if (_myIsListening && ((_myThread == null) || !_myThread.isAlive())) {
				_myIsListening = false;
			}
			if (!_myIsListening) {
				if (!isConnected())
					connect();
				_myIsListening = true;
				_myThread = new Thread(this, "OSCReceiver");
				_myThread.setDaemon(true);
				_myThread.start();
			}
		}
	}

	/**
	 * Queries whether the <code>OSCReceiver</code> is listening or not.
	 */
	public boolean isListening() {
		synchronized (_myGeneralSync) {
			return _myIsListening;
		}
	}

	/**
	 * Stops waiting for incoming messages. This method returns when the receiving thread has terminated. To prevent
	 * deadlocks, this method cancels after five seconds, calling <code>close()</code> on the datagram channel, which
	 * causes the listening thread to die because of a channel-closing exception.
	 * 
	 * @throws IOException if an error occurs while shutting down
	 * 
	 * @throws IllegalStateException when trying to call this method from within the OSC receiver thread (which would
	 *         obviously cause a loop)
	 */
	public void stopListening(){
		synchronized (_myGeneralSync) {
			if (Thread.currentThread() == _myThread)
				throw new CCOSCException("Method call not allowed in this thread");

			if (_myIsListening) {
				_myIsListening = false;
				if ((_myThread != null) && _myThread.isAlive()) {
					try {
						synchronized (_myThreadSync) {
							try {
								sendGuardSignal();
							} catch (IOException e) {
								throw new CCOSCException(e);
							}
							// guard.send( guardPacket );
							_myThreadSync.wait(5000);
						}
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					} finally {
						if ((_myThread != null) && _myThread.isAlive()) {
							try {
								System.err.println("OSCReceiver.stopListening : rude task killing (" + this.hashCode() + ")");
								// ch.close(); // rude task killing
								closeChannel();
							} catch (IOException e3) {
								e3.printStackTrace();
								// System.err.println( "OSCReceiver.stopListening 2: "+e3 );
							}
						}
						_myThread = null;
					}
				}
			}
		}
	}

	public void bufferSize(int size) {
		if (_myIsListening)
			throw new CCOSCException("Cannot be performed while channel is active");
		
		super.bufferSize(size);
	}

	public void dispose() {
			stopListening();
		try {
			// ch.close();
			closeChannel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		_myByteBuffer = null;
	}

	protected abstract void sendGuardSignal() throws IOException;

	protected abstract void setChannel(ChannelType theChannel) throws IOException;

	protected abstract void closeChannel() throws IOException;

	protected static String debugTimeString() {
		return new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date());
	}

	protected void flipDecodeDispatch(SocketAddress sender) throws IOException {
		final CCOSCPacket p;

		try {
			_myByteBuffer.flip();
			// p = CCOSCPacket.decode( byteBuf, map );
			// p = CCOSCPacket.decode( byteBuf );
			p = _myCodec.decode(_myByteBuffer);

			dump(p);
			dispatchPacket(p, sender, CCOSCBundle.NOW); // OSCBundles will override this dummy time tag
		} catch (BufferUnderflowException e1) {
			if (_myIsListening) {
				System.err.println(new CCOSCException("Error while receiving OSC packet " + e1.toString()));
			}
		}
	}

	private void dispatchPacket(CCOSCPacket p, SocketAddress sender, long time) {
		if (p instanceof CCOSCMessage) {
			dispatchMessage((CCOSCMessage) p, sender, time);
		} else if (p instanceof CCOSCBundle) {
			final CCOSCBundle bndl = (CCOSCBundle) p;
			time = bndl.getTimeTag();
			for (int i = 0; i < bndl.getPacketCount(); i++) {
				dispatchPacket(bndl.getPacket(i), sender, time);
			}
		} else {
			assert false : p.getClass().getName();
		}
	}

	private void dispatchMessage(CCOSCMessage msg, SocketAddress sender, long time) {
		_myEvents.proxy().messageReceived(msg, sender, time);
	}

}
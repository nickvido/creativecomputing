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

import java.net.InetSocketAddress;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

/**
 * @author christianriekoff
 *
 */
public class CCOSCChannelAttributes {

	private CCOSCProtocol _myProtocol = CCOSCProtocol.UDP;
	private CCOSCPacketCodec _myCodec = CCOSCPacketCodec.getDefaultCodec();
	private InetSocketAddress _myLocalAddress;
	
	/**
	 * Creates a new instance of CCOSCChannelAttributes, using default codec and a specific transport protocol. It picks an
	 * arbitrary free port and uses the local machine's IP. To determine the resulting port, you can use
	 * <code>getLocalAddress</code> afterwards.
	 * 
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 */
	public CCOSCChannelAttributes(CCOSCProtocol theProtocol) {
		this(CCOSCPacketCodec.getDefaultCodec(), theProtocol);
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using a specific codec and transport protocol. It picks an arbitrary free
	 * port and uses the local machine's IP. To determine the resulting port, you can use <code>getLocalAddress</code>
	 * afterwards.
	 * 
	 * @param theCodec the codec to use
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * 
	 */
	public CCOSCChannelAttributes(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol) {
		this(theCodec, theProtocol, 0);
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using default codec and a specific transport theProtocol and port. It uses
	 * the local machine's IP. Note that the <code>port</code> specifies the local socket, not the remote (or target)
	 * port. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param thePort the port number for the OSC socket, or <code>0</code> to use an arbitrary free port
	 * 
	 */
	public CCOSCChannelAttributes(CCOSCProtocol theProtocol, int thePort) {
		this(CCOSCPacketCodec.getDefaultCodec(), theProtocol, thePort);
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using a specific codec and transport protocol and port. It uses the local
	 * machine's IP. Note that the <code>port</code> specifies the local socket, not the remote (or target) port. This
	 * can be set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param thePort the port number for the OSC socket, or <code>0</code> to use an arbitrary free port
	 */
	public CCOSCChannelAttributes(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, int thePort) {
		this(theCodec, theProtocol, thePort, false);
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using default codec and a specific transport protocol and port. It uses the
	 * local machine's IP or the &quot;loopback&quot; address. Note that the <code>port</code> specifies the local
	 * socket, not the remote (or target) port. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param thePort the port number for the OSC socket, or <code>0</code> to use an arbitrary free port
	 * @param theLoopBack if <code>true</code>, the &quot;loopback&quot; address (<code>&quot;127.0.0.1&quot;</code>) is
	 *        used which limits communication to the local machine. If <code>false</code>, the special IP
	 *        <code>"0.0.0.0"</code> is used which means any local host is picked
	 */
	public CCOSCChannelAttributes(CCOSCProtocol theProtocol, int thePort, boolean theLoopBack) {
		this(CCOSCPacketCodec.getDefaultCodec(), theProtocol, thePort, theLoopBack);
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using a specific codec and transport protocol and port. It uses the local
	 * machine's IP or the &quot;loopback&quot; address. Note that the <code>port</code> specifies the local socket, not
	 * the remote (or target) port. This can be set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param port the port number for the OSC socket, or <code>0</code> to use an arbitrary free port
	 * @param loopBack if <code>true</code>, the &quot;loopback&quot; address (<code>&quot;127.0.0.1&quot;</code>) is
	 *        used which limits communication to the local machine. If <code>false</code>, the special IP
	 *        <code>"0.0.0.0"</code> is used which means any local host is picked
	 */
	public CCOSCChannelAttributes(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, int port, boolean loopBack) {
		this(theCodec, theProtocol, new InetSocketAddress(loopBack ? "127.0.0.1" : "0.0.0.0", port));
	}

	/**
	 * Creates a new instance of CCOSCChannelAttributes, using default codec and a specific transport protocol and local socket
	 * address. Note that <code>localAddress</code> specifies the local socket, not the remote (or target) socket. This
	 * can be set using the <code>setTarget</code> method!
	 * 
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param theLocalAddress a valid address to use for the OSC socket. If the port is <code>0</code>, an arbitrary free
	 *        port is picked when the out is connected. (you can find out the actual port in this case by calling
	 *        <code>getLocalAddress()</code> after the out was connected).
	 */
	public CCOSCChannelAttributes(CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress) {
		this(CCOSCPacketCodec.getDefaultCodec(), theProtocol, theLocalAddress);
	}
	
	/**
	 * Creates a new instance of CCOSCChannelAttributes, using default codec and a specific transport protocol and local socket
	 * address. Note that <code>localAddress</code> specifies the local socket, not the remote (or target) socket. This
	 * can be set using the <code>setTarget</code> method!
	 * 
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param theAddress a valid address to use for the OSC socket. 
	 * @param thePort If the port is <code>0</code>, an arbitrary free
	 *        port is picked when the out is connected. (you can find out the actual port in this case by calling
	 *        <code>getLocalAddress()</code> after the out was connected).
	 */
	public CCOSCChannelAttributes(CCOSCProtocol theProtocol, String theAddress, int thePort) {
		this(CCOSCPacketCodec.getDefaultCodec(), theProtocol, new InetSocketAddress(theAddress, thePort));
	}


	/**
	 * Creates a new instance of CCOSCChannelAttributes, using a specific codec and transport protocol and local socket address.
	 * Note that <code>localAddress</code> specifies the local socket, not the remote (or target) socket. This can be
	 * set using the <code>setTarget</code> method!
	 * 
	 * @param theCodec the codec to use
	 * @param theProtocol the protocol to use, currently either <code>UDP</code> or <code>TCP</code>
	 * @param theLocalAddress a valid address to use for the OSC socket. If the port is <code>0</code>, an arbitrary free
	 *        port is picked when the out is connected. (you can find out the actual port in this case by calling
	 *        <code>getLocalAddress()</code> after the out was connected).
	 */
	public CCOSCChannelAttributes(CCOSCPacketCodec theCodec, CCOSCProtocol theProtocol, InetSocketAddress theLocalAddress) {
		_myCodec = theCodec;
		_myProtocol = theProtocol;
		_myLocalAddress = theLocalAddress;
	}
	
	public CCOSCProtocol protocol() {
		return _myProtocol;
	}
	
	public CCOSCPacketCodec codec() {
		return _myCodec;
	}
	
	public InetSocketAddress localAddress() {
		return _myLocalAddress;
	}
}

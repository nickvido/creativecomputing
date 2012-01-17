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
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import cc.creativecomputing.protocol.osc.codec.CCOSCPacketCodec;

class CCOSCUDPServer extends CCOSCServer {

	private final CCOSCIn _myIn;
	private final CCOSCOut _myOut;

	protected CCOSCUDPServer(CCOSCChannelAttributes theAttributes, String theTargetAddress, int thePort){
		super(theAttributes);

		_myIn = CCOSCIn.createIn(theAttributes);
		_myOut = CCOSCChannel.createOut(theAttributes, theTargetAddress, thePort);
	}

	public void addOSCListener(CCOSCListener listener) {
		_myIn.addOSCListener(listener);
	}

	public void removeOSCListener(CCOSCListener listener) {
		_myIn.removeOSCListener(listener);
	}

	public void codec(CCOSCPacketCodec theCodec) {
		_myIn.codec(theCodec);
		_myOut.codec(theCodec);
		super.codec(theCodec);
	}

	public void setCodec(CCOSCPacketCodec theCodec, SocketAddress target) throws IOException {
		throw new IOException("Not supported in UDP mode");
	}

	public CCOSCPacketCodec getCodec(SocketAddress target) throws IOException {
		throw new IOException("Not supported in UDP mode");
	}

	public void start()  {
		if (!_myOut.isConnected()) {
			_myOut.connect();
			try {
				_myIn.setChannel(_myOut.channel());
			} catch (IOException e) {
				throw new CCOSCException(e);
			}
		}
		_myIn.startListening();
	}

	public void stop(){
		_myIn.stopListening();
	}

	public boolean isActive() {
		return _myIn.isListening();
	}

	@Override
	public void send(CCOSCPacket p, SocketAddress target){
		_myOut.send(p, target);
	}

	public void dispose() {
		_myIn.dispose();
		_myOut.dispose();
	}

	@Override
	public void bufferSize(int size) {
		_myIn.bufferSize(size);
		_myOut.bufferSize(size);
	}

	@Override
	public int bufferSize() {
		return _myIn.bufferSize();
	}

	public void dumpIncomingOSC(CCOSCDumpMode theMode, PrintStream stream) {
		_myIn.dumpOSC(theMode, stream);
	}

	public void dumpOutgoingOSC(CCOSCDumpMode theMode, PrintStream stream) {
		_myOut.dumpOSC(theMode, stream);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.protocol.osc.CCOSCChannel#connect()
	 */
	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.protocol.osc.CCOSCChannel#isConnected()
	 */
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
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
package cc.creativecomputing.protocol.osc.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

class CCOSCBlobAtom extends CCOSCAtom <byte[]>{
	protected CCOSCBlobAtom() {
		super(0x62, 0);
	}

	public byte[] decodeAtom(byte typeTag, ByteBuffer b) throws IOException {
		final byte[] blob = new byte[b.getInt()];
		b.get(blob);
		CCOSCPacketCodec.skipToAlign(b);
		return blob;
	}

	public void encodeAtom(byte[] theBytes, ByteBuffer tb, ByteBuffer db) throws IOException {
		tb.put((byte) 0x62); // 'b'
		db.putInt(theBytes.length);
		db.put(theBytes);
		CCOSCPacketCodec.padToAlign(db);
	}

	@Override
	public int size(byte[] theBytes){
		return ((theBytes.length + 7) & ~3);
	}
}
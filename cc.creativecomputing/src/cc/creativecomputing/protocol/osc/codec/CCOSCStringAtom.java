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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import cc.creativecomputing.protocol.osc.CCOSCException;

class CCOSCStringAtom extends CCOSCAtom<String> {

	private String _myCharSet;
	
	protected CCOSCStringAtom(String theCharSet) {
		super(0x73, 0);
		_myCharSet = theCharSet;
	}

	public String decodeAtom(byte typeTag, ByteBuffer b) throws IOException {
		final int pos1 = b.position();
		final String s;
		final int pos2;
		final byte[] bytes;
		final int len;
		while (b.get() != 0)
			;
		pos2 = b.position() - 1;
		b.position(pos1);
		len = pos2 - pos1;
		bytes = new byte[len];
		b.get(bytes, 0, len);
		s = new String(bytes, _myCharSet);
		b.position((pos2 + 4) & ~3);
		return s;
	}

	public void encodeAtom(String o, ByteBuffer tb, ByteBuffer db) throws IOException {
		tb.put((byte) 0x73); // 's'
		db.put(o.getBytes(_myCharSet)); // faster than using Charset or CharsetEncoder
		CCOSCPacketCodec.terminateAndPadToAlign(db);
	}

	@Override
	public int size(String o){
		try {
			return ((o.getBytes(_myCharSet).length + 4) & ~3);
		} catch (UnsupportedEncodingException e) {
			throw new CCOSCException(e);
		}
	}
}
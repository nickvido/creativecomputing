/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author christianriekoff
 *
 */
public class CCInputStream {

	private InputStream _myStream;
	private long bytePosition;
	
	public CCInputStream(InputStream theStream) {
		_myStream = theStream;
	}
	
	public int readUnsignedByte () throws IOException {
		bytePosition++;
		int b = _myStream.read();
		if (b == -1) throw new EOFException("Unexpected end of file.");
		return b;
	}
 
	public byte readByte () throws IOException {
		return (byte)readUnsignedByte();
	}
 
	public int readUnsignedShort () throws IOException {
		return (readUnsignedByte() << 8) + readUnsignedByte();
	}
 
	public short readShort () throws IOException {
		return (short)readUnsignedShort();
	}
 
	public long readUnsignedLong () throws IOException {
		long value = readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		return value;
	}
 
	public void skip (int bytes) throws IOException {
		_myStream.skip(bytes);
		bytePosition += bytes;
	}
 
	public void seek (long position) throws IOException {
		_myStream.skip(position - bytePosition);
		bytePosition = position;
	}
}

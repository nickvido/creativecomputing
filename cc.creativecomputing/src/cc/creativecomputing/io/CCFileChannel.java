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
package cc.creativecomputing.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author christianriekoff
 *
 */
public class CCFileChannel {

	private RandomAccessFile _myRandomAccessFile;
	private FileChannel _myChannel;
	
	public CCFileChannel(String theFile) {
		try {
			_myRandomAccessFile = new RandomAccessFile(theFile, "rw");
		} catch (FileNotFoundException e) {
			throw new CCIOException(e);
		}
		_myChannel = _myRandomAccessFile.getChannel();
	}
	
	public void write(ByteBuffer theBuffer) {
		try {
			_myChannel.write(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(ByteBuffer theBuffer, int thePosition) {
		try {
			_myChannel.write(theBuffer, thePosition);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
}

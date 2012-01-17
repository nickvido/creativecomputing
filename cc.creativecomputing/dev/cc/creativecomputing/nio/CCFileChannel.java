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
package cc.creativecomputing.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCFileChannel {
	
	/**
	 * The mode argument specifies the access mode in which the file is to be opened.
	 * @author christianriekoff
	 *
	 */
	public static enum CCFileMode{
		/**
		 * Open for reading only. Invoking any of the write methods of the 
		 * resulting object will cause an IOException to be thrown.
		 */
		R("r"),	 
		/**
		 * Open for reading and writing. If the file does not already 
		 * exist then an attempt will be made to create it.
		 */
		RW("rw"),	 
		/**
		 * Open for reading and writing, as with "rw", and also require 
		 * that every update to the file's content or metadata be written 
		 * synchronously to the underlying storage device.
		 */
		RWS("rws"), 
		/**
		 * Open for reading and writing, as with "rw", and also require 
		 * that every update to the file's content be written synchronously
		 *  to the underlying storage device.
		 */
		RWD("rwd"); 	 
		private String _myName;
		
		private CCFileMode(String theName) {
			_myName = theName;
		}
		
		public String modeName() {
			return _myName;
		}
	}

	private String _myFile;
	private CCFileMode _myMode;
	
	private FileChannel _myFileChannel;
	private RandomAccessFile _myRandomAccessFile;
	
	public CCFileChannel(String theFile, CCFileMode theMode) {
		_myFile = theFile;
		_myMode = theMode;
		
		open();
	}
	
	public void open() {
		if(_myFileChannel != null && _myFileChannel.isOpen())return;
		
		CCIOUtil.createPath(_myFile);
		try {
			_myRandomAccessFile = new RandomAccessFile(_myFile, _myMode.modeName());
		} catch (FileNotFoundException e) {
			throw new CCIOException(e);
		}
		
		_myFileChannel = _myRandomAccessFile.getChannel();
		try {
			_myFileChannel.position(_myFileChannel.size());
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(ByteBuffer theBuffer) {
		try {
			_myFileChannel.write(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(ByteBuffer theBuffer, long thePosition) {
		try {
			_myFileChannel.write(theBuffer, thePosition);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void append(ByteBuffer theBuffer) {
		try {
			write(theBuffer, _myFileChannel.size());
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void append() {
		
	}
	
	public void write(byte[] theBytes) {
		ByteBuffer myBuffer = ByteBuffer.wrap(theBytes);
		write(myBuffer);
	}
	
	public void read(ByteBuffer theBuffer) {
		try {
			_myFileChannel.read(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void read(ByteBuffer theBuffer, long thePosition) {
		try {
			_myFileChannel.read(theBuffer, thePosition);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void read(byte[] theBytes) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theBytes.length);
		read(myBuffer);
		myBuffer.flip();
		myBuffer.get(theBytes);
	}
	
	public long position() {
		try {
			return _myFileChannel.position();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void force() {
		try {
			_myFileChannel.force(true);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void close() {
		try {
			_myFileChannel.close();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public long size() {
		try {
			return _myFileChannel.size();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
}

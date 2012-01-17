package cc.creativecomputing.graphics.font;

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
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.io.CCInputStream;
 
/**
 * Reads a TTF font file and provides access to kerning information.
 */
public class CCKerningTable {
	private final Map<Key, Integer> _myKerningMap;
	private int _myUnitsPerEm;
	private long _myHeaderOffset = -1;
	private long _myKerningOffset = -1;
	
	private CCInputStream _myInputStream;
 
	/**
	 * @param theInputStream The data for the TTF font.
	 * @throws IOException If the font could not be read.
	 */
	public CCKerningTable (InputStream theInputStream) throws IOException {
		if (theInputStream == null) throw new IllegalArgumentException("input cannot be null.");
		
		_myInputStream = new CCInputStream(theInputStream);
		
		readTableDirectory();
		if (_myHeaderOffset == -1) throw new IOException("HEAD table not found.");
		if (_myKerningOffset == -1) {
			_myKerningMap = Collections.EMPTY_MAP;
			return;
		}
		_myKerningMap = new HashMap<Key, Integer> (2048);
		if (_myHeaderOffset < _myKerningOffset) {
			readHeader();
			readKERN();
		} else {
			readKERN();
			readHeader();
		}
		theInputStream.close();
	}
 
	/**
	 * Returns the kerning value for the specified glyphs. 
	 * The glyph code for a Unicode codepoint can be retrieved with
	 * {@link GlyphVector#getGlyphCode(int)}.
	 */
	public float kerning(int theFirstGlyphCode, int theSecondGlyphCode) {
		if(theFirstGlyphCode < 0 || theSecondGlyphCode < 0)return 0;
		
		
		Integer value = _myKerningMap.get(new Key(theFirstGlyphCode, theSecondGlyphCode));
		if (value == null) return 0;
		
		return value / (float)_myUnitsPerEm;
	}
 
	private void readTableDirectory () throws IOException {
		_myInputStream.skip(4);
		int myTableCount = _myInputStream.readUnsignedShort();
		_myInputStream.skip(6);
 
		byte[] tagBytes = new byte[4];
		
		for (int i = 0; i < myTableCount; i++) {
			tagBytes[0] = _myInputStream.readByte();
			tagBytes[1] = _myInputStream.readByte();
			tagBytes[2] = _myInputStream.readByte();
			tagBytes[3] = _myInputStream.readByte();
			
			_myInputStream.skip(4);
			
			long offset = _myInputStream.readUnsignedLong();
			_myInputStream.skip(4);
 
			String tag = new String(tagBytes, "ISO-8859-1");
			if (tag.equals("head")) {
				_myHeaderOffset = offset;
				if (_myKerningOffset != -1) break;
			} else if (tag.equals("kern")) {
				_myKerningOffset = offset;
				if (_myHeaderOffset != -1) break;
			}
		}
	}
 
	/**
	 * Reads the needed kerning informations from the font header
	 * @throws IOException
	 */
	private void readHeader () throws IOException {
		_myInputStream.seek(_myHeaderOffset + 2 * 4 + 2 * 4 + 2);
		_myUnitsPerEm = _myInputStream.readUnsignedShort();
	}
 
	private void readKERN () throws IOException {
		_myInputStream.seek(_myKerningOffset + 2);
		for (int n = _myInputStream.readUnsignedShort(); n > 0; n--) {
			_myInputStream.skip(2 * 2);
			int k = _myInputStream.readUnsignedShort();
			if (!((k & 1) != 0) || (k & 2) != 0 || (k & 4) != 0) return;
			if (k >> 8 != 0) continue;
			k = _myInputStream.readUnsignedShort();
			_myInputStream.skip(3 * 2);
			while (k-- > 0) {
				int firstGlyphCode = _myInputStream.readUnsignedShort();
				int secondGlyphCode = _myInputStream.readUnsignedShort();
				int value = _myInputStream.readShort();
				if (value == 0) continue;
				_myKerningMap.put(new Key(firstGlyphCode, secondGlyphCode), value);
			}
		}
	}
	
	
 
	static private class Key {
		private final int firstGlyphCode, secondGlyphCode;
 
		public Key (int firstGlyphCode, int secondGlyphCode) {
			this.firstGlyphCode = firstGlyphCode;
			this.secondGlyphCode = secondGlyphCode;
		}
 
		public int hashCode () {
			final int prime = 31;
			int result = 1;
			result = prime * result + firstGlyphCode;
			result = prime * result + secondGlyphCode;
			return result;
		}
 
		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Key other = (Key)obj;
			if (firstGlyphCode != other.firstGlyphCode) return false;
			if (secondGlyphCode != other.secondGlyphCode) return false;
			return true;
		}
	}
}

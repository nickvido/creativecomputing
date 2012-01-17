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
package cc.creativecomputing.graphics.font;

import cc.creativecomputing.util.CCArrayUtil;

/**
 * @author info
 *
 */
public class CCCharSet {
////////////////////////////////////////////////////////////
	static final private char[] EXTRA_CHARS = {
		0x0080, 0x0081, 0x0082, 0x0083, 0x0084, 0x0085, 0x0086, 0x0087, 0x0088, 0x0089, 
		0x008A, 0x008B, 0x008C, 0x008D, 0x008E, 0x008F, 0x0090, 0x0091, 0x0092, 0x0093, 
		0x0094, 0x0095, 0x0096, 0x0097, 0x0098, 0x0099, 0x009A, 0x009B, 0x009C, 0x009D, 
		0x009E, 0x009F, 0x00A0, 0x00A1, 0x00A2, 0x00A3, 0x00A4, 0x00A5, 0x00A6, 0x00A7, 
		0x00A8, 0x00A9, 0x00AA, 0x00AB, 0x00AC, 0x00AD, 0x00AE, 0x00AF, 0x00B0, 0x00B1, 
		0x00B4, 0x00B5, 0x00B6, 0x00B7, 0x00B8, 0x00BA, 0x00BB, 0x00BF, 0x00C0, 0x00C1,
		0x00C2, 0x00C3, 0x00C4, 0x00C5, 0x00C6, 0x00C7, 0x00C8, 0x00C9, 0x00CA, 0x00CB, 
		0x00CC, 0x00CD, 0x00CE, 0x00CF, 0x00D1, 0x00D2, 0x00D3, 0x00D4, 0x00D5, 0x00D6,
		0x00D7, 0x00D8, 0x00D9, 0x00DA, 0x00DB, 0x00DC, 0x00DD, 0x00DF, 0x00E0, 0x00E1, 
		0x00E2, 0x00E3, 0x00E4, 0x00E5, 0x00E6, 0x00E7, 0x00E8, 0x00E9, 0x00EA, 0x00EB, 
		0x00EC, 0x00ED, 0x00EE, 0x00EF, 0x00F1, 0x00F2, 0x00F3, 0x00F4, 0x00F5, 0x00F6, 
		0x00F7, 0x00F8, 0x00F9, 0x00FA, 0x00FB, 0x00FC, 0x00FD, 0x00FF, 0x0102, 0x0103, 
		0x0104, 0x0105, 0x0106, 0x0107, 0x010C, 0x010D, 0x010E, 0x010F, 0x0110, 0x0111, 
		0x0118, 0x0119, 0x011A, 0x011B, 0x0131, 0x0139, 0x013A, 0x013D, 0x013E, 0x0141, 
		0x0142, 0x0143, 0x0144, 0x0147, 0x0148, 0x0150, 0x0151, 0x0152, 0x0153, 0x0154, 
		0x0155, 0x0158, 0x0159, 0x015A, 0x015B, 0x015E, 0x015F, 0x0160, 0x0161, 0x0162, 
		0x0163, 0x0164, 0x0165, 0x016E, 0x016F, 0x0170, 0x0171, 0x0178, 0x0179, 0x017A, 
		0x017B, 0x017C, 0x017D, 0x017E, 0x0192, 0x02C6, 0x02C7, 0x02D8, 0x02D9, 0x02DA, 
		0x02DB, 0x02DC, 0x02DD, 0x03A9, 0x03C0, 0x2013, 0x2014, 0x2018, 0x2019, 0x201A, 
		0x201C, 0x201D, 0x201E, 0x2020, 0x2021, 0x2022, 0x2026, 0x2030, 0x2039, 0x203A, 
		0x2044, 0x20AC, 0x2122, 0x2202, 0x2206, 0x220F, 0x2211, 0x221A, 0x221E, 0x222B, 
		0x2248, 0x2260, 0x2264, 0x2265, 0x25CA, 0xF8FF, 0xFB01, 0xFB02};

	/**
	 * @shortdesc The default character set.
	 * 
	 * This is the union of the Mac Roman and Windows ANSI (CP1250)
	 * character sets. ISO 8859-1 Latin 1 is Unicode characters 0x80 -> 0xFF,
	 * and would seem a good standard, but in practice, most users would
	 * rather have characters that they expect from their platform's fonts.
	 */
	public static CCCharSet REDUCED_CHARSET;
	static{
		char[] myDefaultChars = new char[126 - 32 + 1 + EXTRA_CHARS.length];
		int index = 0;
		for (int i = 32; i <= 126; i++){
			myDefaultChars[index++] = (char) i;
		}
		for (int i = 0; i < EXTRA_CHARS.length; i++){
			myDefaultChars[index++] = EXTRA_CHARS[i];
		}
		REDUCED_CHARSET = new CCCharSet(myDefaultChars);
	};
	
	public static CCCharSet EXTENDED_CHARSET;
	static{
		EXTENDED_CHARSET = new CCCharSet();
		EXTENDED_CHARSET.addUnicodeBlock(CCUnicodeBlock.BASIC_LATIN);
		EXTENDED_CHARSET.addUnicodeBlock(CCUnicodeBlock.LATIN_1_SUPPLEMENT);
		EXTENDED_CHARSET.addUnicodeBlock(CCUnicodeBlock.LATIN_EXTENDED_A);
		EXTENDED_CHARSET.addUnicodeBlock(CCUnicodeBlock.GENERAL_PUNCTUATION);
		EXTENDED_CHARSET.addUnicodeBlock(CCUnicodeBlock.SUPERSCRIPTS_AND_SUBSCRIPTS);
	};
	
	private char[] _myChars;
	
	public CCCharSet(final char[] theChars) {
		_myChars = theChars;
	}
	
	public CCCharSet() {
		_myChars = new char[0];
	}
	
	public char[] chars() {
		return _myChars;
	}
	
	public void addUnicodeBlock(final CCUnicodeBlock theUnicodeBlock) {
		_myChars = CCArrayUtil.concat(_myChars, theUnicodeBlock.chars());
	}
	
	public int size() {
		return _myChars.length;
	}
}

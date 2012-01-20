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

/**
 * @author christianriekoff
 *
 */
public enum CCOSCCodecMode {
	/**
	 * Support mode: coder only accepts <code>java.lang.Integer</code>, <code>java.lang.Float</code>,
	 * <code>java.lang.String</code>, and <code>byte[]</code>. Decoder only accepts <code>'i'</code>, <code>'f'</code>,
	 * <code>'s'</code>, and <code>'b'</code>. Note that <code>byte[]</code> is used to represents blobs (
	 * <code>'b'</code>).
	 */
	STRICT_V1(0x0000),
	/**
	 * Support mode: like <code>MODE_STRICT_V1</code>, but coder additionally encodes <code>java.lang.Long</code> as a
	 * <code>'i'</code>, <code>java.lang.Double</code> as a <code>'f'</code>, and <code>de.sciss.net.OSCPacket</code> as
	 * a blob <code>'b'</code>. The decoder decodes <code>'h'</code> into <code>java.lang.Integer</code>,
	 * <code>'d'</code> into <code>java.lang.Float</code>, and <code>'S'</code> (Symbol) into
	 * <code>java.lang.String</code>.
	 */
	MODEST (
		CCOSCPacketCodec.MODE_READ_DOUBLE_AS_FLOAT | 
		CCOSCPacketCodec.MODE_READ_LONG_AS_INTEGER | 
		CCOSCPacketCodec.MODE_WRITE_DOUBLE_AS_FLOAT | 
		CCOSCPacketCodec.MODE_WRITE_LONG_AS_INTEGER | 
		CCOSCPacketCodec.MODE_READ_SYMBOL_AS_STRING | 
		CCOSCPacketCodec.MODE_WRITE_PACKET_AS_BLOB
	),
	/**
	 * Support mode: like <code>MODE_MODEST</code>, that is, it will downgrade to 32bit in the encoding process, but
	 * decoding leaves 64bit values intact, so <code>'h'</code> becomes <code>java.lang.Long</code>, and
	 * <code>'d'</code> into <code>java.lang.Double</code>.
	 */
	GRACEFUL(
		CCOSCPacketCodec.MODE_READ_DOUBLE | 
		CCOSCPacketCodec.MODE_READ_LONG | 
		CCOSCPacketCodec.MODE_WRITE_DOUBLE_AS_FLOAT | 
		CCOSCPacketCodec.MODE_WRITE_LONG_AS_INTEGER | 
		CCOSCPacketCodec.MODE_READ_SYMBOL_AS_STRING | 
		CCOSCPacketCodec.MODE_WRITE_PACKET_AS_BLOB
	),
	
	/**
	 * Support mode: like <code>STRICT_V1</code>, but with additional 64bit support, that is a mutual mapping
	 * between <code>'h'</code> &lt;--&gt; <code>java.lang.Long</code>, and <code>'d'</code> &lt;--&gt;
	 * <code>java.lang.Double</code>. Also, <code>'S'</code> (Symbol) is decoded into <code>java.lang.String</code>, and
	 * <code>de.sciss.net.OSCPacket</code> is encoded as a blob <code>'b'</code>.
	 */
	FAT_V1(
		CCOSCPacketCodec.MODE_READ_DOUBLE | 
		CCOSCPacketCodec.MODE_READ_LONG | 
		CCOSCPacketCodec.MODE_WRITE_DOUBLE | 
		CCOSCPacketCodec.MODE_WRITE_LONG | 
		CCOSCPacketCodec.MODE_READ_SYMBOL_AS_STRING | 
		CCOSCPacketCodec.MODE_WRITE_PACKET_AS_BLOB
	);

	int mask;
	
	private CCOSCCodecMode(int theMask){
		mask = theMask;
	}
}
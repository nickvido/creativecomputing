package cc.creativecomputing.protocol.udp;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

/**
 * UDPJavaToByteArrayConverter is a helper class that translates
 * from Java types to their byte stream representations.
 */
public class CCUDPByteOutputStream {

	protected ByteArrayOutputStream _myStream = new ByteArrayOutputStream();
	private byte[] longintBytes = new byte[8];
		// this should be long enough to accomodate any string
	private char[] stringChars = new char[2048];
	private byte[] stringBytes = new byte[2048];

	public CCUDPByteOutputStream() {
		super();
	}

	/**
	 * Line up the Big end of the bytes to a 4 byte boundry
	 * @return byte[]
	 * @param bytes byte[]
	 */
	private byte[] alignBigEndToFourByteBoundry(byte[] bytes) {
		int mod = bytes.length % 4;
		// if the remainder == 0 then return the bytes otherwise pad the bytes to
		// lineup correctly
		if (mod == 0) {
			return bytes;
		}
		int pad = 4 - mod;
		byte[] newBytes = new byte[pad + bytes.length];
		for (int i = 0; i < pad; i++)
			newBytes[i] = 0;
		for (int i = 0; i < bytes.length; i++)
			newBytes[pad + i] = bytes[i];
		return newBytes;
	}

	/**
	 * Pad the stream to have a size divisible by 4.
	 */
	public void appendNullCharToAlignStream() {
		int mod = _myStream.size() % 4;
		int pad = 4 - mod;
		for (int i = 0; i < pad; i++)
			_myStream.write(0);
	}

	/**
	 * Convert the contents of the output stream to a byte array.
	 * @return the byte array containing the byte stream
	 */
	public byte[] toByteArray() {
		return _myStream.toByteArray();
	}

	/**
	 * Write bytes into the byte stream.
	 * @param bytes byte[]
	 */
	public void write(byte[] bytes) {
		writeUnderHandler(bytes);
	}

	/**
	 * Write an int into the byte stream.
	 * @param theInt int
	 */
	public void writeInteger(final int theInt) {
		longintBytes[0] = (byte)((theInt >> 24) & 0xFF);
		longintBytes[1] = (byte)((theInt >> 16) & 0xFF);
		longintBytes[2] = (byte)((theInt >> 8) & 0xFF);
		longintBytes[3] = (byte)(theInt & 0xFF);
		_myStream.write(longintBytes,0,4);
	}
	
	/**
	 * @param theLong java.lang.Integer
	 */
	public void writeLong(final long theLong) {
		longintBytes[0] = (byte)((theLong >> 56) & 0xFF);
		longintBytes[1] = (byte)((theLong >> 48) & 0xFF);
		longintBytes[2] = (byte)((theLong >> 40) & 0xFF);
		longintBytes[3] = (byte)((theLong >> 32) & 0xFF);
		longintBytes[4] = (byte)((theLong >> 24) & 0xFF);
		longintBytes[5] = (byte)((theLong >> 16) & 0xFF);
		longintBytes[6] = (byte)((theLong >> 8) & 0xFF);
		longintBytes[7] = (byte)(theLong & 0xFF);
		_myStream.write(longintBytes,0,8);
	}	

	/**
	 * Write a float into the byte stream.
	 * @param theFloat java.lang.Float
	 */
	public void writeFloat(final float theFloat) {
		writeInteger(Float.floatToIntBits(theFloat));
	}	

	/**
	 * Write a float into the byte stream.
	 * @param theFloat java.lang.Float
	 */
	public void writeDouble(final double theDouble) {
		writeLong(Double.doubleToLongBits(theDouble));
	}

	/**
	 * Write a string into the byte stream.
	 * @param theString java.lang.String
	 */
	public void write(String theString) {
		int stringLength = theString.length();
			// this is a deprecated method -- should use get char and convert
			// the chars to bytes
//		aString.getBytes(0, stringLength, stringBytes, 0);
		theString.getChars(0, stringLength, stringChars, 0);
			// pad out to align on 4 byte boundry
		int mod = stringLength % 4;
		int pad = 4 - mod;
		for (int i = 0; i < pad; i++)
			stringChars[stringLength++] = 0;
		// convert the chars into bytes and write them out
		for (int i = 0; i < stringLength; i++) {
			stringBytes[i] = (byte) (stringChars[i] & 0x00FF);
		}
		_myStream.write(stringBytes, 0, stringLength);		
	}

	/**
	 * Write a char into the byte stream.
	 * @param c char
	 */
	public void write(char c) {
		_myStream.write(c);
	}

	/**
	 * Write an object into the byte stream.
	 * @param anObject one of Float, String, Integer, BigInteger, or array of these.
	 */
	public void write(Object anObject) {
		
		// Can't do switch on class
		if (null == anObject)
			return;
		
		if (anObject instanceof byte[]) {
			write((byte[]) anObject);
		}
		if (anObject instanceof Object[]) {
			Object[] theArray = (Object[]) anObject;
			for(int i = 0; i < theArray.length; ++i) {
				write(theArray[i]);
			}
			return;
		}
		if (anObject instanceof Float) {
			writeFloat((Float) anObject);
			return;
		}
		if (anObject instanceof Double) {
			writeDouble((Double) anObject);
			return;
		}
		if (anObject instanceof String) {
			write((String) anObject);
			return;
		}
		if (anObject instanceof Integer) {
			writeInteger((Integer) anObject);
			return;
		}
		if (anObject instanceof Long) {
			writeLong((Long) anObject);
			return;
		}		
	}

	
	
	/**
	 * Write bytes to the stream, catching IOExceptions and converting them to RuntimeExceptions.
	 * @param bytes byte[]
	 */
	private void writeUnderHandler(byte[] bytes) {
		try {
			_myStream.write(alignBigEndToFourByteBoundry(bytes));
		} catch (IOException e) {
			throw new RuntimeException("You're screwed: IOException writing to a ByteArrayOutputStream");
		}
	}

}


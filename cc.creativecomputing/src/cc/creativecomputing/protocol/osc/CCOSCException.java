/*
 *  OSCException.java
 *  de.sciss.net (NetUtil)
 *
 *  Copyright (c) 2004-2009 Hanns Holger Rutz. All rights reserved.
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 *
 *
 *  Changelog:
 *		25-Jan-05	created from de.sciss.meloncillo.net.OSCException
 *		26-May-05	moved to de.sciss.net package
 */

package cc.creativecomputing.protocol.osc;

import java.io.IOException;

/**
 * Exception thrown by some OSC related methods. Typical reasons are communication timeout and buffer underflows or
 * overflows.
 * 
 * @author Hanns Holger Rutz
 * @version 0.10, 26-May-05
 */
public class CCOSCException extends RuntimeException {
	/**
	 * causeType : communication timeout
	 */
	public static final int TIMEOUT = 0;
	/**
	 * causeType : supercollider replies "fail"
	 */
	public static final int FAILED = 1;
	/**
	 * causeType : buffer overflow or underflow
	 */
	public static final int BUFFER = 2;
	/**
	 * causeType : osc message has invalid format
	 */
	public static final int FORMAT = 3;
	/**
	 * causeType : osc message has invalid or unsupported type tags
	 */
	public static final int TYPETAG = 4;
	/**
	 * causeType : osc message cannot convert given java class to osc primitive
	 */
	public static final int JAVACLASS = 5;
	/**
	 * causeType : network error while receiving osc message
	 */
	public static final int RECEIVE = 6;

	private int causeType;
	private static final String[] errMessages = { "errOSCTimeOut", "errOSCFailed", "errOSCBuffer", "errOSCFormat", "errOSCTypeTag", "errOSCArgClass", "errOSCReceive" };

	public CCOSCException() {
		super();
	}

	public CCOSCException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCOSCException(String theArg0) {
		super(theArg0);
	}

	public CCOSCException(Throwable theArg0) {
		super(theArg0);
	}
}
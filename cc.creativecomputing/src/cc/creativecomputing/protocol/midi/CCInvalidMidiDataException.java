/*

Copyright (c) 2005 Christian Riekoff

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
*/

package cc.creativecomputing.protocol.midi;

/**
 * @invisible
 * @author tex
 *
 */
public class CCInvalidMidiDataException extends RuntimeException{

	/**
	 * 
	 */
	public CCInvalidMidiDataException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCInvalidMidiDataException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCInvalidMidiDataException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCInvalidMidiDataException(Throwable theCause) {
		super(theCause);
	}


}

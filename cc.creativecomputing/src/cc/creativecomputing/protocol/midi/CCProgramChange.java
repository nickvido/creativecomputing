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

import javax.sound.midi.ShortMessage;


/**
 * ProgramChange represents a MIDI program change. It has a MIDI port, a MIDI channel, 
 * and a number. You can receive program changes from MIDI inputs and send 
 * them to MIDI outputs.
 */
public class CCProgramChange extends CCMidiEvent{
	
	/**
	 * Initializes a new ProgramChange object.
	 * @param midiChannel MIDI channel a program change comes from or is send to
	 * @param i_number number of the program change
	 */
	public CCProgramChange(final int theNumber){
		super(ShortMessage.PROGRAM_CHANGE, theNumber,-1);
	}
	
	/**
	 * Use this method to get the program change number.
	 * @return the program change number
	 */
	public int number(){
		return data1();
	}
}

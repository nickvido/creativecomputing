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

import javax.sound.midi.MidiDevice;

abstract class CCMidiDevice{

	/**
	 * the MidiDevice for this input
	 */
	final protected MidiDevice _myMidiDevice;
	
	/**
	 * the number of the midiDevice
	 */
	final protected int _myDeviceNumber;

	/**
	 * Initializes a new MidiIn.
	 * @param libContext
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	CCMidiDevice(
		final MidiDevice theMidiDevice, 
		final int theDeviceNumber
	){
		_myMidiDevice = theMidiDevice;
		_myDeviceNumber = theDeviceNumber;
	}
	
	String name(){
		return _myMidiDevice.getDeviceInfo().getName();
	}
	
	void open(){
		try{
			if(!_myMidiDevice.isOpen()){
				_myMidiDevice.open();
			}
		}catch (Exception e){
			throw new RuntimeException("You wanted to open an unavailable output device: "+_myDeviceNumber + " "+name());
		}
	}

	/**
	 * Closes this device
	 */
	public void close(){
		if(_myMidiDevice.isOpen())_myMidiDevice.close();
	}

}

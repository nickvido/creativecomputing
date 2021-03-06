/*
Part of the proMIDI lib - http://texone.org/promidi

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
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

class CCMidiInDevice extends CCMidiDevice implements Receiver{

	private final Transmitter _myInputTransmitter;

	private final CCMidiIO _myMidiIO;
	
	/**
	 * Contains the states of the 16 midi channels for a device.
	 * true if open otherwise false.
	 */
	private final CCMidiIn[] _myMidiIns = new CCMidiIn [16];

	/**
	 * Initializes a new MidiIn.
	 * @param theMidiIO
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	CCMidiInDevice(
		final CCMidiIO theMidiIO, 
		final MidiDevice theMidiDevice, 
		final int theDeviceNumber
	){
		super(theMidiDevice, theDeviceNumber);
		_myMidiIO = theMidiIO;
		
		try{
			_myInputTransmitter = theMidiDevice.getTransmitter();
		}catch (MidiUnavailableException e){
			throw new RuntimeException();
		}
	}
	
	String name(){
		return _myMidiDevice.getDeviceInfo().getName();
	}
	
	void open(){
		super.open();
		_myInputTransmitter.setReceiver(this);
	}
	
	void openMidiChannel(final int theMidiChannel){
		if(_myMidiIns[theMidiChannel]==null)
			_myMidiIns[theMidiChannel] = new CCMidiIn(theMidiChannel,_myMidiIO);
	}
	
	void closeMidiChannel(final int theMidiChannel){
		_myMidiIns[theMidiChannel]=null;
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theMidiChannel
	){
		open();
		openMidiChannel(theMidiChannel);
		_myMidiIns[theMidiChannel].plug(theObject,theMethodName,-1);
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName
	){
		open();
		for(int i = 0; i < 16;i++){
			openMidiChannel(i);
			_myMidiIns[i].plug(theObject,theMethodName,-1);
		}
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theMidiChannel,
		final int theValue
	){
		open();
		openMidiChannel(theMidiChannel);
		_myMidiIns[theMidiChannel].plug(theObject,theMethodName,theValue);
	}

	/**
	 * Sorts the incoming MidiIO data in the different Arrays.
	 * @invisible
	 * @param theMessage MidiMessage
	 * @param theDeltaTime long
	 */
	@Override
	public void send(final MidiMessage theMessage, final long theDeltaTime){
		final ShortMessage shortMessage = (ShortMessage) theMessage;

		// get messageInfos
		final int midiChannel = shortMessage.getChannel();

		if (_myMidiIns[midiChannel] == null)
			return;

		final int midiCommand = shortMessage.getCommand();
		final int midiData1 = shortMessage.getData1();
		final int midiData2 = shortMessage.getData2();

		if (midiCommand == CCMidiEvent.NOTE_ON && midiData2 > 0){
			final CCNote note = new CCNote(midiData1, midiData2);
			_myMidiIns[midiChannel].sendNoteOn(note,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiEvent.NOTE_OFF || midiData2 == 0){
			final CCNote note = new CCNote(midiData1, midiData2);
			_myMidiIns[midiChannel].sendNoteOff(note,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiEvent.CONTROL_CHANGE){
			final CCController controller = new CCController(midiData1, midiData2);
			_myMidiIns[midiChannel].sendController(controller,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiEvent.PROGRAM_CHANGE){
			final CCProgramChange programChange = new CCProgramChange(midiData1);
			_myMidiIns[midiChannel].sendProgramChange(programChange,_myDeviceNumber,midiChannel);
		}
	}
}

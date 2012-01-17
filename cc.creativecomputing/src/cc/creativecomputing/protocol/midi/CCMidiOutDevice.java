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
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

/**
 * This class has no accessable constructor use MidiIO.openOutput() to get a MidiOut. 
 * MidiOut is the direct connection to one of your MIDI out ports. You can use different  
 * methods to send notes, control and program changes through one MIDI out port.
 */
class CCMidiOutDevice extends CCMidiDevice{

	private final Receiver outputReceiver;

	CCMidiOutDevice(
		final MidiDevice theMidiDevice,
		final int theDeviceNumber
	) throws MidiUnavailableException{
		super(theMidiDevice,theDeviceNumber);
		outputReceiver = theMidiDevice.getReceiver();
	}

	/**
	 * @param theEvent
	 * @throws CCInvalidMidiDataException
	 */
	public void sendEvent(final CCMidiEvent theEvent){
		if (theEvent.getChannel() > 15 || theEvent.getChannel() < 0){
			throw new CCInvalidMidiDataException("You tried to send to MIDI channel" + theEvent.getChannel() + ". With MIDI you only have the channels 0 - 15 available.");
		}
		outputReceiver.send(theEvent, -1);
	}

	/**
	 * Use this method to send a control change to the MIDI output. You can send 
	 * control changes to change the sound on MIDI sound sources for example.
	 * @param theController the controller you want to send.
	 */
	public void sendController(CCController theController){
		try{
			sendEvent(theController);
		}catch (CCInvalidMidiDataException e){
			if (theController.number() > 127 || theController.number() < 0){
				throw new CCInvalidMidiDataException("You tried to send the controller number " + theController.number() + ". With MIDI you only have the controller numbers 0 - 127 available.");
			}
			if (theController.value() > 127 || theController.value() < 0){
				throw new CCInvalidMidiDataException("You tried to send the controller value " + theController.value() + ". With MIDI you only have the controller values 0 - 127 available.");
			}
		}
	}

	/**
	 * With this method you can send a note on to your MIDI output. You can send note on commands
	 * to trigger MIDI sound sources. Be aware that you have to take care to send note off commands
	 * to release the notes otherwise you get MIDI hang ons.
	 * @param theNote Note, the note you want to send the note on for
	 */
	public void sendNoteOn(CCNote theNote){
		try{
			sendEvent(theNote);
		}catch (CCInvalidMidiDataException e){
			if (theNote.pitch() > 127 || theNote.pitch() < 0){
				throw new CCInvalidMidiDataException("You tried to send a note with the pitch " + theNote.pitch() + ". With MIDI you only have pitch values from 0 - 127 available.");
			}
			if (theNote.velocity() > 127 || theNote.velocity() < 0){
				throw new CCInvalidMidiDataException("You tried to send a note with the velocity " + theNote.velocity() + ". With MIDI you only have velocities values from 0 - 127 available.");
			}
		}
	}

	/**
	 * Use this method to send a note off command to your MIDI output. You have to send note off commands 
	 * to release send note on commands.
	 * @param theNote Note, the note you want to send the note off for
	 */
	public void sendNoteOff(CCNote theNote){
		theNote.setToNoteOff();
		sendNoteOn(theNote);
	}

	/**
	 * With this method you can send program changes to your MIDI output. Program changes are used 
	 * to change the preset on a MIDI sound source.
	 * @param programChange ProgramChange, program change you want to send
	 */
	public void sendProgramChange(CCProgramChange theProgramChange){
		try{
			sendEvent(theProgramChange);
		}catch (CCInvalidMidiDataException e){
			if (theProgramChange.number() > 127 || theProgramChange.number() < 0){
				throw new CCInvalidMidiDataException("You tried to send the program number " + theProgramChange.number() + ". With MIDI you only have the program numbers 0 - 127 available.");
			}
		}
	}
}

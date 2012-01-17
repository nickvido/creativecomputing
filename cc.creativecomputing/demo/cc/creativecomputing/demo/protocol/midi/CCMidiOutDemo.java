package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCMidiOut;
import cc.creativecomputing.protocol.midi.CCNote;

public class CCMidiOutDemo extends CCApp{

	private CCMidiIO _myMidiIO;
	private CCMidiOut _myMidiOut;

	public void setup(){

		// get an instance of MidiIO
		_myMidiIO = CCMidiIO.getInstance();
		_myMidiIO.printDevices();

		// print a list with all available devices
		_myMidiIO.printDevices();

		// open an midiout using the first device and the first channel
		_myMidiOut = _myMidiIO.midiOut(0, 1);

//		for(int i = 0; i < 20;i++){
//			Note note = new Note((int) random(127), (int) random(127), (int) random(1000,10000));
//			midiOut.sendNote(note);
//		}
	}
	
	boolean isPressed = true;
	
	public void mousePressed(){
		for(int i = 0; i < 100;i++){
			CCNote note = new CCNote(i, (int) CCMath.random(127));
			if(isPressed)_myMidiOut.sendNoteOn(note);
			else _myMidiOut.sendNoteOff(note);
		}
		isPressed = !isPressed;
	}

	public void draw(){
		
//		println("framerate:" + frameRate);
	}

	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMidiOutDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


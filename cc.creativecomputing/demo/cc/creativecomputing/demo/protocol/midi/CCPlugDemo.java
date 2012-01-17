package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.protocol.midi.CCController;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCNote;
import cc.creativecomputing.protocol.midi.CCProgramChange;

public class CCPlugDemo extends CCApp {

	private CCMidiIO midiIO;
	private MidiEventHandler midiEventHandler;

	public void setup() {

		midiIO = CCMidiIO.getInstance();
		midiIO.printDevices();
		midiIO.openInput(8);
		midiEventHandler = new MidiEventHandler();
	}

	public void draw() {

	}

	public class MidiEventHandler {

		MidiEventHandler() {
			midiIO.plug(this, "noteOn", 8, 0);
			midiIO.plug(this, "noteOff", 8, 0);
			midiIO.plug(this, "controllerIn", 8, 0);
			midiIO.plug(this, "programChange", 8, 0);
		}

		public void noteOn(CCNote note) {
			int vel = note.velocity();
			int pit = note.pitch();

			g.color(255, vel * 2, pit * 2, vel * 2);
			g.ellipse(vel * 5, pit * 5, 30, 30);
		}

		public void noteOff(CCNote note) {
			int pit = note.pitch();

			g.color(255, pit * 2, pit * 2, pit * 2);
			g.ellipse(pit * 5, pit * 5, 30, 30);
		}

		public void controllerIn(CCController controller) {
			int num = controller.number();
			int val = controller.value();
			g.color(255, num * 2, val * 2, num * 2);
			g.ellipse(num * 5, val * 5, 30, 30);
		}

		public void programChange(CCProgramChange programChange) {
			int num = programChange.number();

			g.color(255, num * 2, num * 2, num * 2);
			g.ellipse(num * 5, num * 5, 30, 30);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPlugDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}

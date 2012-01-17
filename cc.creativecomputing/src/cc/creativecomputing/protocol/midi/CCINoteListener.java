package cc.creativecomputing.protocol.midi;


public interface CCINoteListener {

	public void onNoteOn(final CCNote theNote);
	
	public void onNoteOff(final CCNote theNote);
}

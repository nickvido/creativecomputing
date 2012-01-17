package cc.creativecomputing.sound.supercollider;

@SuppressWarnings("serial")
public class CCSoundIDNotFoundException extends Exception {
	
	public CCSoundIDNotFoundException(String theSoundID) {
		super("Sound id not found: " + theSoundID);
	}
	
}

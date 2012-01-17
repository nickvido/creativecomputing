package cc.creativecomputing.sound.supercollider;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class CCSoundLibrary extends HashMap<String, CCSound> {
	
	Map<String, CCSound> _myDictionary;
	
	public String getSoundPath(String theSoundID) {
		return get(theSoundID).path;
	}
	
	public void setSound(String theSoundID, CCSound theSound) {
		put(theSoundID, theSound);
	}
	
	public void setSound(String theSoundID, String thePath) {
		setSound(theSoundID, thePath, false);
	}
	
	public void setSound(String theSoundID, String thePath, boolean theLoop) {
		CCSound mySound = new CCSound(theSoundID, thePath, theLoop);
		put(theSoundID, mySound);
	}
	
	public CCSound getSound(String theSoundID) {
		return get(theSoundID);
	}
	
}

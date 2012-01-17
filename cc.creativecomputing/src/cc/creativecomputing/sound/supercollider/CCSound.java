package cc.creativecomputing.sound.supercollider;

public class CCSound {
	
	public String 	id;
	public String 	path;
	public boolean 	loop;
	
	public CCSound(String theID, String thePath, boolean theLoop) {
		id = theID; 
		path = thePath;
		loop = theLoop;
	}
	
	public CCSound(String theID, String thePath) {
		this(theID, thePath, false);
	}

}

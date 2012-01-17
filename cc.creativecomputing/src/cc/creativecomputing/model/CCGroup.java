package cc.creativecomputing.model;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;



public class CCGroup {
	private String _myGroupName;
	private List<CCSegment> _mySegments;
	private boolean _myIsActive = true;

	public CCGroup(final String theGroupName){
		_mySegments = new ArrayList<CCSegment>();
		_myGroupName = theGroupName;
	}

	public String groupName() {
		return _myGroupName;
	}

	public List<CCSegment> segments() {
		return _mySegments;
	}

	public void isActiv(final boolean theIsActive){
		_myIsActive = theIsActive;
	}

	public boolean isActiv(){
		return _myIsActive;
	}
	
	public void draw(CCGraphics g){
		for(CCSegment mySegment:_mySegments){
			mySegment.draw(g);
		}
	}
}

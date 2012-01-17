package cc.creativecomputing.cv;

import cc.creativecomputing.math.CCVector2f;

public class CCTouch {

	private CCVector2f _myPosition;
	private CCVector2f _myRelativePosition;
	private CCVector2f _myMotion;
	
	private boolean _myIsCorrelated = false;
	private int _myCorrelatedID;

	public CCTouch() {
		_myPosition = new CCVector2f();
		_myRelativePosition = new CCVector2f();
		_myMotion = new CCVector2f();
		_myCorrelatedID = -1;

	}
	
	public CCTouch(CCVector2f thePosition, CCVector2f theRelativePosition) {

		this();
		_myPosition = thePosition;
		_myRelativePosition = theRelativePosition;
	}

	/**
	 * @param thePosition the position to set
	 */
	void position(CCVector2f thePosition) {
		_myPosition = thePosition;
	}

	/**
	 * @return the position
	 */
	public CCVector2f position() {
		return _myPosition;
	}
	
	public CCVector2f relativePosition() {
		return _myRelativePosition;
	}

	/**
	 * @param theIsCorrelated the isCorrelated to set
	 */
	public void isCorrelated(boolean theIsCorrelated) {
		_myIsCorrelated = theIsCorrelated;
	}

	/**
	 * @return the isCorrelated
	 */
	public boolean isCorrelated() {
		return _myIsCorrelated;
	}

	/**
	 * @param theMotion the motion to set
	 */
	public void motion(CCVector2f theMotion) {
		_myMotion = theMotion;
	}

	/**
	 * @return the motion
	 */
	public CCVector2f motion() {
		return _myMotion;
	}

	/**
	 * @param correlatedID the correlatedID to set
	 */
	public void id(int theCorrelatedID) {
		_myCorrelatedID = theCorrelatedID;
	}

	/**
	 * @return the correlatedID
	 */
	public int id() {
		return _myCorrelatedID;
	}

}
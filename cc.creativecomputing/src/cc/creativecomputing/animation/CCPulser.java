package cc.creativecomputing.animation;

/**
 * CCPulser is a simple animation class to change a value between two states.
 * 
 * @author jenswunderling
 * 
 */
public class CCPulser extends CCAbstractAnimation {

	private float _myOnDuration, _myOffDuration;
	private boolean _myIsOn;

	public void update(float theDeltaTime) {
		super.update(theDeltaTime);
		if (_myIsOn && _myPassedTime > _myOnDuration) {
			_myIsOn = false;
			_myAnimationValue = 0;
			_myPassedTime = 0;
		}
		if (!_myIsOn && _myPassedTime > _myOffDuration) {
			_myIsOn = true;
			_myAnimationValue = 1f;
			_myPassedTime = 0;
			executeAction();
		}
		updateTarget();
	}

	public void start(Object theTarget, String theTargetValueName, float theOnDuration,
			float theOffDuration) {
		addTarget(theTarget, theTargetValueName);
		_myOnDuration = theOnDuration;
		_myOffDuration = theOffDuration;
		CCAnimationManager.addAnimation(this);
	}

	public void start(Object theTarget, String theTargetValueName, float theOnDuration,
			float theOffDuration, String thePulseActionName) {
		start(theTarget, theTargetValueName, theOnDuration, theOffDuration);
		setAction(thePulseActionName);
	}

	public boolean isOn() {
		return _myIsOn;
	}

	public void stop() {
		finished = true;
	}
}

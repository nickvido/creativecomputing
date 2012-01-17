package cc.creativecomputing.animation;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasingMode;

public class CCLfo extends CCAbstractAnimation {
	private float _myCycleTime = 1;
	private CCEasing _myEasing;
	private CCEaseCurve _myEasingCurve;
	private float _myMinValue = 0;
	private float _myMaxValue = 1;

	public static enum CCLfoCycleMode {
		UP, UP_DOWN, DOWN;
	};

	private CCLfoCycleMode _myCycleMode = CCLfoCycleMode.UP;

	public void start(Object theTarget, String field, float cycleTime) {
		if (theTarget != null)
			addTarget(theTarget, field);
		_myCycleTime = cycleTime;
		CCAnimationManager.addAnAnimation(this);
	}

	public void setRange(float min, float max) {
		_myMinValue = min;
		_myMaxValue = max;
	}

	public void start(Object theTarget, String field, float cycleTime,
			CCEasingMode easingMode, CCEaseCurve easeCurve) {
		addTarget(theTarget, field);
		_myCycleTime = cycleTime;
		_myEasing = new CCEasing(easingMode);
		_myEasingCurve = easeCurve;
		CCAnimationManager.addAnAnimation(this);
	}

	public void setCycleMode(CCLfoCycleMode theCycleMode) {
		_myCycleMode = theCycleMode;
	}
	
	public void setEasingCurve(CCEaseCurve theEaseCurve){
		_myEasingCurve = theEaseCurve;
	}

	private boolean GOING_UP;
	private float _myPhase;

	public void update(float theDeltaTime) {
		switch (_myCycleMode) {

		case UP:
			_myPassedTime += theDeltaTime / _myCycleTime;
			_myPassedTime %= 1;
			break;

		case DOWN:
			if (_myPassedTime < 0)
				_myPassedTime = 1;
			_myPassedTime -= theDeltaTime / _myCycleTime;
			break;

		case UP_DOWN:
			if (GOING_UP) {
				_myPassedTime += theDeltaTime / (_myCycleTime / 2);
				if (_myPassedTime >= 1) {
					_myPassedTime = 1;
					GOING_UP = false;
				}
			} else {
				_myPassedTime -= theDeltaTime / (_myCycleTime / 2);
				if (_myPassedTime <= 0) {
					_myPassedTime = 0;
					GOING_UP = true;
				}
			}
			break;

		}
		
		_myAnimationValue = _myPassedTime;
		
		if (_myEasing != null)
			switch (_myEasingCurve) {
			case IN:
				_myAnimationValue = _myEasing.easeIn(_myPassedTime);
				break;
			case OUT:
				_myAnimationValue = _myEasing.easeOut(_myPassedTime);

				break;
			case IN_OUT:
				_myAnimationValue = _myEasing.easeInOut(_myPassedTime);
				break;
			}
//		_myAnimationValue += _myPhase;
//		_myAnimationValue %= 1f;
		
		_myAnimationValue = CCMath.map(_myAnimationValue, 0, 1, _myMinValue, _myMaxValue);
		updateTarget();
	}

	public void stop() {
		finished = true;
	}

	public void setCycleTime(float theCycleTime) {
		_myCycleTime = theCycleTime;

	}

	public void setPhase(float thePhase) {
		_myPhase = thePhase;

	}

	public void start(Object target, float theCycleTime) {
		start(target, null, theCycleTime);
		
	}
}

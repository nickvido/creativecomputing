package cc.creativecomputing.animation;

import cc.creativecomputing.math.easing.*;

/**
 * A Tween is an Object which enables Animation of a Float field in any Object
 * to be animated from n1 to n2 in a certain time Step. makes use of
 * cc.math.easing
 * 
 * uses reflection. therefore, fields that need to be animated need to be
 * visible "public".
 * 
 * @author jenswunderling
 * 
 */
public class CCTween extends CCAbstractAnimation {
	//TODO: extract more fields and functions from tween to abstract animation, for optimized re-use.
	
	/*
	 * Standard easing Parameters
	 */
	private static CCEasingMode _myStandardEasingMode = CCEasingMode.LINEAR;
	private static CCEaseCurve _myStandardEasingCurve = CCEaseCurve.IN_OUT;
	
	private CCEasing _myEasing;
	private CCEaseCurve _myTweenCurve;

	public CCTween() {

	}

	public CCTween(float theDelay) {
		myDelay = theDelay;
	}
	/**
	 * sets all parameters and starts the tween
	 * @param theTarget
	 * @param theTargetValueName
	 * @param theTweenFrom
	 * @param theTweenTo
	 * @param theDuration
	 * @param theOnStop
	 * @param theEasingMode
	 * @param theEaseCurve
	 */
	public void start(Object theTarget, String theTargetValueName, float theTweenFrom,
			float theTweenTo, float theDuration, String theOnStop,
			CCEasingMode theEasingMode, CCEaseCurve theEaseCurve) {

		setTargetValue(theTweenTo);
		addTarget(theTarget, theTargetValueName);
		setAction(theOnStop);
		_myPassedTime = 0;
		_myAnimationDistance = theTweenTo - _myInitValue;
		_myAnimationDuration = theDuration;
		_myEasing = new CCEasing(theEasingMode);
		_myTweenCurve = theEaseCurve;

		CCAnimationManager.addAnimation(this);
	}

	/**
	 * simpler start method, uses standard esing mode and standard easing curve
	 * 
	 * 
	 * @param theTarget
	 * @param theTargetValueName
	 * @param theTweenFrom
	 * @param theTweenTo
	 * @param theDuration
	 * @param theOnStop
	 */
	public void start(Object theTarget, String theTargetValueName, float theTweenFrom,
			float theTweenTo, float theDuration, String theOnStop) {
		this.start(
				theTarget,
				theTargetValueName,
				theTweenFrom,
				theTweenTo,
				theDuration,
				theOnStop,
				_myStandardEasingMode,
				_myStandardEasingCurve);
	}
	
	public static void setStandardEasingMode(CCEasingMode myStandardEasingMode) {
		_myStandardEasingMode = myStandardEasingMode;
	}

	public static void setStandardEasingCurve(CCEaseCurve myStandardEasingCurve) {
		_myStandardEasingCurve = myStandardEasingCurve;
	}

	public void update(float theDeltaTime) {
		super.update(theDeltaTime);

		if (_myPassedTime < _myAnimationDuration) {
			switch (_myTweenCurve) {
			case IN:
				_myAnimationValue = _myEasing.easeIn(
						_myInitValue,
						targetValue,
						_myPassedTime,
						_myAnimationDuration);
				break;
			case OUT:
				_myAnimationValue = _myEasing.easeOut(
						_myInitValue,
						targetValue,
						_myPassedTime,
						_myAnimationDuration);
				break;
			case IN_OUT:
				_myAnimationValue = _myEasing.easeInOut(
						_myInitValue,
						targetValue,
						_myPassedTime,
						_myAnimationDuration);
				break;
			}

			updateTarget();
		} else {
			executeAction();
			finished = true;
			_myAnimationValue = _myInitValue + _myAnimationDistance;
			updateTarget();
		}

	}
}

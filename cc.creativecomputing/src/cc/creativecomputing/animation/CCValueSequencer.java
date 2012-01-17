package cc.creativecomputing.animation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CCValueSequencer extends CCAbstractAnimation {

	ArrayList<ArrayList<Float>> _mySequence = new ArrayList<ArrayList<Float>>();
	private int _mySequenceIndex;
	private boolean _myDoLoop = true;

	//TODO add HashMap of methods to call, use steps as key.
	private HashMap<ArrayList<Float>, Method> _myTriggerActions = new HashMap<ArrayList<Float>, Method>();

	public void update(float theDeltaTime) {
		_myPassedTime += theDeltaTime;

		ArrayList<Float> myCurrentStep = _mySequence.get(_mySequenceIndex);
		if (_myPassedTime > myCurrentStep.get(0) && !finished) {
			_mySequenceIndex++;
			if (_mySequenceIndex >= _mySequence.size()) {
				if (_myDoLoop)
					_mySequenceIndex = 0;
				else {
					finished = true;
					_mySequenceIndex = 0;
					return;
				}
			}
			myCurrentStep = _mySequence.get(_mySequenceIndex);
			_myAnimationValue = myCurrentStep.get(1);
			_myPassedTime = 0;
			if (_myTriggerActions.get(myCurrentStep) != null)
				try {
					_myTriggerActions.get(myCurrentStep).invoke(target, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			updateTarget();
		}
	}

	public ArrayList<Float> addStep(float theHoldTime, float theValue) {
		ArrayList myStep = new ArrayList();
		myStep.add(theHoldTime);
		myStep.add(theValue);
		_mySequence.add(myStep);
		return myStep;
	}

	public void addStep(float theHoldTime, float theValue, String theActionName) {
		Method myMethod = null;
		if (target != null) {
			try {
				myMethod = target.getClass().getMethod(theActionName, null);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		_myTriggerActions.put(addStep(theHoldTime, theValue), myMethod);
	}

	public void start(Object theTarget, String theTargetValueName) {
		addTarget(theTarget, theTargetValueName);
		CCAnimationManager.addAnimation(this);
	}

	public void stop() {
		finished = true;
	}
}

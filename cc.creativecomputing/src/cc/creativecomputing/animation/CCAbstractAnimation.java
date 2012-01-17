package cc.creativecomputing.animation;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.creativecomputing.util.logging.CCLog;

/**
 * basic animation class, holding target and timing info
 * 
 * @author jenswunderling
 * 
 */
public abstract class CCAbstractAnimation implements CCAnimation {

	Method _myAction;
	protected Field valueTarget;
	//	protected Object target;
	protected WeakReference<Object> target;
	protected String targetValueName;

	protected float _myAnimationDuration = 0.5f;
	protected float _myPassedTime = 0;
	protected float _myAnimationDistance = 1;
	protected float _myInitValue = 0;
	protected float _myAnimationValue = 0;
	protected float targetValue;

	protected boolean finished = false;

	protected float myDelay = -1;

	public static enum CCEaseCurve {
		IN, OUT, IN_OUT
	};

	public float getAnimationValue() {
		return _myAnimationValue;
	}

	private Object target() {
		Object o = null;
		if (target != null)
			o = target.get();
		return o;
	}

	public void addTarget(Object theTarget, String theTargetValueName) {
		target = new WeakReference<Object>(theTarget);

		targetValueName = theTargetValueName;

		try {
			valueTarget = target().getClass().getField(theTargetValueName);
			_myInitValue = valueTarget.getFloat(target());
			_myAnimationValue = _myInitValue;
		} catch (Exception e) {
			CCLog.error("no field or no target given");
		}

	}

	public void setAction(String theOnStop) {
		if (theOnStop != null) {
			try {
				_myAction = target().getClass().getMethod(theOnStop, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected void executeAction() {
		if (_myAction != null) {
			try {
				_myAction.invoke(target(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void updateTarget() {
		if (target() != null)
			try {
				valueTarget.setFloat(target(), _myAnimationValue);
			} catch (Exception e) {
				//				e.printStackTrace();
			}
		else
			finished = true;
	}

	public void setTargetValue(float theTargetValue) {
		targetValue = theTargetValue;
	}

	public boolean finished() {
		return finished;
	}

	public Object getTarget() {
		return target();
	}

	public String targetValueName() {
		return targetValueName;
	}

	public void update(float theDeltaTime) {
		myDelay -= theDeltaTime;

		if (myDelay > 0)
			return;
		else
			_myPassedTime += theDeltaTime;

	}

}

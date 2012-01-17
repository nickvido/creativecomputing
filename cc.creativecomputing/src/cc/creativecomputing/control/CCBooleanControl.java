package cc.creativecomputing.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIButton;
import cc.creativecomputing.math.CCVector2f;

public class CCBooleanControl extends CCValueControl<Boolean>{

	public CCBooleanControl(CCControlConnector<Boolean> theConnector, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCUIButton createUIElement(final float theWidth, final float theHeight) {
		return new CCUIButton(
			_myName, 
			_myConnector.toggle(),
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight)
		);
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	protected Boolean stringToValue(String theStringValue) {
		return Boolean.parseBoolean(theStringValue);
	}

}

package cc.creativecomputing.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIDropDown;
import cc.creativecomputing.math.CCVector2f;

public class CCEnumControl extends CCValueControl<Enum<?>>{
	
	private Method _myMethod;

	public CCEnumControl(CCControlConnector<Enum<?>> theConnector, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCUIDropDown createUIElement(final float theWidth, final float theHeight) {
		return new CCUIDropDown(
			_myName, 
			_myConnector.type(),
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight)
		);
	}

	@Override
	protected Enum<?> stringToValue(String theStringValue) {
		try {
			if(_myMethod == null)_myMethod = _myConnector.type().getMethod("valueOf", new Class<?>[] {String.class});
			return (Enum<?>)_myMethod.invoke(null, new Object[] {theStringValue});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

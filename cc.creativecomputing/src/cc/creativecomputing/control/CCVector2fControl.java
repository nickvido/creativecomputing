/**
 * 
 */
package cc.creativecomputing.control;

import java.lang.reflect.Member;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIField;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;


public class CCVector2fControl extends CCValueControl<CCVector2f> implements CCUIChangeListener{

	
	/**
	 * @param theMyField
	 * @param theMyObject
	 */
	public CCVector2fControl(CCControlConnector<CCVector2f> theConnector, final float theElementWidth, final float theElementHeight, final int theNumberOfPresets) {
		super(theConnector, theElementWidth, theElementHeight, theNumberOfPresets);
	}



	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.controls.CCValueControl#createUIElement(java.lang.String, cc.creativecomputing.control.CCUIControlTab, cc.creativecomputing.control.CCUIControl, java.util.List, int)
	 */
	@Override
	public CCUIField createUIElement(final float theWidth, final float theHeight) {
		return new CCUIField(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			new CCVector2f(_myConnector.minX(), _myConnector.minY()),
			new CCVector2f(_myConnector.maxX(), _myConnector.maxY())
		);
	}



	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.controls.CCValueControl#defaultValue(cc.creativecomputing.control.CCUIControl)
	 */
	@Override
	public CCVector2f defaultValue() {
		if(_myDefault == null) {
			_myDefault = new CCVector2f();
		}
		
		_myDefault.x = CCMath.constrain(_myDefault.x, _myConnector.minX(), _myConnector.maxX());
		_myDefault.y = CCMath.constrain(_myDefault.y, _myConnector.minY(), _myConnector.maxY());
		
		return _myDefault;
	}



	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.controls.CCValueControl#stringToValue(java.lang.String)
	 */
	@Override
	protected CCVector2f stringToValue(String theStringValue) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
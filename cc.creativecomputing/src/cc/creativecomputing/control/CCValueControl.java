package cc.creativecomputing.control;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIElement;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.util.logging.CCLog;

public abstract class CCValueControl<ValueType> implements CCUIChangeListener{
	
	public static interface CCChangeValueListener<ValueType>{
		public void onChangeValue(ValueType theValue);
	}
	
	protected CCControlConnector<ValueType> _myConnector;
	
	protected ValueType _myDefault;
	protected CCUIValueElement<ValueType> _myUIElement;
	
	protected CCListenerManager<CCChangeValueListener> _myEvents = CCListenerManager.create(CCChangeValueListener.class);
	
	protected String _myName;
	
	/**
	 * @param theMyField
	 * @param theMyObject
	 */
	public CCValueControl(CCControlConnector<ValueType> theConnector, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		_myConnector = theConnector;
		
		_myDefault = null;
		
		_myName = _myConnector.name();
		
		_myUIElement = createUIElement(theDefaultWidth, theDefaultHeight);
		
		numberOfPresets(theNumberOfPresets);
	
		_myUIElement.addChangeListener(this);
		_myUIElement.onChange();
	}
	
	public CCListenerManager<CCChangeValueListener> events(){
		return _myEvents;
	}
	
	public void numberOfPresets(int theNumberOfPresets) {
		List<ValueType> myValues = new ArrayList<ValueType>();
		for(int i = 0; i < theNumberOfPresets;i++) {
			myValues.add(defaultValue());
		}
		_myUIElement.values(myValues);
	}
	
	public CCUIValueElement<ValueType> element() {
		return _myUIElement;
	}

	protected abstract ValueType stringToValue(final String theStringValue);
	
	ValueType defaultValue() {
		return _myDefault;
	}
	
	public String name() {
		return _myName;
	}
	
	public void name(String theName){
		_myName = theName;
		_myUIElement.label(theName);
	}
	
	public void value(ValueType theValue){
		_myUIElement.changeValue(theValue);
		_myEvents.proxy().onChangeValue(theValue);
	}
	
	public ValueType value() {
		return _myUIElement.value();
	}
	
	public boolean external() {
		return _myConnector.external();
	}
	
	public boolean accumulate() {
		return _myConnector.accumulate();
	}
	
	public float min() {
		return _myConnector.min();
	}
	
	public float max() {
		return _myConnector.max();
	}
	
	abstract CCUIValueElement createUIElement(final float theWidth, final float theHeight);
	
	public void onChange(CCUIElement theElement){
		CCUIValueElement<ValueType> myValueElement = (CCUIValueElement<ValueType>)theElement;
		_myConnector.onChange(myValueElement);
		_myEvents.proxy().onChangeValue(myValueElement.value());
	}

	@SuppressWarnings("unchecked")
	public void readBack() {
		_myConnector.readBack(_myUIElement);
	}
}

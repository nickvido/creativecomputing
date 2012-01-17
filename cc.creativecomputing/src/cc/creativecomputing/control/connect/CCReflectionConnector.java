/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.control.connect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import quicktime.app.ui.UIElement;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 *
 */
public class CCReflectionConnector<ValueType> extends CCControlConnector<ValueType>{

	protected CCControl _myControl;
	protected final Member _myMember;
	protected final Object _myObject;
	protected Class<?> _myClass;
	
	public CCReflectionConnector(CCControl theControl, Member theMember, Object theObject) {
		_myControl = theControl;
		_myMember = theMember;
		_myObject = theObject;
		
		if(_myMember instanceof Field){
			_myClass = ((Field)_myMember).getType();
		}else{
			_myClass = ((Method)_myMember).getParameterTypes()[0];
		}
	}
	
	public CCControl control() {
		return _myControl;
	}
	
	public void onChange(CCUIValueElement<ValueType> theElement){
		try {
			if(_myMember instanceof Field){
				((Field)_myMember).set(_myObject, theElement.value());
			}else{
				((Method)_myMember).invoke(_myObject, theElement.value());
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public ValueType defaultValue() {
		if(_myMember instanceof Field){
			_myClass = ((Field)_myMember).getType();
			try {
				return (ValueType)((Field)_myMember).get(_myObject);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	@Override
	public void readBack(CCUIValueElement<ValueType> theElement) {
		try {
			if (_myMember instanceof Field) {
				theElement.value((ValueType)((Field)_myMember).get(_myObject));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
		 	e.printStackTrace();
		} catch (ClassCastException e) {
			CCLog.error("warning: could not cast " + _myMember.getName() + " for readBack.");
		}
	}
	
	@Override
	public String name() {
		return _myControl.name().equals("") ? _myMember.getName() : _myControl.name();
	}
	
	@Override
	public float min() {
		return _myControl.min();
	}
	
	@Override
	public float max() {
		return _myControl.max();
	}
	
	@Override
	public float minX() {
		return _myControl.minX();
	}
	
	@Override
	public float maxX() {
		return _myControl.maxX();
	}
	
	@Override
	public float minY() {
		return _myControl.minY();
	}
	
	@Override
	public float maxY() {
		return _myControl.maxY();
	}

	@Override
	public boolean toggle() {
		return _myControl.toggle();
	}
	
	@Override
	public Class<?> type() {
		return _myClass;
	}
	
	@Override
	public int numberOfEnvelopes() {
		return _myControl.numberOfEnvelopes();
	}
	
	@Override
	public boolean external() {
		return _myControl.external();
	}
	
	@Override
	public boolean accumulate() {
		return _myControl.accumulate();
	}
}

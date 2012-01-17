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

import cc.creativecomputing.control.ui.CCUIValueElement;

/**
 * @author christianriekoff
 *
 */
public class CCDirectConnector<ValueType> extends CCControlConnector<ValueType>{

	private String _myName;
	
	private ValueType _myValue;
	private ValueType _myDefault;
	private ValueType _myMin;
	private ValueType _myMax;
	
	public CCDirectConnector(
		String theName,
		ValueType theValue, ValueType theDefault, ValueType theMin, ValueType theMax
	) {
		_myName = theName;
		_myValue = theValue;
		_myDefault = theDefault;
		_myMin = theMin;
		_myMax = theMax;
	}
	
	@Override
	public ValueType defaultValue() {
		return _myDefault;
	}

	@Override
	public float max() {
		if(_myMax instanceof Number) return ((Number)_myMax).floatValue() ;
		return 0;
	}

	@Override
	public float maxX() {
		return 0;
	}

	@Override
	public float maxY() {
		return 0;
	}

	@Override
	public float min() {
		if(_myMin instanceof Number) return ((Number)_myMin).floatValue() ;
		return 0;
	}

	@Override
	public float minX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float minY() {
		return 0;
	}

	@Override
	public String name() {
		return _myName;
	}

	@Override
	public int numberOfEnvelopes() {
		return 0;
	}

	@Override
	public void onChange(CCUIValueElement<ValueType> theElement) {
	}

	@Override
	public void readBack(CCUIValueElement<ValueType> theElement) {
	}

	@Override
	public boolean toggle() {
		return false;
	}

	@Override
	public Class<?> type() {
		return null;
	}

	@Override
	public boolean external() {
		return false;
	}
	
	@Override
	public boolean accumulate() {
		return false;
	}
}

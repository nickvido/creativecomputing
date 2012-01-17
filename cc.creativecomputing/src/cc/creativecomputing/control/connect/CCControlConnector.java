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

import quicktime.app.ui.UIElement;
import cc.creativecomputing.control.ui.CCUIValueElement;

/**
 * @author christianriekoff
 *
 */
public abstract class CCControlConnector <ValueType>{

	public abstract String name();
	
	public abstract ValueType defaultValue();
	
	public abstract void onChange(CCUIValueElement<ValueType> theElement);
	
	public abstract void readBack(CCUIValueElement<ValueType> theElement);
	
	public abstract float min();
	
	public abstract float max();
	
	public abstract float minX();
	
	public abstract float maxX();
	
	public abstract float minY();
	
	public abstract float maxY();
	
	public abstract boolean toggle();
	
	public abstract int numberOfEnvelopes();
	
	public abstract Class<?> type();
	
	public abstract boolean external();
	
	public abstract boolean accumulate();
}

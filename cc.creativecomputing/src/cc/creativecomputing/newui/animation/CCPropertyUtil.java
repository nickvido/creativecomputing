/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.newui.animation;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

/**
 * @author christianriekoff
 *
 */
public class CCPropertyUtil {
	
	public static CCProperty propertyAnnotation(Field theField, String theProperty) {
		CCProperty myProperty = theField.getAnnotation(CCProperty.class);
		if(myProperty == null)return null;
		if(!myProperty.name().equals(theProperty))return null;
		return myProperty;
	}
	
	public static Object property(String theProperty, Object theObject) {
		String[] myProperties = theProperty.split("\\.");
		
		CCPropertyObject myPropertyObject = theObject.getClass().getAnnotation(CCPropertyObject.class);
		if(myPropertyObject == null) {
			return null;
		}
		
		Object myObject = theObject;
		Class<?> myClass = theObject.getClass();
		
		try {
		for(int i = 0; i < myProperties.length;i++) {
			String myPropertyName = myProperties[i];
			boolean myHasClass = false;
			
			for(Field myField:myClass.getDeclaredFields()) {
				myField.setAccessible(true);
				
				CCProperty myProperty = propertyAnnotation(myField,myPropertyName);
				if(myProperty == null)continue;
				
				Object myObject2 = myField.get(myObject);
				if(myObject2 == null)return null;
				
				myClass = myObject2.getClass();
				myObject = myObject2;
				myHasClass = true;
				
				if(i == myProperties.length - 1) {
					return myObject2;
				}
			}
			
			if(myHasClass)continue;
			
			
		}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}

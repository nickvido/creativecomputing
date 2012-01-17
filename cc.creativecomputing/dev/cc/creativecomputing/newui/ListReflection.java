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
package cc.creativecomputing.newui;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * @author christianriekoff
 *
 */
public class ListReflection {
	private List<Float> stringList;
	
	public static void main(String[] args) {
		Field field;
		try {
			field = ListReflection.class.getDeclaredField("stringList");
			field.setAccessible(true);
		} catch (Exception e) {
			return;
		}

		Type genericFieldType = field.getGenericType();
		
		if(genericFieldType instanceof Class) {
			System.out.println("NO GENERIC");
		}
		
		
		System.out.println(genericFieldType);
		    
		if(genericFieldType instanceof ParameterizedType){
		    ParameterizedType aType = (ParameterizedType) genericFieldType;
		    Type[] fieldArgTypes = aType.getActualTypeArguments();
		    if(fieldArgTypes[0] instanceof WildcardType) {
		    	System.out.println("wildcard");
		    }else {
		    	 for(Type fieldArgType : fieldArgTypes){
				        Class fieldArgClass = (Class) fieldArgType;
				        System.out.println("fieldArgClass = " + fieldArgClass);
				    }
		    }
		   
		}
		
		for(Class myInterface:List.class.getInterfaces()) {
			System.out.println(myInterface.getName());
		}
	}
}

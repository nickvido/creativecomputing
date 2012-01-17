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
package cc.creativecomputing.demo.xml;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLObjectSerializer;

/**
 * 
 * @author christianriekoff
 *
 */
public class CCXMLAddObjectFieldsDemo extends CCApp {
	
	public static enum TestEnum{
		CONST1, CONST2
	}
	
	@CCPropertyObject(name="type1")
	public static class Type1{
		
		@CCProperty(name = "t1_value1")
		private float _myValue1;
		
		@CCProperty(name = "t1_value2", node=false)
		private float _myValue2;
		
		@CCProperty(name = "t1_value3")
		private boolean _myValue3;
		
		@CCProperty(name = "t1_value4")
		private TestEnum _myValue4;
		
		public Type1(float theValue1, float theValue2, boolean theValue3, TestEnum theValue4) {
			_myValue1 = theValue1;
			_myValue2 = theValue2;
			_myValue3 = theValue3;
			_myValue4 = theValue4;
		}
		
		private Type1() {
			
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TYPE1:" + 
			"\nvalue1:" + _myValue1 + 
			"\nvalue2:" + _myValue2 + 
			"\nvalue3:" + _myValue3 + 
			"\nvalue4:" + _myValue4;
		}
	}

	@CCPropertyObject(name="type2")
	public static class Type2{
		
		@CCProperty(name = "t2_value1")
		Type1 _myType1;
		
		public Type2(Type1 theType1) {
			_myType1 = theType1;
		}
		
		private Type2() {
			
		}
		
		@Override
		public String toString() {
			return "TYPE2:" + 
			"\nvalue1:" + _myType1;
		}
	}
	
	public static void main(String[] args) {
		Type1 myObject1 = new Type1(2.3f,4.3f, true, TestEnum.CONST2);
		
		CCXMLObjectSerializer mySerializer = new CCXMLObjectSerializer();
		
		CCXMLElement myElement = new CCXMLElement("test");
		mySerializer.addChild(myElement,myObject1);
		
		Type2 myObject2 = new Type2(myObject1);
		mySerializer.addChild(myElement,myObject2);
		myElement.print();
		
		Type1 myReadObject1 = mySerializer.toObject(myElement.child("type1"),Type1.class);
		System.out.println(myReadObject1);
		
		Type2 myReadObject2 = mySerializer.toObject(myElement.child("type2"),Type2.class);
		System.out.println(myReadObject2);
	}
}


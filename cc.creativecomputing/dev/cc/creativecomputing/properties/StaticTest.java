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
package cc.creativecomputing.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author christianriekoff
 *
 */
public class StaticTest {
	
	public static class Class1{
		static Map<String, String> yo = new HashMap<String, String>();
		
		static {
			System.out.println("class1");
			yo.put("bla1", "bla1");
		}
	}
	
	public static class Class2 extends Class1{
		static {
			System.out.println("class2");
			yo.put("bla2", "bla2");
		}
	}
	
	public static class Class3 extends Class1{
		static {
			System.out.println("class3");
			yo.put("bla3", "bla3");
		}
	}
	
	public static void main(String[] args) {
	for(String myKey:Class3.yo.keySet()) {
		System.out.println(myKey);
	}
	}
}

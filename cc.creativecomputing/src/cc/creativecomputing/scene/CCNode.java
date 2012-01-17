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
package cc.creativecomputing.scene;

import java.util.ArrayList;
import java.util.List;

/**
 * @author christianriekoff
 *
 */
public class CCNode extends CCSpatial{
	
	protected List<CCSpatial> _myChildren;
	
	public CCNode () {
		_myChildren = new ArrayList<CCSpatial>();
	}
	
	public int numberOfChildren() {
		return _myChildren.size();
	}
	
	public void addChild (CCSpatial theChild) {
		_myChildren.add(theChild);
	}
	public boolean removeChild (CCSpatial theChild) {
		return _myChildren.remove(theChild);
	}

	public CCSpatial removeChild(int theIndex) {
		return _myChildren.remove(theIndex);
	}
	public CCSpatial setChild (int theIndex, CCSpatial theChild){
		return _myChildren.set(theIndex, theChild);
	}
		
	public CCSpatial getChild (int theIndex) {
		return _myChildren.get(theIndex);
	}
		
}

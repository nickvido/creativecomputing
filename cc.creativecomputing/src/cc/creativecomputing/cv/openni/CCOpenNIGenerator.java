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
package cc.creativecomputing.cv.openni;

import org.OpenNI.Context;
import org.OpenNI.Generator;
import org.OpenNI.Point3D;

import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
abstract class CCOpenNIGenerator <GeneratorType extends Generator>{

	protected GeneratorType _myGenerator;
	
	CCOpenNIGenerator(Context theContext){
		_myGenerator = create(theContext);
	}
	
	GeneratorType generator() {
		return _myGenerator;
	}
	
	abstract GeneratorType create(Context theContext);
	
	void update() {};
	
	/**
	 * Converts an openNI Point3D to a CCVector3f object
	 * @param thePoint3D
	 * @return
	 */
	public CCVector3f convert(Point3D thePoint3D) {
		return new CCVector3f(thePoint3D.getX(), thePoint3D.getY(), thePoint3D.getZ());
	}
	
	/**
	 * Converts a CCVector3f object to an openNI Point3D
	 * @param thePoint3D
	 * @return
	 */
	public Point3D convert(CCVector3f thePoint) {
		return new Point3D(thePoint.x, thePoint.y, thePoint.z);
	}
}

/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.math.signal;

import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCSinSignal extends CCSignal{

	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#noiseImpl(float, float, float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY, float theZ) {
		return new float[]{CCMath.sin((CCMath.sin(theX) + 1) / 2 + (CCMath.sin(theY) + 1) / 2 + (CCMath.sin(theZ) + 1) / 2)};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float, float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY) {
		return new float[]{(CCMath.sin(theX) + 1) / 2 + (CCMath.sin(theY) + 1) / 2};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float)
	 */
	@Override
	public float[] signalImpl(float theX) {
		return new float[]{(CCMath.sin(theX) + 1) / 2};
	}

}

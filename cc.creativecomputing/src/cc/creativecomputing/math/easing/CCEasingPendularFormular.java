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
package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

/**
 * @author info
 *
 */
public class CCEasingPendularFormular implements CCEasingFormular{
	
	private double _myFrequency = 3;
	private double _myPendularAmount = 1;
	private double _myFrequencyIncrease = 0;
	
	public CCEasingPendularFormular(final double theFrequency, final double thePendularAmount) {
		_myFrequency = theFrequency;
		_myPendularAmount = thePendularAmount;
	}
	
	public CCEasingPendularFormular(final double theFrequency) {
		_myFrequency = theFrequency;
	}
	
	public CCEasingPendularFormular() {
		this(3);
	}
	
	public void frequency(final double theFrequency) {
		_myFrequency = theFrequency;
	}
	
	public void frequencyIncrease(final double theFrequencyIncrease) {
		_myFrequencyIncrease = theFrequencyIncrease;
	}
	
	public void pendularAmount(final double theAmount) {
		_myPendularAmount = theAmount;
	}

	public double easeIn(final double theBlend) {
		return 1 - CCMath.cos(theBlend * CCMath.HALF_PI);
	}

	public double easeOut(final double theBlend) {
		return 
			(CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2 + 
			(CCMath.cos(CCMath.TWO_PI * theBlend * CCMath.blend(1, _myFrequencyIncrease,theBlend) * _myFrequency + CCMath.PI) + 1) * (1 - theBlend);
	}

	public double easeInOut(final double theBlend) {
		return 
			(CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2 + 
			(CCMath.cos(CCMath.TWO_PI * theBlend * CCMath.blend(1, theBlend, _myFrequencyIncrease) * _myFrequency + CCMath.PI) + 1) * (1 - theBlend) * _myPendularAmount;
	}

	public CCEasingPendularFormular clone() {
		return new CCEasingPendularFormular(_myFrequency);
	}
}

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
package cc.creativecomputing.math.util;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCMotionLimiter {
	@CCControl(name = "look ahead", min = 0, max = 1)
	public static float _cLookAhead = 0;
	
	@CCControl(name = "max velocity", min = 0, max = 250)
	public static float _cMaxVelocity = 0;
	
	@CCControl(name = "max acceleration", min = 0, max = 250)
	public static float _cMaxAcceleration = 0;

	protected double _myPosition = 0;
	protected double _myFuturePosition = 0;
	protected double _myVelocity = 0;
	protected double _myAcceleration = 0;
	
	public double limit2(double theTarget, double theDeltaTime) {
		_myFuturePosition = _myPosition + _myVelocity * _cLookAhead;
		
		double myNeededVelocity = theTarget - _myFuturePosition;
		myNeededVelocity /= theDeltaTime;
		myNeededVelocity = CCMath.constrain(myNeededVelocity, -_cMaxVelocity, _cMaxVelocity);
		
		double myNeededAcceleration = myNeededVelocity - _myVelocity;
		myNeededAcceleration /= theDeltaTime;
		
		_myAcceleration = CCMath.constrain(myNeededAcceleration, -_cMaxAcceleration, _cMaxAcceleration);
		_myVelocity += _myAcceleration * theDeltaTime;
		_myPosition += _myVelocity * theDeltaTime;

		return _myPosition;
	}
	
	public float limit2(float theTarget, float theDeltaTime) {
		return (float)limit2((double)theTarget, (double)theDeltaTime);
	}
	
	public double limit(double theTarget, double theDeltaTime) {
		_myFuturePosition = _myPosition + _myVelocity * _cLookAhead;
		
		double myFutureTargetDiff = theTarget - _myFuturePosition;
		
		_myAcceleration = myFutureTargetDiff;
		
		_myAcceleration = CCMath.constrain(_myAcceleration, -_cMaxAcceleration, _cMaxAcceleration);
		
		_myVelocity += _myAcceleration * theDeltaTime;
		_myVelocity = CCMath.constrain(_myVelocity, -_cMaxVelocity, _cMaxVelocity);
		
		_myVelocity += _myAcceleration * theDeltaTime;
		_myPosition += _myVelocity * theDeltaTime;
		
		return _myPosition;
	}
	
	public float limit(float theTarget, float theDeltaTime) {
		return (float)limit((double)theTarget, (double)theDeltaTime);
	}
	
	public double futurePosition() {
		return _myFuturePosition;
	}
	
	public double acceleration() {
		return _myAcceleration;
	}
	
	public double velocity() {
		return _myVelocity;
	}
}

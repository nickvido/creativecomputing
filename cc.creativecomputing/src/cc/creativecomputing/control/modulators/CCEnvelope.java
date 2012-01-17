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
package cc.creativecomputing.control.modulators;

import cc.creativecomputing.control.timeline.CCTimedData;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCEnvelope implements CCUpdateListener{

	private CCTimedData[] _myTimedDatas;
	
	private float _myTime = Float.MIN_VALUE;
	private float _myValue;
	
	private float _myMin = 0;
	private float _myMax = 1;
	
	private int _myIndex = 0;
	
	public CCEnvelope() {
		
	}

    public float min() {
        return _myMin;
    }

    public float max() {
        return _myMax;
    }
	
	public void min(final float theMin) {
		_myMin = theMin;
	}
	
	public void max(final float theMax) {
		_myMax = theMax;
	}

    public CCTimedData data(final int theIndex) {
        if (theIndex < _myTimedDatas.length) {
            return _myTimedDatas[theIndex];
        } else {
            return null;
        }
    }

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	public void update(float theDeltaTime) {
		_myTime += theDeltaTime;
		if(_myTimedDatas == null){
			return;
		}
		if(_myIndex >= _myTimedDatas.length) {
			return;
		}
		_myTimedDatas[_myIndex].time(_myTime);
		_myValue = _myTimedDatas[_myIndex].value(_myTime);
	}
	
	public void play() {
		_myTime = 0;
	}
	
	public void play(final int theIndex) {
		_myIndex = theIndex;
		_myTime = 0;
	}
	
	public float value() {
		return CCMath.blend(_myMin, _myMax, _myValue);
	}
	
	public CCEnvelope instance(){
		CCEnvelope myResult = new CCEnvelope();
		myResult._myMin = _myMin;
		myResult._myMax = _myMax;
		myResult._myTimedDatas = _myTimedDatas;
		return myResult;
	}
}

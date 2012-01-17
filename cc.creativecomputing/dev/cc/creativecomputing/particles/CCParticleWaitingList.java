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
package cc.creativecomputing.particles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author christianriekoff
 *
 */
public class CCParticleWaitingList {
	
	private float _myTimeStep;
	private int _myOffset = 0;
	private List<CCParticle>[] _myWaitLists;
	private List<CCParticle> _myDeadParticles = new ArrayList<CCParticle>();

	public CCParticleWaitingList(float theTimeStep) {
		_myTimeStep = theTimeStep;
		
		// asume a default max lifetime of 120 s
		int myNumberOfSteps = (int)(120 / theTimeStep);
		_myWaitLists = new ArrayList[myNumberOfSteps];
	}
	
	public void add(CCParticle theParticle, float theLifeTime) {
		int myStep = (int)(theLifeTime / _myTimeStep);
		myStep += _myOffset;
		myStep %= _myWaitLists.length;
		
		if(_myWaitLists[myStep] == null)_myWaitLists[myStep] = new ArrayList<CCParticle>();
		_myWaitLists[myStep].add(theParticle);
	}
	
	private float _myStepTime = 0;
	
	public void update(float theDeltaTime) {
		_myStepTime += theDeltaTime;
		if(_myStepTime > _myTimeStep) {
			_myStepTime -= _myTimeStep;
			
			if(_myWaitLists[_myOffset] != null) {
				_myDeadParticles.addAll(_myWaitLists[_myOffset]);
				_myWaitLists[_myOffset].clear();
			}
			_myOffset++;
			_myOffset %= _myWaitLists.length;
		}
	}
	
	public List<CCParticle> deadParticles(){
		return _myDeadParticles;
	}
	
	public void reset() {
		_myDeadParticles.clear();
		for(int i = 0; i < _myWaitLists.length; i++) {
			if(_myWaitLists[i] != null)_myWaitLists[i].clear();
		}
	}
}

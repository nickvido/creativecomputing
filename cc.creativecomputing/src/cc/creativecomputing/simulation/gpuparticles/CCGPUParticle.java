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
package cc.creativecomputing.simulation.gpuparticles;

public class CCGPUParticle implements Comparable<CCGPUParticle>{
	private double _myTimeOfDeath;
	private double _myTimeOfBirth;
	private float lifeTime;
	private boolean _myIsFixed;
	
	int x;
	int y;
	int index;
	
	private CCGPUParticles _myParticles;
	
	public CCGPUParticle(CCGPUParticles theParticles) {
		_myParticles = theParticles;
	}

	public int compareTo(CCGPUParticle theParticle) {
		if(_myTimeOfDeath < theParticle._myTimeOfDeath)return -1;
		return 1;
	}
	
	public boolean isDead() {
		return _myTimeOfDeath < _myParticles.currentTime();
	}
	
	public float lifeTime() {
		return lifeTime;
	}
	
	public void lifeTime(final float theLifeTime) {
		lifeTime = theLifeTime;
	}
	
	public double timeOfDeath() {
		return _myTimeOfDeath;
	}
	
	public void timeOfDeath(final double theTimeOfDeath) {
		_myTimeOfDeath = theTimeOfDeath;
	}
	
	public double timeOfBirth() {
		return _myTimeOfBirth;
	}
	
	public void timeOfBirth(final double theTimeOfBirth) {
		_myTimeOfBirth = theTimeOfBirth;
	}
	
	public int index() {
		return index;
	}
	
	public float x() {
		return x;
	}
	
	public float y() {
		return y;
	}
}
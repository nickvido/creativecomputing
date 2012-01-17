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
package cc.creativecomputing.particles.forces;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.opencl.CCCLCommandQueue;

import com.jogamp.opencl.CLMemory;

/**
 * @author christianriekoff
 *
 */
public class CCAttractor extends CCForce{

	@CCControl(name="x", min = -1000, max = 1000)
	private float _myX = 1F;
	
	@CCControl(name="y", min = -1000, max = 1000)
	private float _myY = 1F;
	
	@CCControl(name="z", min = -1000, max = 1000)
	private float _myZ = 1F;
	
	@CCControl(name="radius", min = 0, max = 1000)
	private float _myRadius = 0;
	
	@CCControl(name="strength", min = -1, max = 1)
	private float _myStrength = 1F;
	
	public CCAttractor() {
		super("attractor.cl", "attractor");
	}
	
	public void prepareKernel(CLMemory<?> theForces) {
		_myKernel.argument(0, theForces);
		_myKernel.argument(1, _myParticles.velocities());
		_myKernel.argument(2, _myParticles.positions());
		_myKernel.argument3f(3, 0, 0, 0);
		_myKernel.argument1f(4, 100);
		_myKernel.argument1f(5, 1);
	}
	
	public void addCommands(CCCLCommandQueue theCommandQueue, float theDeltaTime) {
		_myKernel.argument3f(3, _myX, _myY, _myZ);
		_myKernel.argument1f(4, _myRadius);
		_myKernel.argument1f(5, _myStrength);
		System.out.println(_myRadius+":"+_myStrength);
		theCommandQueue.put1DKernel(_myKernel, _myParticles.size());
	}
}

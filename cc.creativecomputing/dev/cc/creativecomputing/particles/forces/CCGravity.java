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
public class CCGravity extends CCForce{

	@CCControl(name="x", min = -100, max = 100)
	private float _myX = 1F;
	
	@CCControl(name="y", min = -100, max = 100)
	private float _myY = 1F;
	
	@CCControl(name="z", min = -100, max = 100)
	private float _myZ = 1F;
	
	@CCControl(name="strength", min = 0, max = 1)
	private float _myStrength = 1F;
	
	public CCGravity() {
		super("gravity.cl", "gravity");
	}
	
	public void prepareKernel(CLMemory<?> theForces) {
		_myKernel.argument(0, theForces);
		_myKernel.argument3f(1, 0, 0, 0);
		_myKernel.argument1f(2, 1);
	}
	
	public void addCommands(CCCLCommandQueue theCommandQueue, float theDeltaTime) {
		_myKernel.argument3f(1, _myX, _myY, _myZ);
		_myKernel.argument1f(2, _myStrength);
		theCommandQueue.put1DKernel(_myKernel, _myParticles.size());
	}
}

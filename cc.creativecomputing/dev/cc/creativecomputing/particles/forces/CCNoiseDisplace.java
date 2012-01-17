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
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;
import cc.creativecomputing.particles.CCParticles;

import com.jogamp.opencl.CLMemory;

/**
 * @author christianriekoff
 *
 */
public class CCNoiseDisplace extends CCForce{

	@CCControl(name="scale x", min = 0, max = 1)
	private float _myScaleX = 1F;
	
	@CCControl(name="scale y", min = -500, max = 500)
	private float _myScaleY = 1F;
	
	@CCControl(name="scale z", min = 0, max = 1)
	private float _myScaleZ = 1F;
	

	@CCControl(name="offset x", min = -1000, max = 1000)
	private float _myOffsetX = 1F;
	
	@CCControl(name="offset y", min = -1000, max = 1000)
	private float _myOffsetY = 1F;
	
	@CCControl(name="offset z", min = -1000, max = 1000)
	private float _myOffsetZ = 1F;
	
	
	@CCControl(name="strength", min = -1, max = 1)
	private float _myStrength = 1F;
	
	public CCNoiseDisplace() {
		super("noisedisplace.cl", "displace");
	}
	
	@Override
	public void build(CCParticles theParticles, CLMemory<?> theForces) {
		_myParticles = theParticles;
		_myProgram = new CCCLProgram(_myParticles.openCL());
		_myProgram.appendSource(CCOpenCL.class, "util/noiseutil.cl");
		_myProgram.appendSource(CCOpenCL.class, "util/perlin.cl");
		_myProgram.appendSource(getClass(), _myCLFile);
		_myProgram.build();
		
		_myKernel = _myProgram.createKernel(_myKernelName);
		prepareKernel(theForces);
	}
	
	public void prepareKernel(CLMemory<?> theForces) {
		_myKernel.argument(0, theForces);
		_myKernel.argument(1, _myParticles.velocities());
		_myKernel.argument(2, _myParticles.positions());
		
		_myKernel.argument3f(3, 1, 1, 1);
		_myKernel.argument3f(4, 0, 0, 0);
		_myKernel.argument1f(5, 0);
		_myKernel.argument1f(6, 1);
	}
	
	public void addCommands(CCCLCommandQueue theCommandQueue, float theDeltaTime) {
		_myKernel.argument3f(3, _myScaleX * 0.01f, _myScaleY, _myScaleZ * 0.01f);
		_myKernel.argument3f(4, _myOffsetX, _myOffsetY, _myOffsetZ);
		_myKernel.argument1f(5, theDeltaTime);
		_myKernel.argument1f(6, _myStrength);
		theCommandQueue.put1DKernel(_myKernel, _myParticles.size());
	}
}

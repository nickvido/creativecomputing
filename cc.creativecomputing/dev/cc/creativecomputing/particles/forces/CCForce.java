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

import cc.creativecomputing.opencl.CCCLCommandQueue;
import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.particles.CCParticles;

import com.jogamp.opencl.CLMemory;

/**
 * @author christianriekoff
 *
 */
public abstract class CCForce {
	protected CCCLProgram _myProgram;
	protected CCCLKernel _myKernel;
	
	protected CCParticles _myParticles;
	
	protected final String _myCLFile;
	protected final String _myKernelName;
	
	public CCForce(String theCLFile, String theKernelName) {
		_myCLFile = theCLFile;
		_myKernelName = theKernelName;
	}

	public void build(CCParticles theParticles, CLMemory<?> theForceBuffer) {
		_myParticles = theParticles;
		_myProgram = new CCCLProgram(_myParticles.openCL());
		_myProgram.appendSource(getClass(), _myCLFile);
		_myProgram.build();
		
		_myKernel = _myProgram.createKernel(_myKernelName);
		prepareKernel(theForceBuffer);
	}
	
	public abstract void prepareKernel(CLMemory<?> theForceBuffer);
	
	public abstract void addCommands(CCCLCommandQueue theCommandQueue, float theDeltaTime);
	
	public void release() {
		_myKernel.release();
		_myProgram.release();
	}
}

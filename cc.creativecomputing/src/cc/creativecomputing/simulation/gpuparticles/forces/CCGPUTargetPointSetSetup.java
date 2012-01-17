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
package cc.creativecomputing.simulation.gpuparticles.forces;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Form that sets the particle targets to create a texture
 * @author info
 *
 */
public class CCGPUTargetPointSetSetup implements CCGPUTargetSetup{
	
	private List<CCVector3f> _myPointSet = new ArrayList<CCVector3f>();
	
	private float _myScale;
	
	private boolean _myKeepTargets;
	
	private float[] _myTargets;
	private int _myWidth;
	private int _myHeight;
	
	
	public CCGPUTargetPointSetSetup(final float theScale){
		_myScale = theScale;
	}
	
	public void keepTargets(final boolean theIsKeepingTargets){
		_myKeepTargets = theIsKeepingTargets;
	}
	
	public CCVector3f target(final int theParticleID) {
		return new CCVector3f(
			_myTargets[theParticleID * 3],
			_myTargets[theParticleID * 3 + 1],
			_myTargets[theParticleID * 3 + 2]
		);
	}
	
	public List<CCVector3f> points(){
		return _myPointSet;
	}

	public void setParticleTargets(final CCGraphics g, final int theWidth, final int theHeight) {
		if(_myKeepTargets){
			_myTargets = new float[theWidth * theHeight * 3];
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		
		if(_myPointSet.size() == 0)return;
		
		for(int x = 0; x < theWidth;x++) {
			for(int y = 0; y < theHeight;y++) {
				
				CCVector3f myPoint = _myPointSet.get((int)CCMath.random(_myPointSet.size()));
				int id = y * theWidth + x;
				
				if(_myKeepTargets) {
					_myTargets[id * 3 + 0] = myPoint.x;
					_myTargets[id * 3 + 1] = myPoint.y;
					_myTargets[id * 3 + 2] = myPoint.z;
				}
				
				g.textureCoords(0, myPoint);
				g.vertex(x, y);
			}
		}
	}
}


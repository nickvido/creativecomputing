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

import cc.creativecomputing.graphics.CCGraphics;

/**
 * @author info
 *
 */
public class CCGPUTargetQuadSetup implements CCGPUTargetSetup{
	
	private float _myXSpace;
	private float _myYSpace;
	
	public CCGPUTargetQuadSetup(final float theSpace) {
		this(theSpace, theSpace);
	}
	
	public CCGPUTargetQuadSetup(final float theXspace, final float theYspace) {
		_myXSpace = theXspace;
		_myYSpace = theYspace;
	}
	
	public void setParticleTargets(final CCGraphics g, final int theWidth, final int theHeight) {
		
		float myXtranspose = -_myXSpace * theWidth / 2;
		float myYtranspose = +_myYSpace * theHeight / 2;
		
		for(int x = 0; x < theWidth;x++) {
			for(int y = 0; y < theHeight;y++) {
				float xPos =  + x * _myXSpace + myXtranspose;
				float yPos =  - y * _myYSpace + myYtranspose;
				float zPos = 0;
					
				g.textureCoords(0, xPos , yPos, zPos);
				g.vertex(x, y);
			}
		}
	}
}

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
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.CCArrayUtil;

/**
 * Form that sets the particle targets to create a texture
 * @author info
 *
 */
public class CCGPUTargetMaskSetup implements CCGPUTargetSetup{
	
	public static class CCGPUTargetMaskSetupArea{
		private int _myX;
		private int _myY;
		private int _myWidth;
		private int _myHeight;
		
		public CCGPUTargetMaskSetupArea(final int theX, final int theY, final int theWidth, final int theHeight) {
			_myX = theX;
			_myY = theY;
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		
		private int xStart() {
			return _myX;
		}
		
		private int xEnd() {
			return _myX + _myWidth;
		}
		
		private int yStart() {
			return _myY;
		}
		
		private int yEnd() {
			return _myY + _myHeight;
		}
	}
	
	public static enum CCGPUTargetMaskSetupPlane{
		XY, XZ;
	}
	
	private static class CCGPUPixelInfo{
		private float x;
		private float y;
		private float gray;
		
		private int[] _myIndices = new int[10];
		private int _myIndex;
		
		private CCGPUPixelInfo(final float theX, final float theY, final float theGray) {
			x = theX;
			y = theY;
			gray = theGray;
		}
		
		private void addIndex(final int theIndex) {
			if(_myIndex >= _myIndices.length) {
				_myIndices = CCArrayUtil.expand(_myIndices,_myIndex+10);
			}
			_myIndices[_myIndex] = theIndex;
			_myIndex++;
		}
	}
	
	private List<CCGPUPixelInfo> _myPixels = new ArrayList<CCGPUPixelInfo>();
	private CCGPUPixelInfo[][] _myPixelGrid;
	
	private float _myScale;
	
	private boolean _myKeepTargets;
	
	private float[] _myTargets;
	private int _myWidth;
	private int _myHeight;
	
	private CCGPUTargetMaskSetupPlane _myPlane;
	
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, final float theScale, final CCGPUTargetMaskSetupPlane thePlane){
		_myScale = theScale;
		_myPlane = thePlane;
		
		_myPixelGrid = new CCGPUPixelInfo[theTextureData.width()][theTextureData.height()];
		
		for(int x = 0; x < theTextureData.width();x++) {
			for(int y = 0; y < theTextureData.height();y++) {
				float myGray = theTextureData.getPixel(x, y).red();
				if(myGray > 0) {
					CCGPUPixelInfo myPixelInfo = new CCGPUPixelInfo(x - theTextureData.width()/2,y - theTextureData.height()/2,myGray);
					_myPixels.add(myPixelInfo);
					_myPixelGrid[x][y] = myPixelInfo;
				}
			}
		}
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, final float theScale) {
		this(theTextureData, theScale, CCGPUTargetMaskSetupPlane.XY);
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, List<CCGPUTargetMaskSetupArea> theAreas, final float theScale, final CCGPUTargetMaskSetupPlane thePlane){
		_myScale = theScale;
		_myPlane = thePlane;
		
		_myPixelGrid = new CCGPUPixelInfo[theTextureData.width()][theTextureData.height()];
		
		for(CCGPUTargetMaskSetupArea myArea:theAreas) {
			for(int x = myArea.xStart(); x < myArea.xEnd();x++) {
				for(int y = myArea.yStart(); y < myArea.yEnd();y++) {
					float myGray = theTextureData.getPixel(x, y).red();
					if(myGray > 0) {
						CCGPUPixelInfo myPixelInfo = new CCGPUPixelInfo(x - theTextureData.width()/2,y - theTextureData.height()/2,myGray);
						_myPixels.add(myPixelInfo);
						_myPixelGrid[x][y] = myPixelInfo;
					}
				}
			}
		}
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTexture, List<CCGPUTargetMaskSetupArea> theAreas, final float theScale) {
		this(theTexture, theAreas, theScale, CCGPUTargetMaskSetupPlane.XY);
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

	public void setParticleTargets(final CCGraphics g, final int theWidth, final int theHeight) {
		if(_myKeepTargets){
			_myTargets = new float[theWidth * theHeight * 3];
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		
		if(_myPixels.size() == 0)return;
		
		for(int x = 0; x < theWidth;x++) {
			for(int y = 0; y < theHeight;y++) {
				float xPos, yPos, zPos;
				
				CCGPUPixelInfo myPixel = null;
				do{
					myPixel = _myPixels.get((int)CCMath.random(_myPixels.size()));
				}while(myPixel.gray < CCMath.random(1));
				
				CCVector2f myRandom2f = CCVecMath.random2f(CCMath.random(_myScale/2));
				
				if(_myPlane == CCGPUTargetMaskSetupPlane.XY){
					xPos = myPixel.x * _myScale + myRandom2f.x;
					yPos = -myPixel.y * _myScale + myRandom2f.y;
					zPos = 0;
				}else{
					xPos = myPixel.x * _myScale + myRandom2f.x; 
					yPos =  0;
					zPos = myPixel.y * _myScale + myRandom2f.y;
				}
				
				int id = y * theWidth + x;
				
				if(_myKeepTargets) {
					_myTargets[id * 3] = xPos;
					_myTargets[id * 3 + 1] = yPos;
					_myTargets[id * 3 + 2] = zPos;
				}
				
				myPixel.addIndex(id);
				
				g.textureCoords(0, xPos, yPos, zPos);
				g.vertex(x, y);
			}
		}
	}
	
	public List<Integer> indicesForArea(final int theXStart, final int theYStart, final int theXend, final int theYend){
		List<Integer> myResultList = new ArrayList<Integer>();
		
		for(int x = theXStart; x < theXend;x++) {
			for(int y = theYStart; y < theYend; y++) {
				if(_myPixelGrid[x][y] != null) {
					for(int value:_myPixelGrid[x][y]._myIndices ) {
						myResultList.add(value);
					}
				}
			}
		}
		
		return myResultList;
	}
}


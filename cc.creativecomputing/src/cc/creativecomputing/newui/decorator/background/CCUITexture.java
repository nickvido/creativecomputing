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
package cc.creativecomputing.newui.decorator.background;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.CCUISpacing;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name = "uitexture")
public class CCUITexture {
	
	@CCPropertyObject(name="texture_splice")
	public static class CCUITextureSplice{
		@CCProperty(name="texture_start", node=false)
		private float _myStart;
		@CCProperty(name="texture_end", node=false)
		private float _myEnd;
		
		@CCProperty(name="stretch", node=false)
		private boolean _myIsStretch;
		
		private CCUITextureSplice() {
			
		}
	}
	
	@CCProperty(name = "spacing", optional = true)
	private CCUISpacing _mySpacing = new CCUISpacing();

	@CCProperty(name="file")
	private String _myTextureFile;
	
	private CCTexture2D _myTexture;
	
	@CCProperty(name="vertical_splices", optional = true)
	private CCUITextureSplice[] _myVerticalSplices;
	
	@CCProperty(name="horizontal_splices", optional = true)
	private CCUITextureSplice[] _myHorizontalSplices;
	
	private float _myLeftOver = 0;
	
	private float[][] _myXpositions;
	private float[][] _myYpositions;
	
	public CCUITexture() {
		
	}
	
	public void setup(CCUI theUI) {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData(_myTextureFile));
	}
	
	private float[][] splicesToPositions(CCUITextureSplice[] theSplices, float theSize, float theTextureSize, float theStart) {
		
		
		float[][] myPositions;
		
		if(theSplices == null) {
			myPositions = new float[2][2];
			myPositions[0][0] = 0;
			myPositions[0][1] = 1;
			myPositions[1][0] = theStart;
			myPositions[1][1] = theStart + theSize;
			return myPositions;
		}else {
			myPositions = new float[2][theSplices.length + 1];
		}
		
		float myFixedSize = 0;
		int myNumberOfVariableSplices = 0;
		
		for(CCUITextureSplice mySplice : theSplices) {
			if(!mySplice._myIsStretch)myFixedSize += mySplice._myEnd - mySplice._myStart;
			else myNumberOfVariableSplices++;
		}
		
		float myVariableSize = (theSize - myFixedSize) / myNumberOfVariableSplices;
		
		int i = 1;
		float myPosition = 0;
		myPositions[0][0] = theSplices[0]._myStart / theTextureSize;
		myPositions[1][0] = theStart;
		
		for(CCUITextureSplice mySplice : theSplices) {
			if(mySplice._myIsStretch) {
				myPosition += myVariableSize;
			}else {
				myPosition += mySplice._myEnd - mySplice._myStart;
			}
			myPositions[0][i] = mySplice._myEnd / theTextureSize;
			myPositions[1][i] = theStart + myPosition;
			i++;
		}
		return myPositions;
	}
	
	public void dimension(float theWidth, float theHeight) {
		theWidth += _mySpacing.left();
		theWidth += _mySpacing.right();
		_myXpositions = splicesToPositions(_myHorizontalSplices, theWidth, _myTexture.width(), -_mySpacing.left());
		
		theHeight += _mySpacing.top();
		theHeight += _mySpacing.bottom();
		_myYpositions = splicesToPositions(_myVerticalSplices, theHeight, _myTexture.height(), -_mySpacing.top());
	}
	
	public void draw(CCGraphics g) {

		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		
		for(int y = 0; y < _myYpositions[0].length - 1; y++) {
			for(int x = 0; x < _myXpositions[0].length - 1; x++) {
				float myTexX1 = _myXpositions[0][x];
				float myTexX2 = _myXpositions[0][x + 1];

				float myTexY1 = _myYpositions[0][y];
				float myTexY2 = _myYpositions[0][y + 1];
				
				float myX1 = _myXpositions[1][x];
				float myX2 = _myXpositions[1][x + 1];

				float myY1 = _myYpositions[1][y];
				float myY2 = _myYpositions[1][y + 1];
				
				g.textureCoords(myTexX1, myTexY1);
				g.vertex(myX1, myY1);

				g.textureCoords(myTexX2, myTexY1);
				g.vertex(myX2, myY1);

				g.textureCoords(myTexX2, myTexY2);
				g.vertex(myX2, myY2);

				g.textureCoords(myTexX1, myTexY2);
				g.vertex(myX1, myY2);
			}
		}
		
		g.endShape();
		g.noTexture();
	}
}

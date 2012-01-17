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
package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphicsCore.CCShapeMode;
import cc.creativecomputing.math.CCAABoundingRectangle;

/**
 * @author info
 *
 */
public abstract class CCAbstractShape implements CCDrawListener{
	
	protected float _myScale = 1;
	protected CCColor _myColor;
	
	protected CCShapeMode _myShapeMode;
	protected CCAABoundingRectangle _myBoundingRectangle;
	
	protected boolean _myIsVisible = true;
	protected boolean _myHasTexture = false;
	
	protected CCAbstractShape(){
		_myShapeMode = CCShapeMode.CORNER;
		_myBoundingRectangle = new CCAABoundingRectangle();
	}
	
	public void draw(CCGraphics g) {
	}

	/**
	 * Moves the shape by the given amount
	 * @param theX
	 * @param theY
	 */
	public void translate(float theX, float theY) {
	}
	
	public abstract void position(final float theX, final float theY);
	
	public void scale(final float theScale){
		_myScale = theScale;
	}
	
	public void shapeMode(final CCShapeMode theShapeMode){
		_myShapeMode = theShapeMode;
	}
	
	/**
	 * Return the bounding rectangle surrounding the shape. The
	 * rectangle is axis aligned. 
	 * @return the rectangle surrounding the shape
	 */
	public CCAABoundingRectangle boundingRect(){
		return _myBoundingRectangle;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public void isVisible(final boolean theIsVisible){
		_myIsVisible = theIsVisible;
	}
	
	public void hasTexture(final boolean theHasTexture){
		_myHasTexture = theHasTexture;
	}
	
	public boolean hasTexture(){
		return _myHasTexture;
	}
}

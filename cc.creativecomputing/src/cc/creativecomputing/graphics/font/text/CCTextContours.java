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
package cc.creativecomputing.graphics.font.text;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCOutlineChar;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author info
 * 
 */
public class CCTextContours extends CCAbstractText<CCOutlineFont>{

	/**
	 * save the text to display
	 */
	protected String _myText;
	
	/**
	 * rectangle to save the bounding box around the text
	 */
	private CCAABoundingRectangle _myBoundingRectangle;

	protected char[] _myTextBuffer;

	/**
	 * store if the text has been changed and the width needs to be recalculated
	 */
	private boolean _myChangedTextWidth = true;

	/**
	 * store the current text width
	 */
	private float _myTextWidth = 0;

	/**
	 * store if the text has been changed and the height needs to be recalculated
	 */
	protected boolean _myChangedTextHeight = true;
	
	/**
	 * store if the text has been changed and the bounding box needs to be recalculated
	 */
	private boolean _myChangedBoundingBox = true;

	/**
	 * store the current text height
	 */
	protected float _myTextHeight = 0;

	protected CCVector3f _myPosition = new CCVector3f();
	
	private List<List<CCVector2f>> _myContours = new ArrayList<List<CCVector2f>>();

	/**
	 * Create a new Text object with the given Font
	 * 
	 * @param theFont
	 */
	public CCTextContours(final CCOutlineFont theFont) {
		super(theFont);
		text("");
		_myBoundingRectangle = new CCAABoundingRectangle();
	}
	
	private void createContours( 
		final int theStart, final int theStop, 
		float theX, final float theY, final float theZ
	) {
		if (_myFont == null)
			throw new RuntimeException("Set a font using textFont befor writing text.");
		
		switch (_myTextAlign) {
		case CENTER:
			theX -= _myFont.width(_myTextBuffer, theStart, theStop) / 2f * _myTextSize;
			break;
		case RIGHT:
			theX -= _myFont.width(_myTextBuffer, theStart, theStop) * _myTextSize;
			break;
		}

		for (int index = theStart; index < theStop; index++) {
			final char myChar = _myTextBuffer[index];
			switch (myChar) {
			case ' ':
				theX += _myFont.spaceWidth() * _myTextSize * _mySpacing;
				break;
			default:
				final int myIndex = _myFont.index(myChar);
				CCOutlineChar myOutlineChar = _myFont.chars()[myIndex];
				
				final float myWidth = myOutlineChar.width() * _myTextSize;
				
				final float myScale = _myTextSize / _myFont.size();
				
				for(List<CCVector2f> myPath:myOutlineChar.contour()){
					List<CCVector2f> myContour = new ArrayList<CCVector2f>();
					for(CCVector2f myVertex:myPath){
						myContour.add(new CCVector2f(theX + myVertex.x * myScale, theY - myVertex.y * myScale));
					}
					_myContours.add(myContour);
				}
				
				theX += myWidth * _mySpacing;
			}
		}
	}

	/**
	 * Set the text to display
	 * 
	 * @param theText
	 */
	public void text(final String theText) {
		_myChangedTextWidth = true;
		_myChangedTextHeight = true;
		_myChangedBoundingBox = true;
		
		_myText = theText;
		_myTextBuffer = theText.toCharArray();
		
		_myContours.clear();
		
		int myStart = 0;
		int myIndex = 0;

		float myX = _myPosition.x;
		float myY = _myPosition.y + ascent() ;
		float myZ = _myPosition.z();

		while (myIndex < _myTextBuffer.length) {
			if (_myTextBuffer[myIndex] == '\n') {
				createContours(myStart, myIndex, myX, myY, myZ);
				myStart = myIndex + 1;
				myY -= _myLeading;
			}
			myIndex++;
		}
		
		
		if (myStart < _myTextBuffer.length) {
			createContours(myStart, myIndex, myX, myY, myZ);
		}
	}
	
	public void text(final int theText) {
		text(Integer.toString(theText));
	}
	
	public void text(final char theChar) {
		text(Character.toString(theChar));
	}
	
	public void text(final float theText) {
		text(Float.toString(theText));
	}

	/**
	 * Returns the current text.
	 * @return the current text.
	 */
	public String text() {
		return _myText;
	}

	/**
	 * Return the width of a line of text. If the text has multiple lines, this returns the length of the longest line.
	 * Note this is only recalculated once you have changed the text.
	 * 
	 * @param theString
	 * @return
	 */
	public float width() {
		if (!_myChangedTextWidth)
			return _myTextWidth * _myTextSize;

		float myWidth = 0;
		int myIndex = 0;
		int myStart = 0;

		while (myIndex < _myTextBuffer.length) {
			if (_myTextBuffer[myIndex] == '\n') {
				myWidth = Math.max(myWidth, _myFont.width(_myTextBuffer, myStart, myIndex));
				myStart = myIndex + 1;
			}
			myIndex++;
		}
		if (myStart < _myTextBuffer.length) {
			_myTextWidth = Math.max(myWidth, _myFont.width(_myTextBuffer, myStart, myIndex));
		}
		return _myTextWidth * _myTextSize;
	}
	
	public float height() {
		if (!_myChangedTextHeight)
			return _myTextHeight;
		
		_myTextHeight = _myFont.normedAscent() * _myTextSize;
		
		int myIndex = 0;
		
		while (myIndex < _myTextBuffer.length) {
			if (_myTextBuffer[myIndex] == '\n') {
//				_myTextHeight += _myLeading;
			}
			myIndex++;
		}
		
		return _myTextHeight;
	}
	
	/**
	 * Returns the bounding rectangle around the text.
	 * @return the bounding rectangle around the text.
	 */
	public CCAABoundingRectangle boundingBox(){
		if(_myChangedBoundingBox){
			_myBoundingRectangle.min().x = _myPosition.x;
			_myBoundingRectangle.min().y = _myPosition.y-_myFont.normedDescent() * _myTextSize;
			_myBoundingRectangle.width(width());
			_myBoundingRectangle.height(height());
		}
		return _myBoundingRectangle;
	}
	
	/**
	 * Sets the position of the text to the given coordinates.
	 * @param theX new x position for the text
	 * @param theY new y position for the text
	 */
	public void position(final float theX, final float theY) {
		_myPosition = new CCVector3f(theX, theY);
	}
	
	/**
	 * Sets the position of the text to the given vector.
	 * @param thePosition the new position of the text
	 */
	public void position(final CCVector2f thePosition){
		_myPosition.set(thePosition);
	}
	
	/**
	 * Sets the 3D position of the text to the given vector.
	 * @param thePosition the new position of the text
	 */
	public void position(final CCVector3f thePosition){
		_myPosition.set(thePosition);
	}

	/**
	 * Returns a reference to the position of the text.
	 * @return reference to the position of the text
	 */
	public CCVector3f position() {
		return _myPosition;
	}

	protected void drawTextLine(
		final CCGraphics g, 
		final int theStart, final int theStop, 
		float theX, final float theY, final float theZ
	) {
		if (_myFont == null)
			throw new RuntimeException("Set a font using textFont befor writing text.");
		
		switch (_myTextAlign) {
		case CENTER:
			theX -= _myFont.width(_myTextBuffer, theStart, theStop) / 2f * _myTextSize;
			break;
		case RIGHT:
			theX -= _myFont.width(_myTextBuffer, theStart, theStop) * _myTextSize;
			break;
		}

		int myLastIndex = -1;
		
		for (int index = theStart; index < theStop; index++) {
			final char myChar = _myTextBuffer[index];
			final int myIndex = _myFont.index(myChar);
				
			theX += _myFont.drawChar(g, myIndex, _myTextSize, theX, theY, theZ) * _mySpacing;
			theX += _myFont.kerning(myLastIndex, myIndex) * _myTextSize;
			
			myLastIndex = myIndex;
		}
	}

	public void draw(CCGraphics g) {
		for(List<CCVector2f> myContour:_myContours) {
			g.beginShape(CCDrawMode.LINE_LOOP);
			for(CCVector2f myPoint:myContour) {
				g.vertex(myPoint);
			}
			g.endShape();
			
		}
	}

	@Override
	public CCTextContours clone() {
		final CCTextContours _myResult = new CCTextContours(_myFont);
		_myResult._myPosition = _myPosition.clone();
		_myResult.text(_myText);
		_myResult.align(_myTextAlign);
		return _myResult;
	}
	
	public List<List<CCVector2f>> contours(){
		return _myContours;
	}
}

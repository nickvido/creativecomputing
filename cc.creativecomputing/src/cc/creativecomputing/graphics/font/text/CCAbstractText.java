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

import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.graphics.font.CCFont;

/**
 * @author info
 * 
 */
public abstract class CCAbstractText<FontType extends CCFont> implements CCDrawListener{

	protected FontType _myFont;

	/**
	 * The current text align
	 **/
	protected CCTextAlign _myTextAlign = CCTextAlign.LEFT;

	/**
	 * The current text size
	 **/
	protected float _myTextSize;

	/**
	 * The current text leading
	 **/
	protected float _myLeading;
	
	/**
	 * The current text spacing
	 */
	protected float _mySpacing = 1;


	/**
	 * Create a new Text object with the given Font
	 * 
	 * @param theFont
	 */
	public CCAbstractText(final FontType theFont) {
		_myFont = theFont;
		size(_myFont.size());
	}

	
	public void align(final CCTextAlign theTextAlign) {
		_myTextAlign = theTextAlign;
	}

	public CCTextAlign textAlign() {
		return _myTextAlign;
	}

	/**
	 * Returns the ascent of the current font at the current size.
	 * 
	 * @return the ascent of the current font at the current size
	 */
	public float ascent() {
		return _myFont.normedAscent() * _myTextSize;
	}

	public float descent() {
		return _myFont.normedDescent() * _myTextSize;
	}

	/**
	 * Sets the current font. The font's size will be the "natural" size of this font 
	 * The leading will also be reset.
	 * @param theFont
	 */
	public void font(final FontType theFont) {
		_myFont = theFont;
		if (_myFont != null)
			size(theFont.size());
	}

	/**
	 * Useful function to set the font and size at the same time.
	 * @param theFont the new font for the text
	 * @param theSize the new font size for the text
	 */
	public void font(final FontType theFont, final float theSize) {
		font(theFont);
		if (theFont != null)
			size(theSize);
	}

	/**
	 * Returns the font currently in use by the text
	 * 
	 * @return
	 */
	public FontType font() {
		return _myFont;
	}

	/**
	 * Sets the leading of the text. 
	 * In typography, leading refers to the amount of added vertical spacing between lines of type. 
	 * This concept is also often referred to as "line spacing".
	 * @param theTextLeading
	 */
	public void leading(final float theTextLeading) {
		_myLeading = theTextLeading;
	}
	
	/**
	 * Returns the used text leading.
	 * @return the used text leading
	 */
	public float leading() {
		return _myLeading;
	}
	
	/**
	 * Sets the letter spacing of the text. In typography, letter-spacing, also called 
	 * tracking, refers to the amount of space between a group of letters to affect 
	 * density in a line or block of text. A spacing of one will have no effect, while 
	 * a spacing of 2 will double the space between two letters.
	 * @param theSpacing letter spacing to use
	 */
	public void spacing(final float theSpacing) {
		_mySpacing = theSpacing;
	}
	
	/**
	 * Returns the used letter spacing
	 * @return the used letter spacing
	 */
	public float spacing() {
		return _mySpacing;
	}

	/**
	 * Sets the font size of the text. Also sets the text leading dependent on the font size. 
	 * @param theSize the font size of the text
	 */
	public void size(final float theSize) {
		_myTextSize = theSize;
		_myLeading = (ascent() + descent()) * 1.275f;
	}

	/**
	 * Returns the font size of the text.
	 * @return the font size of the text
	 */
	public float size() {
		return _myTextSize;
	}

}

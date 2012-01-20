package cc.creativecomputing.graphics.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;


import cc.creativecomputing.graphics.CCGraphics;

/**
 * This class provides the basic font features. It contains the font metrics as well as the charset
 * information. Overwrite this class to create you own font class, which can be handled by the text
 * methods of {@link CCGraphics}. To create your own font you have to at least overwrite the 
 * {@link #createChars(BufferedImage, Graphics2D)} method to build the chars for drawing and the
 * {@link #drawChar(CCGraphics, int, float, float, float, float)} to define how a char is drawn.
 * If needed you can overwrite the {@link CCFont#beginText(CCGraphics)} and @link {@link #endText(CCGraphics)}
 * text method to make calls that are needed when the drawing of text begins or ends.
 * 
 * @author Christian Riekoff
 *
 */
public abstract class CCFont<CharType extends CCChar>{
	protected static final Color WHITE = Color.WHITE;
	protected static final Color TRANSPARENT = new Color(0F,0F,0F,0F);
	
	protected Font _myFont;
	protected FontMetrics _myFontMetrics;
	
	protected CCKerningTable _myKerningTable;
	
	protected int _mySize;
	
//	protected CCGraphics g;
	
	protected int _myAsciiLookUpTable[]; // quick lookup for the ascii chars
	
	protected int _myCharCount;
	protected CCCharSet _myCharSet;
	protected int _myCharCodes[];
	
	//Arrays for saving the metrics
	protected CharType[] _myChars;
	
	protected float _mySpaceWidth;
	protected int _myHeight;
	
	protected int _myAscent;
	protected int _myDescent;
	protected float _myNormalizedAscent;
	protected float _myNormalizedDescent;
	protected float _myNormalizedHeight;
	
	protected int _myLeading;
	
	protected boolean _myIsAntialiase;
	
	
	protected CCFont(final CCCharSet theCharSet){
		_myFont = null;
		_myAsciiLookUpTable = new int[128];
		Arrays.fill(_myAsciiLookUpTable, -1);
		
		if(theCharSet == null){
			_myCharSet = CCCharSet.REDUCED_CHARSET;
		}else{
			_myCharSet = theCharSet;
		}
		_myCharCount = _myCharSet.size();
		
		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myFontMetrics = null;
		_mySize = 0;
		_myNormalizedHeight = 0;
		_myIsAntialiase = true;
	}
	
	protected CCFont(final Font theFont, final CCKerningTable theKerningTable, final CCCharSet theCharSet, final boolean theIsAntialias){
		setFont(theFont, theCharSet, theIsAntialias);
		_myKerningTable = theKerningTable;
	}
	
	protected void setFont(final Font theFont, final CCCharSet theCharSet, final boolean theIsAntialias) {
		_myFont = theFont;
		_mySize = theFont.getSize();
		
		_myAsciiLookUpTable = new int[128];
		Arrays.fill(_myAsciiLookUpTable, -1);
		
		if(theCharSet == null){
			_myCharSet = CCCharSet.REDUCED_CHARSET;
		}else{
			_myCharSet = theCharSet;
		}
		_myCharCount = _myCharSet.size();

		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myIsAntialiase = theIsAntialias;

		final int myImageSize = _mySize * 3;
		
		final Object myAntialias = _myIsAntialiase ? 
		RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
			
		final BufferedImage myCharImage = new BufferedImage(myImageSize, myImageSize, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D myGraphics = (Graphics2D) myCharImage.getGraphics();
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, myAntialias);
//			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		myGraphics.setFont(_myFont);
		myGraphics.setBackground(TRANSPARENT);
			
		_myFontMetrics = myGraphics.getFontMetrics();
		_mySpaceWidth = (float)_myFontMetrics.charWidth(' ') / _mySize;
		_myHeight = _myFontMetrics.getHeight();
		_myNormalizedHeight = (float)_myHeight/ _mySize;
		
		_myAscent = _myFontMetrics.getAscent();
		_myDescent = _myFontMetrics.getDescent();
		
		_myLeading = _myFontMetrics.getLeading();
		
		_myNormalizedAscent = (float)_myAscent / _mySize;
		_myNormalizedDescent = (float)_myDescent / _mySize;
		
		createChars(myCharImage,myGraphics);
	}
	
	public float kerning(char theChar1, char theChar2) {
		return kerning(index(theChar1), index(theChar2));
	}
	
	public float kerning(int theIndex1, int theIndex2) {
		if(theIndex1 < 0 || theIndex2 < 0)return 0;
		if(_myKerningTable == null)return 0;
		return _myKerningTable.kerning(_myChars[theIndex1].glyphCode(), _myChars[theIndex2].glyphCode());
	}
	
	protected abstract CharType[] createCharArray(final int theSize);
	
	protected float charWidth(final char theChar) {
		return (float)_myFontMetrics.charWidth(theChar) / _mySize;
	}
	
	protected abstract void createChars(final BufferedImage theCharImage, final Graphics2D theGraphics);
	
	public CharType[] chars() {
		return _myChars;
	}
	
	/**
	 * Called by the graphics object when rendering text, th
	 * @param g
	 */
	public void beginText(CCGraphics g){};
	
	public float drawChar(CCGraphics g, int theIndex, float theSize, float theX, float theY, float theZ) {
		if(theIndex < 0 || theIndex > _myChars.length || _myChars[theIndex] == null)return 0;
		return _myChars[theIndex].draw(g, theX, theY, theZ, theSize);
	}
	
	public void endText(CCGraphics g){};
	
	/**
	 * Get index for the char (convert from unicode to bagel charset).
	 * @return index into arrays or -1 if not found
	 */
	public int index(final char theChar){
		// degenerate case, but the find function will have trouble
		// if there are somehow zero chars in the lookup
		if (_myCharCodes.length == 0)
			return -1;
		
		// quicker lookup for the ascii fellers
		if (theChar < 128)
			return _myAsciiLookUpTable[theChar];

		// some other unicode char, hunt it out
		return index_hunt(theChar, 0, _myCharCount - 1);
	}

	protected int index_hunt(int c, int start, int stop){
		int pivot = (start + stop) / 2;

		// if this is the char, then return it
		if (c == _myCharCodes[pivot])
			return pivot;

		// char doesn't exist, otherwise would have been the pivot
		//if (start == stop) return -1;
		if (start >= stop)
			return -1;

		// if it's in the lower half, continue searching that
		if (c < _myCharCodes[pivot])
			return index_hunt(c, start, pivot - 1);

		// if it's in the upper half, continue there
		return index_hunt(c, pivot + 1, stop);
	}
	
	/**
	 * Returns the ascent of this font from the baseline.
	 * The value is based on a font of size 1.
	 */
	public float normedAscent(){
		return _myNormalizedAscent;
	}
	
	public int ascent() {
		return _myAscent;
	}

	/**
	 * Returns how far this font descends from the baseline.
	 * The value is based on a font size of 1.
	 */
	public float normedDescent(){
		return _myNormalizedDescent;
	}
	
	public int descent() {
		return _myDescent;
	}
	
	public float width(final int theIndex){
		if(theIndex < 0 || theIndex > _myChars.length || _myChars[theIndex] == null)return 0;
		return _myChars[theIndex].width();
	}

	/**
	 * Width of this character for a font of size 1.
	 */
	public float width(final char theChar){
		return width(index(theChar));
	}
	
	public float width(final char[] theTextBuffer, final int theStart, final int theStop){
		float myResult = 0;
		for(char myChar:theTextBuffer) {
			myResult += width(myChar);
		}
		return myResult;
	}
	
	public float width(final String theText){
		return width(theText.toCharArray(), 0, theText.length());
	}
	
	public float height(){
		return _myNormalizedHeight;
	}
	
	public float spaceWidth(){
		return _mySpaceWidth;
	}
	
	public int leading() {
		return _myLeading;
	}
	
	public void leading(int theLeading) {
		_myLeading = theLeading;
	}
	
	public int size(){
		return _mySize;
	}
	
	public boolean canDisplay(final char theChar) {
		return _myFont.canDisplay(theChar) && _myFontMetrics.charWidth(theChar) > 0;
	}
}
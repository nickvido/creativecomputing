package cc.creativecomputing.graphics.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;


import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.font.CCGlutFont.CCGlutFontType;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.font.util.CCTextRenderer;
import cc.creativecomputing.graphics.font.util.CCTextTexture;
import cc.creativecomputing.io.CCIOUtil;


/**
 * Use CCFontIO to create Fonts and text objects. You can create different
 * kinds of fonts and text objects. 
 * @author texone
 *
 */
public class CCFontIO{
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
	
	/**
	 * Get a list of the fonts installed on the system that can be used
	 * by Java. Not all fonts can be used in Java, in fact it's mostly
	 * only TrueType fonts. OpenType fonts with CFF data such as Adobe's
	 * OpenType fonts seem to have trouble (even though they're sort of
	 * TrueType fonts as well, or may have a .ttf extension). Regular
	 * PostScript fonts seem to work O.K. though.
	 */
	static public String[] list(){

		// getFontList is deprecated in 1.4, so this has to be used
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font fonts[] = ge.getAllFonts();
			String list[] = new String[fonts.length];
			for (int i = 0; i < list.length; i++){
				list[i] = fonts[i].getName();
			}
			return list;

		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error inside PFont.list()");
		}
	}
	
	static public void printFontList(){
		for(String myFontName:list()){
			System.out.println(myFontName);
		}
	}
	
	private static class CCFontData{
		Font _myJavaFont;
		CCKerningTable _myKerning;
	}
	
	private static CCFontData createFont(final String theName, final float theSize){
		CCFontData myFontData = new CCFontData();
		final String lowerName = theName.toLowerCase();
		try{
			if (lowerName.endsWith(".otf") || lowerName.endsWith(".ttf")){
				myFontData._myJavaFont = Font.createFont(Font.TRUETYPE_FONT, CCIOUtil.openStream(theName)).deriveFont(theSize);
				myFontData._myKerning = new CCKerningTable(CCIOUtil.openStream(theName));
			}else{
				myFontData._myJavaFont = new Font(theName, Font.PLAIN, 1).deriveFont(theSize);
			}
			
			return myFontData;
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + theName);
		}
	}
	
	
	/////////////////////////////////////////////
	//
	// VECTOR FONT HANDLING
	//
	/////////////////////////////////////////////
	
	/**
	 * Creates a vector font.
	 * @param theName
	 * @param theSize
	 * @param theIsSmooth
	 * @param theCharset
	 * @return
	 */
	static public CCVectorFont createVectorFont(final String theName, final float theSize, final boolean theIsSmooth, final CCCharSet theCharset){
		CCFontData font = createFont(theName, theSize);
		return new CCVectorFont(font._myJavaFont, font._myKerning, theCharset, theIsSmooth);
	}

	static public CCVectorFont createVectorFont(final String theName,final float theSize, final boolean theIsSmooth){
		return createVectorFont(theName,theSize, theIsSmooth, CCCharSet.REDUCED_CHARSET);
	}

	static public CCVectorFont createVectorFont(final String theName,final float theSize){
		return createVectorFont(theName,theSize, true, CCCharSet.REDUCED_CHARSET);
	}

	static public CCVectorFont createVectorFont(final String theName,final float theSize, final CCCharSet theCharset){
		return createVectorFont(theName,theSize, true, theCharset);
	}
	
	static public CC3DFont create3DFont(final String theName, final float theSize, final boolean theIsSmooth, final CCCharSet theCharset, final float theDepth){
		CCFontData font = createFont(theName, theSize);
		return new CC3DFont(font._myJavaFont, font._myKerning, theCharset, theIsSmooth, theDepth);
	}

	static public CC3DFont create3DFont(final String theName,final float theSize, final boolean theIsSmooth, final float theDepth){
		return create3DFont(theName,theSize, theIsSmooth, CCCharSet.REDUCED_CHARSET, theDepth);
	}

	static public CC3DFont create3DFont(final String theName,final float theSize, final float theDepth){
		return create3DFont(theName,theSize, true, CCCharSet.REDUCED_CHARSET, theDepth);
	}
	
	private static final HashMap<String, CCOutlineFont> _myOutlineFontMap = new HashMap<String, CCOutlineFont>();
	
	static public CCMesh createVectorText(String theText,String name, float theSize){
		return createVectorText(theText,name, theSize, 20);
	}
	
	static public CCMesh createVectorText(String theText,String name, float theSize, int theDetail){
		String lowerName = name.toLowerCase();
		CCOutlineFont baseFont = null;

		try{
			if(_myOutlineFontMap.containsKey(lowerName)){
				baseFont = _myOutlineFontMap.get(lowerName);
			}else{
				baseFont = createOutlineFont(name, theSize,theDetail);
				_myOutlineFontMap.put(lowerName, baseFont);
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + name);
		}
		
		
		return baseFont.createMesh(theText, CCTextAlign.CENTER, theSize, 0, -theSize/2, 0);
	}
	
	/////////////////////////////////////////////
	//
	// OUTLINE FONT HANDLING
	//
	/////////////////////////////////////////////
	
	/**
	 * Creates an outline font.
	 * @param theName
	 * @param theSize
	 * @param theIsSmooth
	 * @param theCharset
	 * @param theDetail
	 * @return
	 */
	static public CCOutlineFont createOutlineFont(final String theName, final float theSize, final boolean theIsSmooth, final CCCharSet theCharset, final int theDetail){
		CCFontData font = createFont(theName, theSize);
		return new CCOutlineFont(font._myJavaFont, font._myKerning, theCharset, theIsSmooth, theDetail);
	}

	static public CCOutlineFont createOutlineFont(final String theName,final float theSize, final boolean theIsSmooth, final int theDetail){
		return createOutlineFont(theName,theSize, theIsSmooth, CCCharSet.REDUCED_CHARSET, theDetail);
	}

	static public CCOutlineFont createOutlineFont(final String theName,final float theSize, final int theDetail){
		return createOutlineFont(theName,theSize, true, CCCharSet.REDUCED_CHARSET, theDetail);
	}

	/////////////////////////////////////////////
	//
	// BITMAP FONT HANDLING
	//
	/////////////////////////////////////////////
	
	static private Map<String, CCFont<?>> _myRegisteredFonts = new HashMap<String, CCFont<?>>();
	
	static public void registerFont(final String theKey, final CCFont<?> theFont) {
		_myRegisteredFonts.put(theKey, theFont);
	}
	
	static public CCFont<?> registeredFont(final String theKey) {
		return _myRegisteredFonts.get(theKey);
	}
	
	static public Map<String,Font> _myFontMap = new HashMap<String, Font>();
	
	static private CCTextRenderer STANDARD_TEXT_RENDERER = new CCTextRenderer();
	
	static public CCTextTexture createBitmapText(final CCTextRenderer theRenderer, String theText,String name, float theSize, boolean theIsSmooth){

		String lowerName = name.toLowerCase();
		Font font = null;

		try{
			if (lowerName.endsWith(".otf") || lowerName.endsWith(".ttf")){
				if(_myFontMap.containsKey(lowerName)){
					font = _myFontMap.get(lowerName);
				}else{
					Font baseFont = Font.createFont(Font.TRUETYPE_FONT, CCIOUtil.openStream(name));
					font = baseFont.deriveFont(theSize);
					_myFontMap.put(lowerName, font);
				}
			}else{
				Font baseFont = new Font(name, Font.PLAIN, 1);
				font = baseFont.deriveFont(theSize);
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + name);
		}
		return new CCTextTexture(theRenderer, theText,font, theIsSmooth);
	}
	
	static public CCTextTexture createBitmapText(String theText,String name, float theSize){
		return createBitmapText(STANDARD_TEXT_RENDERER, theText,name, theSize, true);
	}
	
	static public CCTextTexture createBitmapText(CCTextRenderer theTextRenderer, String theText, String name, float theSize){
		return createBitmapText(theTextRenderer, theText,name, theSize, true);
	}

	/////////////////////////////////////////////////////
	//
	// TEXTURE MAP FONTS
	//
	/////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	static public CCTextureMapFont createTextureMapFont(final String theName, final float theSize, final boolean theIsSmooth, final CCCharSet theCharset){
		CCFontData font = createFont(theName, theSize);
		return new CCTextureMapFont(font._myJavaFont, font._myKerning, theCharset, theIsSmooth);
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final float theSize, final boolean theIsSmooth){
		return createTextureMapFont(theName, theSize, theIsSmooth, CCCharSet.EXTENDED_CHARSET);
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final float theSize, final CCCharSet theCharSet){
		return createTextureMapFont(theName, theSize, true, theCharSet);
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final float theSize){
		return createTextureMapFont(theName, theSize, true, CCCharSet.EXTENDED_CHARSET);
	}
	
	/////////////////////////////////////////////
	//
	// GLUT FONT HANDLING
	//
	/////////////////////////////////////////////
	
	static public CCGlutFontType BITMAP_8_BY_13 = CCGlutFontType.BITMAP_8_BY_13;
	static public CCGlutFontType BITMAP_9_BY_15 = CCGlutFontType.BITMAP_9_BY_15;
	static public CCGlutFontType BITMAP_HELVETICA_10 = CCGlutFontType.BITMAP_HELVETICA_10;
	static public CCGlutFontType BITMAP_HELVETICA_12 = CCGlutFontType.BITMAP_HELVETICA_12;
	static public CCGlutFontType BITMAP_HELVETICA_18 = CCGlutFontType.BITMAP_HELVETICA_18;
	static public CCGlutFontType BITMAP_TIMES_ROMAN_10 = CCGlutFontType.BITMAP_TIMES_ROMAN_10;
	static public CCGlutFontType BITMAP_TIMES_ROMAN_24 = CCGlutFontType.BITMAP_TIMES_ROMAN_24;
	
	static public CCGlutFont createGlutFont(final CCGlutFontType theFontType, final CCCharSet theCharSet){
		return new CCGlutFont(theFontType,theCharSet);
	}
	
	static public CCGlutFont createGlutFont(final CCGlutFontType theFontType){
		return new CCGlutFont(theFontType,CCCharSet.REDUCED_CHARSET);
	}
	
	static public CCFont<?> defaultFont(){
		return createGlutFont(CCGlutFontType.BITMAP_HELVETICA_10);
	}
}

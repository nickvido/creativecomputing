package cc.creativecomputing.graphics.font.text;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapChar;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

public class CCTextureMapMeshText extends CCAbstractText<CCTextureMapFont> {
	
	private CCVBOMesh _myMesh;

	private List<CCVector3f> _myVertices = new ArrayList<CCVector3f>();
	private List<CCVector2f> _myTextureCoords = new ArrayList<CCVector2f>();

	public CCTextureMapMeshText(final String theFont, final float theSize) {
		super(CCFontIO.createTextureMapFont(theFont, theSize));
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS);
	}
	
	public void addText(final String theText, final CCVector3f thePosition) {
		addText(theText, thePosition.x(), thePosition.y, thePosition.z());
	}

	/**
	 * Draw a chunk of text. Newlines that are \n (Unix newline or linefeed char, ascii 10) are honored, but \r
	 * (carriage return, Windows and Mac OS) are ignored.
	 */
	public void addText(final String theString, final float theX, float theY, final float theZ) {
		final int myStringLength = theString.length();
		
		char[] myTextBuffer = theString.toCharArray();

		int myStart = 0;
		int myIndex = 0;

		while (myIndex < myStringLength) {
			if (myTextBuffer[myIndex] == '\n') {
				textLine(myTextBuffer, myStart, myIndex, theX, theY, theZ);
				myStart = myIndex + 1;
				theY += _myLeading;
			}
			myIndex++;
		}

		if (myStart < myStringLength) {
			textLine(myTextBuffer, myStart, myIndex, theX, theY, theZ);
		}
	}

	/**
	 * Handles placement of a text line, then calls textLinePlaced to actually render at the specific point.
	 */
	private void textLine(
		final char theTextBuffer[], final int theStart, final int theStop, 
		float theX, final float theY, final float theZ
	) {
		switch (_myTextAlign) {
		case CENTER:
			theX -= _myFont.width(theTextBuffer, theStart, theStop) / 2f * _myTextSize;
			break;
		case RIGHT:
			theX -= _myFont.width(theTextBuffer, theStart, theStop) * _myTextSize;
			break;
		}

		for (int index = theStart; index < theStop; index++) {
			final char myChar = theTextBuffer[index];
			switch (myChar) {
			case ' ':
				theX += _myFont.spaceWidth() * _myTextSize * _mySpacing;
				break;
			default:
				final int myIndex = _myFont.index(myChar);
				if (myIndex < 0)
					continue;
				final CCTextureMapChar glyph = _myFont.chars()[myIndex];
				final float myWidth = _myFont.width(myIndex) * _myTextSize;
				final float myHeight = _myFont.height() * _myTextSize;

				_myVertices.add(new CCVector3f(theX, theY, theZ));
				_myVertices.add(new CCVector3f(theX + myWidth + 0.1f * _myTextSize, theY, theZ));
				_myVertices.add(new CCVector3f(theX + myWidth + 0.1f * _myTextSize, theY - myHeight, theZ));
				_myVertices.add(new CCVector3f(theX, theY - myHeight, theZ));

				_myTextureCoords.add(new CCVector2f(glyph.min().x, glyph.min().y));
				_myTextureCoords.add(new CCVector2f(glyph.max().x, glyph.min().y));
				_myTextureCoords.add(new CCVector2f(glyph.max().x, glyph.max().y));
				_myTextureCoords.add(new CCVector2f(glyph.min().x, glyph.max().y));

				theX += myWidth * _mySpacing;
			}
		}
	}
	
	public void updateMesh() {
		_myMesh.vertices(_myVertices, true);
		_myMesh.textureCoords(_myTextureCoords);
	}
	
	public void clear() {
		_myVertices.clear();
		_myTextureCoords.clear();
	}

	public CCVBOMesh mesh() {
		return _myMesh;
	}

	public CCTexture texture() {
		return _myFont.texture();
	}

	public void draw(CCGraphics g) {
		g.texture(_myFont.texture());
		_myMesh.draw(g);
		g.noTexture();
	}
}

package cc.creativecomputing.graphics.font.text;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorChar;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.math.CCVector3f;

public class CCVectorMeshText extends CCAbstractText<CCVectorFont> {
	
	private CCVBOMesh _myMesh;

	private List<CCVector3f> _myVertices = new ArrayList<CCVector3f>();

	public CCVectorMeshText(final String theFont, final float theSize) {
		super(CCFontIO.createVectorFont(theFont, theSize));
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES);
	}
	
	public void addText(final String theText, final CCVector3f thePosition) {
		addText(theText, thePosition.x(), thePosition.y(), thePosition.z());
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
		float myScale = _myTextSize / _myFont.size();
		
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
				theX += _myFont.spaceWidth() * _myTextSize;
				break;
			default:
				final int myIndex = _myFont.index(myChar);
				if (myIndex < 0)
					continue;
				final CCVectorChar glyph = _myFont.chars()[myIndex];
				final float myWidth = _myFont.width(myIndex) * _myTextSize;

				for(int i = 0; i < glyph.numberOfVertices();i++){
					_myVertices.add(new CCVector3f(
						theX + glyph.vertices()[i * 2] * myScale, 
						theY - glyph.vertices()[i * 2 + 1] * myScale,
						theZ
					));
				}
				theX += myWidth;
			}
		}
	}
	
	public void updateMesh() {
		_myMesh.vertices(_myVertices, true);
	}
	
	public void clear() {
		_myVertices.clear();
	}

	public CCVBOMesh mesh() {
		return _myMesh;
	}

	public void draw(CCGraphics g) {
		_myMesh.draw(g);
	}
}

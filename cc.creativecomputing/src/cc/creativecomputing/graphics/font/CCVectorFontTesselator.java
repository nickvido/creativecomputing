package cc.creativecomputing.graphics.font;


import java.awt.FontMetrics;

import cc.creativecomputing.graphics.CCTesselator;


public class CCVectorFontTesselator extends CCTesselator{
	
	private CCVectorChar _myVectorChar;

	public CCVectorFontTesselator() {
		super();
	}

	@Override
	public void begin(int theMode) {
	}

	@Override
	public void combineData(
		double[] theCoords, Object[] theInputData,
		float[] theWeight, Object[] theOutputData, Object theUserData
	) {
	}

	@Override
	public void edgeFlagData(final boolean theArg0, Object theData) {
	}

	@Override
	public void end() {
		_myVectorChar.end();
	}

	@Override
	public void errorData(final int theErrorNumber, Object theUserData) {
	}

	@Override
	public void vertex(final Object theVertexData) {
		if (theVertexData instanceof double[]) {
	        double[] d = (double[]) theVertexData;
			_myVectorChar.addVertex((float)d[0],(float)d[1]);
		}
	}

	public void beginPolygon(final CCVectorChar theVectorChar) {
		_myVectorChar = theVectorChar;
		glu.gluTessBeginPolygon(_myTesselator,null);
	}
	
	

}

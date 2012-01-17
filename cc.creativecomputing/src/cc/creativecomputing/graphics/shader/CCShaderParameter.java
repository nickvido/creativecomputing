package cc.creativecomputing.graphics.shader;

import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

public abstract class CCShaderParameter {
	
	private Object _myValue;

	public void set(final float theValue) {
		_myValue = theValue;
	}

	public void set(final float theX, final float theY) {
		_myValue = new CCVector2f(theX, theY);
	}

	public void set(final float theX, final float theY, final float theZ) {
		_myValue = new CCVector3f(theX, theY, theZ);
	}
}

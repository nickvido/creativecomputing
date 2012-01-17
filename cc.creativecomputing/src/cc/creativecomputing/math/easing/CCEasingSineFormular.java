package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

public class CCEasingSineFormular implements CCEasingFormular {

	public double easeIn(final double theBlend) {
		return 1 - CCMath.cos(theBlend * CCMath.HALF_PI);
	}

	public double easeOut(final double theBlend) {
		return CCMath.sin(theBlend * CCMath.HALF_PI);
	}

	public double easeInOut(final double theBlend) {
		return (CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2;
	}

	public CCEasingSineFormular clone() {
		return new CCEasingSineFormular();
	}
}

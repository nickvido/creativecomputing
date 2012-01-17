package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

public class CCEasingQuadraticFormular implements CCEasingFormular{
	
	public double easeIn(final double theBlend) {
		return CCMath.sq(theBlend);
	}
	
	public double easeOut(final double theBlend) {
		return -(theBlend) * (theBlend - 2);
	}
	
	public double  easeInOut(final double theBlend) {
		if (theBlend < 0.5) return 2 * theBlend * theBlend;
		return 1 - 2 * CCMath.sq(1 - theBlend);
	}
	
	public CCEasingQuadraticFormular clone() {
		return new CCEasingQuadraticFormular();
	}
}

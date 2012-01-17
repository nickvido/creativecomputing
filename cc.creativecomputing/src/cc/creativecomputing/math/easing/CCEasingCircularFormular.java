package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

public class CCEasingCircularFormular implements CCEasingFormular{
	
	public double easeIn(final double theBlend) {
		return 1 - CCMath.sqrt(1 - theBlend * theBlend);
	}
	
	public double easeOut(double theBlend) {
		theBlend = 1 - theBlend;
		return CCMath.sqrt(1 - theBlend * theBlend);
	}
	
	public double easeInOut(double theBlend) {
		theBlend *= 2;
		if (theBlend < 1) return -0.5 * (CCMath.sqrt(1 - theBlend * theBlend) - 1);
		return 0.5f * (CCMath.sqrt(1 - CCMath.sq(2  - theBlend)) + 1);
	}

	public CCEasingCircularFormular clone() {
		return new CCEasingCircularFormular();
	}
}

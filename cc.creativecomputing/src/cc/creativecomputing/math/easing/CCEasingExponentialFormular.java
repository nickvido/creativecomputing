package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;
 
public class CCEasingExponentialFormular implements CCEasingFormular{
	
	public double easeIn(final double theBlend) {
		return (theBlend==0) ? 0 : theBlend * CCMath.pow(2, 10 * (theBlend - 1));
	}
	
	public double easeOut(final double theBlend) {
		return (theBlend == 1) ? 1 : (-CCMath.pow(2, -10 * theBlend) + 1);	
	}
	
	public double  easeInOut(double theBlend) {
		if (theBlend == 0) return 0;
		if (theBlend == 1) return 1;
		if ((theBlend) < 0.5) return 0.5f * CCMath.pow(2, 10 * (theBlend - 1));
		return 0.5f * (-CCMath.pow(2, -10 * (theBlend-1)) + 2);
	}

	public CCEasingExponentialFormular clone() {
		return new CCEasingExponentialFormular();
	}
}

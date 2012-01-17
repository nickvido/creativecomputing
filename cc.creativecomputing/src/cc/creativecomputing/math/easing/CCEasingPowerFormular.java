package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

public class CCEasingPowerFormular implements CCEasingFormular{
	
	private final double _myPow;
	
	public CCEasingPowerFormular(final double thePow){
		_myPow = thePow;
	}
	
	public double easeIn (final double theBlend) {
		return CCMath.pow(theBlend, _myPow);
	}
	
	public double easeOut (final double theBlend) {
		return 1 - CCMath.pow(1 - theBlend, _myPow);
	}
	
	public double easeInOut (final double theBlend) {
		if (theBlend < 0.5f) return CCMath.pow(theBlend * 2, _myPow) / 2;
		return 1 - CCMath.pow((1 - theBlend) * 2, _myPow) / 2;
	}

	public CCEasingPowerFormular clone() {
		return new CCEasingPowerFormular(_myPow);
	}
}

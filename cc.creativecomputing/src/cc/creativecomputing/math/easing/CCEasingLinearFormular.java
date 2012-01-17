package cc.creativecomputing.math.easing;

public class CCEasingLinearFormular implements CCEasingFormular{
	
//	public static float easeNone (float t,float b , float c, float d) {
//		return c*t/d + b;
//	}
	
	public double easeIn (final double theBlend) {
		return theBlend;
	}
	
	public double easeOut (final double theBlend) {
		return theBlend;
	}
	
	public double easeInOut (final double theBlend) {
		return theBlend;
	}
	
	public CCEasingLinearFormular clone() {
		return new CCEasingLinearFormular();
	}
}

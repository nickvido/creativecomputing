package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

public class CCEasing {
	private CCEasingFormular _myFormular;
	
	public CCEasing(final CCEasingFormular theFormular){
		_myFormular = theFormular;
	}
	
	public CCEasing(final CCEasingMode theMode){
		_myFormular = theMode.formular();
	}
	
	public CCEasing(){
		this(new CCEasingLinearFormular());
	}
	
	public void mode(final CCEasingMode theMode){
		_myFormular = theMode.formular();
	}
	
	public void formular(final CCEasingFormular theFormular) {
		_myFormular = theFormular;
	}
	
	public float easeIn(final float theBlend){
		return (float)_myFormular.easeIn(theBlend);
	}
	
	public double easeIn(final double theBlend){
		return _myFormular.easeIn(theBlend);
	}
	
	public float easeOut(final float theBlend){
		return (float)_myFormular.easeOut(theBlend);
	}
	
	public double easeOut(final double theBlend){
		return _myFormular.easeOut(theBlend);
	}
	
	public float easeInOut(final float theBlend){
		return (float)_myFormular.easeInOut(theBlend);
	}
	
	public double easeInOut(final double theBlend){
		return _myFormular.easeInOut(theBlend);
	}
	
	public float easeIn(final float theStart, final float theStop, final float theBlend){
		return CCMath.blend(theStart, theStop, easeIn(theBlend));
	}
	
	public float easeOut(final float theStart, final float theStop, final float theBlend){
		return CCMath.blend(theStart, theStop, easeOut(theBlend));
	}
	
	public float easeInOut(final float theStart, final float theStop, final float theBlend){
		return CCMath.blend(theStart, theStop, easeInOut(theBlend));
	}
	
	public float easeIn(final float theStart, final float theStop, final float theTime, final float theDuration){
		return CCMath.blend(theStart, theStop, easeIn(theTime/theDuration));
	}
	
	public float easeOut(final float theStart, final float theStop, final float theTime, final float theDuration){
		return CCMath.blend(theStart, theStop, easeOut(theTime/theDuration));
	}
	
	public float easeInOut(final float theStart, final float theStop, final float theTime, final float theDuration){
		return CCMath.blend(theStart, theStop, easeInOut(theTime/theDuration));
	}
	
	private static CCEasing easing = new CCEasing();
	
	public static float easeIn(final CCEasingMode theMode, final float theBlend){
		easing.mode(theMode);
		return easing.easeIn(theBlend);
	}
	
	public static float easeOut(final CCEasingMode theMode, final float theBlend){
		easing.mode(theMode);
		return easing.easeOut(theBlend);
	}
	
	public static float easeInOut(final CCEasingMode theMode, final float theBlend){
		easing.mode(theMode);
		return easing.easeInOut(theBlend);
	}
	
	public static double easeInOut(final CCEasingMode theMode, final double theBlend){
		easing.mode(theMode);
		return easing.easeInOut(theBlend);
	}
}

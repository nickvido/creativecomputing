package cc.creativecomputing.math.easing;

public enum CCEasingMode {
	SINE(new CCEasingSineFormular()),
	LINEAR(new CCEasingLinearFormular()),
	QUADRATIC(new CCEasingQuadraticFormular()),
	CUBIC(new CCEasingPowerFormular(3)),
	QUARTIC(new CCEasingPowerFormular(4)),
	QUINTIC(new CCEasingPowerFormular(5)),
	EXPONENTIAL(new CCEasingExponentialFormular()),
	CIRCULAR(new CCEasingCircularFormular()),
	PENDULAR(new CCEasingPendularFormular());
	
	private final CCEasingFormular _myFormular;
	
	private CCEasingMode(final CCEasingFormular theFormular){
		_myFormular = theFormular;
	}
	
	public CCEasingFormular formular(){
		return _myFormular;
	}
}

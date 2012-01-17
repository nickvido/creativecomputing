package cc.creativecomputing.math.easing;

public interface CCEasingFormular {
	public double easeIn(final double theBlend);
	
	public double easeOut(final double theBlend);
	
	public double easeInOut(final double theBlend);
	
	public CCEasingFormular clone();
}

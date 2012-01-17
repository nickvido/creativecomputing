package cc.creativecomputing.graphics.util;

import cc.creativecomputing.math.CCVector3f;

public interface CCIFrustumWrapable {

	public int frustumMode(CCClipSpaceFrustum theFrustum);
	
	public CCVector3f frustumWrapPosition();
	
	public CCVector3f frustumWrapDimension();
	
	public void frustumWrap(CCVector3f theWrapVector);
}

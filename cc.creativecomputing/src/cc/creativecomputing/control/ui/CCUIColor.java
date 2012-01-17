package cc.creativecomputing.control.ui;

import cc.creativecomputing.graphics.CCColor;

public class CCUIColor {

	public CCColor colorBackground = CCColor.parseFromInteger(0xff00344D);
	public CCColor colorBackgroundOver = CCColor.parseFromInteger(0xff004C73);
	public CCColor colorForeground = CCColor.parseFromInteger(0xff006799);
	public CCColor colorForegroundOver = CCColor.parseFromInteger(0xff007FBF);
	public CCColor colorActive = CCColor.parseFromInteger(0xff0099E5);

	public CCColor colorLabel = CCColor.parseFromInteger(0xffffffff);
	public CCColor colorValue = CCColor.parseFromInteger(0xffffffff);

	protected void set(CCUIColor theColor) {
		colorBackground = theColor.colorBackground;
		colorBackgroundOver = theColor.colorBackgroundOver;
		colorForeground = theColor.colorForeground;
		colorForegroundOver = theColor.colorForegroundOver;

		colorActive = theColor.colorActive;
		colorLabel = theColor.colorLabel;
		colorValue = theColor.colorValue;
	}

	public CCUIColor() {}

	public CCUIColor(CCUIColor theColor) {
		set(theColor);
	}

	public boolean equals(CCUIColor theColor) {
		if (colorBackground == theColor.colorBackground && colorForeground == theColor.colorForeground && colorActive == theColor.colorActive && colorLabel == theColor.colorLabel
				&& colorValue == theColor.colorValue) {
			return true;
		}
		return false;
	}
}
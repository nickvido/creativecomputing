package cc.creativecomputing.control.ui.properties;

import cc.creativecomputing.graphics.CCColor;

public class CCColorProperty extends CCProperty<CCColor> {

    public CCColorProperty() {
        super();
        _myValue = new CCColor(1f);
    }

	@Override
	public void blend(float theBlend, CCColor theStart, CCColor theEnd) {
        value(CCColor.blend(theStart, theEnd, theBlend));
	}
    

}

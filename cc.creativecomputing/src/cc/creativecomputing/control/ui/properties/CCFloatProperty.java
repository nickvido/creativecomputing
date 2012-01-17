package cc.creativecomputing.control.ui.properties;

import cc.creativecomputing.math.CCMath;

public class CCFloatProperty extends CCProperty<Float> {

    public CCFloatProperty() {
        super();
        _myValue = 0.0f;
    }
    
    public CCFloatProperty(Float theValue) {
    	super(theValue);
    }

    @Override
    public void blend(float theBlend, Float theStart, Float theEnd) {
        value(CCMath.blend(theStart, theEnd, theBlend));
    }

}

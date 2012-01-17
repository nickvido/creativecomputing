package cc.creativecomputing.control.ui.properties;

import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVecMath;

public class CCVector2fProperty extends CCProperty<CCVector2f> {

    public CCVector2fProperty(CCVector2f theVector) {
        super(theVector);
    }

    @Override
    public void value(CCVector2f theVector) {
        _myValue.set(theVector);
        notifyChangeListeners();
    }

    @Override
    public void blend(float theBlend, CCVector2f theStart, CCVector2f theEnd) {
        value(CCVecMath.blend(theBlend,theStart, theEnd));
    }

}

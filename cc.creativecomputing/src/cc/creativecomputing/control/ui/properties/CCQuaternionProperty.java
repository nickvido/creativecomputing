package cc.creativecomputing.control.ui.properties;

import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCMath;

public class CCQuaternionProperty extends CCProperty<CCQuaternion> {
    
    public CCQuaternionProperty(CCQuaternion theQuaternion) {
        super(theQuaternion);
    }

    @Override
    public void value(CCQuaternion theQuaternion) {
        _myValue.set(theQuaternion);
        notifyChangeListeners();
    }

	@Override
	public void blend(float theBlend, CCQuaternion theStart, CCQuaternion theEnd) {
        // TODO: implement slerp
	}

}

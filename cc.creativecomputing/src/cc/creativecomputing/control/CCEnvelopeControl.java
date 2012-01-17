package cc.creativecomputing.control;

import java.lang.reflect.Member;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.timeline.CCTimedData;
import cc.creativecomputing.control.timeline.CCTimedDataUI;
import cc.creativecomputing.math.CCVector2f;

public class CCEnvelopeControl extends CCValueControl<CCTimedData[]>{

	public CCEnvelopeControl(CCControlConnector<CCTimedData[]> theConnector, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCTimedDataUI createUIElement(final float theWidth, final float theHeight) {
		return new CCTimedDataUI(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			_myConnector.numberOfEnvelopes()
		);
	}

	@Override
	public CCTimedData[] defaultValue() {
		CCTimedData[] myResult = new CCTimedData[_myConnector.numberOfEnvelopes()];
		for(int i = 0; i < myResult.length; i++) {
			myResult[i] = new CCTimedData();
		}
		return myResult;
	}

	@Override
	protected CCTimedData[] stringToValue(String theStringValue) {
		return null;
	}

}

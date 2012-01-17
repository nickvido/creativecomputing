/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.newui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.ui.properties.CCPropertyMap;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector1f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.CCUIEditPolicy;
import cc.creativecomputing.newui.CCUIInputEventType;
import cc.creativecomputing.newui.CCUIPropertyObject;
import cc.creativecomputing.newui.CCUIHorizontalAlignment;
import cc.creativecomputing.newui.CCUIVerticalAlignment;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.decorator.CCUIDecorator;
import cc.creativecomputing.newui.decorator.CCUIForegroundDecorator;
import cc.creativecomputing.newui.decorator.background.CCUIBackgroundDecorator;
import cc.creativecomputing.newui.decorator.border.CCUIBorderDecorator;
import cc.creativecomputing.newui.event.CCUIWidgetEventListener;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;
import cc.creativecomputing.newui.event.CCUIWidgetInteractionEvent;
import cc.creativecomputing.newui.event.CCUIWidgetUpdateEvent;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name = "widget")
public class CCUIWidget extends CCUIPropertyObject{
	
	public static final String TRANSLATION_PROPERTY = "translation";
	public static final String SCALE_PROPERTY = "scale";
	public static final String ROTATION_PROPERTY = "rotation";
	
	public static final String BACKGROUND = "background";
	public static final String BORDER = "border";
	public static final String FOREGROUND = "foreground";

	protected CCMatrix32f _myMatrix = new CCMatrix32f();
	private CCMatrix32f _myInverseMatrix = new CCMatrix32f();
	
	@CCProperty(name=TRANSLATION_PROPERTY, optional = true)
	private CCVector2f _myTranslation = new CCVector2f();
	@CCProperty(name=SCALE_PROPERTY, optional = true)
	private CCVector2f _myScale = new CCVector2f(1,1);
	@CCProperty(name=ROTATION_PROPERTY, optional = true)
	private CCVector1f _myRotation = new CCVector1f();
	
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.BOTTOM;
	private CCVector2f _myAlignment = new CCVector2f();
	
	@CCProperty(name=BACKGROUND, optional = true)
	protected CCUIBackgroundDecorator _myBackground;
	@CCProperty(name=BORDER, optional = true)
	protected CCUIBorderDecorator _myBorder;
	@CCProperty(name=FOREGROUND, optional = true)
	protected CCUIForegroundDecorator _myForeground;
	
	@CCProperty(name="event_handler", optional = true)
	private List<CCUIWidgetEventListener> _myListener = new ArrayList<CCUIWidgetEventListener>();
	
	private boolean _myIsOver = false;
	private boolean _myIsPressed = false;
	
	@CCProperty(name = "width", node = false, optional = true)
	private float _myWidth = 0;
	
	@CCProperty(name = "height", node = false, optional = true)
	private float _myHeight = 0;
	
	@CCProperty(name = "edit_policy", node = false, optional = true)
	protected CCUIEditPolicy _myEditPolicy = CCUIEditPolicy.ADMIN;
	
	@CCProperty(name = "children", optional = true)
	private List<CCUIWidget> _myChildren;
	
	private boolean _myIsInEditMode = false;
	
	public CCUIWidget(float theWidth, float theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myProperties.put(TRANSLATION_PROPERTY, _myTranslation);
		_myProperties.put(SCALE_PROPERTY, _myScale);
		_myProperties.put(ROTATION_PROPERTY, _myRotation);
	}
	
	public CCUIWidget() {
		this(0,0);
	}
	
	public void setup(CCUI theUI, CCUIWidget theParent) {
		if(_myBackground != null) {
			_myBackground.setup(theUI, this);
			_myProperties.put(BACKGROUND, _myBackground);
		}
		if(_myBorder != null)_myBorder.setup(theUI, this);
		if(_myForeground != null)_myForeground.setup(theUI, this);
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.setup(theUI, this);
		}
	}

	@CCProperty(name="horizontal_alignment", optional = true)
	public CCUIHorizontalAlignment horizontalAlignment() {
		return _myHorizontalAlignment;
	}

	@CCProperty(name="horizontal_alignment", optional = true)
	public void horizontalAlignment(CCUIHorizontalAlignment theHorizontalAlignment) {
		_myHorizontalAlignment = theHorizontalAlignment;
		
		switch(_myHorizontalAlignment) {
		case RIGHT:
			_myAlignment.x = -1;
			break;
		case CENTER:
			_myAlignment.x = -0.5f;
			break;
		case LEFT:
			_myAlignment.x = 0f;
			break;
		}
	}

	@CCProperty(name="vertical_alignment", optional = true)
	public CCUIVerticalAlignment verticalAlignment() {
		return _myVerticalAlignment;
	}

	@CCProperty(name="vertical_alignment", optional = true)
	public void verticalAlignment(CCUIVerticalAlignment theVerticalAlignment) {
		_myVerticalAlignment = theVerticalAlignment;
		
		switch(theVerticalAlignment) {
		case TOP:
			_myAlignment.y = -1;
			break;
		case CENTER:
			_myAlignment.y = -0.5f;
			break;
		case BOTTOM:
			_myAlignment.y = -0f;
			break;
		}
	}
	
	public CCMatrix32f transformation() {
		return _myMatrix;
	}
	
	public void background(CCUIBackgroundDecorator theBackground) {
		_myBackground = theBackground;
	}
	
	public void border(CCUIBorderDecorator theBorder) {
		_myBorder = theBorder;
	}
	
	public CCUIBorderDecorator border() {
		return _myBorder;
	}
	
	public void foreground(CCUIForegroundDecorator theForeground) {
		_myForeground = theForeground;
	}
	
	public CCUIForegroundDecorator foreground() {
		return _myForeground;
	}
	
	public void addChild(CCUIWidget theWidget) {
		if(_myChildren == null)_myChildren = new ArrayList<CCUIWidget>();
		_myChildren.add(theWidget);
	}
	
	public void removeChild(CCUIWidget theWidget) {
		if(_myChildren == null)return;
		_myChildren.remove(theWidget);
	}
	
	public boolean isInside(CCVector2f theVector) {
		return 
			theVector.x >= 0 && 
			theVector.x <= width() &&
			theVector.y >= 0 && 
			theVector.y <= height();
	}
	
	public boolean isInside(float theX, float theY) {
		return isInside(new CCVector2f(theX, theY));
	}
	
	public void addListener(CCUIWidgetEventListener theListener) {
		_myListener.add(theListener);
	}
	
	public void removeListener(CCUIWidgetEventListener theListener) {
		_myListener.remove(theListener);
	}
	
	private void callListener(CCUIWidgetEventType theEventType, CCVector2f thePosition, CCVector2f theTransformedPosition) {
		for(CCUIWidgetEventListener myListener:new ArrayList<CCUIWidgetEventListener>(_myListener)) {
			myListener.onEvent(new CCUIWidgetInteractionEvent(theEventType, thePosition, theTransformedPosition), this);
		}
	}
	
	private void handleEvent(CCVector2f theVector, CCVector2f theTransformedVector, CCUIInputEventType theEventType) {
		boolean myIsInside = isInside(theTransformedVector);
		
		switch(theEventType) {
		case PRESS:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.PRESS, theVector, theTransformedVector);
				_myIsPressed = true;
			} else {
				callListener(CCUIWidgetEventType.PRESS_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		case CLICK:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.CLICK, theVector, theTransformedVector);
			}
			break;
		case DOUBLE_CLICK:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.DOUBLE_CLICK, theVector, theTransformedVector);
			}
			break;
		case RELEASE:
			if(!_myIsPressed)return;
			if(myIsInside) {
				callListener(CCUIWidgetEventType.RELEASE, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.RELEASE_OUTSIDE, theVector, theTransformedVector);
			}
			_myIsPressed = false;
			break;
		case MOVE:
			if(_myIsOver && !myIsInside) {
				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
				_myIsOver = false;
			}
			
			if(!_myIsOver && myIsInside) {
				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
				_myIsOver = true;
			}
			if(myIsInside) {
				callListener(CCUIWidgetEventType.MOVE, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.MOVE_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		case DRAGG:
			if(_myIsOver && !myIsInside) {
				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
				_myIsOver = false;
			}
			
			if(!_myIsOver && myIsInside) {
				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
				_myIsOver = true;
			}
			if(myIsInside) {
				callListener(CCUIWidgetEventType.DRAGG, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.DRAGG_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		}
	}
	
	public void checkEvent(CCVector2f theVector, CCUIInputEventType theEventType) {
		CCVector2f myTransformedVector = _myInverseMatrix.transform(theVector);
		
		if(_myListener.size() > 0) {
			handleEvent(theVector, myTransformedVector, theEventType);
		}
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.checkEvent(myTransformedVector, theEventType);
		}
	}
	
	public void keyEvent(CCKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.keyEvent(theKeyEvent, theEventType);
		}
	}
	
	public void update(float theDeltaTime) {
		_myMatrix.reset();
		_myMatrix.translate(_myTranslation);
		_myMatrix.rotate(CCMath.radians(_myRotation.x));
		_myMatrix.scale(_myScale.x, _myScale.y);
		_myMatrix.translate(_myAlignment.x * _myWidth, _myAlignment.y * _myHeight);
		
		_myInverseMatrix = _myMatrix.inverse();
		
		for(CCUIWidgetEventListener myListener:new ArrayList<CCUIWidgetEventListener>(_myListener)) {
			myListener.onEvent(new CCUIWidgetUpdateEvent(theDeltaTime), this);
		}
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.update(theDeltaTime);
		}
	}
	
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myMatrix);
		
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
		if(_myChildren != null) {;
			for(CCUIWidget myChild:_myChildren) {
				myChild.draw(g);
			}
		}
		
		g.popMatrix();
	}
	
	public float width() {
		return _myWidth;
	}
	
	public void width(float theWidth) {
		_myWidth = theWidth;
	}
	
	public float height() {
		return _myHeight;
	}
	
	public void height(float theHeight) {
		_myHeight = theHeight;
	}
	
	public float x() {
		return _myTranslation.x;
	}
	
	public void x(float theX) {
		_myTranslation.x = theX;
	}
	
	public float y() {
		return _myTranslation.y;
	}
	
	public void y(float theY) {
		_myTranslation.y = theY;
	}
}

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
package cc.creativecomputing.newui.animation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.newui.CCUIException;
import cc.creativecomputing.util.CCTuple;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name="animation")
public class CCUIAnimation {
	
	/**
	 * @author christianriekoff
	 *
	 */
	public class CCUIAnimationProperty<Type extends CCBlendable<Type>> implements CCBlendModifier{
		
		private Field _myField;
		private Method _myMethod;
		
		private Type _myObject;
		private Type _myStart;
		private Type _myTarget;
		
		private float _myTime = 0;
		private float _myDuration;
		
		public CCUIAnimationProperty(String theProperty, Object theObject, Type theTarget, float theDuration) {
			long time = System.currentTimeMillis();
			Object myProperty = CCPropertyUtil.property(theProperty, theObject);
			if(myProperty == null) {
				throw new CCUIException("The given property:" + theProperty + " is not accessable.");
			}
			_myObject = (Type)myProperty;
			_myObject.modifier(this);
			_myStart = _myObject.clone();
			_myTarget = theTarget;
			_myDuration = theDuration;
		}
		
		public void blend(float theBlend) {
			_myObject.blend(theBlend, _myStart, _myTarget);
		}
		
		public void update(final float theDeltaTime) {
			_myTime += theDeltaTime;
			
			float myBlend = CCMath.saturate(_myTime / _myDuration);
			blend(myBlend);
		}
		
		public boolean isOver() {
			return _myTime >= _myDuration;
		}

		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.animation.CCBlendModifier#onReplace()
		 */
		@Override
		public void onReplace() {
			_myAnimationProperties.remove(this);
		}
	}
	
	@CCPropertyObject(name="animation_property")
	public static class CCPropertyTarget{
		@CCProperty(name="target", node=false)
		private String _myTarget;
		@CCProperty(name="value")
		private CCBlendable _myValue;
		@CCProperty(name="duration", node=false, optional = true)
		private float _myDuration;
		
		private CCPropertyTarget() {
			_myDuration = -1;
		}
	}
	
	@CCProperty(name="properties")
	private CCPropertyTarget[] _myProperties;
	
	private List<CCUIAnimationProperty> _myAnimationProperties = new ArrayList<CCUIAnimationProperty>();
	
	@CCProperty(name = "duration", node = false, optional = true)
	private float _myDuration;
	
//	public CCUIAnimation(CCXMLElement theAnimationXML) {
//		_myDuration = theAnimationXML.floatAttribute("duration",0);
//		
//		CCXMLElement myPropertiesXML = theAnimationXML.child("animation_properties");
//
//		_myProperties = new CCPropertyTarget[myPropertiesXML.countChildren()];
//		
//		int i = 0;
//		for(CCXMLElement myPropertyXML:myPropertiesXML) {
//			CCPropertyTarget myPropertyTarget = myPropertyXML.toObject(CCPropertyTarget.class);
//			_myProperties[i++] = myPropertyTarget;
//		}
//	}
	
	public CCUIAnimation() {
		
	}
	
	public void animate(Object theObject) {
		for(CCPropertyTarget myPropertyTarget:_myProperties) {
			float myDuration = myPropertyTarget._myDuration >= 0 ? myPropertyTarget._myDuration : _myDuration;
			try {
				_myAnimationProperties.add(new CCUIAnimationProperty(myPropertyTarget._myTarget, theObject, myPropertyTarget._myValue, myDuration));
			}catch(CCUIException e) {
				CCLog.info(e.getMessage());
			}
		}
	}
	
	public void update(final float theDeltaTime) {
		for(CCUIAnimationProperty myProperty:new ArrayList<CCUIAnimationProperty>(_myAnimationProperties)) {
			if(myProperty.isOver())_myAnimationProperties.remove(myProperty);
			myProperty.update(theDeltaTime);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer myBuffer = new StringBuffer("CCUIAnimation\n");
		
		for(CCPropertyTarget myProperty:_myProperties) {
			myBuffer.append("Target  :" + myProperty._myTarget+"\n");
			myBuffer.append("value   :" + myProperty._myValue+"\n");
			myBuffer.append("duration:" + myProperty._myDuration+"\n");
		}
		
		return myBuffer.toString();
	}
}

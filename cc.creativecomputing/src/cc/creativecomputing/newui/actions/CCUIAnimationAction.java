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
package cc.creativecomputing.newui.actions;

import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.animation.CCUIAnimation;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 * 
 */
@CCPropertyObject(name = "animation_action")
public class CCUIAnimationAction extends CCUIAction {

	// private String _myAnimationID;
	@CCProperty(name = "animation")
	private CCUIAnimation _myAnimation;

	public CCUIAnimationAction(CCUIAnimation theAnimation, CCUIWidgetEventType... theTypes) {
		super(theTypes);
		_myAnimation = theAnimation;
	}

	protected CCUIAnimationAction() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.newui.actions.CCUIAction#init(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void init(CCUI theUI) {
	// _myAnimation = theUI.animation(_myAnimationID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.newui.actions.CCUIAction#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myAnimation.update(theDeltaTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.creativecomputing.newui.actions.CCUIAction#execute(cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void execute(CCUIWidget theWidget) {
		_myAnimation.animate(theWidget);
	}

}

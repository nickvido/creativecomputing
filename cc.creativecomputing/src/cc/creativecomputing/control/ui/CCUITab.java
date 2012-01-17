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
package cc.creativecomputing.control.ui;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCUITab extends CCUIComponent{

	private class CCUITabButton extends CCUIButton{

		public CCUITabButton(String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
			super(theLabel, thePosition, theDimension);
		}
		
		public CCUITabButton(String theLabel, float theX, float theY, float theWidth, float theHeight) {
			super(theLabel, theX, theY, theWidth, theHeight);
		}
		
		@Override
		public void setupText() {
			_myLabel.position(_myPosition.x + 3, _myPosition.y + _myDimension.y - 3);
		}

		@Override
		public void draw(CCGraphics g) {
			if(value()){
				g.color(_myUIColor.colorActive);
			}else{
				g.color(_myForeGround);
			}
			g.rect(_myPosition,_myDimension);
			g.color(_myUIColor.colorLabel);
			_myLabel.draw(g);
		}
		
		@Override
		public void onChange(){
			if(value()){
				_myUI.activeTab(CCUITab.this);
			}
			value(true);
		}
		@Override
		protected void onPress(CCMouseEvent theEvent){
		}
		
		@Override
		protected void onRelease(CCMouseEvent theEvent) {
			changeValue(true);
		}
		
		@Override
		protected void onReleaseOutside(CCMouseEvent theEvent) {
		}
	}
	
	protected final CCControlUI _myUI;
	
	private CCUIButton _myTabButton;

	/**
	 * @param theLabel
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUITab(final CCControlUI theUI, String theLabel, CCVector2f thePosition, CCVector2f theDimension, float theButtonOffset) {
		super(theLabel, thePosition.clone(), theDimension);
		_myDrawLabel = false;
		_myUI = theUI;
		_myTabButton = new CCUITabButton(theLabel, thePosition.clone().add(theButtonOffset,0), theDimension);
	}

	/**
	 * @param theLabel
	 * @param theX
	 * @param theY
	 * @param theButtonWidth
	 * @param theButtonHeight
	 */
	public CCUITab(final CCControlUI theUI, String theLabel, float theX, float theY, float theButtonWidth, float theButtonHeight, float theButtonOffset) {
		super(theLabel, theX, theY, theButtonWidth, theButtonHeight);
		_myDrawLabel = false;
		_myUI = theUI;
		_myTabButton = new CCUITabButton(theLabel, theX + theButtonOffset, theY, theButtonWidth, theButtonHeight);
	}
	
	public CCUIButton button(){
		return _myTabButton;
	}

	@Override
	public void draw(CCGraphics g){
		_myTabButton.draw(g);
		if(_myTabButton.value()){
			super.draw(g);
		}
	}
}

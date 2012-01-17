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
package cc.creativecomputing.newui.input;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.CCUI;
import cc.creativecomputing.newui.CCUIInputEventType;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUIMouseInput extends CCUIInput implements CCMouseListener, CCMouseMotionListener, CCKeyListener{
	private static int DOUBLE_CLICK_MILLIS = 1000;
	private long _myLastClickMillis = 0;
	
	private CCApp _myApp;
	
	public CCUIMouseInput(CCApp theApp, CCUI theUI) {
		super(theUI);
		_myApp = theApp;
		_myApp.addMouseListener(this);
		_myApp.addMouseMotionListener(this);
		_myApp.addKeyListener(this);
	}
	
	/**
	 * Converts mouse coordinates to screen coordinates
	 * @param theEvent
	 * @return
	 */
	private CCVector2f mouseToScreen(CCMouseEvent theEvent) {
		return new CCVector2f(theEvent.x() - _myApp.width/2, _myApp.height/2 - theEvent.y());
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseClicked(cc.creativecomputing.events.CCMouseEvent)
	 */
	@Override
	public void mouseClicked(CCMouseEvent theMouseEvent) {
		if(System.currentTimeMillis() - _myLastClickMillis > DOUBLE_CLICK_MILLIS) {
			_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.CLICK);
			_myLastClickMillis = System.currentTimeMillis();
			return;
		}
		
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.DOUBLE_CLICK);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseEntered(cc.creativecomputing.events.CCMouseEvent)
	 */
	@Override
	public void mouseEntered(CCMouseEvent theEvent) {
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCMouseListener#mouseExited(cc.creativecomputing.events.CCMouseEvent)
	 */
	@Override
	public void mouseExited(CCMouseEvent theEvent) {
	}
	
	@Override
	public void mousePressed(CCMouseEvent theMouseEvent) {
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.PRESS);
	}
	
	@Override
	public void mouseReleased(CCMouseEvent theMouseEvent) {
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.RELEASE);
	}

	@Override
	public void mouseMoved(CCMouseEvent theMouseEvent) {
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.MOVE);
	}

	@Override
	public void mouseDragged(CCMouseEvent theMouseEvent) {
		_myUI.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.DRAGG);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCKeyListener#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		for(CCUIWidget myWidget:_myUI.widgets()) {
			myWidget.keyEvent(theKeyEvent, CCUIInputEventType.PRESS);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCKeyListener#keyReleased(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyReleased(CCKeyEvent theKeyEvent) {
		for(CCUIWidget myWidget:_myUI.widgets()) {
			myWidget.keyEvent(theKeyEvent, CCUIInputEventType.RELEASE);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCKeyListener#keyTyped(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyTyped(CCKeyEvent theKeyEvent) {
		// TODO Auto-generated method stub
		
	}
}

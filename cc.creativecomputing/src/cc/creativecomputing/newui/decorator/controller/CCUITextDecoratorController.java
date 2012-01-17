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
package cc.creativecomputing.newui.decorator.controller;

import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.io.CCClipboard;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.newui.CCUIInputEventType;
import cc.creativecomputing.newui.decorator.CCUITextDecorator;
import cc.creativecomputing.newui.event.CCUIWidgetEvent;
import cc.creativecomputing.newui.event.CCUIWidgetEventType;
import cc.creativecomputing.newui.event.CCUIWidgetInteractionEvent;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public class CCUITextDecoratorController extends CCUIDecoratorController<CCUITextDecorator>{
	
	private boolean _myIsShiftPressed = false;
	
	private boolean _myIsValueText = false;
	
	private int _myStartCursorIndex = 0;
	private int _myCursorEndIndex = 0;
	
	private float _myMoveX = 0;
	
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#reset()
	 */
	@Override
	public void reset() {
		if(_myDecorator != null) {
			_myDecorator.showCursor(false);
			_myDecorator.isInSelection(false);
		}
	}
	
	public void valueText(boolean theIsValueText) {
		_myIsValueText = theIsValueText;
	}

	private boolean _myWasDragging = false;
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#onEvent(cc.creativecomputing.newui.CCUIWidgetEventType, cc.creativecomputing.math.CCVector2f, cc.creativecomputing.math.CCVector2f)
	 */
	@Override
	public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
		if(theEvent instanceof CCUIWidgetInteractionEvent) {
			CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
			
			switch(theEvent.type()) {
			case DOUBLE_CLICK:
				_myDecorator.showCursor(true);
				moveCursorToPosition(myEvent.transformedPosition());
				break;
			case PRESS:
				if(!_myDecorator.showCursor()) return;
				if(!_myIsShiftPressed)endSelection();
				moveCursorToPosition(myEvent.transformedPosition());
				_myWasDragging = false;
				break;
			case DRAGG:
				_myWasDragging = true;
				if(!_myDecorator.showCursor()) return;
				startSelection();
				moveCursorToPosition(myEvent.transformedPosition());
				break;
			case RELEASE:
			case RELEASE_OUTSIDE:
				break;
			case PRESS_OUTSIDE:
				_myDecorator.showCursor(false);
				endSelection();
				break;
			}
		}
	}
	
	private void startSelection() {
		if(!_myDecorator.isInSelection()) {
			_myDecorator.endCursorIndex(_myStartCursorIndex);
			_myDecorator.isInSelection(true);
		}
	}
	
	private void endSelection() {
		_myDecorator.isInSelection(false);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.controller.CCUIDecoratorController#keyEvent(cc.creativecomputing.events.CCKeyEvent, cc.creativecomputing.newui.CCUIInputEventType)
	 */
	@Override
	public void keyEvent(CCKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		if(!_myDecorator.showCursor()) return;
		
		if(theEventType == CCUIInputEventType.PRESS && (theKeyEvent.isControlDown() || theKeyEvent.isMetaDown())) {
			switch(theKeyEvent.keyCode()) {
			case CCKeyEvent.VK_C:
				copySelectionToClipboard();
				break;
			case CCKeyEvent.VK_X:
				copySelectionToClipboard();
				removeSelection();
				break;
			case CCKeyEvent.VK_V:
				copySelectionFromClipboard();
				break;
			}
			return;
		}
		
		switch(theKeyEvent.keyCode()) {
		case CCKeyEvent.VK_SHIFT:
			if(theEventType == CCUIInputEventType.PRESS) {
				_myIsShiftPressed = true;
				startSelection();
			}
			if(theEventType == CCUIInputEventType.RELEASE)_myIsShiftPressed = false;
			break;
		}
		
		if(theEventType != CCUIInputEventType.PRESS)return;
		if(!_myDecorator.showCursor())return;
			
		switch(theKeyEvent.keyCode()) {
		case CCKeyEvent.VK_RIGHT:
			if(!_myIsShiftPressed)endSelection();
			moveCursorForward();
			break;
		case CCKeyEvent.VK_LEFT:
			if(!_myIsShiftPressed)endSelection();
			moveCursorBack();
			break;
		case CCKeyEvent.VK_UP:
			if(!_myIsShiftPressed)endSelection();
			moveCursorUp();
			break;
		case CCKeyEvent.VK_DOWN:
			if(!_myIsShiftPressed)endSelection();
			moveCursorDown();
			break;
		case CCKeyEvent.VK_BACK_SPACE:
		case CCKeyEvent.VK_DELETE:
			delete();
			break;
		default:
			char myChar = theKeyEvent.keyChar();
			if(_myIsValueText) {
				switch(myChar) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '.':
					append(myChar);
					break;
				case '\n':
					_myDecorator.showCursor(false);
					endSelection();
					break;
				}
			}else {
				if(_myDecorator.text().font().canDisplay(myChar)) {
					append(myChar);
				}
			}
			break;
		}
	}
	
	private void moveCursorToPosition(CCVector2f thePosition) {
		_myStartCursorIndex = _myDecorator.text().textGrid().gridIndex(thePosition);
		_myDecorator.startCursorIndex(_myStartCursorIndex);
		_myMoveX = _myDecorator.cursorPosition().x;
	}
	
	public void moveCursorBack() {
		if(_myStartCursorIndex > 0)_myStartCursorIndex--;
		_myDecorator.startCursorIndex(_myStartCursorIndex);
		_myMoveX = _myDecorator.cursorPosition().x;
	}
	
	public void moveCursorForward() {
		moveCursorForward(1);
	}
	
	public void moveCursorForward(int theSteps) {
		if(_myStartCursorIndex + theSteps <= _myDecorator.text().text().length()) {
			_myStartCursorIndex += theSteps;
		}
		_myDecorator.startCursorIndex(_myStartCursorIndex);
		_myMoveX = _myDecorator.cursorPosition().x;
	}
	
	private void moveCursorUp() {
		_myStartCursorIndex = _myDecorator.text().textGrid().upperIndex(_myMoveX, _myStartCursorIndex);
		_myDecorator.startCursorIndex(_myStartCursorIndex);
	}
	
	private void moveCursorDown() {
		_myStartCursorIndex = _myDecorator.text().textGrid().lowerIndex(_myMoveX, _myStartCursorIndex);
		_myDecorator.startCursorIndex(_myStartCursorIndex);
	}
	
	private void removeSelection() {
		int myStartIndex = _myDecorator.startCursorIndex();
		int myEndIndex = _myDecorator.endCursorIndex();
		
		if(myStartIndex > myEndIndex) {
			int myTmp = myEndIndex;
			myEndIndex = myStartIndex;
			myStartIndex = myTmp;
		}
		
		_myDecorator.text().delete(myStartIndex - 1, myEndIndex - 1);
		endSelection();

		_myStartCursorIndex = myStartIndex;
		_myDecorator.startCursorIndex(_myStartCursorIndex);
	}
	
	private void copySelectionToClipboard() {
		int myStartIndex = _myDecorator.startCursorIndex();
		int myEndIndex = _myDecorator.endCursorIndex();
		
		if(myStartIndex > myEndIndex) {
			int myTmp = myEndIndex;
			myEndIndex = myStartIndex;
			myStartIndex = myTmp;
		}
		
		String myCopiedText = _myDecorator.text().text(myStartIndex - 1, myEndIndex - 1);
		CCClipboard.instance().setData(myCopiedText);
	}
	
	private void copySelectionFromClipboard() {
		append(CCClipboard.instance().getStringData());
	}
	
	private void delete() {
		if(_myDecorator.isInSelection()) {
			removeSelection();
		}else {
			char myDeletedChar = _myDecorator.text().delete(_myStartCursorIndex - 1);
			moveCursorBack();
		}
	}
	
	private void append(char theChar) {
		if(_myDecorator.isInSelection()) {
			removeSelection();
		}
		_myDecorator.text().append(_myStartCursorIndex - 1, theChar+"");
		moveCursorForward();
	}
	
	private void append(String theText) {
		if(_myDecorator.isInSelection()) {
			removeSelection();
		}
		_myDecorator.text().append(_myStartCursorIndex - 1, theText);
		moveCursorForward(theText.length());
	}
}

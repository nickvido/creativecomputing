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
package cc.creativecomputing.cv.openni;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 * 
 */
public class CCOpenNIHand {

	private int _myID;

	private CCVector3f _myPosition = new CCVector3f();
	private List<CCVector3f> _myHistory = new ArrayList<CCVector3f>();
	private int _myHistorySize;

	CCOpenNIHand(int theID, int theHistorySize) {
		_myID = theID;
		_myHistorySize = theHistorySize;
	}

	public void draw(CCGraphics g) {
		g.color(255, 0, 0, 200);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (CCVector3f myPoint : _myHistory) {
			g.vertex(myPoint);
		}

		g.color(255, 0, 0);
		g.strokeWeight(4);
		g.point(_myPosition);
	}

	public CCVector3f position() {
		return _myPosition;
	}
	
	public List<CCVector3f> history(){
		return _myHistory;
	}
	
	public void historySize(int theHistorySize) {
		_myHistorySize = theHistorySize;
		while (_myHistory.size() >= _myHistorySize) { // remove the last point
			_myHistory.remove(_myHistory.size() - 1);
		}
	}
	
	void position(CCVector3f thePosition) {
		_myPosition.set(thePosition);

		_myHistory.add(0, thePosition);
		while (_myHistory.size() > _myHistorySize) { // remove the last point
			_myHistory.remove(_myHistory.size() - 1);
		}
	}
}

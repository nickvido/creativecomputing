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
package cc.creativecomputing.newui.decorator.border;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shape.CCRoundedRectangleOutline;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name="line_border")
public class CCUILineBorderDecorator extends CCUIBorderDecorator{
	
	private CCRoundedRectangleOutline _myRoundedRectangle;
	
	@CCProperty(name = "weight", node = false, optional = true)
	private float _myWeight = 1;
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);
	
	@CCProperty(name = "radius", optional = true)
	private  float _myCornerRadius = 0;

	/**
	 * @param theID
	 */
	public CCUILineBorderDecorator() {
		super("border");
		_myRoundedRectangle = new CCRoundedRectangleOutline();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.ui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.ui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		_myRoundedRectangle.color().set(_myColor);
		_myRoundedRectangle.radius(_myCornerRadius);
		_myRoundedRectangle.position(0, 0);
		_myRoundedRectangle.size(theWidget.width(), theWidget.height());
		_myRoundedRectangle.draw(g);
	}

	
}

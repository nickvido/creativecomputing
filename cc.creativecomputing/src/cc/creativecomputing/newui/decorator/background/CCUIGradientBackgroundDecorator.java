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
package cc.creativecomputing.newui.decorator.background;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.newui.animation.CCProperty;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
@CCPropertyObject(name=CCUIGradientBackgroundDecorator.ID)
public class CCUIGradientBackgroundDecorator extends CCUIBackgroundDecorator{
	
	public final static String ID = "gradient_background";
	
	
	@CCProperty(name = "gradient")
	private CCUIGradient _myGradient = new CCUIGradient();

	/**
	 * @param theID
	 */
	public CCUIGradientBackgroundDecorator() {
		super("gradient");
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		g.beginShape(CCDrawMode.QUADS);
		g.color(_myGradient.leftBottom());
		g.vertex(0,0);
		g.color(_myGradient.rightBottom());
		g.vertex(theWidget.width(), 0);
		g.color(_myGradient.rightTop());
		g.vertex(theWidget.width(), theWidget.height());
		g.color(_myGradient.leftTop());
		g.vertex(0, theWidget.height());
		g.endShape();
	}

}

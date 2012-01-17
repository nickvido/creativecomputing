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
package cc.creativecomputing.demo.font;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CC3DFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CC3DTextTest extends CCApp {
	
	private class Number{
		private CCText _myText;
		private CCVector3f _myTranslation;
		private CCVector3f _myRotationAxis;
		private float _myAngle;
		private float _myScale;
		
		public Number() {
			_myText = new CCText(_myFont);
			_myText.text((int)CCMath.random(100));
			_myText.align(CCTextAlign.CENTER);
			
			_myTranslation = CCVecMath.random3f(CCMath.random(300));
			_myRotationAxis = CCVecMath.random3f();
			_myAngle = CCMath.random(360);
			_myScale = CCMath.random(1);
		}
		
		public void update(final float theDeltaTime) {
			_myAngle += theDeltaTime * 10;
		}
		
		public void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(_myTranslation);
			g.rotate(_myAngle, _myRotationAxis);
			g.scale(_myScale);
			_myText.draw(g);
			g.popMatrix();
		}
	}
	
	private CC3DFont _myFont;
	private CCArcball _myArcball;
    
    private List<Number> _myNumbers = new ArrayList<Number>();

	@Override
	public void setup() {
		_myFont = CCFontIO.create3DFont("Helvetica", 50, 10);
		
		_myArcball = new CCArcball(this);
		
		for(int i = 0; i < 500;i++) {
			_myNumbers.add(new Number());
		}
	}

	@Override
	public void update(final float theDeltaTime) {
		for(Number myNumber:_myNumbers) {
			myNumber.update(theDeltaTime);
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		for(Number myNumber:_myNumbers) {
			myNumber.draw(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CC3DTextTest.class);
		myManager.settings().size(1000, 700);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


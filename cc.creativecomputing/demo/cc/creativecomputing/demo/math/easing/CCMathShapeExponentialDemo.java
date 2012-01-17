package cc.creativecomputing.demo.math.easing;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;

public class CCMathShapeExponentialDemo extends CCApp{

	@CCControl(name = "breakpoint", min = 0f, max = 1)
	private float _cBreakPoint = 1;
	
	@CCControl(name = "exponent", min = 0.1f, max = 10)
	private float _cExponent = 1;

	@Override
	public void setup() {
		frameRate(30);
		
		addControls("app", "app", this);
		showControls();
		_myUI.drawBackground(false);
	}

	@Override
	public void draw() {
		g.clear();
		
		g.translate(-200, -200);
		g.scale(400);
		g.color(1f);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float a = 0; a < 1; a +=0.01f){
			g.vertex(a, CCMath.shapeExponential(a, _cBreakPoint, _cExponent));
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMathShapeExponentialDemo.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

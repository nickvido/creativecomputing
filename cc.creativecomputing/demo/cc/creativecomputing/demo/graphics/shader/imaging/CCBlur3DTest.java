package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.math.util.CCArcball;

public class CCBlur3DTest extends CCApp {
	
	public final static float MAXIMUM_BLUR_RADIUS = 50;
	
	@CCControl(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	private CCArcball _myArcball;

	public void setup() {
		addControls("blur", "blur", this);

		_myBlur = new CCGPUSeperateGaussianBlur(g,20);
		
		_myArcball = new CCArcball(this);
	}
	float _myTime = 0;
	public void update(final float theTime){
		_myTime += theTime;
		_myBlur.radius(_cBlurRadius);
	}

	public void draw() {
		g.color(255);
		g.clear();
		
		_myBlur.beginDraw(g);
		g.clear();
		_myArcball.draw(g);
		g.box(300);
		_myBlur.endDraw(g);
		
		g.color(0);
		g.text(frameRate,0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlur3DTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

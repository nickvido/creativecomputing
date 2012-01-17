package cc.creativecomputing.demo.graphics.shader.filteredlines;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

public class CCGPULineAntiAliasingTest extends CCApp {
	
	private CCGPULineAntiAliasing _myLineAliasing;
	
	@CCControlClass(name = "line")
	private static class CCLineControls{
		@CCControl(name = "radius", min = 0, max = 20)
		private static float radius = 0;
	}

	@Override
	public void setup() {
		addControls(CCGPULineAntiAliasingTest.class);
		_myLineAliasing = new CCGPULineAntiAliasing(this);
		_myLineAliasing.radius(10);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myLineAliasing.radius(CCLineControls.radius);
	}

	
	
	@Override
	public void draw() {
		g.clear();
		_myLineAliasing.begin();
		g.color(255);
		g.blend(CCBlendMode.ADD);
		g.beginShape(CCDrawMode.TRIANGLES);
		for(int i = 0; i < 360;i+=3) {
			float x = CCMath.sin(CCMath.radians(i)) * 200;
			float y = CCMath.cos(CCMath.radians(i)) * 200;
			_myLineAliasing.drawLine(new CCVector3f(0,0,0), new CCVector3f(x,y,0), 10, 1);
		}
		g.endShape();
		_myLineAliasing.end();
		
		g.image(_myLineAliasing.filterTexture(), 0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPULineAntiAliasingTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

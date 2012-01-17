package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics.CCStencilFunction;
import cc.creativecomputing.graphics.CCGraphics.CCStencilOperation;

public class CCStencilMaskDemo extends CCApp {

	@Override
	public void setup() {
		g.clearStencil(0);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clearStencil(0);
		g.clear();
		
		g.stencilTest();
		g.stencilFunc(CCStencilFunction.NEVER, 1, 1);
		g.stencilOperation(CCStencilOperation.REPLACE);
		
		g.noDepthTest();
		g.ellipse(0,0, 200,200);
		
		g.depthTest();
		g.stencilFunc(CCStencilFunction.EQUAL, 1, 1);
		g.stencilOperation(CCStencilOperation.KEEP);
		
		g.rect(0, 0, 200, 200);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCStencilMaskDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().stencilBits(8);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


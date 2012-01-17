package cc.creativecomputing.demo.graphics;

 

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCMaskDemo extends CCApp {
	
	@Override
	public void setup() {
	}

	@Override
	public void draw() {
		g.clear();
		
		g.beginMask();
		g.rect(0,0,100,100);
		g.endMask();
		
		g.ellipse(0,0, 200);
	}

	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMaskDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCInfoTest extends CCApp {

	@Override
	public void setup() {
		System.out.println("VENDOR:"+g.vendor());
		System.out.println("RENDERER:"+g.renderer());
		System.out.println("VERSION:"+g.version());
		
		for(String myExtension:g.extensions()){
			System.out.println(myExtension);
		}
	}

	@Override
	public void draw() {
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCInfoTest.class);
		myManager.start();
	}
}

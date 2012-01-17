package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;

public class CCUIFloatMethodTest extends CCApp {

	@Override
	public void setup() {
		addControls("test","test", this);
	}
	
	@CCControl(name = "print", min=0, max = 10)
	public void printValue(final float theValue){
		System.out.println(theValue);
	}

	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIFloatMethodTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

package cc.creativecomputing.control.ui.test;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;

public class CCUIBooleanMethodTest extends CCApp {

	@Override
	public void setup() {
		addControls("test", "test", this);
	}
	
	@CCControl(name = "print toggle", toggle = true)
	public void printValue(final boolean theValue){
		System.out.println(theValue);
	}
	
	@CCControl(name = "print bang", toggle = false)
	public void printValue(){
		System.out.println("bang");
	}

	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIBooleanMethodTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

package cc.creativecomputing.demo;

import javax.media.opengl.GLProfile;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCAppTest extends CCApp{

	@Override
	public void setup(){
		System.out.println("Vendor:"+g.vendor());
		System.out.println("Renderer:" + g.renderer());
		System.out.println("Version:" + g.version());
		System.out.println(
		GLProfile.isGL2ES2Available());
		System.out.println(
				GLProfile.isGL3Available());
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAppTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}

}

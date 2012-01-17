package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCFog;


public class CCFogTest extends CCApp{
	
// The number of control points for this curve

	int nNumPoints = 4;

	private CCFog _myFog;

	public void setup(){
		_myFog = new CCFog(this,400,800);
		g.clearColor(25, 0, 255);
	}
	
	public void draw(){
		g.clear();
		
		float z = mouseX/(float)width * -400-50;
		
		System.out.println(z);
		_myFog.begin();
		_myFog.color(255, 0, 25);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -200, z);
		g.vertex(200, -200, z);
		g.vertex(200, 200, z);
		g.vertex(-200, 200, z);
		g.endShape();
		_myFog.end();
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCFogTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

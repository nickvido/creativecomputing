package cc.creativecomputing.demo.graphics.mesh;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.util.CCArcball;


public class CCMeshTest extends CCApp {
	
	private CCMesh _myMesh;
	private CCArcball _myArcball;

	public void setup() {
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, 12);
		
		for(int i = 0; i < 12;i++){
			_myMesh.addVertex(CCVecMath.random(-100,100,-100,100,-100,100));
//			_myMesh.addColor(CCMath.random(), CCMath.random(), CCMath.random());
		}
		
		_myArcball = new CCArcball(this);
	}

	public void draw() {
		_myArcball.draw(g);
		g.clear();
		_myMesh.draw(g);
		
//		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCMeshTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}

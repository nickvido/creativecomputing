package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.font.text.CCTextureMapMeshText;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCTextureMeshMapTextTest extends CCApp {
	
	private CCTextureMapMeshText _myText;
	private CCArcball _myArcball;
	
	public void setup() {
		_myText = new CCTextureMapMeshText("Arial", 20);
		_myText.font().texture().textureMipmapFilter(CCTextureMipmapFilter.NEAREST);
		
		for(int i = 0; i < 100000;i++) {
			_myText.addText(i+"", CCVecMath.random3f(CCMath.random(10000)));
		}
		_myText.updateMesh();
		
		_myArcball = new CCArcball(this);
	}

	public void draw() {
		g.clear();
		g.color(255,150);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		g.translate(0, 0, -2000);
		_myArcball.draw(g);
		_myText.draw(g);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCTextureMeshMapTextTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}

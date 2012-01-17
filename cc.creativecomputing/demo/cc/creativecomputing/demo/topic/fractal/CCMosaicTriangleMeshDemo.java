package cc.creativecomputing.demo.topic.fractal;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.d.CCTriangle2d;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.util.CCArcball;

public class CCMosaicTriangleMeshDemo extends CCApp {
	
	

	private CCArcball _myArcball;
	@CCControl(name = "mosaic")
	private CCMosaicTriangleMesh _myParticleTriangleMesh;
	
	@CCControl (name = "texture", min = 0, max = 10)
	private int _cTextureIndex = 0;
	
	private List<CCTexture2D> _myTextures = new ArrayList<CCTexture2D>();

	@Override
	public void setup() {

		frameRate(60);

		_myUI.drawBackground(false);
		
		_myArcball = new CCArcball(this);
		g.clearColor(0.2f, 0.2f, 0.2f);
		
		int myColumns = 10;
		
		float myEdgelength = width / (float)myColumns;
		float myEdgeScale = myEdgelength / CCMath.SQRT3;
		float myTriangleHeight = myEdgelength / 2 * CCMath.SQRT3;
		float _myShortCenter = myEdgelength / 2 * CCMath.tan(CCMath.radians(30));
		float myLongCenter = myTriangleHeight - _myShortCenter;
		
		List<CCTriangle2d> myTriangles = new ArrayList<CCTriangle2d>();
		
		int myRows = CCMath.ceil(height / myTriangleHeight);
		
		for(int myColumn = 0; myColumn < 10; myColumn++) {
			for(int myRow = 0; myRow < myRows;myRow++) {
				CCVector2d myOrigin0;
				CCVector2d myOrigin1;
				CCVector2d myOrigin2;

				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians( -30)), CCMath.sin(CCMath.radians( -30))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-150)), CCMath.sin(CCMath.radians(-150))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians(-270)), CCMath.sin(CCMath.radians(-270))).scale(myEdgeScale);
					
				CCVector2d myTranslation = new CCVector2d(
					(myColumn + 0.5f) * myEdgelength,
					myRow * myTriangleHeight + _myShortCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
				
				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians(-210)), CCMath.sin(CCMath.radians(-210))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-330)), CCMath.sin(CCMath.radians(-330))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians( -90)), CCMath.sin(CCMath.radians( -90))).scale(myEdgeScale);
					
				myTranslation = new CCVector2d(
					(myColumn + 1) * myEdgelength,
					myRow * myTriangleHeight + myLongCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
			}
		}
		
		for(CCTriangle2d myTriangle:myTriangles){
			System.out.println(myTriangle);
		}
		
		_myParticleTriangleMesh = new CCMosaicTriangleMesh(g, myTriangles, 6);
		_myParticleTriangleMesh.textureSize(width, height);
		for(CCTextureData myData:CCTextureIO.newTextureDatas("textures/gradients")){
			_myTextures.add(new CCTexture2D(myData));
		}
			
		addControls("mosaic", "demo",0, this);
	}

	@Override
	public void update(final float theDeltaTime) {
		if(_cTextureIndex < _myTextures.size() - 1){
			_myParticleTriangleMesh.texture0(_myTextures.get(_cTextureIndex));
			_myParticleTriangleMesh.texture1(_myTextures.get(_cTextureIndex + 1));
		}
	}
	
	@Override
	public void draw() {
		g.clearColor(40, 40, 40);
		g.clear();
		
		g.pushMatrix();
		_myArcball.draw(g);
				
		g.color(255,0,0);
			
	
		g.translate(-width/2, -height/2);

		// draw the "real" triangles with its full volume
		g.blend();
//		_myTriangleManager.draw(g);
		_myParticleTriangleMesh.draw(g);

		g.popMatrix();
		g.color(255);
//		g.image(_myTriangleManager.forceField().texture(), -_myVisual.heightMap().width()/2,-_myVisual.heightMap().height()/2);
//		g.image(_myParticleTriangleMesh.forceBlendTexture(),0,0);
		g.blend();
		
//		g.image(_myTextureVisual.renderTexture(), 0,0);
	}
	
	int _myTrixel = 0;
	
	public void keyPressed(CCKeyEvent theKeyEvent){
		super.keyPressed(theKeyEvent);
		
		switch(theKeyEvent.keyCode()){
		case CCKeyEvent.VK_S:
			CCScreenCapture.capture("export/trixels/trixel_" + _myTrixel+".png", width, height);
			_myTrixel++;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMosaicTriangleMeshDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().fov(20);
		myManager.settings().display(1);
		myManager.start();
	}
}

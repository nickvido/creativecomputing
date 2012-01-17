package cc.creativecomputing.demo.model;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCLight;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.CCContent3dIO;
import cc.creativecomputing.model.CCGroup;
import cc.creativecomputing.model.CCModel;


public class CCOBJDemo extends CCApp {
	
	private CCModel _myModel;
	
	private CCArcball _myArcball;
	private CCLight _myLight;
    private CCMaterial _myMaterial;
    
    private List<CCGroup> _myGroups;
    private int _myIndex = 0;
    
	@Override
	public void setup() {
		// frameRate(30);
		_myModel = CCContent3dIO.createModel("demo/model/bla.obj");
		_myModel.center();
//		_myModel.scale(1000);
		_myModel.convert();
		System.out.println(_myModel.boundingBox());
		_myGroups = new ArrayList<CCGroup>(_myModel.groupMap().values());
		for(CCGroup myGroup:_myGroups) {
			myGroup.isActiv(false);
		}
		
		_myArcball = new CCArcball(this);
		
		g.clearColor(0.5f);
		// setup lighting
        g.lights();
		_myLight = new CCDirectionalLight(1f,1f,1f,0f,2f,0f);
//		myLight.specular(0, 0.5f, 1);
		g.light(_myLight);
		_myLight = new CCDirectionalLight(1f,1f,1f,2f,-2f,0f);
//		myLight.specular(0, 0.5f, 1);
		g.light(_myLight);
//		g.colorMaterial(CCGraphics.OFF);
		g.lightModelLocalViewer(true);
		
		_myMaterial = new CCMaterial();
		_myMaterial.ambient(255, 0, 0);
		_myMaterial.diffuse(255, 0, 0);
		_myMaterial.specular(0,75,45);
		
		System.out.println(_myGroups.size());
	}
	
	long time = System.currentTimeMillis();

	public void draw() {
		g.clear();
		g.color(255);
		g.pushMatrix();
		_myArcball.draw(g);
//		g.material(_myMaterial);
		_myModel.draw(g);

		g.popMatrix();
	}
	
	public void keyPressed(CCKeyEvent theKeyEvent) {
		_myGroups.get(_myIndex).isActiv(false);
		switch(theKeyEvent.keyCode()) {
		case CCKeyEvent.VK_UP:
			_myIndex++;
			_myIndex %= _myGroups.size();
			break;
		case CCKeyEvent.VK_DOWN:
			if(_myIndex < 0) {
				_myIndex += _myGroups.size();
			}
			break;
		}
		_myGroups.get(_myIndex).isActiv(true);
	};

	boolean bTexture = true;
	boolean bStroke = true;


	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCOBJDemo.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}

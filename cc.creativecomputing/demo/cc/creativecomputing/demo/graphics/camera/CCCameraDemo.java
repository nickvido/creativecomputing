package cc.creativecomputing.demo.graphics.camera;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCCameraDemo extends CCApp {
	
	@CCControl(name = "camera near", min = 0, max = 10000)
	private float _cCameraNear = 10;
	
	@CCControl(name = "camera far", min = 0, max = 10000)
	private float _cCameraFar = 1000;
	
	@CCControl(name = "frustum offset x", min = -500, max = 500)
	private float _cFrustumOffsetX = 0;
	
	@CCControl(name = "frustum offset y", min = -500, max = 500)
	private float _cFrustumOffsetY = 0;
	
	@CCControl(name = "x rotation", min = -CCMath.HALF_PI, max = CCMath.HALF_PI)
	private float _cCameraXrotation = 0;
	
	@CCControl(name = "y rotation", min = 0, max = CCMath.TWO_PI)
	private float _cCameraYrotation = 0;
	
	@CCControl(name = "z rotation", min = 0, max = CCMath.TWO_PI)
	private float _cCameraZrotation = 0;
	
	
	@CCControl(name = "fov", min = 0, max = CCMath.PI)
	private float _cCameraFov = CCMath.radians(60);
	
	private CCCamera _myCamera;
	private CCFrustum _myFrustum;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myCamera = new CCCamera(g);
		_myFrustum = new CCFrustum(_myCamera);
		
		_myArcball = new CCArcball(this);
		
		addControls("camera", "camera", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myCamera.near(_cCameraNear);
		_myCamera.far(_cCameraFar);
		
		_myCamera.frustumOffset().x = _cFrustumOffsetX;
		_myCamera.frustumOffset().y = _cFrustumOffsetY;
		
		_myCamera.xRotation(_cCameraXrotation);
		_myCamera.yRotation(_cCameraYrotation);
		_myCamera.zRotation(_cCameraZrotation);
		
		_myCamera.fov(_cCameraFov);
		
		_myFrustum.updateFromCamera();
	}
	
	private boolean _myDrawCamera = false;

	@Override
	public void draw() {
		if(_myDrawCamera) {
			_myCamera.draw(g);
		}else {
			_myArcball.draw(g);
		}
		
		g.clear();
		g.polygonMode(CCPolygonMode.LINE);
		g.box(140);
		
		_myFrustum.drawLines(g);
		_myFrustum.drawNormals(g);
		_myFrustum.drawPoints(g);
		g.polygonMode(CCPolygonMode.FILL);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.getKeyCode()) {
		case CCKeyEvent.VK_C:
			_myDrawCamera = !_myDrawCamera;
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCameraDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

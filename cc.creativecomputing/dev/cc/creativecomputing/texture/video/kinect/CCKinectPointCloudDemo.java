package cc.creativecomputing.texture.video.kinect;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.CCVector3f;

public class CCKinectPointCloudDemo extends CCApp {
	
	@CCControl(name = "tilt", min = -31, max = 31)
	private float _cTilt = 0;
	
	@CCControl(name = "average")
	private boolean _cAverage = false;
	
	@CCControl(name = "angle", min = 0, max = 360)
	private float _cAngle = 0;

	// Kinect Library object
	private CCKinect _myKinect;


	public void setup() {
		_myKinect = new CCKinect(this);
		_myKinect.start();
		_myKinect.isDepthActive(true);
		_myKinect.isColorActive(true);
		
		addControls("app", "app", this);

		g.textFont(CCFontIO.createTextureMapFont("arial", 24));
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myKinect.depthData().averageData(_cAverage);
		_myKinect.tilt(_cTilt);
	}

	public void draw() {

		g.clear();
		g.color(255);
		g.text("Kinect FR: " + (int) _myKinect.depthFrameRate() + "\nProcessing FR: " + (int) frameRate, 10, 16);

		// We're just going to calculate and draw every 4th pixel (equivalent of 160x120)
		int skip = 2;

		// Translate and rotate
		g.translate(0, 0, -50);
		g.rotateY(_cAngle);

		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < CCKinect.DEVICE_WIDTH; x += skip) {
			for (int y = 0; y < CCKinect.DEVICE_HEIGHT; y += skip) {
				int offset = x + y * CCKinect.DEVICE_WIDTH;

				// Convert kinect data to world xyz coordinate
				int rawDepth = _myKinect.depthData().depth(x, y);
				CCVector3f myWorldCoords = CCKinectUtil.depthToWorld(x, y, rawDepth);

				g.color(255);
				// Scale up by 200
				float factor = 200;
				g.vertex(myWorldCoords.x * factor, myWorldCoords.y * factor, factor - myWorldCoords.z * factor);

			}
		}
		g.endShape();

	}

	public static void main(String args[]) {
		CCApplicationManager myManager = new CCApplicationManager(CCKinectPointCloudDemo.class);
		myManager.settings().size(800, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

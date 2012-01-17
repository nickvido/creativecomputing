package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserJoint;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserJointType;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.protocol.midi.CCController;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCMidiOut;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCMidiOpenNIDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	private CCOpenNIRenderer _myRenderer;
	
	private CCMidiIO _myMidiIO;
	private CCMidiOut _myMidiOut;
	
	private enum CCArmAxisMode{
		POSITIVE, NEGATIVE, POSITIVE_NEGATIVE
	}
	
	private class CCAxisControls{
		@CCControl(name = "axis to controller", min = 0, max = 127)
		private int _cController = 0;
		
		@CCControl(name = "axis mode", min = 0, max = 2)
		private int _cAxisMode = 0;
		
		@CCControl(name = "invert")
		private boolean _cAxisInvert = false;
		
		@CCControl(name = "min", min = 0, max = 127)
		private int _cValueMin = 0;
		
		@CCControl(name = "max", min = 0, max = 127)
		private int _cValueMax = 127;
		
		@CCControl(name = "active")
		private boolean _cActive = true;
		
		private float calcValue(float theValue, float theMax) {
			if(!_cActive)return 0;
			float myValue = theValue / theMax;
			switch(_cAxisMode) {
			case 0:
				myValue = CCMath.max(0, myValue);
				if(_cAxisInvert) {
					myValue = 1 - myValue;
				}
				break;
			case 1:
				myValue = CCMath.min(0, myValue);
				myValue = -1 - myValue;
				myValue += 1;
				break;
			default:
				myValue = -myValue;
				myValue += 1;
				myValue /= 2;
				break;
			}
			int myMidiValue = (int)CCMath.blend(_cValueMin, _cValueMax, myValue);
			_myMidiOut.sendController(_cController, myMidiValue);
			return myValue;
		}
	}
	
	private class CCArmController{
		private float _myMaxDist = 0;
		private float _myX = 0;
		private float _myY = 0;
		private float _myZ = 0;
		
		private CCVector3f _myRelativeHandPosition = new CCVector3f();
		
		private CCUserJointType _myShoulderJoint;
		private CCUserJointType _myElbowJoint;
		private CCUserJointType _myHandJoint;
		
		@CCControl(name = "x axis")
		private CCAxisControls _cXControls = new CCAxisControls();
		
		@CCControl(name = "y axis")
		private CCAxisControls _cYControls = new CCAxisControls();
		
		@CCControl(name = "z axis")
		private CCAxisControls _cZControls = new CCAxisControls();
		
		private CCVector3f _myLastHandPosition = new CCVector3f();
		
		private CCArmController(
			CCUserJointType theShoulderJoint,
			CCUserJointType theElbowJoint,
			CCUserJointType theHandJoint
		) {
			_myShoulderJoint = theShoulderJoint;
			_myElbowJoint = theElbowJoint;
			_myHandJoint = theHandJoint;
		}
		
		public void update(CCOpenNIUser theUser) {
			CCUserJoint myShoulderJoint = theUser.joint(_myShoulderJoint);
			CCUserJoint myElbowJoint = theUser.joint(_myElbowJoint);
			CCUserJoint myHandJoint = theUser.joint(_myHandJoint);
			_myMaxDist = 0;
			_myMaxDist += myShoulderJoint.position().distance(myElbowJoint.position());
			_myMaxDist += myHandJoint.position().distance(myElbowJoint.position());
			
			_myRelativeHandPosition.set(myShoulderJoint.position());
			_myRelativeHandPosition.subtract(myHandJoint.position());
			
			_myRelativeHandPosition.x = _cXControls.calcValue(_myRelativeHandPosition.x, _myMaxDist);
			_myRelativeHandPosition.y = _cYControls.calcValue(_myRelativeHandPosition.y, _myMaxDist);
			_myRelativeHandPosition.z = _cZControls.calcValue(_myRelativeHandPosition.z, _myMaxDist);
		}
		
		public void draw(CCGraphics g) {
			g.color(255);
			g.line(0,0,_myRelativeHandPosition.x * 300, 0);
			g.line(0,20,_myRelativeHandPosition.y * 300, 20);
			g.line(0,40,_myRelativeHandPosition.z * 300, 40);
		}
	}
	
	private CCArmController _myLeftArmController;
	private CCArmController _myRightArmController;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		_myMidiIO = CCMidiIO.getInstance();
		_myMidiOut = _myMidiIO.midiOut(0, 0);

		_myOpenNI = new CCOpenNI(this);
//		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myRenderer = new CCOpenNIRenderer(_myOpenNI); 

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		// enable skeleton generation for all joints
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		g.strokeWeight(3);
		g.perspective(95, width / (float) height, 10, 150000);
		
		_myLeftArmController = new CCArmController(
			CCUserJointType.LEFT_SHOULDER, 
			CCUserJointType.LEFT_ELBOW,
			CCUserJointType.LEFT_HAND
		);
		
		_myRightArmController = new CCArmController(
				CCUserJointType.RIGHT_SHOULDER, 
				CCUserJointType.RIGHT_ELBOW,
				CCUserJointType.RIGHT_HAND
			);
		
		addControls("controls", "left", 0, _myLeftArmController);
		addControls("controls", "right", 1, _myRightArmController);
	}
	
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		if(_myUserGenerator.user().size() <= 0)return;
		
		for(CCOpenNIUser myUser:_myUserGenerator.user()) {
			_myLeftArmController.update(myUser);
			_myRightArmController.update(myUser);
			return;
		}
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		_myArcball.draw(g);
		//
		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myRenderer.drawDepthMesh(g);
		
		g.color(255);
		g.strokeWeight(1);

		g.noDepthTest();
//		_myDepthGenerator.drawDepthMap(g);

		g.color(255, 0, 0);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.draw(g);
		}
		g.popMatrix();
		
		g.clearDepthBuffer();

		g.blend(CCBlendMode.BLEND);
		_myLeftArmController.draw(g);
		g.pushMatrix();
		g.scale(-1);
		_myRightArmController.draw(g);
		g.popMatrix();
//		

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMidiOpenNIDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

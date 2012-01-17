/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.cv.openni;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.OpenNI.DepthGenerator;
import org.OpenNI.Point3D;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointOrientation;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.StatusException;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNIUser {
	
	public static enum CCUserJointType{
		HEAD (SkeletonJoint.HEAD),
		NECK (SkeletonJoint.NECK),
		TORSO (SkeletonJoint.TORSO),
//		WAIST (SkeletonJoint.WAIST),
//		LEFT_COLLAR (SkeletonJoint.LEFT_COLLAR),
		LEFT_SHOULDER (SkeletonJoint.LEFT_SHOULDER),
		LEFT_ELBOW (SkeletonJoint.LEFT_ELBOW),
//		LEFT_WRIST (SkeletonJoint.LEFT_WRIST),
		LEFT_HAND (SkeletonJoint.LEFT_HAND),
//		LEFT_FINGERTIP (SkeletonJoint.LEFT_FINGERTIP),
//		RIGHT_COLLAR (SkeletonJoint.RIGHT_COLLAR),
		RIGHT_SHOULDER (SkeletonJoint.RIGHT_SHOULDER),
		RIGHT_ELBOW (SkeletonJoint.RIGHT_ELBOW),
//		RIGHT_WRIST (SkeletonJoint.RIGHT_WRIST),
		RIGHT_HAND (SkeletonJoint.RIGHT_HAND),
//		RIGHT_FINGERTIP (SkeletonJoint.RIGHT_FINGERTIP),
		LEFT_HIP (SkeletonJoint.LEFT_HIP),
		LEFT_KNEE (SkeletonJoint.LEFT_KNEE),
//		LEFT_ANKLE (SkeletonJoint.LEFT_ANKLE),
		LEFT_FOOT (SkeletonJoint.LEFT_FOOT),
		RIGHT_HIP (SkeletonJoint.RIGHT_HIP),
		RIGHT_KNEE (SkeletonJoint.RIGHT_KNEE),
//		RIGHT_ANKLE (SkeletonJoint.RIGHT_ANKLE),
		RIGHT_FOOT (SkeletonJoint.RIGHT_FOOT);
			
		private SkeletonJoint _myJoint;
		
		CCUserJointType(SkeletonJoint theJoint){
			_myJoint = theJoint;
		}
	}
	
	public class CCUserLimb{
		private CCUserJoint _myJoint1;
		private CCUserJoint _myJoint2;
		
		public CCUserLimb(CCUserJoint theUserJoint1, CCUserJoint theUserJoint2) {
			_myJoint1 = theUserJoint1;
			_myJoint2 = theUserJoint2;
		}
		
		public CCUserLimb(CCUserJointType theUserJoint1, CCUserJointType theUserJoint2) {
			_myJoint1 = _myJointMap.get(theUserJoint1);
			_myJoint2 = _myJointMap.get(theUserJoint2);
		}
		
		public CCUserJoint joint1() {
			return _myJoint1;
		}
		
		public CCUserJoint joint2() {
			return _myJoint2;
		}
		
		public void draw(CCGraphics g) {

			// draw the joint position

			g.vertex(_myJoint1._myPosition);
			g.vertex(_myJoint2._myPosition);

//			drawJointOrientation(g, myJoint1, 50);
		}
		
		/**
		 * Draws a limb from joint1 to joint2
		 * 
		 * @param userId int
		 * @param joint1 int
		 * @param joint2 int
		 */
		public void draw2D(CCGraphics g) {

			g.vertex(_myJoint1.position2D());
			g.vertex(_myJoint2.position2D());
		}
	}
	
	public class CCUserJoint{
		
		private CCMatrix4f _myOrientation;
		private float _myOrientationConfidence;
		
		private CCVector3f _myPosition;
		private CCVector3f _myPosition2D;
		private float _myPositionConfidence;
		
		private CCUserJointType _myType;
		
		public CCUserJoint(CCUserJointType theType) {
			_myOrientation = new CCMatrix4f();
			_myType = theType;
			_myPosition = new CCVector3f();
			_myPosition2D = new CCVector3f();
		}
		
		private void update() {
			try {
	
				// set the matrix by hand, openNI matrix is only 3*3(only rotation, no translation)
//				SkeletonJointOrientation myOrientation = _mySkeletonCapability.getSkeletonJointOrientation(_myID, _myType._myJoint);
//				_myOrientation.set(
//					myOrientation.getX1(), myOrientation.getX2(), myOrientation.getX3(), 0, 
//					myOrientation.getY1(), myOrientation.getY2(), myOrientation.getY3(), 0, 
//					myOrientation.getZ1(), myOrientation.getZ2(), myOrientation.getZ3(), 0, 
//					0, 0, 0, 1
//				);
//	
//				_myOrientationConfidence =  myOrientation.getConfidence();
	
				SkeletonJointPosition myPosition = _mySkeletonCapability.getSkeletonJointPosition(_myID, _myType._myJoint);
				_myPosition.set(
					myPosition.getPosition().getX(), 
					myPosition.getPosition().getY(), 
					myPosition.getPosition().getZ()
				);
				
				Point3D myPoint2d = _myDepthGenerator.convertRealWorldToProjective(myPosition.getPosition());
				 _myPosition2D = new CCVector3f(myPoint2d.getX(), myPoint2d.getY(), myPoint2d.getZ());
				_myPositionConfidence =  myPosition.getConfidence();
				
			} catch (StatusException e) {
//				throw new CCOpenNIException(e);
			}
//			

			
		}
		
		public CCMatrix4f orientation() {
			return _myOrientation;
		}
		
		public float orientationConfidence() {
			return _myOrientationConfidence;
		}
		
		public CCVector3f position() {
			return _myPosition;
		}
		
		public CCVector3f position2D() {
			return _myPosition2D;
		}
		
		public float positionConfidence() {
			return _myPositionConfidence;
		}
		
		public CCUserJointType type() {
			return _myType;
		}
	}

	private SkeletonCapability _mySkeletonCapability;
	private DepthGenerator _myDepthGenerator;
	
	private int _myID;

	private ShortBuffer _myUserRaw;
	
	private boolean _myIsCalibrated = false;
	private boolean _myIsTracking = false;
	
	private boolean _myUpdatePixels = false;
	
	private CCVector3f _myCenterOfMass = new CCVector3f();
	
	private Map<CCUserJointType, CCUserJoint>_myJointMap = new HashMap<CCUserJointType, CCUserJoint>();
	
	private List<CCUserLimb> _myLimbs = new ArrayList<CCUserLimb>();
	
	CCOpenNIUser(SkeletonCapability theOpenNI, DepthGenerator theDepthGenerator, final int theID){
		_mySkeletonCapability = theOpenNI;
		_myDepthGenerator = theDepthGenerator;
		
		_myID = theID;
		for(CCUserJointType myType:CCUserJointType.values()) {
			if(_mySkeletonCapability.isJointActive(myType._myJoint))_myJointMap.put(myType, new CCUserJoint(myType));
		}
		
		_myLimbs.add(new CCUserLimb(CCUserJointType.HEAD, CCUserJointType.NECK));

		_myLimbs.add(new CCUserLimb(CCUserJointType.NECK, CCUserJointType.LEFT_SHOULDER));
		_myLimbs.add(new CCUserLimb(CCUserJointType.LEFT_SHOULDER, CCUserJointType.LEFT_ELBOW));
		_myLimbs.add(new CCUserLimb(CCUserJointType.LEFT_ELBOW, CCUserJointType.LEFT_HAND));

		_myLimbs.add(new CCUserLimb(CCUserJointType.NECK, CCUserJointType.RIGHT_SHOULDER));
		_myLimbs.add(new CCUserLimb(CCUserJointType.RIGHT_SHOULDER, CCUserJointType.RIGHT_ELBOW));
		_myLimbs.add(new CCUserLimb(CCUserJointType.RIGHT_ELBOW, CCUserJointType.RIGHT_HAND));

		_myLimbs.add(new CCUserLimb(CCUserJointType.LEFT_SHOULDER, CCUserJointType.TORSO));
		_myLimbs.add(new CCUserLimb(CCUserJointType.RIGHT_SHOULDER, CCUserJointType.TORSO));

		_myLimbs.add(new CCUserLimb(CCUserJointType.TORSO, CCUserJointType.LEFT_HIP));
		_myLimbs.add(new CCUserLimb(CCUserJointType.LEFT_HIP, CCUserJointType.LEFT_KNEE));
		_myLimbs.add(new CCUserLimb(CCUserJointType.LEFT_KNEE, CCUserJointType.LEFT_FOOT));

		_myLimbs.add(new CCUserLimb(CCUserJointType.TORSO, CCUserJointType.RIGHT_HIP));
		_myLimbs.add(new CCUserLimb(CCUserJointType.RIGHT_HIP, CCUserJointType.RIGHT_KNEE));
		_myLimbs.add(new CCUserLimb(CCUserJointType.RIGHT_KNEE, CCUserJointType.RIGHT_FOOT));
	}
	
	public List<CCUserLimb> limbs(){
		return _myLimbs;
	}
	
	public int id() {
		return _myID;
	}
	
	public void updatePixels(final boolean theUpdatePixels) {
		_myUpdatePixels = theUpdatePixels;
	}
	
	public void update() {
		_myIsCalibrated = _mySkeletonCapability.isSkeletonCalibrated(_myID);
		_myIsTracking = _mySkeletonCapability.isSkeletonTracking(_myID);
		
		if(!needsUpdate())return;
		
		
		for(CCUserJoint myJoint:_myJointMap.values()) {
			myJoint.update();
		}
	}
	
	boolean needsUpdate() {
		return _myIsCalibrated && _myIsTracking;
	}
	
	public ShortBuffer raw() {
		return _myUserRaw;
	}
	
	void raw(ShortBuffer theUserRaw) {
		_myUserRaw = theUserRaw;
	}
	
	public CCVector3f centerOfMass() {
		return _myCenterOfMass;
	}
	
	/**
	 * gets the orientation of a joint
	 * 
	 * @param userId int
	 * @param joint int
	 * @param jointOrientation PMatrix3D
	 * @return The confidence of this joint float
	 */
	public CCUserJoint joint(CCUserJointType theType) {
		return _myJointMap.get(theType);
	}
	
	
	
	// draw the skeleton with the selected joints
	public void draw2D(CCGraphics g) {
		g.beginShape(CCDrawMode.LINES);
		for(CCUserLimb myLimb:_myLimbs) {
			myLimb.draw2D(g);
		}
		g.endShape();
	}

	

	public void drawJointOrientation(CCGraphics g, CCUserJoint theJoint, float length) {
		// draw the joint orientation

		if (theJoint.orientationConfidence() < 0.001f)
			// nothing to draw, orientation data is useless
			return;

		g.pushMatrix();
		g.translate(theJoint.position());

		// set the local coordsys
		g.applyMatrix(theJoint.orientation());

		// coordsys lines are 100mm long
		// x - r
		g.color(1f, 0f, 0f, theJoint._myOrientationConfidence * 0.8f + 0.2f);
		g.line(0, 0, 0, length, 0, 0);
		// y - g
		g.color(0f, 1f, 0f, theJoint._myOrientationConfidence * 0.8f + 0.2f);
		g.line(0, 0, 0, 0, length, 0);
		// z - b
		g.color(0, 0, 255, theJoint._myOrientationConfidence * 0.8f + 0.2f);
		g.line(0, 0, 0, 0, 0, length);
		g.popMatrix();
	}

	// draw the skeleton with the selected joints
	public void draw(CCGraphics g) {
		
		g.beginShape(CCDrawMode.LINES);
		for(CCUserLimb myLimb:_myLimbs) {
			myLimb.draw(g);
		}
		g.endShape();

	}
}

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.CalibrationStartEventArgs;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.SceneMetaData;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.cv.openni.CCOpenNIUser.CCUserJoint;
import cc.creativecomputing.util.logging.CCLog;

/**
 * An object that generates data relating to a figure in the scene.
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUserGenerator extends CCOpenNIGenerator<UserGenerator>{
	
	class NewUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onNewUser - userId: " + theArgs.getId());
			if(CCOpenNI.DEBUG)CCLog.info("  start pose detection");

			try {
				if(_mySkeletonCapability.needPoseForCalibration()) {
					_myPoseDetectionCapability.StartPoseDetection(_myCalibrationPose, theArgs.getId());
				}else {
					_mySkeletonCapability.requestSkeletonCalibration(theArgs.getId(), true);
				}
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}

	class LostUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onLostUser - userId: " + theArgs.getId());
			CCOpenNIUser myUser = _myUserMap.remove(theArgs.getId());
			_myEvents.proxy().onLostUser(myUser);
		}
	}
	
	class CalibrationStartObserver implements IObserver<CalibrationStartEventArgs> {
		@Override
		public void update(IObservable<CalibrationStartEventArgs> theObservable, CalibrationStartEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onStartCalibration - userId: " + theArgs.getUser());
		}
	}
	
	class CalibrationProgressObserver implements IObserver<CalibrationProgressEventArgs> {

		@Override
		public void update(IObservable<CalibrationProgressEventArgs> theObservable, CalibrationProgressEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onUpdateCalibration - userId: " + theArgs.getUser() + ", status: " + theArgs.getStatus());
		}
	}

	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs> {
		@Override
		public void update(IObservable<CalibrationProgressEventArgs> observable, CalibrationProgressEventArgs theArgs) {
			boolean sucessful = theArgs.getStatus() == CalibrationProgressStatus.OK;
			int myUserID = theArgs.getUser();
			if(CCOpenNI.DEBUG)CCLog.info("onEndCalibration - userId: " + myUserID + ", successfull: " + sucessful);

			if (sucessful) {
				if(CCOpenNI.DEBUG)CCLog.info("  User calibrated !!!");
				try {
					_mySkeletonCapability.startTracking(myUserID);
				} catch (StatusException e) {
					throw new CCOpenNIException(e);
				}
				CCOpenNIUser myUser = new CCOpenNIUser(_mySkeletonCapability, _myDepthGenerator, myUserID);
				myUser.updatePixels(_myUpdatePixels);
				
				_myEvents.proxy().onNewUser(myUser);
				
				_myUserMap.put(myUserID, myUser);
			} else {
				if(CCOpenNI.DEBUG)CCLog.info("  Failed to calibrate user !!!");
				if(CCOpenNI.DEBUG)CCLog.info("  Start pose detection");
				try {
					_myPoseDetectionCapability.StartPoseDetection(_myCalibrationPose, myUserID);
				} catch (StatusException e) {
					throw new CCOpenNIException(e);
				}
			}
		}
	}
	

	class OutOfPoseObserver implements IObserver<PoseDetectionEventArgs> {

		@Override
		public void update(IObservable<PoseDetectionEventArgs> theObservable, PoseDetectionEventArgs theArgs) {
			CCLog.info("onEndPose - userId: " + theArgs.getUser() + ", pose: " + theArgs.getPose());
		}
	}

	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable, PoseDetectionEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onStartPose - userId: " + theArgs.getUser() + ", pose: " + theArgs.getPose());
			if(CCOpenNI.DEBUG)CCLog.info(" stop pose detection");

			try {
				_myPoseDetectionCapability.StopPoseDetection(theArgs.getUser());
				_mySkeletonCapability.requestSkeletonCalibration(theArgs.getUser(), true);
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
	
	public static interface CCUserListener{
		public void onNewUser(CCOpenNIUser theUser);
		public void onLostUser(CCOpenNIUser theUser);
	}
	
	public static enum CCUserSkeletonProfile{
		NONE(SkeletonProfile.NONE),
		ALL(SkeletonProfile.ALL),
		UPPER(SkeletonProfile.UPPER_BODY),
		LOWER(SkeletonProfile.LOWER_BODY),
		HEAD_HANDS (SkeletonProfile.HEAD_HANDS);
		
		private SkeletonProfile _myProfile;
		
		CCUserSkeletonProfile(SkeletonProfile theID){
			_myProfile = theID;
		}
		
		public SkeletonProfile profile() {
			return _myProfile;
		}
	}
	
	private Map<Integer, CCOpenNIUser> _myUserMap = new HashMap<Integer, CCOpenNIUser>();
	
	private CCListenerManager<CCUserListener> _myEvents = new CCListenerManager<CCUserListener>(CCUserListener.class);
	
	private SkeletonCapability _mySkeletonCapability;
	private String _myCalibrationPose;
	private PoseDetectionCapability _myPoseDetectionCapability;
	
	private boolean _myUpdatePixels = false;
	
	private DepthGenerator _myDepthGenerator;

	CCOpenNIUserGenerator(Context theContext) {
		super(theContext);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.openni2.CCOpenNIGenerator#create(org.OpenNI.Context)
	 */
	@Override
	UserGenerator create(Context theContext) {
		try {
			UserGenerator myUserGenerator = UserGenerator.create(theContext);
			_mySkeletonCapability = myUserGenerator.getSkeletonCapability();
			_myCalibrationPose = _mySkeletonCapability.getSkeletonCalibrationPose();
			_myPoseDetectionCapability = myUserGenerator.getPoseDetectionCapability();
			
			myUserGenerator.getNewUserEvent().addObserver(new NewUserObserver());
			myUserGenerator.getLostUserEvent().addObserver(new LostUserObserver());
			
			_mySkeletonCapability.getCalibrationStartEvent().addObserver(new CalibrationStartObserver());
			_mySkeletonCapability.getCalibrationInProgressEvent().addObserver(new CalibrationProgressObserver());
			_mySkeletonCapability.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
			
			_mySkeletonCapability.setSkeletonProfile(SkeletonProfile.ALL);
			
			_myPoseDetectionCapability.getOutOfPoseEvent().addObserver(new OutOfPoseObserver());
			_myPoseDetectionCapability.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
			
			_myDepthGenerator = DepthGenerator.create(theContext);
			
			return myUserGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCListenerManager<CCUserListener> events(){
		return _myEvents;
	}
	
	public void updatePixels(final boolean theUpdatePixels) {
		_myUpdatePixels = theUpdatePixels;
	}

	/**
	 * Save the calibration data to file.
	 * @param user
	 * @param calibrationFile
	 */
	public void saveSkeletonCalibrationDataToFile(int user, String calibrationFile) {
		String path = CCIOUtil.dataPath(calibrationFile);
		CCIOUtil.createPath(path);
		try {
			_mySkeletonCapability.saveSkeletonCalibrationDataToFile(user, path);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}

	/**
	 * Load previously saved calibration data from file.
	 * @param user
	 * @param calibrationFile
	 */
	public void loadCalibrationDataSkeleton(int user, String calibrationFile) {
		String path = CCIOUtil.dataPath(calibrationFile);
		try {
			_mySkeletonCapability.loadSkeletonCalibrationDatadFromFile(user, path);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	void update() {
		for(CCOpenNIUser myUser:_myUserMap.values()) {
			myUser.update();
			
			if(!myUser.needsUpdate())return;
			
			if(_myUpdatePixels) {
				SceneMetaData myMetaData = _myGenerator.getUserPixels(myUser.id());
				myUser.raw(myMetaData.getData().createShortBuffer());
			}
			
			Point3D myCom;
			try {
				myCom = _myGenerator.getUserCoM(myUser.id());
				myUser.centerOfMass().set(myCom.getX(), myCom.getY(), myCom.getZ());
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
	
	public Collection<CCOpenNIUser> user(){
		return _myUserMap.values();
	}
}

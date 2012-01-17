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

import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.GestureProgressEventArgs;
import org.OpenNI.GestureRecognizedEventArgs;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * An object that enables specific body or hand gesture tracking
 * @author christianriekoff
 * 
 */
public class CCOpenNIGestureGenerator extends CCOpenNIGenerator<GestureGenerator>{
	
	public static String WAVE = "Wave";
	public static String CLICK = "Click";
	public static String RAISE_HAND = "RaiseHand";

	private CCOpenNIHandGenerator _myHandGenerator;
	
	private float _myProgress;
	private String _myGesture;
	
	private CCListenerManager<CCOpenNIGestureListener> _myEvents = new CCListenerManager<CCOpenNIGestureListener>(CCOpenNIGestureListener.class);

	/**
	 * @param theOpenNI
	 * @param theHandGenerator
	 */
	public CCOpenNIGestureGenerator(Context theContext) {
		super(theContext);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.openni2.CCOpenNIGenerator#create(org.OpenNI.Context)
	 */
	@Override
	GestureGenerator create(Context theContext) {
		try {
			GestureGenerator myGestureGenerator = GestureGenerator.create(theContext);
			
			myGestureGenerator.getGestureRecognizedEvent().addObserver(new IObserver<GestureRecognizedEventArgs>() {
				
				@Override
				public void update(IObservable<GestureRecognizedEventArgs> theObservable, GestureRecognizedEventArgs theArgs) {
					String myGesture = theArgs.getGesture();
					CCVector3f myIdPosition = convert(theArgs.getIdPosition());
					CCVector3f myEndPosition3D = convert(theArgs.getEndPosition());
					
					CCLog.info("onRecognizeGesture - strGesture: " + myGesture + ", idPosition: " + myIdPosition + ", endPosition:" + myEndPosition3D);
					_myEvents.proxy().onRecognizeGesture(myGesture, myIdPosition, myEndPosition3D);
					_myGesture = myGesture;
				}
			});
			
			myGestureGenerator.getGestureProgressEvent().addObserver(new IObserver<GestureProgressEventArgs>() {

				@Override
				public void update(IObservable<GestureProgressEventArgs> theObservable, GestureProgressEventArgs theArgs) {
					String myGesture = theArgs.getGesture();
					CCVector3f myPosition = convert(theArgs.getPosition());
					float myProgress = theArgs.getProgress();
					
					CCLog.info("onProgressGesture - strGesture: " + myGesture + ", position: " + myPosition + ", progress:" + myProgress);
					_myEvents.proxy().onProgressGesture(myGesture, myPosition, myProgress);
					_myGesture = myGesture;
					_myProgress = myProgress;
				}});
			return myGestureGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCListenerManager<CCOpenNIGestureListener> events(){
		return _myEvents;
	}
	
	/**
	 * Get the names of the gestures that are currently active.
	 * @return
	 */
	public String[] activeGestures() {
		try {
			return _myGenerator.getAllActiveGestures();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Get the names of all gestures available.
	 * @return
	 */
	public String[] enumerateAllGestures() {
		try {
			return _myGenerator.enumerateAllGestures();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Check if a specific gesture is available in this generator.
	 * @param theGesture
	 * @return
	 */
	public boolean isGestureAvailable(String theGesture) {
		return _myGenerator.isGestureAvailable(theGesture);
	}
	
	/**
	 * Check if the specific gesture supports 'in progress' callbacks.
	 * @param theGesture
	 * @return
	 */
	public boolean isGestureProgressSupported(String theGesture) {
		return _myGenerator.isGestureProgressSupported(theGesture);
	}
	
	/**
	 * Turn on a gesture. Once turned on, the generator will start looking for this gesture.
	 * @param theGestures
	 */
	public void addGestures(String...theGestures) {
		for(String theGesture:theGestures) {
			try {
				_myGenerator.addGesture(theGesture);
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
	
	/**
	 * Turn off a gesture. Once turned off, the generator will stop looking for this gesture.
	 * @param theGestures
	 */
	public void removeGestures(String...theGestures) {
		for(String theGesture:theGestures) {
			try {
				_myGenerator.removeGesture(theGesture);
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
}

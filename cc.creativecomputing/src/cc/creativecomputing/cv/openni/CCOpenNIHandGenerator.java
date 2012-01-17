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

import org.OpenNI.ActiveHandEventArgs;
import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.InactiveHandEventArgs;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * A Hands Generator node is a Generator that tracks hand points.
 * @author christianriekoff
 * 
 */
public class CCOpenNIHandGenerator extends CCOpenNIGenerator<HandsGenerator>{
	
	private Map<Integer, CCOpenNIHand> _myHandMap = new HashMap<Integer, CCOpenNIHand>();
	
	private CCListenerManager<CCOpenNIHandListener> _myListenerManager = new CCListenerManager<CCOpenNIHandListener>(CCOpenNIHandListener.class);
	private int _myHistorySize = 0;
	
	public CCOpenNIHandGenerator(Context theContext) {
		super(theContext);
	}

	@Override
	HandsGenerator create(Context theContext) {
		try {
			HandsGenerator myHandGenerator = HandsGenerator.create(theContext);
			myHandGenerator.getHandCreateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {
				
				@Override
				public void update(IObservable<ActiveHandEventArgs> theObservable, ActiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					CCVector3f myPosition = convert(theArgs.getPosition());
					_myListenerManager.proxy().onCreateHands(myID, myPosition, myTime);
					
					CCLog.info("onCreateHands - handId: " + myID + ", pos: " + myPosition + ", time:" + myTime);

					CCOpenNIHand myHand = new CCOpenNIHand(myID, _myHistorySize);
					_myHandMap.put(myID, myHand);
					_myHandMap.get(myID).position(myPosition);
				}
			});
			
			myHandGenerator.getHandUpdateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {

				@Override
				public void update(IObservable<ActiveHandEventArgs> theObservable, ActiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					CCVector3f myPosition = convert(theArgs.getPosition());
					
					_myListenerManager.proxy().onUpdateHands(myID, myPosition, myTime);
					
					CCLog.info("onUpdateHandsCb - handId: " + myID + ", pos: " + myPosition + ", time:" + myTime);
					_myHandMap.get(myID).position(myPosition);
				}
				
			});
			
			myHandGenerator.getHandDestroyEvent().addObserver(new IObserver<InactiveHandEventArgs>() {

				@Override
				public void update(IObservable<InactiveHandEventArgs> theObservable, InactiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					_myListenerManager.proxy().onDestroyHands(myID, myTime);
					
					CCLog.info("onDestroyHandsCb - handId: " + myID + ", time:" + myTime);
					_myHandMap.remove(myID);
				}});
			return myHandGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCListenerManager<CCOpenNIHandListener> events(){
		return _myListenerManager;
	}
	
	/**
	 * Change smoothing factor. Smoothing factor, in the range 0..1. 
	 * 0 Means no smoothing, 1 means infinite smoothing. Inside the 
	 * range is generator dependent.
	 * @param theSmoothing Smoothing factor, in the range 0..1
	 */
	public void smoothing(float theSmoothing) {
		try {
			_myGenerator.SetSmoothing(theSmoothing);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Start tracking at a specific position.
	 * @param thePosition
	 */
	public void startTracking(CCVector3f thePosition) {
		try {
			_myGenerator.StartTracking(convert(thePosition));
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public void historySize(int theHistorySize) {
		_myHistorySize = theHistorySize;
		
		for(CCOpenNIHand myHand:_myHandMap.values()) {
			myHand.historySize(theHistorySize);
		}
	}
	
	public Collection<CCOpenNIHand> hands(){
		return _myHandMap.values();
	}
}

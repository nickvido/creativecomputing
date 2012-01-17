/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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

import java.util.ArrayList;
import java.util.List;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.License;
import org.OpenNI.StatusException;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNI implements Runnable, CCDisposeListener, CCUpdateListener{
	
	static boolean DEBUG = true;
	
	private Context _myContext;
	private Thread _myThread;
	
	private boolean _myIsRunning = true;
	
	private List<CCOpenNIGenerator<?>> _myGenerators = new ArrayList<CCOpenNIGenerator<?>>();

	public CCOpenNI(CCApp theApp) {
		theApp.addUpdateListener(this);
		theApp.addDisposeListener(this);
		
		try {
			_myContext = new Context();
			License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
			_myContext.addLicense(licence); 
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Make sure all generators are generating data.
	 */
	public void start() {
		try {
			_myContext.startGeneratingAll();
			_myThread = new Thread(this);
			_myThread.start();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Stop all generators from generating data.
	 */
	public void stop() {
		try {
			_myContext.stopGeneratingAll();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Sets the global mirror flag. This will set all current existing nodes' mirror state, 
	 * and also affect future created nodes. The default mirror flag is FALSE.
	 * @param theMirror New Mirror state.
	 */
	public void mirror(boolean theMirror) {
		try {
			_myContext.setGlobalMirror(theMirror);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Opens a recording file, adding all nodes in it to the context.
	 * @param theFile The file to open.
	 * @return The created player node.
	 */
	public CCOpenNIPlayer openFileRecording(String theFile) {
		try {
			return new CCOpenNIPlayer(_myContext.openFileRecordingEx(CCIOUtil.dataPath(theFile)));
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private CCOpenNIImageGenerator _myImageGenerator;
	
	public CCOpenNIImageGenerator createImageGenerator() {
		if(_myImageGenerator == null) {
			_myImageGenerator = new CCOpenNIImageGenerator(_myContext);
			_myGenerators.add(_myImageGenerator);
		}
		return _myImageGenerator;
	}
	
	private CCOpenNIIRGenerator _myIRGenerator;
	
	public CCOpenNIIRGenerator createIRGenerator() {
		if(_myIRGenerator == null) {
			_myIRGenerator = new CCOpenNIIRGenerator(_myContext);
			_myGenerators.add(_myIRGenerator);
		}
		return _myIRGenerator;
	}
	
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	public CCOpenNIDepthGenerator createDepthGenerator() {
		if(_myDepthGenerator == null) {
			_myDepthGenerator = new CCOpenNIDepthGenerator(_myContext);
			_myGenerators.add(_myDepthGenerator);
		}
		return _myDepthGenerator;
	}
	
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	
	public CCOpenNISceneAnalyzer createSceneAnalyzer() {
		if(_mySceneAnalyzer == null) {
			_mySceneAnalyzer = new CCOpenNISceneAnalyzer(_myContext);
			_myGenerators.add(_mySceneAnalyzer);
		}
		return _mySceneAnalyzer;
	}
	
	private CCOpenNIHandGenerator _myHandGenerator;
	
	public CCOpenNIHandGenerator createHandGenerator() {
		if(_myHandGenerator == null) {
			_myHandGenerator = new CCOpenNIHandGenerator(_myContext);
			_myGenerators.add(_myHandGenerator);
		}
		return _myHandGenerator;
	}
	
	private CCOpenNIGestureGenerator _myGestureGenerator;
	
	public CCOpenNIGestureGenerator createGestureGenerator() {
		if(_myGestureGenerator == null) {
			_myGestureGenerator = new CCOpenNIGestureGenerator(_myContext);
			_myGenerators.add(_myGestureGenerator);
		}
		return _myGestureGenerator;
	}
	
	private CCOpenNIUserGenerator _myUserGenerator;
	
	public CCOpenNIUserGenerator createUserGenerator() {
		if(_myUserGenerator == null) {
			_myUserGenerator = new CCOpenNIUserGenerator(_myContext);
			_myGenerators.add(_myUserGenerator);
		}
		return _myUserGenerator;
	}
	
	@Override
	public void update(float theDeltaTime) {
		if(!_myIsUpdated)return;
		
		_myIsUpdated = false;
		for(CCOpenNIGenerator<?> myGenerator:_myGenerators) {
			myGenerator.update();
		}
	}
	
	@Override
	public void dispose() {
		_myIsRunning = false;
	}
	
	private long myLastNanos;
	private boolean _myIsUpdated = false;
	
	@Override
	public void run() {
		myLastNanos = System.nanoTime();
		while(_myIsRunning) {
			try {
				_myContext.waitAndUpdateAll();
				long myNewNanos = System.nanoTime();
				long mySpentTime = myNewNanos - myLastNanos;
				myLastNanos = myNewNanos;
				_myIsUpdated = true;
			} catch (StatusException e) {
				
			}
			
		}
	}
	
	/////////////////////////////////////////////////
	//
	// MATH STUFF
	//
	/////////////////////////////////////////////////

	@CCControl(name = "scaleX", min = 500, max = 700)
	public static float scaleX = 594.21434211923247f;
	@CCControl(name = "scaleY", min = 500, max = 700)
	public static float scaleY = 591.04053696870778f;
	
	@CCControl(name = "centerX", min = 300, max = 400)
	public static float centerX = 339.30780975300314f;
	@CCControl(name = "centerY", min = 200, max = 300)
	public static float centerY = 242.73913761751615f;
	
	
}

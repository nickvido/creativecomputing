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

package cc.creativecomputing.input;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCUpdateListener;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;


/**
 * <p>
 * CCInputIO is the base class for using controllers in creative computing.
 * It provides methods to retrieve information about the connected 
 * devices and get the input data from them.<br>
 * On start you should use the printDevices() function to see if all controllers
 * are connected and found.
 * </p>
 * @see CCInputDevice
 */
public class CCInputIO implements CCDisposeListener, CCUpdateListener{
	/**
	 * @invisible
	 */
	public static final int ON_PRESS = 0;
	/**
	 * @invisible
	 */
	public static final int ON_RELEASE = 1;
	/**
	 * @invisible
	 */
	public static final int WHILE_PRESS = 2;

	/**
	 * Ensures that there only exists one instance of CCInputIO
	 */
	static private CCInputIO instance;

	/**
	 * Holds the environment of JInput
	 */
	private final ControllerEnvironment _myEnvironment;

	/**
	 * Instance to the parent application
	 */
	private final CCAbstractApp _myApp;

	/**
	 * List of the available _myDevices
	 */
	private final List<CCInputDevice> _myDevices = new ArrayList<CCInputDevice>();

	/**
	 * Initialize the CCInputIO instance
	 * @param theApp
	 */
	private CCInputIO(final CCAbstractApp theApp){
		_myEnvironment = ControllerEnvironment.getEnvironment();
		_myApp = theApp;
		_myApp.addDisposeListener(this);
		_myApp.addUpdateListener(this);
		setupDevices();
	}
	
	/**
	 * Call this method to initialize CCInput. This typically happens one time
	 * in the setup() method of your app. 
	 * <pre>
	 * public void setup(){
	 * 	CCInputIO.init(this);
	 * }
	 * </pre>
	 * 
	 * @shortdesc Initializes CCInputIO
	 * @param theApp reference to the parent app
	 */
	public static void init(final CCAbstractApp theApp) {
		if (instance == null){
			instance = new CCInputIO(theApp);
		}
	}

	/**
	 * Puts the available devices into the device list
	 */
	private void setupDevices(){
		final Controller[] controllers = _myEnvironment.getControllers();
		for (int i = 0; i < controllers.length; i++){
			_myDevices.add(new CCInputDevice(controllers[i]));
		}
	}

	/**
	 * dispose method called by the app after closing. The update thread is stopped here
	 * @invisible
	 */
	public void dispose(){
	}

	
	private void _printDevices(){
		System.out.println("\n<<< available input devices: >>>\n");
		for (int i = 0; i < _myDevices.size(); i++){
			System.out.print("     "+i+": ");
			System.out.println(_myDevices.get(i).name());
		}
		System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
	
	/**
	 * Lists the available devices in the console window. This method
	 * is useful at start to see if all devices are properly connected
	 * and get the name of the desired device.
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #device(int)
	 */
	public static void printDevices() {
		instance._printDevices();
	}

	
	private int _numberOfDevices(){
		return _myDevices.size();
	}
	
	/**
	 * Returns the number of available devices
	 * @return the number of available devices
	 * @see CCInputDevice
	 * @see #device(int)
	 */
	public static int numberOfDevices() {
		return instance._numberOfDevices();
	}
	
	private CCInputDevice _device(final int theDeviceId){
		if (theDeviceId >= _numberOfDevices()){
			throw new RuntimeException("There is no device with the number " + theDeviceId + ".");
		}
		CCInputDevice result = _myDevices.get(theDeviceId);
		result.open();
		return result;
	}
	
	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceId number of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	public static CCInputDevice device(final int theDeviceId) {
		return instance._device(theDeviceId);
	}

	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceName String, name of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	private CCInputDevice _device(final String theDeviceName){
		for (int i = 0; i < _numberOfDevices(); i++){
			CCInputDevice device = _myDevices.get(i);
			if (device.name().equals(theDeviceName)){
				device.open();
				return device;
			}
		}
		throw new RuntimeException("There is no device with the name " + theDeviceName + ".");
	}
	
	public static CCInputDevice device(final String theDeviceName) {
		return instance._device(theDeviceName);
	}

	/**
	 * Updates the _myDevices, to get the actual data before a new
	 * frame is drawn
	 * @invisible
	 */
	public void update(float theDeltaTime){
		for (int i = 0; i < _myDevices.size(); i++){
			_myDevices.get(i).update(theDeltaTime);
		}
	}
}

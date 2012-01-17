package cc.creativecomputing;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Constructor;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import cc.creativecomputing.appmodes.CCAppMode;
import cc.creativecomputing.graphics.CCCamera;



/**
 * <p>The Application Manager handles the setup and start off an application.
 * Here you can make all settings like Window size, window position etc.
 * To define the settings you can use various methods of the CCSettings class.</p>
 * @author texone
 *
 * @param <CCAppType>
 * @see CCApp
 * @see CCAppMode
 * @see CCApplicationSettings
 * @example basics.CCAppExample
 */
public class CCApplicationManager{
	/**
	 * Class name of the Application to start
	 */
	private final Class<?> _myClass;
	
	/**
	 * Application that is controlled by the manager
	 */
	private CCApp _myApplication;
	
	private CCApplicationSettings _myCCSettings;
	
	private final GraphicsEnvironment _myGraphicsEnvironment;
	
	/**
	 * Creates a new manager from the class object of your application
	 * @param theClass class object of your application
	 * @example basics.CCAppExample
	 */
	public CCApplicationManager(final Class<?> theClass){
		_myClass = theClass;
		
		_myCCSettings = new CCApplicationSettings();
		_myGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_myCCSettings.title(_myClass.getSimpleName());
	}
	
	/**
	 * Gives you access to the internal settings object for more
	 * advanced settings of your application.
	 * @shortdesc settings object for more precise control
	 * @return settings object for more detailed control
	 * @example basics.CCAppManagerUndecorated
	 */
	public CCApplicationSettings settings(){
		return _myCCSettings;
	}
	
	/**
	 * Starts your Application with the settings you made in the manager.
	 */
	public void start(){
		try {
			
			GLProfile myProfile = GLProfile.getDefault();
			final GLCapabilities myCapabilities = new GLCapabilities(myProfile);
			myCapabilities.setSampleBuffers(_myCCSettings.isAntialiasing());
			myCapabilities.setNumSamples(_myCCSettings.antialiasing());
			if(_myCCSettings.stencilBits() >= 0)myCapabilities.setStencilBits(_myCCSettings.stencilBits());
			_myCCSettings.glCapabilities = myCapabilities;
//			for(GraphicsDevice _myGraphicsDevice:_myGraphicsEnvironment.getScreenDevices()){
//				System.out.println("memory:"+_myGraphicsDevice.getAvailableAcceleratedMemory());
//				System.out.println("id:"+_myGraphicsDevice.getIDstring());
//				for(DisplayMode myDisplayMode:_myGraphicsDevice.getDisplayModes()){
//					System.out.println("myDisplayMode:"+myDisplayMode.getWidth()+":"+myDisplayMode.getHeight()+":"+myDisplayMode.getBitDepth()+":"+myDisplayMode.getRefreshRate());
//				}
//				System.out.println("refreshrate:"+_myGraphicsDevice.getDisplayMode().getWidth());
//			}
			GraphicsDevice _myGraphicsDevice = _myCCSettings.display();
			
			final Class<?> myArguments[] = new Class[]{};
			final Constructor<?> myContructor = _myClass.getConstructor( myArguments );
			
			_myApplication = (CCApp)myContructor.newInstance();
			_myApplication.makeSettings(_myCCSettings);
			_myCCSettings.appMode().setSize(_myCCSettings.width(), _myCCSettings.height());
			_myCCSettings.appMode().setApp(_myApplication);
			
			CCCamera.DEFAULT_FOV = _myCCSettings.fov();
			
			Frame frame;
			switch(_myCCSettings.displayMode()){
			case WINDOW:
				frame = new CCFrame(_myApplication, _myCCSettings);
				break;
			case FULLSCREEN:
				frame = new CCFullFrame(_myApplication, _myCCSettings);
				break;
			default:
				frame = new CCFrame(_myApplication, _myCCSettings);
			}
			
			frame.setVisible(_myCCSettings.showAppOnStart());
		} catch (Exception e) {
			throw new RuntimeException("COULD NOT START APPLICATION:",e);
		}
	}
	
	/**
	 * Ends the application that is currently running.
	 */
	public void end() {
		_myApplication.frame().dispose();
		_myApplication.dispose();
	}
	
	/**
	 * Use this method to get a reference to the running application. Is only working after
	 * you have started the application. This can be useful if you start your application
	 * from inside another program and need access to it.
	 * @shortdesc Returns a reference to the running application.
	 * @return a reference to your application
	 */
	public CCApp app(){
		return _myApplication;
	}
}
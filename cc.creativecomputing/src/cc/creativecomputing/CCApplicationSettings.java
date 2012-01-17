package cc.creativecomputing;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.media.opengl.GLCapabilities;
import javax.swing.WindowConstants;

import cc.creativecomputing.appmodes.CCAppMode;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;


/**
 * This class is used to setup your applications parameters.
 * @author texone
 * @see CCApplicationManager
 * @see CCApp
 */
public class CCApplicationSettings {
	
	private final Map<String, GraphicsDevice> _myGraphicsDeviceMap = new HashMap<String, GraphicsDevice>();
	private final String[] _myGraphicDeviceNames;
	private final GraphicsDevice[] _myGraphicDevices;
	
	private final Map<String, GraphicsConfiguration> _myGraphicsConfigurationMap = new HashMap<String, GraphicsConfiguration>();
	private String[] _myGraphicConfigurationNames;
	private GraphicsConfiguration[] _myGraphicConfigurations;
	
	private GraphicsDevice _myGraphicsDevice;
	private GraphicsConfiguration _myGraphicsConfiguration;
	
	public CCApplicationSettings() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_myGraphicsDevice = ge.getDefaultScreenDevice();
		
		_myGraphicDevices = ge.getScreenDevices();
		_myGraphicDeviceNames = new String[_myGraphicDevices.length];
		
		for (int i = 0; i < _myGraphicDevices.length;i++) {
			GraphicsDevice myDevice = _myGraphicDevices[i];
			_myGraphicDeviceNames[i] = myDevice.getIDstring();
			_myGraphicsDeviceMap.put(myDevice.getIDstring(), myDevice);
		}
		updateConfigurations();
	}
	
	/**
	 * Update the available graphic configurations based on the current graphics device
	 */
	private void updateConfigurations() {
		_myGraphicsConfigurationMap.clear();
		_myGraphicsConfiguration = _myGraphicsDevice.getDefaultConfiguration();
		_myGraphicConfigurations = _myGraphicsDevice.getConfigurations();
		_myGraphicConfigurationNames = new String[_myGraphicConfigurations.length];
		
		for (int i = 0; i < _myGraphicConfigurations.length;i++) {
			GraphicsConfiguration myConfig = _myGraphicConfigurations[i];
			String myID = myConfig.getBounds().width + " x " + myConfig.getBounds().height;
			_myGraphicConfigurationNames[i] = myID;
			_myGraphicsConfigurationMap.put(myID, myConfig);
		}
	}
	
	/*
	 * DISPLAY MODE
	 */
	/**
	 * @invisible
	 */
	public static enum CCDisplayMode{
		WINDOW,FULLSCREEN;
	}
	
	private CCDisplayMode _myDisplayMode = CCDisplayMode.WINDOW;
	
	/**
	 * Use this method to set and get the display mode of your application.
	 * Application can run as window or full screen.
	 * @param theDisplayMode 
	 */
	public void displayMode(final CCDisplayMode theDisplayMode){
		_myDisplayMode = theDisplayMode;
	}
	
	public CCDisplayMode displayMode(){
		return _myDisplayMode;
	}
	
	/*
	 * CLOSE OPERATION
	 */
	
	/**
	 * Represents the operations that will happen by default when the user initiates a 
	 * "close" on the application.
	 */
	public static enum CCCloseOperation{
		/**
		 * Don't do anything; require the program to handle the operation in the 
		 * windowClosing method of a registered WindowListener object.
		 */
		DO_NOTHING_ON_CLOSE(WindowConstants.DO_NOTHING_ON_CLOSE),
		
		/**
		 * Automatically hide the frame after invoking any registered WindowListener objects.
		 */
		HIDE_ON_CLOSE(WindowConstants.HIDE_ON_CLOSE),
		
		/**
		 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.
		 */
		DISPOSE_ON_CLOSE(WindowConstants.DISPOSE_ON_CLOSE),
		
		/**
		 * Exit the application using the System exit method. Use this only in applications.
		 */
		EXIT_ON_CLOSE(WindowConstants.EXIT_ON_CLOSE);
		
		private int _myID;
		
		private CCCloseOperation(final int theID) {
			_myID = theID;
		}
		
		public int id() {
			return _myID;
		}
	}
	
	private CCCloseOperation _myCloseOperation = CCCloseOperation.EXIT_ON_CLOSE;
	
	/**
	 * Sets the operation that will happen by default when the user initiates a 
	 * "close" on the application. You must specify one of the following choices:
	 * <ul>
	 * <li><code>DO_NOTHING_ON_CLOSE</code> 
	 * Don't do anything; require the program to handle the operation in the 
	 * windowClosing method of a registered WindowListener object.</li>
	 * <li><code>HIDE_ON_CLOSE</code> 
	 * Automatically hide the frame after invoking any registered WindowListener objects.</li>
	 * <li><code>DISPOSE_ON_CLOSE</code> 
	 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.</li> 
	 * <li><code>EXIT_ON_CLOSE </code> Exit the application using the System exit method. Use this only in applications.</li>
	 * </ul>
	 * The value is set to <code>EXIT_ON_CLOSE</code> by default. Changes to the value of 
	 * this property cause the firing of a property change event, with property name "defaultCloseOperation".
	 * @param theCloseOperation
	 */
	public void closeOperation(final CCCloseOperation theCloseOperation) {
		_myCloseOperation = theCloseOperation;
	}
	
	/**
	 * Returns the operation that will happen by default when the user initiates a 
	 * "close" on the application. Specified is one of the following choices:
	 * <ul>
	 * <li><code>DO_NOTHING_ON_CLOSE</code> 
	 * Don't do anything; require the program to handle the operation in the 
	 * windowClosing method of a registered WindowListener object.</li>
	 * <li><code>HIDE_ON_CLOSE</code> 
	 * Automatically hide the frame after invoking any registered WindowListener objects.</li>
	 * <li><code>DISPOSE_ON_CLOSE</code> 
	 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.</li> 
	 * <li><code>EXIT_ON_CLOSE </code> Exit the application using the System exit method. Use this only in applications.</li>
	 * </ul>
	 * The value is set to <code>EXIT_ON_CLOSE</code> by default. Changes to the value of 
	 * this property cause the firing of a property change event, with property name "defaultCloseOperation".
	 * @param theCloseOperation
	 */
	public CCCloseOperation closeOperation() {
		return _myCloseOperation;
	}
	
	/**
	 * flag to define if the app is visible on start or not
	 */
	private boolean _myShowAppOnStart = true;
	
	/**
	 * Defines if the app is visible on start or not
	 * @param theShowAppOnStart
	 */
	public void showAppOnStart(final boolean theShowAppOnStart) {
		_myShowAppOnStart = theShowAppOnStart;
	}
	
	/**
	 * Returns if the app is visible on start or not
	 * @return
	 */
	public boolean showAppOnStart() {
		return _myShowAppOnStart;
	}
	
	public static enum CCGLContainer{
		CANVAS,
		JPANEL
	}
	 
	private CCGLContainer _myContainer = CCGLContainer.CANVAS;
	
	/**
	 * Sets the implementation type of the jogl drawable this can be canvas or jpanel,
	 * So far canvas is the default value. 
	 * @param theContainer
	 */
	public void container(CCGLContainer theContainer) {
		_myContainer = theContainer;
	}
	
	public CCGLContainer container() {
		return _myContainer;
	}
	
	/**
	 * Returns an array with the available Graphic devices.
	 * @return array with the available Graphic devices
	 */
	public String[] deviceNames() {
		return _myGraphicDeviceNames;
	}

	/**
	 * Sets the display for the application.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(int theDisplay) {
		_myGraphicsDevice = _myGraphicDevices[theDisplay];
		updateConfigurations();
	}
	
	/**
	 * Sets the display for the application. This is useful for multi
	 * screen setups. If the display does not exist, the application will start
	 * at the first available one.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(String theDisplayName) {
		_myGraphicsDevice = _myGraphicsDeviceMap.get(theDisplayName);
		updateConfigurations();
	}

	/**
	 * Returns the graphics device that should be used for Graphic output.
	 * @return the display of the application
	 */
	public GraphicsDevice display() {
		return _myGraphicsDevice;
	}
	
	/**
	 * Returns an array with the available configurations.
	 * Be aware that this will return the configurations for the current device.
	 * So to get the right configurations set the display first.
	 * @return array with the available configurations
	 */
	public String[] configurationNames() {
		return _myGraphicConfigurationNames;
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theConfiguration display for the application
	 */
	public void displayConfiguration(int theConfiguration) {
		_myGraphicsConfiguration = _myGraphicConfigurations[theConfiguration];
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theDisplay display for the application
	 */
	public void displayConfiguration(String theConfigurationName) {
		_myGraphicsConfiguration = _myGraphicsConfigurationMap.get(theConfigurationName);
	}

	/**
	 * Returns the display configuration that should be used for graphic output.
	 * @return the display configuration of the application
	 */
	public GraphicsConfiguration displayConfiguration() {
		return _myGraphicsConfiguration;
	}
	
	private boolean _myIsInVsync = true;
	
	/**
	 * Provides a platform-independent way to sync the application to vertical screen refreshes. 
	 * <code>false</code> disables sync-to-vertical-refresh completely, while <code>true</code> 
	 * causes the application to wait until the next vertical refresh until swapping buffers. 
	 * The default, which is <code>true</code>. This function is not guaranteed to have an effect, 
	 * and in particular only affects heavyweight onscreen components.
	 * @param theIsInVsync
	 */
	public void vsync(boolean theIsInVsync){
		_myIsInVsync = theIsInVsync;
	}
	
	/**
	 * Returns if the app is running in vsync
	 * @return <code>true</code> if the app runs in vsync otherwise <code>false</code>
	 */
	public boolean vsync(){
		return _myIsInVsync;
	}
	
	private int _myStencilBits = 8;
	
	/**
	 * Sets the number of stencilbits to be used by opengl by default 8
	 * @param theStencilBits
	 */
	public void stencilBits(int theStencilBits){
		_myStencilBits = theStencilBits;
	}
	
	/**
	 * Returns the number of stencilbits
	 * @return
	 */
	public int stencilBits(){
		return _myStencilBits;
	}
	
	/* 
	 * TITLE
	 */
	private String _myTitle = "Creative Computing Application";
	
	/**
	 * Sets or returns the title of the application
	 * @param theTitle title for the application
	 * @example basics.CCAppManager
	 */
	public void title(final String theTitle) {
		_myTitle = theTitle;
	}

	/**
	 * @return title of the application
	 */
	public String title() {
		return _myTitle;
	}
	
	/*
	 * FRAME DECORATION
	 */
	private boolean _myIsResizable = true;
	
	/**
	 * Defines if the application window is resizable.
	 * @param theIsResizable true if window should be resizable otherwise false
	 */
	public void isResizable(boolean theIsResizable) {
		_myIsResizable = theIsResizable;
	}

	/**
	 * @return true if the frame is resizable otherwise false
	 */
	public boolean isResizable() {
		return _myIsResizable;
	}
	
	/*
	 * FRAME DECORATION
	 */
	private boolean _myIsUndecorated = false;
	
	/**
	 * Defines if the application window will have decoration. If set true
	 * the window will show borders and a head. Otherwise the window appears
	 * without borders and head. The version with a parameter returns true
	 * if the application runs undecorated otherwise false;
	 * @param theIsUndecorated true if window should be decorated otherwise false
	 * @example basics.CCAppManagerUndecorated
	 */
	public void undecorated(boolean theIsUndecorated) {
		_myIsUndecorated = theIsUndecorated;
	}

	/**
	 * @return true if the frame is undecorated otherwise false
	 */
	public boolean undecorated() {
		return _myIsUndecorated;
	}

	/*
	 * ANTIALIASING
	 */
	private boolean _myIsAntialiasing = false;
	private int _myAntialiasingLevel = 0;
	
	/**
	 * @invisible
	 * @return true if antialiasing is active otherwise false
	 */
	public boolean isAntialiasing() {
		return _myIsAntialiasing;
	}

	/**
	 * Sets and returns the antialising level of the application.
	 * @param theAntialiasingLevel the level of antialiasing
	 * @example basics.CCAppManagerUndecorated
	 */
	public void antialiasing(final int theAntialiasingLevel) {
		_myAntialiasingLevel = theAntialiasingLevel;
		_myIsAntialiasing = _myAntialiasingLevel > 0;
	}

	/**
	 * 
	 * @return the antialiasing level of the application
	 */
	public int antialiasing() {
		return _myAntialiasingLevel;
	}
	
	/*
	 * FRAME SIZE
	 */
	private int _myWidth = 400;


	public int width() {
		return _myWidth;
	}
	
	private int _myHeight = 400;

	/**
	 * Returns the height for the application.
	 * @return theheight for the application
	 */
	public int height() {
		return _myHeight;
	}
	
	/**
	 * Sets the size of your application window. Calling size with the values
	 * 320, 240 will open a window of the size 320 x 240 px.
	 * The default size of the window is 400 x 400 pixels
	 * @shortdesc Sets the size of your application window.
	 * @param theWidth the width of the application window
	 * @param theHeight the height of the application window
	 * @see #location(int, int)
	 * @example basics.CCAppManager
	 */
	public void size(final int theWidth, final int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	private CCColor _myBackground = new CCColor(200);
	
	public CCColor background(){
		return _myBackground;
	}
	
	public void background(final CCColor theBackground){
		_myBackground = theBackground;
	}
	
	private float _myFov = 60;
	
	/**
	 * Sets the initial field of view to be used from the camera in degrees,
	 * default value is 60.
	 * @param theFov
	 */
	public void fov(final float theFov) {
		_myFov = theFov;
	}
	
	public float fov() {
		return _myFov;
	}
	
	/*
	 * FRAME POSITION
	 */

	private int _myX = -1;
	private int _myY = -1;
	
	/**
	 * Sets the location of the application window. Call setLocation
	 * with the values 100,200 will open the position at the screen
	 * position 100, 200. By default the window will be placed in the 
	 * center of the screen.
	 * @shortdesc Sets the location of the application window.
	 * @param theX the x location of the application window
	 * @param theY the y location of the application window
	 * @see #size(int, int)
	 * @example basics.CCAppManager
	 */
	public void location(final int theX, final int theY){
		_myX = theX;
		_myY = theY;
	}

	/**
	 * Returns the y position of the application window. By default
	 * this value will be -1 and the window will appear in the center
	 * of the screen.
	 * @return y position of the application window
	 * @shortdesc Returns the y position of the application window.
	 * @see #location(int, int)
	 * @see #x()
	 */
	public int y() {
		return _myY;
	}


	/**
	 * Returns the x position of the application window. By default
	 * this value will be -1 and the window will appear in the center
	 * of the screen.
	 * @return x position of the application window
	 * @shortdesc Returns the y position of the application window.
	 * @see #location(int, int)
	 * @see #y()
	 */
	public int x() {
		return _myX;
	}
	
	/**
	 * Reads the application settings from a given XML file
	 * @param theFile link to the XML file with the application settings
	 */
	public void setFromFile(final String theFile) {
		set(CCXMLIO.createXMLElement(theFile));
	}
	
	/**
	 * Sets the application settings from the string arguments passed to 
	 * to the main method.
	 * @param theArgs
	 */
	public void setFromArgs(String[] theArgs){
		for(int i = 0; i < theArgs.length;i++){
			String myArgument = theArgs[i];
			if(myArgument.startsWith("-")){
				if(myArgument.equals("-undecorated")){
					i++;
					undecorated(Boolean.parseBoolean(theArgs[i]));
				}
				if(myArgument.equals("-width")){
					i++;
					_myWidth = Integer.parseInt(theArgs[i]);
				}
				if(myArgument.equals("-height")){
					i++;
					_myHeight = Integer.parseInt(theArgs[i]);
				}
				if(myArgument.equals("-x")){
					i++;
					_myX = Integer.parseInt(theArgs[i]);
				}
				if(myArgument.equals("-y")){
					i++;
					_myY = Integer.parseInt(theArgs[i]);
				}
				if(myArgument.equals("-displayMode")){
					i++;
					displayMode(CCDisplayMode.valueOf(theArgs[i]));
				}
				if(myArgument.equals("-background")) {
					i++;
					background(CCColor.parseFromString(theArgs[i]));
				}
			}
		}
	}

	/**
	 * Reads the application settings from the given XMLElement.
	 * @param theSettingsXML XMLElement containing the settings
	 */
	public void set(final CCXMLElement theSettingsXML){
		if(!theSettingsXML.name().equals("applicationSettings"))return;
		
		CCXMLElement myChild = theSettingsXML.child("displaymode");
		if(myChild != null)displayMode(CCDisplayMode.valueOf(myChild.content(_myDisplayMode.toString())));
		
		myChild = theSettingsXML.child("display");
		if(myChild != null)display(myChild.intContent(0));
		
		myChild = theSettingsXML.child("undecorated");
		if(myChild != null)undecorated(myChild.booleanContent(false));
		
		myChild = theSettingsXML.child("title");
		if(myChild != null)title(myChild.content());
		
		myChild = theSettingsXML.child("antialiasing");
		if(myChild != null)antialiasing(myChild.intContent(0));
		
		myChild = theSettingsXML.child("fov");
		if(myChild != null)fov(myChild.floatContent(0));
		
		myChild = theSettingsXML.child("background");
		if(myChild != null)background(CCColor.parseFromString(myChild.content("#AAAAAA")));
		
		myChild = theSettingsXML.child("size");
		if(myChild != null) {
			_myWidth = myChild.intAttribute("width", _myWidth);
			_myHeight = myChild.intAttribute("height", _myHeight);
		}
		
		myChild = theSettingsXML.child("location");
		if(myChild != null) {
			_myX = myChild.intAttribute("x", _myX);
			_myY = myChild.intAttribute("y", _myY);
		}
		
		CCXMLElement myAppModeSettingsXML = theSettingsXML.child("CCAppModeSettings");
		if(myAppModeSettingsXML != null)setAppMode(myAppModeSettingsXML);
	}
	
	/**
	 * @invisible
	 */
	public static enum CCAppModes{
		NORMAL;
	}
	
	/**
	 * 
	 * @param theSettingsXML
	 */
	private void setAppMode(final CCXMLElement theSettingsXML){
		final CCAppModes myMode = CCAppModes.valueOf(theSettingsXML.attribute("appmode","NORMAL"));
		
		switch(myMode){
		case NORMAL:
			_myAppMode = new CCAppMode();
			break;
		}
	}
	
	Color background = Color.black;
	Color stopColor = Color.gray;
	
	boolean hideStop = false;
	
	private CCAppMode _myAppMode = new CCAppMode();
	
	/**
	 * Creative Computing supports different kinds of application modes
	 * that allow you to run you application with different settings
	 * without the need to change your code. So far there are to modes
	 * available the default mode. Starts the application in a single 
	 * frame according to the other setting. The split screen mode allows
	 * to create a frame that goes over more than one display and has an
	 * overlap so that you can use projectors with edge blending for
	 * better multi projector support. further application modes are planed
	 * but not yet implemented.
	 * @shortdesc sets the active application mode
	 * @param theAppMode
	 * @see CCSplitScreenMode
	 */
	public void appMode(final CCAppMode theAppMode){
		_myAppMode = theAppMode;
	}
	
	/**
	 * 
	 * @return the app mode
	 */
	public CCAppMode appMode(){
		return _myAppMode;
	}
	
	GLCapabilities glCapabilities = null;
	
	////////////////////////////////////////////////////////
	//
	// SETUP LOGGING
	//
	////////////////////////////////////////////////////////
	
	/**
	 * @invisible
	 */
	public static enum CCLoggingFormat{
		SIMPLE,XML;
	}
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static enum CCLoggingHandler{
		CONSOLE,FILE,MEMORY,SOCKET,STREAM;
	}
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static class CCLoggingSettings{
		
		private CCLoggingFormat _myFormater = CCLoggingFormat.SIMPLE;
		private CCLoggingHandler _myHandler = CCLoggingHandler.FILE;
		private Level _myLevel = Level.WARNING;
		
		public Logger logger(final CCAbstractApp theApp){
			final Logger myLogger = Logger.getLogger(theApp.getClass().getName());
			try {
				Handler myHandler;
				
				/* check handler to use for logging */
				switch(_myHandler){
				case FILE:
					final String myPath = "log/"+theApp.getClass().getSimpleName()+".txt";
					CCIOUtil.createPath(myPath);
					myHandler = new FileHandler(myPath);
					break;
				default:
					myHandler = new ConsoleHandler();
				break;
				}
				
				/* check formatter to use for logging*/
				switch(_myFormater){
				case SIMPLE:
					myHandler.setFormatter(new SimpleFormatter());
					break;
				case XML:
					myHandler.setFormatter(new XMLFormatter());
					break;
				}
				
				myHandler.setLevel(_myLevel);
				myLogger.addHandler(myHandler);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return myLogger;
		}
	}
	
	private CCLoggingSettings _myLogginSettings = new CCLoggingSettings();
	
	public CCLoggingSettings logingSettings(){
		return _myLogginSettings;
	}
	
	private CCVector2f _myUiTranslation = new CCVector2f();
	
	/**
	 * Returns a translation vector to move the ui from the upper left corner of the window.
	 * @return translation vector to move the ui 
	 */
	public CCVector2f uiTranslation(){
		return _myUiTranslation;
	}
	
	/**
	 * Sets the position of the ui. By default the ui is placed at the upper left
	 * corner of the window. To move the ui 100 px down and 200 px right you would call
	 * <code>uiTranslation(200,100)</code>
	 * @param theX horizontal translation in pixel
	 * @param theY vertical translation in pixel
	 */
	public void uiTranslation(final float theX, final float theY){
		_myUiTranslation.set(theX, theY);
	}
}

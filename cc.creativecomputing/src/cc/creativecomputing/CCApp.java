package cc.creativecomputing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import cc.creativecomputing.CCApplicationSettings.CCGLContainer;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCSetupListener;
import cc.creativecomputing.events.CCSizeListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.util.logging.CCLog;


/**
 * <p>This is the base class for your applications. Everything starts with an
 * extended class off CCApp. To create your own application you must first
 * define a constructor that passes the given settings to its super class.</p>
 * <blockquote><pre>public CCAppExample(CCApplicationSettings theSettings) {
 *    super(theSettings);
 *}</pre></blockquote>
 * <p>This always needs to be included in your application so far there is no
 * other way to initialize it. If your are using eclipse you can load a template
 * file that comes with the download to create a first stub.</p>
 * <p>To start your application you have to define a main method where you
 * create an instance of <code>CCApplicationManager</code> to setup your application
 * and start it. For more information for setting up and starting your application
 * have a look at <code>CCApplicationManager</code> and <code>CCApplicationSettings</code>
 * </p>
 * <blockquote><pre>public static void main(String[] args) {
 *    final CCApplicationManager _myManager = new CCApplicationManager(CCAppExample.class);
 *    _myManager.setSize(400, 400);
 *    _myManager.start();
 * }</pre></blockquote>
 * <p>To define the behavior of your program you have to override the <code>setup</code>
 * and <code>draw</code> function. <code>setup</code> is executed once when your program
 * has started. <code>draw</code> is continuously called after setup for every frame.</p>
 * 
 * @see CCApplicationManager
 * @see CCApplicationSettings
 * @example basics.CCAppExample
 * @author texone
 * @nosuperclasses
 */
public class CCApp extends CCAbstractApp 
	implements 
		GLEventListener, 
		FocusListener, 
		CCSetupListener,
		CCMouseListener, 
		CCMouseMotionListener,
		CCKeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 889753638757768610L;

	/**
	 * Used to animate the scene
	 * specify if the display must be called in loop
	 */
	protected CCAnimator animator;
	
	/**
	 * Contains the number of frames since your application started.
	 * Inside <code>setup()</code>, <code>frameCount</code> is 0. 
	 * For every call of <code>draw</code> <code>frameCount</code> is increased by
	 * one.
	 * @shortdesc frames since application start
	 * @see #frameRate
	 * @example basics.CCFrameRate
	 */
	public int frameCount = 0;
	
	/**
	 * Contains the current value of frames per second.
	 * The initial value will be 10 fps, and will be updated with each
	 * frame thereafter. The value is not instantaneous (since that
	 * wouldn't be very useful since it would jump around so much),
	 * but is instead averaged (integrated) over several frames.
	 * As such, this value won't be valid until after 5-10 frames.
	 * @shortdesc The current value of frames per second.
	 * @see #frameCount
	 * @see #frameRate(int)
	 * @example basics.CCFrameRate
	 */
	public float frameRate = 10;
	public float frameTime = 0;

	protected long _myFrameRateLastNanos = 0;
	protected long _myMillisSinceStart = 0;
	
	protected GLAutoDrawable _myAutoDrawable;
	protected Component _myGLComponent;
	
	/**
	 * The graphics object is used for all the drawing inside your application.
	 * @shortdesc graphics object for all drawing
	 * @example basics.CCAppExample
	 * @see CCGraphics
	 */
	public CCGraphics g;
	
	protected CCListenerManager<CCDrawListener> _myDrawListener;
	
	/**
	 * The setting field contains all properties of the active application.
	 * @shortdesc settings of the application
	 * @see CCApplicationSettings
	 */
	public CCApplicationSettings settings;
	protected JFrame _myFrame;
	
	protected CCControlUI _myUI;
	
	private static GLContext glContext;
	
	public void makeSettings(CCApplicationSettings theSettings) {
		if(theSettings.container() == CCGLContainer.CANVAS) {
			GLCanvas myGLCanvas = new GLCanvas(theSettings.glCapabilities, glContext);
			_myAutoDrawable = myGLCanvas;
			_myGLComponent = myGLCanvas;
		}else {
			GLJPanel myGLCanvas = new GLJPanel(theSettings.glCapabilities, null, glContext);
			_myAutoDrawable = myGLCanvas;
			_myGLComponent = myGLCanvas;
		}
		
		_myAutoDrawable.addGLEventListener(this);
		_myGLComponent.addKeyListener(this);
		_myGLComponent.addMouseListener(this);
		_myGLComponent.addMouseMotionListener(this);
		
		settings = theSettings;
		width = settings.width();
		height = settings.height();
		
		_myMouseListener.add(this);
		_myMouseMotionListener.add(this);
		_myKeyListener.add(this);
		_mySetupListener.add(this);
		
		_myDrawListener = CCListenerManager.create(CCDrawListener.class);
		
		_myUI = new CCControlUI(this);
		_myUI.hide();
		_myUI.translation().set(theSettings.uiTranslation());
	}
	
	public Component component() {
		return _myGLComponent;
	}
	
	public JFrame frame() {
		return _myFrame;
	}
	
	/**
	 * Add the controls to the static members of a class. In this case 
	 * the tab name and object name are taken from the {@link CCControlClass}
	 * annotation of the given class.
	 * @param theClass the class to be controlled
	 */
	public void addControls(final Class<?> theClass) {
		_myUI.addControls(theClass);
	}
	
	/**
	 * Add the controls to the static members of a class. In this case the 
	 * tab is not read from the {@link CCControlClass} annotation but the given 
	 * tab name.
	 * @param theTabName name of the tab for the controls
	 * @param theClass the class to be controlled
	 */
	public void addControls(final String theTabName, final Class<?> theClass) {
		_myUI.addControls(theTabName, theClass);
	}
	

	public void addControls(final String theTabName, final int theColumn, final Class<?> theClass) {
		_myUI.addControls(theTabName, theColumn, theClass);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectID, final Class<?> theClass) {
		_myUI.addControls(theTabName, theObjectID, theClass);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectID, final int theColumn, final Class<?> theClass) {
		_myUI.addControls(theTabName, theObjectID, theColumn, theClass);
	}
	
	/**
	 * Adds an object to be controlled to the user interface.
	 * @param theTabName the name of the tab for the controls 
	 * @param theObjectID the id for saving the object data
	 * @param theObject the object to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectID, final Object theObject) {
		_myUI.addControls(theTabName, theObjectID, theObject);
	}
	
	/**
	 * Adds an object to be controlled to the user interface.
	 * @param theTabName the name of the tab for the controls 
	 * @param theObjectID the id for saving the object data
	 * @param theColumn column in which to place the ui elements
	 * @param theObject the object to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectID, final int theColumn, final Object theObject) {
		_myUI.addControls(theTabName, theObjectID, theColumn,theObject);
	}
	
	/**
	 * hides the user interface
	 */
	public void hideControls() {
		_myUI.hide();
	}
	
	/**
	 * Shows the userinterface
	 */
	public void showControls() {
		_myUI.show();
	}
	
	/**
	 * Checks if the user interface is visible.
	 * @return <code>true</code> if the user interface is visible otherwise <code>false</code>
	 */
	public boolean areControlsVisible() {		//TODO fix typo! (areControlsVisible)
		return _myUI.isVisible();
	}

	/**
	 * Get UI Object
	 */
	public CCControlUI getUI() {
		return _myUI;
	}
	
	/**
	 * Adds a listener to the application, to draw different objects. 
	 * This can be used to forward the draw event to 
	 * different objects. To do so the class needs to implement
	 * the <code>CCDrawListener</code> interface and must be added
	 * as listener. 
	 * @shortdesc adds a listener reacting to draw events
	 * @param theDrawListener the listener for draw events
	 * @see #draw()
	 * @see CCDrawListener
	 */
	public void addDrawListener(final CCDrawListener theDrawListener) {
		_myDrawListener.add(theDrawListener);
	}
	
	public void removeDrawListener(final CCDrawListener theDrawListener) {
		_myDrawListener.remove(theDrawListener);
	}
	
	/**
	 * Returns the current x coordinate of the application window.
	 * @return x coordinate of the application window
	 */
	public int windowX() {
		return _myFrame.getX();
	}

	/**
	 * Returns the current y coordinate of the application window.
	 * @return y coordinate of the application window
	 */
	public int windowY() {
		return _myFrame.getY();
	}

	/**
	 * Returns the current width of the application window.
	 * @return width of the application window
	 */
	public int windowWidth() {
		return _myFrame.getWidth();
	}

	/**
	 * Returns the current height of the application window.
	 * @return height of the application window
	 */
	public int windowHeight() {
		return _myFrame.getHeight();
	}
	
	/**
	 * Hide this Window, its subcomponents, and all of its owned children. 
	 * The Window and its subcomponents can be made visible again with a call to show.
	 */
	public void hide() {
		_myGLComponent.setVisible(false);
		_myFrame.setVisible(false);
	}
	
	/**
	 * Makes the Window visible. If the Window and/or its owner are not yet displayable, 
	 * both are made displayable. The Window will be validated prior to being made visible. 
	 * If the Window is already visible, this will bring the Window to the front.
	 */
	public void show() {
		_myGLComponent.setVisible(true);
		_myFrame.setVisible(true);
		
		System.out.println("SHOW");
	}
	
	/**
	 * Call by the GLDrawable just after the Gl-Context is 
	 * initialized.    
	 * @invisible
	 **/
	public void init(GLAutoDrawable glDrawable) {
		_myAutoDrawable = glDrawable;
		if(glContext == null) {
			glContext = _myAutoDrawable.getContext();
		}
		final GL2 gl = glDrawable.getGL().getGL2();
		
		if(settings.vsync()){
			gl.setSwapInterval(1);
		}else{
			gl.setSwapInterval(0);
		}
//		gl.glShadeModel(GL.GL_SMOOTH);
//		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//		gl.glClearDepth(1.0); //Enable Clearing of the Depth buffer
//		gl.glDepthFunc(GL.GL_LEQUAL); //Type of Depth test
//		gl.glEnable(GL.GL_DEPTH_TEST); //Enable Depth Testing
//
//		//Define the correction done to the perspective calculation (perspective looks a it better)
//		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		//Create the animator to call the display method in loop
		animator = new CCAnimator(_myAutoDrawable);
		animator.start();
		
		g = new CCGraphics(gl);
		
		CCAppCapabilities.init(_myAutoDrawable.getGL().getGL2GL3());
	}
	
	/**
	 * Called once when the program is started. Used to define initial environment properties such as clear color, 
	 * loading images, etc. before the draw() begins executing. Simply overwrite this function for initialization
	 * steps. 
	 * @shortdesc called on application start
	 * @see #draw()
	 * @see #finish()
	 * @example basics.CCSetupDraw
	 * @order 1
	 */
	public void setup(){
		
	}
	
	public void frameSetup() {
		
	}
	
	public void dispose(GLAutoDrawable theAutoDrawable){
		
	}
	
	/**
	 *   
	 * @invisible
	 */
	public void dispose(){
		try{
			finish();
			_myDisposeListener.proxy().dispose();
		}catch(RuntimeException e){
			CCLog.error(e);
		}
	}
	
	/**
	 * Called once when the program is closed. Can be used to store relevant data to a file
	 * before the application ends.
	 * @shortdesc called on application end
	 * @order 3
	 * @see #draw()
	 * @see #setup()
	 * @example basics.CCFinish
	 */
	public void finish(){
		
	}
	
	/**
	 * Specifies the number of frames to be displayed every second. If the processor is not 
	 * fast enough to maintain the specified rate, it will not be achieved. For example, 
	 * the function call frameRate(30) will attempt to refresh 30 times a second. It is 
	 * recommended to set the frame rate within setup(). Per default the application runs
	 * as fast as possible.
	 * @shortdesc Specifies the number of frames to be displayed every second.
	 * @param theFrameRate number of frames per second
	 * @order 4
	 * @see #frameCount
	 * @see #frameRate
	 * @example basics.CCFrameRate
	 */
	public void frameRate(final int theFrameRate){
		if(theFrameRate < 0){
			animator.stop();
			animator = new CCAnimator(_myAutoDrawable);
			animator.start();
		}else{
			animator.stop();
			animator = new CCFPSAnimator(_myAutoDrawable,theFrameRate);
			animator.start();
		}
		
	}

	/**
	 * Called when the display mode has been changed : 
	 * CURRENTLY UNIMPLEMENTED IN JOGL  
	 * @invisible
	 **/
	public void displayChanged(GLAutoDrawable glDrawable, boolean modeChanged, boolean deviceChanged) {
	}
	
	private boolean _myHasCalledSetup = false;

	/**
	 * Called when the application window is resized. You can overwrite this function 
	 * to change settings that are dependent on the window size.
	 * @param theWidth the new width of the application window
	 * @param theHeight the new height of the application window
	 * @shortdesc Called when the application window is resized.
	 * @see #addSizeListener(CCSizeListener)
	 * @see CCSizeListener
	 * @example basics.CCResize
	 */
	public void resize(int theWidth, int theHeight){
		
	}

	/**
	 * This method is called by the GLDrawable at each resize event of the
	 * OpenGl component. It resize the view port of the scene and set the Viewing Volume  
	 * @invisible
	 */
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int theWidth, int theHeight) {
		// let the appmode handle all resizing
		settings.appMode().resize(glDrawable, theWidth, theHeight);
		
		g.camera().viewport(new CCViewport(x,y,theWidth,theHeight));
		
		resize(width, height);
		
		// first call resize listener so that screen modes can change size before call of setup
		_mySizeListener.proxy().size(width,height);
		
		
	}
    
	private void updateFrameRate(){
		if (_myFrameRateLastNanos != 0){
			long myNanos = System.nanoTime();
			frameTime = myNanos - _myFrameRateLastNanos;
			_myMillisSinceStart += myNanos - _myFrameRateLastNanos;
			if (frameTime != 0){
				//frameRate = (frameRate * 0.9f) + ((1.0f / (frameTime / 1000.0f)) * 0.1f);
				frameRate = (float)(1.0 / (frameTime / 1e9));
			}
			_myFrameRateLastNanos = myNanos;
		}else {
			_myFrameRateLastNanos = System.nanoTime();
		}
	}
	
	public String appTime(){
		long myTime = _myMillisSinceStart;
		int millis = (int)(myTime % 1000);
		
		myTime /= 1000;
		int seconds = (int)myTime;
		seconds %= 60;
		
		myTime /= 60;
		int minutes = (int)myTime;
		minutes %= 60;
		
		myTime /= 60;
		int hours = (int)myTime;
		
		return
			"HOURS:" + CCFormatUtil.nf(hours,2) +
			" MINUTES:"+CCFormatUtil.nf(minutes, 2)+
			" SECONDS:"+CCFormatUtil.nf(seconds, 2)+
			" MILLIS:"+CCFormatUtil.nf(millis, 4);
	}
	
	private boolean _myIsFixedUpdateTime = false;
	private float _myFixedUpdateTime = 1;
	
	public void fixUpdateTime(final float theTime){
		_myIsFixedUpdateTime = true;
		_myFixedUpdateTime = theTime;
	}
	
	public void freeUpdateTime(){
		_myIsFixedUpdateTime = false;
	}

	private int _myDisplayCounter = 0;
	/**
	 * Here we place all the code related to the drawing of the scene.
	 * This method is called by the drawing loop (the display method)  
	 * @invisible
	 **/
	public void display(GLAutoDrawable glDrawable) {
		final GL2 gl = glDrawable.getGL().getGL2();

		g.gl = gl;
		
		// call setup for the window size is set for the first time so that the values are set
		if(!_myHasCalledSetup){
			try{
				settings.appMode().begin();
				// set default font to enable traces to screen
//				g.textFont(CCFontIO.createGlutFont(CCGlutFontType.BITMAP_HELVETICA_10));
				_mySetupListener.proxy().setup();
				settings.appMode().end();
			}catch(RuntimeException e){
				CCLog.error(e);
				System.exit(0);
			}catch(Exception e){
				CCLog.error(e);
				throw new RuntimeException(e);
			}
			
//			g.clear();
			g.noTexture();
			_myHasCalledSetup = true;
			return;
		}
		
		processEvents(glDrawable);
		updateFrameRate();
		
		
		settings.appMode().begin();

		try{
			
			dequeueKeyEvents();
			dequeueMouseEvents();

			float myUpdateTime;
			if(_myIsFixedUpdateTime){
				myUpdateTime = _myFixedUpdateTime;
			}else{
				myUpdateTime = 1/frameRate;
			}
			g.gl.glFinish();
			update(myUpdateTime);
			_myUpdateListener.proxy().update(myUpdateTime);
		
			settings.appMode().draw();
			
			_myDrawListener.proxy().draw(g);
		
			_myPostListener.proxy().post();
		}catch(RuntimeException e){
			CCLog.error(e);
		}catch(Exception e){
			CCLog.error(e);
			throw new RuntimeException(e);
		}
		settings.appMode().end();
		
		frameCount++;
	}
	
	/**
	 * This method is called before the draw method on every frame. Override this
	 * method to define updates in your applications logic. The update function
	 * receives the time since the last frame as float value in seconds. You can
	 * use this parameter to calculate you updates dependent on the frame rate. 
	 * @shortdesc automatically called before draw for application updates
	 * @param theDeltaTime time since the last frame in seconds
	 * @see #setup()
	 * @see #draw()
	 * @example basics.CCUpdate
	 */
	public void update(float theDeltaTime){
		
	}
	
	/**
	 * Called directly after <code>setup()</code> and continuously executes the lines of code contained 
	 * inside its block until the program is stopped. The <code>draw()</code> function is called automatically 
	 * and should never be called explicitly. The number of times <code>draw()</code> executes in each second may be 
	 * controlled with the frameRate() function.
	 * @shortdesc automatically called on every frame for drawing
	 * @order 2
	 * @see #setup()
	 * @see #finish()
	 * @see #update(float)
	 * @see #addDrawListener(CCDrawListener)
	 * @example basics.CCSetupDraw
	 */
	public void draw(){
	}

	/*The events*/

	/**
	 * Put here all the events related to a key or a mouse pressed.  
	 * @invisible
	 **/
	public void processEvents(GLDrawable glDrawable) {
		/*
		 * Put here all related OpenGl effects of the events
		 *
		 * How put the events here:
		 * Just because if you would call an OpenGl method in on of
		 * the key or mouse method, it do not work !
		 *
		 * Ex: in the Tutorial 04, the call: gl.glShadeModel(...)
		 * could not be placed on an event method.
		 */
	}
	
	//////////////////////////////////////////////////////////////
	//
	// CURSOR
	// 
	//////////////////////////////////////////////////////////////
	
	/**
	 * Simply maps the java awt cursor types for better convenience  
	 */
	public static enum CCCursor{
		/**
	     * The default cursor type (gets set if no cursor is defined).
	     */
	    DEFAULT_CURSOR(Cursor.DEFAULT_CURSOR),

	    /**
	     * The crosshair cursor type.
	     */
	    CROSSHAIR_CURSOR(Cursor.CROSSHAIR_CURSOR),

	    /**
	     * The text cursor type.
	     */
	    TEXT_CURSOR(Cursor.TEXT_CURSOR),

	    /**
	     * The wait cursor type.
	     */
	    WAIT_CURSOR(Cursor.WAIT_CURSOR),

	    /**
	     * The south-west-resize cursor type.
	     */
	    SW_RESIZE_CURSOR(Cursor.SW_RESIZE_CURSOR),

	    /**
	     * The south-east-resize cursor type.
	     */
	    SE_RESIZE_CURSOR(Cursor.SE_RESIZE_CURSOR),

	    /**
	     * The north-west-resize cursor type.
	     */
	    NW_RESIZE_CURSOR(Cursor.NW_RESIZE_CURSOR),

	    /**
	     * The north-east-resize cursor type.
	     */
	    NE_RESIZE_CURSOR(Cursor.NE_RESIZE_CURSOR),

	    /**
	     * The north-resize cursor type.
	     */
	    N_RESIZE_CURSOR(Cursor.N_RESIZE_CURSOR),

	    /**
	     * The south-resize cursor type.
	     */
	    S_RESIZE_CURSOR(Cursor.S_RESIZE_CURSOR),

	    /**
	     * The west-resize cursor type.
	     */
	    W_RESIZE_CURSOR(Cursor.W_RESIZE_CURSOR),

	    /**
	     * The east-resize cursor type.
	     */
	    E_RESIZE_CURSOR(Cursor.E_RESIZE_CURSOR),

	    /**
	     * The hand cursor type.
	     */
	    HAND_CURSOR(Cursor.HAND_CURSOR),

	    /**
	     * The move cursor type.
	     */
	    MOVE_CURSOR(Cursor.DEFAULT_CURSOR);
	    
	    private Cursor _myJavaCursor;
	    
	    private CCCursor(final int theJavaID) {
	    	_myJavaCursor = new Cursor(theJavaID);
	    	
	    }
	}
	
	/**
	 * Use this method to hide the mouse cursor.
	 */
	public void noCursor(){
		_myGLComponent.setCursor(_myGLComponent.getToolkit().createCustomCursor(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB), new Point(10,10), "Cursor" ));
	}
	
	
	public void cursor(final CCCursor theCursor){
		_myGLComponent.setCursor(theCursor._myJavaCursor);
	}
	
	/**
	 * Use this method to activate the mouse cursor
	 */
	public void cursor() {
		cursor(CCCursor.DEFAULT_CURSOR);
	}
	

	//////////////////////////////////////////////////////////////
	//
	// ADD LISTENERS
	//
	//////////////////////////////////////////////////////////////
	
	/**
	 * @invisible
	 * @param theMouseWheelListener
	 */
	public void addMouseWheelListener(final MouseWheelListener theMouseWheelListener){
		_myGLComponent.addMouseWheelListener(theMouseWheelListener);
	}
	
	
	
	/**
	 * Called every time the mouse button has been pressed on the application window.
	 * @shortdesc called on mouse press
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 * 
	 */
	public void mousePressed(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called every time the mouse button has been released on the application window.
	 * @shortdesc called on mouse release
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseReleased(final CCMouseEvent theMouseEvent){};
	/**
	 * Called every time the mouse button has been clicked (pressed and released) on the application window.
	 * @shortdesc called on mouse click
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseClicked(final CCMouseEvent theMouseEvent){};
	/**
	 * Called every time the mouse has entered the application window.
	 * @shortdesc called on mouse enter
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseEntered(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called every time the mouse has left the application window.
	 * @shortdesc called on mouse exit
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseExited(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called when a mouse button is pressed on the application window and then 
     * dragged. Mouse drag events will continue to be delivered to the application
     * until the mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the application window).
     * @shortdesc called when the mouse is dragged over the application window
	 * @param theMouseEvent the related mouse event
	 * @example basics.CCMouseMoveTest
	 * @see CCMouseEvent
	 * @see #mouseDragged(CCMouseEvent)
	 */
	public void mouseMoved(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     * @shortdesc called when the mouse has moved over the application window
	 * @param theMouseEventn the related mouse event
	 * @example basics.CCMouseDragTest
	 * @see CCMouseEvent
	 * @see #mouseMoved(CCMouseEvent)
	 */
	public void mouseDragged(final CCMouseEvent theMouseEvent){};
	
	//////////////////////////////////////////////////////////////
	//
	// KEY HANDLING
	//
	//////////////////////////////////////////////////////////////
	
	@Override
	/**
	 * 
	 * @invisible
	 */
	public void keyPressed(KeyEvent theKeyEvent) {
		if(theKeyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
			dispose();
			System.exit(0);
		}
		if(theKeyEvent.getKeyCode() == _myUI.openKey()) {
			if(_myUI.isVisible())_myUI.hide();
			else _myUI.show();
		}
		
		if(theKeyEvent.getKeyCode() == KeyEvent.VK_S && theKeyEvent.isControlDown()){
			_myUI.save();
		}
		
		super.keyPressed(theKeyEvent);
	}
	
	/**
	 * The keyPressed() function is called once every time a key is pressed. 
	 * The key that was pressed is passed as key event. Because of how operating 
	 * systems handle key repeats, holding down a key may cause multiple calls to 
	 * keyPressed() (and keyReleased() as well). The rate of repeat is set by the 
	 * operating system and how each computer is configured.
	 * @shortdesc The keyPressed() function is called once every time a key is pressed. 
	 * @param theKeyEvent event object with all information on the pressed key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyPressed(final CCKeyEvent theKeyEvent){
	}
	
	/**
	 * The keyReleased() function is called once every time a key is released. 
	 * The key that was released is passed as key event. See key and 
	 * keyReleased for more information.
	 * @shortdesc The keyReleased() function is called once every time a key is released.
	 * @param theKeyEvent event object with all information on the released key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyReleased(final CCKeyEvent theKeyEvent){
	}
	
	/**
	 * The keyTyped() function is called once every time a key is pressed, 
	 * but action keys such as CTRL, SHIFT, and ALT are ignored. Because of
	 * how operating systems handle key repeats, holding down a key will cause 
	 * multiple calls to keyTyped(), the rate is set by the operating system 
	 * and how each computer is configured.
	 * @shortdesc The keyTyped() function is called once every time a  ASCII key is pressed, 
	 * @param theKeyEvent event object with all information on the typed key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyTyped(final CCKeyEvent theKeyEvent){
	}

	//////////////////////////////////////////////////////////////

	/**
	 * @invisible
	 */
	public void focusGained(final FocusEvent theFocusEvent){
		focusGained();
	}
	
	/**
	 * This method will be called every time the application window
	 * is focused.
	 */
	public void focusGained(){
		
	}

	/**
	 * @invisible
	 */
	public void focusLost(final FocusEvent theFocusEvent){
		focusLost();
	}
	
	/**
	 * This method will be called every time the application window
	 * loosed the focus.
	 */
	public void focusLost(){
		
	}
}
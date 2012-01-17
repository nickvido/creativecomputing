package cc.creativecomputing.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.DebugGL2ES2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;
import javax.media.opengl.GLES2;
import javax.media.opengl.TraceGL2;
import javax.media.opengl.TraceGL2ES2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;

import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCGlutFont.CCGlutFontType;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.texture.CCPixelStorageModes;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.util.CCClipSpaceFrustum;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.d.CCVector3d;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.util.logging.CCLog;


/**
 * This class represents the render module for creative computing
 * it contains all methods for drawing and is completely based on
 * OPENGL. OpenGL is strictly defined as "a software interface to 
 * graphics hardware." It is a 3D graphics and modeling library that 
 * is highly portable and very fast. Using OpenGL, you can create 
 * elegant and beautiful 3D graphics with nearly the visual quality 
 * of a ray tracer. Creative computing uses jogl as interface to 
 * OpenGL. It is aimed to simplify the access to OpengGL.
 * 
 * Every instance of CCApp has an instance of CCGraphics that
 * can be used for drawing.
 * @see CCApp 
 */
public class CCGraphicsCore<GLType extends GL2ES2>{
	
	public static CCGraphicsCore instance;
	
	private static boolean debug = false;
	
	/**
	 * Changes the plain gl implementation with a composable pipeline which wraps 
	 * an underlying GL implementation, providing error checking after each OpenGL 
	 * method call. If an error occurs, causes a GLException to be thrown at exactly 
	 * the point of failure. 
	 */
	public static void debug() {
		debug = true;
	}
	
	/**
	 * Ends debugging.
	 * @see #debug()
	 */
	public void noDebug() {
		debug = false;
	}
	
	public static GL2ES2 currentGL() {
		if(!debug)return GLU.getCurrentGL().getGL2();
		else return new DebugGL2(GLU.getCurrentGL().getGL2());
	}

	/**
	 * width of the parent application
	 */
	public int width;

	/**
	 * height of the parent application
	 */
	public int height;

	private CCCamera _myCamera;

	/**
	 * Gives you the possibility to directly access OPENGL
	 */
	public GL2ES2 gl;
	
	private GL2ES2 _myPlainGL;
	
	private DebugGL2ES2 _myDebugGL;
	
	private TraceGL2ES2 _myTraceGL2;

	/**
	 * Gives you the possibility to directly access OPENGLs utility functions
	 */
	public GLU glu;

	GLUtessellator tobj;

	/**
	 * true if the host system is big endian (PowerPC, MIPS, SPARC),
	 * false if little endian (x86 Intel).
	 */
	static private boolean BIG_ENDIAN = System.getProperty("sun.cpu.endian").equals("big");

	/**
	 * Quadratic object for drawing primitives and to define how 
	 * they have to be drawn
	 */
	private GLUquadric quadratic;
	

	private CCTexture[] _myTextures;

	public CCGraphicsCore(final GL2ES2 theGL){
		instance = this;
		gl = theGL;
		_myPlainGL = theGL;
		glu = new GLU();
		quadratic = glu.gluNewQuadric();
		
		gl.glClearDepth(1.0f);										// Depth Buffer Setup
	    gl.glDepthFunc(GL.GL_LEQUAL);									// The Type Of Depth Testing (Less Or Equal)
	    gl.glEnable(GL.GL_DEPTH_TEST);									// Enable Depth Testing
	   

		// 
		frontFace(CCFace.COUNTER_CLOCK_WISE);

		// these are necessary for alpha (i.e. fonts) to work
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		int myTextureUnits = textureUnits();
		_myTextures = new CCTexture[myTextureUnits];
	}
	
	///////////////////////////////////////////////////
	//
	// OPENGL INFORMATIONS
	//
	//////////////////////////////////////////////////

	private int[] _myIntegerGet = new int[1];
	
	public int getInteger(int theGLIID){
		gl.glGetIntegerv(theGLIID, _myIntegerGet,0);
		return _myIntegerGet[0];
	}
	
	public IntBuffer getIntBuffer(int theGLID, int theNumberOfValues){
		final IntBuffer myResult = IntBuffer.allocate(theNumberOfValues);
		gl.glGetIntegerv(theGLID, myResult);
		myResult.rewind();
		return myResult;
	}
	
	public int[] getIntArray(int theGLID, int theNumberOfValues){
		int[] result = new int[theNumberOfValues];
		gl.glGetIntegerv(theGLID, result,0);
		return result;
	}
	
	private float[] _myFloatGet = new float[1];
	
	public float getFloat(int theGLIID){
		gl.glGetFloatv(theGLIID, _myFloatGet,0);
		return _myFloatGet[0];
	}
	
	public FloatBuffer getFloatBuffer(int theGLID, int theNumberOfValues){
		final FloatBuffer myResult = FloatBuffer.allocate(theNumberOfValues);
		gl.glGetFloatv(theGLID, myResult);
		myResult.rewind();
		return myResult;
	}
	
	public float[] getFloatArray(int theGLID, int theNumberOfValues){
		float[] result = new float[theNumberOfValues];
		gl.glGetFloatv(theGLID, result,0);
		return result;
	}
	
	public String getString(int theGLID){
		return gl.glGetString(theGLID);
	}
	
	/**
	 * Returns the name of the hardware vendor.
	 * @return the name of the hardware vendor
	 */
	public String vendor(){
		return getString(GL.GL_VENDOR);
	}
	
	/**
	 * Returns a brand name or the name of the vendor dependent on the
	 * OPENGL implementation.
	 * @return brand name or name of the vendor
	 */
	public String renderer(){
		return getString(GL.GL_RENDERER);
	}
	
	/**
	 * returns the version number followed by a space and any vendor-specific information. 
	 * @return the version number
	 */
	public String version(){
		return getString(GL.GL_VERSION);
	}
	
	/**
	 * Returns an array with all the extensions that are available on the current hardware setup.
	 * @return the available extensions
	 */
	public String[] extensions(){
		return getString(GL.GL_EXTENSIONS).split(" ");
	}
	
	/**
	 * Returns true if the given extension is available at the current hardware setup.
	 * @param theExtension extension to check
	 * @return true if the extension is available otherwise false
	 */
	public boolean isExtensionSupported(final String theExtension){
		for(String myExtension:extensions()){
			if(myExtension.equals(theExtension))return true;
		}
		return false;
	}
	
	/**
	 * true if you want to report that no error occurred
	 */
	private boolean _myReportNoError = false;
	protected boolean _myReportErrors = true;
	
	/**
	 * Call this method to check for drawing errors. cc checks
	 * for drawing errors at the end of each frame automatically.
	 * However only the last error will be reported. You can call
	 * this method for debugging to find where errors occur. Error 
	 * codes are cleared when checked, and multiple error flags may 
	 * be currently active. To retrieve all errors, call this function 
	 * repeatedly until you get no error.
	 * @shortdesc Use this method to check for drawing errors.
	 */
	public void checkError(final String theString){
		switch(gl.glGetError()){
		case GL.GL_NO_ERROR:
			if(_myReportNoError)CCLog.error(theString + " # NO ERROR REPORTED");
			return;
		case GL.GL_INVALID_ENUM:
			CCLog.error(theString + " # INVALID ENUMERATION REPORTED. check for errors in OPENGL calls with constants.");
			return;
		case GL.GL_INVALID_VALUE:
			CCLog.error(theString + "# INVALID VALUE REPORTED. check for errors with passed values that are out of a defined range.");
			return;
		case GL.GL_INVALID_OPERATION:
			CCLog.error(theString + "# INVALID OPERATION REPORTED. check for function calls that are invalid in the current graphics state.");
			return;
		case GL.GL_OUT_OF_MEMORY:
			CCLog.error(theString + "# OUT OF MEMORY. not enough memory to execute the commands");
			return;
		}
	}
	
	public void checkError(){
		checkError("");
	}
	
	/**
	 * Use this method to tell cc if it should report no error
	 * @param theReportNoError
	 */
	public void reportNoError(final boolean theReportNoError){
		_myReportNoError = theReportNoError;
	}
	
	public void reportError(final boolean theReportError){
		_myReportErrors = theReportError;
	}

	protected boolean displayed = false;
	
	///////////////////////////////////////////////////
	//
	// GRAPHICS SETUP
	//
	//////////////////////////////////////////////////

	/**
	 * Called in response to a resize event, handles setting the
	 * new width and height internally.
	 * @invisible
	 */
	public void resize(final int theWidth, final int theHeight){ // ignore
		width = theWidth;
		height = theHeight;
	}

	boolean firstFrame = true;

	/**
	 * @invisible
	 */
	public void beginDraw(){
		if(debug) {
			if(_myDebugGL == null)_myDebugGL = new DebugGL2ES2(_myPlainGL);
			gl = _myDebugGL;
		}else {
			gl = _myPlainGL;
		}
	}

	/**
	 * @invisible
	 */
	public void endDraw(){
		if(_myReportErrors)checkError();
	}
	
	public static enum CCCompare{
		NEVER(GL.GL_NEVER),
		ALWAYS(GL.GL_ALWAYS),
		LESS(GL.GL_LESS),
		LESS_EQUAL(GL.GL_LEQUAL),
		GREATER(GL.GL_GREATER),
		GREATER_EQUAL(GL.GL_GEQUAL),
		EQUAL(GL.GL_EQUAL),
		NOT_EQUAL(GL.GL_NOTEQUAL);
		
		private final int glID;
		
		CCCompare(final int theGlID){
			glID = theGlID;
		}
	}

	/**
	 * Use this method to define a mask. You can use all available draw methods
	 * after it. After calling endMask everything drawn will be masked by the
	 * defined mask.
	 */
	public void beginMask(){
		colorMask(false, false, false, false);
		gl.glClearStencil(0x1);
		gl.glEnable(GL.GL_STENCIL_TEST);
        gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
        gl.glStencilFunc (GL.GL_ALWAYS, 0x1, 0x1);
        gl.glStencilOp (GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
        
	}

	/**
	 * Ends the mask
	 */
	public void endMask(){
		colorMask(true, true, true, true);
		gl.glStencilFunc (GL.GL_NOTEQUAL, 0x1, 0x1);
		gl.glStencilOp (GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
	}

	/**
	 * Disables a mask once you have defined one using beginMask and endMask
	 */
	public void noMask(){
		gl.glDisable(GL.GL_STENCIL_TEST);
	}
	
	public void scissor(final int theX, final int theY, final int theWidth, final int theHeight) {
		gl.glEnable(GL.GL_SCISSOR_TEST);
		gl.glScissor(theX, theY, theWidth, theHeight);
	}
	
	public void noScissor() {
		gl.glEnable(GL.GL_SCISSOR_TEST);
	}
	
	/**
	 * Specifies the depth comparison function.
	 * @author Riekoff
	 *
	 */
	public static enum CCDepthFunc{
		/**
		 * Never passes.
		 */
		NEVER(GL.GL_NEVER),
		/**
		 * Passes if the incoming depth value is less than the stored depth value.
		 */
		ALWAYS(GL.GL_ALWAYS),
		/**
		 * Passes if the incoming depth value is equal to the stored depth value.
		 */
		LESS(GL.GL_LESS),
		/**
		 * Passes if the incoming depth value is less than or equal to the stored depth value.
		 */
		LESS_EQUAL(GL.GL_LEQUAL),
		/**
		 * Passes if the incoming depth value is greater than the stored depth value.
		 */
		GREATER(GL.GL_GREATER),
		/**
		 * Passes if the incoming depth value is greater than or equal to the stored depth value.
		 */
		GREATER_EQUAL(GL.GL_GEQUAL),
		/**
		 * Passes if the incoming depth value is equal to the stored depth value.
		 */
		EQUAL(GL.GL_EQUAL),
		/**
		 * Passes if the incoming depth value is not equal to the stored depth value.
		 */
		NOT_EQUAL(GL.GL_NOTEQUAL);
		
		private final int glID;
		
		CCDepthFunc(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * Specifies the function used to compare each incoming pixel depth 
	 * value with the depth value present in the depth buffer. The comparison 
	 * is performed only if depth testing is enabled.
	 * </p>
	 * <p>
	 * The initial value of func is LESS_EQUAL. Initially, depth testing is disabled. 
	 * If depth testing is disabled or if no depth buffer exists, it is as if the depth test always passes.
	 * @param theCompare Specifies the depth comparison function.
	 */
	public void depthFunc(final CCDepthFunc theCompare){
		gl.glDepthFunc(theCompare.glID);
	}
	
	public void depthTest(){
		gl.glEnable(GL.GL_DEPTH_TEST);
	}
	
	public void noDepthTest(){
		gl.glDisable(GL.GL_DEPTH_TEST);
	}
	
	public void depthMask(){
		gl.glDepthMask(true);
	}

	public void noDepthMask(){
		gl.glDepthMask(false);
	}
	
	/**
	 * Clears the depth buffer
	 */
	public void clearDepthBuffer(){
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearDepth(final float theDefaultDepth){
		gl.glClearDepth(theDefaultDepth);
	}

	/**
	 * specifies the index used by clearStencil() to clear the stencil buffer. 
	 * s is masked with 2 m - 1 , where m is the number of bits in the stencil buffer.
	 * @param theS Specifies the index used when the stencil buffer is cleared. The initial value is 0.
	 */
	public void clearStencil(int theS){
		gl.glClearStencil(theS);
	}
	
	/**
	 * Clears the stencil buffer.
	 */
	public void clearStencilBuffer(){
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void stencilTest(){
		gl.glEnable(GL.GL_STENCIL_TEST);
	}

	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void noStencilTest(){
		gl.glDisable(GL.GL_STENCIL_TEST);
	}
	
	/**
	 * Specifies thestencil  test function.
	 * @author christianriekoff
	 *
	 */
	public static enum CCStencilFunction{
		/**
		 * Always fails.
		 */
		NEVER(GL.GL_NEVER),
		/**
		 * Passes if ( ref & mask ) < ( stencil & mask ).
		 */
		LESS(GL.GL_LESS),
		
		/**
		 * Passes if ( ref & mask ) <= ( stencil & mask ).
		 */
		LESS_EQUAL(GL.GL_LEQUAL),
		/**
		 * Passes if ( ref & mask ) > ( stencil & mask ).
		 */
		GREATER(GL.GL_GREATER),
		/**
		 * Passes if ( ref & mask ) >= ( stencil & mask ).
		 */
		GREATER_EQUAL(GL.GL_GEQUAL),
		/**
		 * Passes if ( ref & mask ) = ( stencil & mask ).
		 */
		EQUAL(GL.GL_EQUAL),
		/**
		 * Passes if ( ref & mask ) != ( stencil & mask ).
		 */
		NOT_EQUAL(GL.GL_NOTEQUAL),
		/**
		 * Always passes.
		 */
		ALWAYS(GL.GL_ALWAYS);
		
		
		private final int glID;
		
		CCStencilFunction(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * Stenciling, like depth-buffering, enables and disables drawing on a per-pixel basis. 
	 * Stencil planes are first drawn into using GL drawing primitives, then geometry and 
	 * images are rendered using the stencil planes to mask out portions of the screen. 
	 * Stenciling is typically used in multipass rendering algorithms to achieve special 
	 * effects, such as decals, outlining, and constructive solid geometry rendering.
	 * </p>
	 * <p>
	 * The stencil test conditionally eliminates a pixel based on the outcome of a comparison 
	 * between the reference value and the value in the stencil buffer. To enable and disable 
	 * the test, call {@linkplain #stencilTest()} and {@linkplain #noStencilTest()}. To specify 
	 * actions based on the outcome of the stencil test, call glStencilOp or glStencilOpSeparate.
	 * </p>
	 * <p>
	 * There can be two separate sets of func, ref, and mask parameters; one affects back-facing 
	 * polygons, and the other affects front-facing polygons as well as other non-polygon primitives. 
	 * glStencilFunc sets both front and back stencil state to the same values. Use glStencilFuncSeparate 
	 * to set front and back stencil state to different values.
	 * </p>
	 * <p>
	 * func is a symbolic constant that determines the stencil comparison function. It accepts 
	 * one of eight values, shown in the following list. ref is an integer reference value that 
	 * is used in the stencil comparison. It is clamped to the range 0 2 n - 1 , where n is the 
	 * number of bitplanes in the stencil buffer. mask is bitwise ANDed with both the reference 
	 * value and the stored stencil value, with the ANDed values participating in the comparison.
	 * </p>
	 * <p>
	 * If stencil represents the value stored in the corresponding stencil buffer location, the 
	 * following list shows the effect of each comparison function that can be specified by func. 
	 * Only if the comparison succeeds is the pixel passed through to the next stage in the 
	 * rasterization process (see glStencilOp). All tests treat stencil values as unsigned 
	 * integers in the range 0 2 n - 1 , where n is the number of bitplanes in the stencil buffer.
	 * </p>
	 * @param theFunc Specifies the test function. Eight symbolic constants are valid: 
	 * @param theRef 
	 * 		Specifies the reference value for the stencil test. ref is clamped to the range 
	 * 		0 2 n - 1 , where n is the number of bitplanes in the stencil buffer. The initial value is 0.
	 * @param theMask
	 * 		Specifies a mask that is ANDed with both the reference value and the stored stencil value when 
	 * 		the test is done. The initial value is all 1's.
	 */
	public void stencilFunc(CCStencilFunction theFunc, int theRef, int theMask){
		gl.glStencilFunc(theFunc.glID, theRef, theMask);
	}
	
	/**
	 * Specifies thestencil  test function.
	 * @author christianriekoff
	 *
	 */
	public static enum CCStencilOperation{
		/**
		 * Keeps the current value.
		 */
		KEEP(GL.GL_KEEP),
		/**
		 * Sets the stencil buffer value to 0.
		 */
		ZERO(GL.GL_ZERO),
		
		/**
		 * Sets the stencil buffer value to ref, as specified by stencilFunction.
		 */
		REPLACE(GL.GL_REPLACE),
		/**
		 * Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.
		 */
		INCREMENT(GL.GL_INCR),
		/**
		 * Increments the current stencil buffer value. Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.
		 */
		INCREMENT_WRAP(GL.GL_INCR_WRAP),
		/**
		 * Decrements the current stencil buffer value. Clamps to 0.
		 */
		DECREMENT(GL.GL_DECR),
		/**
		 * Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.
		 */
		DECREMENT_WRAP(GL.GL_DECR_WRAP),
		/**
		 * Bitwise inverts the current stencil buffer value.
		 */
		INVERT(GL.GL_INVERT);
		
		
		private final int glID;
		
		CCStencilOperation(final int theGlID){
			glID = theGlID;
		}
	}
	
	public void stencilOperation(
		CCStencilOperation theStencilTestFailOp,
		CCStencilOperation theDepthTestFailOp,
		CCStencilOperation ThePassOp
	){
		gl.glStencilOp(theStencilTestFailOp.glID, theDepthTestFailOp.glID, ThePassOp.glID);
	}
	
	public void stencilOperation(CCStencilOperation theOperation){
		stencilOperation(theOperation,theOperation,theOperation);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  COLOR HANDLING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the clear color for OPENGL the clear color is the color the
	 * background is filled with after the call of clear. As long as you haven't
	 * defined a clear color it will be set to black. Normally you once define a
	 * clear color and than use clear to clear the screen
	 */
	public void clearColor(final float theRed, final float theGreen, final float theBlue, final float theAlpha) {
		gl.glClearColor(theRed, theGreen, theBlue, theAlpha);
	}
	
	public void clearColor(final float theRed, final float theGreen, final float theBlue) {
		gl.glClearColor(theRed, theGreen, theBlue, 1);
	}
	
	public void clearColor(final float theGray, final float theAlpha){
		gl.glClearColor(theGray,theGray,theGray,theAlpha);
	}
	
	public void clearColor(final float theGray){
		gl.glClearColor(theGray,theGray,theGray,1);
	}
	
	public void clearColor(final CCColor theColor){
		clearColor(theColor.r,theColor.g,theColor.b,theColor.a);
	}
	
	public void clearColor(final int theRGB) {
		if (((theRGB & 0xff000000) == 0) && (theRGB <= 255)) {
			clearColor(theRGB, theRGB, theRGB);
		} else {
			clearColor(
				(theRGB >> 16) & 0xff,
				(theRGB >> 8)  & 0xff,
				(theRGB)	   & 0xff,
				(theRGB >> 24) & 0xff
			);
		}
	}
	
	public void clearColor(final int theGray, final int theAlpha){
		clearColor(theGray,theGray,theGray,theAlpha);
	}
	
	public void clearColor(final int theRed, final int theGreen, final int theBlue){
		clearColor((float)theRed/255, (float)theGreen/255, (float)theBlue/255);
	}
	
	public void clearColor(final int theRed, final int theGreen, final int theBlue, final int theAlpha){
		clearColor((float)theRed/255, (float)theGreen/255, (float)theBlue/255, (float)theAlpha/255);
	}
	
	public void clearColor(){
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Fills the background with the actual clear color, so that the screen is cleared.
	 * As long as you haven't defined clear color it will be set to black. 
	 * Normally you once define a clear color and than use clear to clear the screen
	 */
	public void clear() {
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * colorMask specifies whether the individual color components in the frame buffer 
	 * can or cannot be written. If theMaskRed is false, for example, no change is made to the red
	 * component of any pixel in any of the color buffers, regardless of the drawing operation attempted.
	 * @param theMaskRed
	 * @param theMaskGreen
	 * @param theMaskBlue
	 * @param theMaskAlpha
	 */
	public void colorMask(final boolean theMaskRed, final boolean theMaskGreen, final boolean theMaskBlue, final boolean theMaskAlpha) {
		gl.glColorMask(theMaskRed, theMaskGreen, theMaskBlue, theMaskAlpha);
	}
	
	/**
	 * Disables a previous color mask.
	 */
	public void noColorMask() {
		colorMask(true, true, true, true);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// DRAWING
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Specifies whether front- or back-facing facets are candidates for culling.
	 */
	public static enum CCCullFace{
		FRONT(GL.GL_FRONT),
		BACK(GL.GL_BACK),
		FRONT_AND_BACK(GL.GL_FRONT_AND_BACK);
		
		int glID;
		
		private CCCullFace(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * specify whether front- or back-facing facets can be culled. cullFace enables culling and 
	 * specifies whether front- or back-facing facets are culled (as specified by mode). 
	 * Facet culling is initially disabled. Facets include triangles, quadrilaterals,
	 * polygons, and rectangles.
	 * </p>
	 * <p>
	 * {@link #frontFace(CCFace)} specifies which of the clockwise and counterclockwise facets
	 * are front-facing and back-facing.
	 * </p>
	 * @param theFace 
	 * 		Specifies whether front- or back-facing facets are candidates for culling.
	 * 		CCCullFace.FRONT, CCCullFace.BACK, and CCCullFace.FRONT_AND_BACK are accepted.
	 * 		The initial value is CCCullFace.BACK.
	 * @see #frontFace(CCFace)
	 * @see #noCullFace()
	 */
	public void cullFace(CCCullFace theFace){
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(theFace.glID);
	}
	
	public void noCullFace(){
		gl.glDisable(GL.GL_CULL_FACE);
	}
	
	public static enum CCFace{
		CLOCK_WISE(GL.GL_CW),
		COUNTER_CLOCK_WISE(GL.GL_CCW);
		
		int glID;
		
		private CCFace(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * define front- and back-facing polygons. In a scene composed entirely of opaque closed surfaces,
	 * back-facing polygons are never visible. Eliminating these invisible polygons has the obvious benefit
	 * of speeding up the rendering of the image. To enable and disable elimination of back-facing polygons, 
	 * call cullFace with the desired mode.
	 * </p>
	 * <p>
	 * The projection of a polygon to window coordinates is said to have clockwise winding if an imaginary 
	 * object following the path from its first vertex, its second vertex, and so on, to its last vertex,
	 * and finally back to its first vertex, moves in a clockwise direction about the interior of the polygon.
	 * The polygon's winding is said to be counterclockwise if the imaginary object following the same path moves 
	 * in a counterclockwise direction about the interior of the polygon.
	 * </p>
	 * <p>
	 * frontFace specifies whether polygons with clockwise winding in window coordinates, or counterclockwise 
	 * winding in window coordinates, are taken to be front-facing. Passing CCFace.COUNTER_CLOCK_WISE to mode selects 
	 * counterclockwise polygons as front-facing; CCFace.CLOCK_WISE selects clockwise polygons as front-facing.
	 * By default, counterclockwise polygons are taken to be front-facing.
	 * </p>
	 * @param theFace 
	 * 		specifies the orientation of front-facing polygons.
	 * 		CCFace.CLOCK_WISE and CCFace.COUNTER_CLOCK_WISE are accepted.
	 * 		The initial value is CCFace.COUNTER_CLOCK_WISE.
	 * 
	 * @see #cullFace(CCCullFace)
	 */
	public void frontFace(final CCFace theFace) {
		gl.glFrontFace(theFace.glID);
	}
	
	/**
	 * By default, a line is drawn solid and one pixel wide.
	 * Use this method to set the width in pixels for rendered lines,
	 * the width size must be greater than 0.0 and by default is 1.0.
	 * <br>
	 * The actual rendering of lines is affected by the antialising mode. 
	 * Without antialiasing, widths of 1, 2, and 3 draw lines one, two, 
	 * and three pixels wide. With antialiasing enabled, nonintegral line 
	 * widths are possible, and pixels on the boundaries are typically partially filled.
	 * <br>
	 * Keep in mind that by default lines are one pixel wide, so they 
	 * appear wider on lower-resolution screens. For computer displays, 
	 * this isn't typically an issue, but if you're using OpenGL to render 
	 * to a high-resolution plotter, one-pixel lines might be nearly invisible. 
	 * To obtain resolution-independent line widths, you need to take into 
	 * account the physical dimensions of pixels. 
	 * @param lineWidth
	 */
	public void lineWidth(final float lineWidth){
		gl.glLineWidth(lineWidth);
	}
	
	
	
	/**
	 * Returns the current line width
	 * @return
	 */
	public float lineWidth(){
		return getFloat(GL.GL_LINE_WIDTH);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// TEXTURE / IMAGE HANDLING
	//
	//////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	
	/**
	 * Returns the maximum texture size supported by the hardware
	 */
	public int maxTextureSize() {
		return getInteger(GL.GL_MAX_TEXTURE_SIZE);
	}
	
	/**
	 * Returns the number of texture units that are supported
	 * by the graphics card. This allows using multi textures,
	 * to combine different textures.
	 */
	public int textureUnits(){
		return getInteger(GL2ES2.GL_MAX_TEXTURE_IMAGE_UNITS);
	}
	
	protected boolean _myDrawTexture = false;
	
	protected boolean[] _myTexturesInUse = new boolean[8];
	
	/**
	 * Specifies which texture unit to make active. 
	 * The number of texture units is implementation dependent, but must be at least two.
	 * @param theTextureUnit
	 */
	public void activeTexture(int theTextureUnit){
		gl.glActiveTexture(GL.GL_TEXTURE0 + theTextureUnit);
	}
	
	public void texture(final CCTexture theTexture){
		_myTextures[0] = theTexture;
		gl.glEnable(_myTextures[0].target().glID);
		_myTextures[0].bind();
		_myDrawTexture = true;
	}
	
	public void texture(final int theTextureUnit, final CCTexture theTexture){
		// GL_TEXTURE_RECTANGLE_ARB
		_myTextures[theTextureUnit] = theTexture;
		activeTexture(theTextureUnit);
		gl.glEnable(_myTextures[theTextureUnit].target().glID);
		
		_myTextures[theTextureUnit].bind();
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		
		_myDrawTexture = true;
	}
	
	public void texture(final int theTextureUnit, final CCTexture theTexture, final int theID) {
		_myTextures[theTextureUnit] = theTexture;
		gl.glActiveTexture(GL.GL_TEXTURE0 + theTextureUnit);
		gl.glEnable(_myTextures[theTextureUnit].target().glID);
		
		theTexture.bind(theID);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		
		_myDrawTexture = true;
	}
	
	public void noTexture(){
		for(int i = 0; i < _myTextures.length;i++){
			if(_myTextures[i] != null){
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glDisable(_myTextures[i].target().glID);
				_myTextures[i] = null;
			}
		}
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glDisable(GL.GL_TEXTURE_2D);
		_myDrawTexture = false;
	}
	
	/**
	 * <p>
	 * This enables texturing after you have disabled it. This
	 * method is not to confuse with the {@link #texture(CCAbstractTexture)} method
	 * which has to be used first to define which texture to use. So to use textures
	 * for drawing you call one of the texture methods and pass it the texture to use.
	 * If you do not need the texture anymore you call {@link #noTexture()}.
	 * </p>
	 * <p>
	 * Sometimes you might want to draw objects with one texture but inbetween you draw 
	 * objects that are not textured. Lets say you have a number of cubes and you only
	 * want to texture one side of the cube. You can use enableTextures and disableTextures.
	 * To say whether to use the texture or not. This avoid calling texture and noTexture that
	 * are more expensive performance wise.
	 * </p>
	 */
	public void enableTextures() {
		for(int i = 0; i < _myTextures.length;i++){
			if(_myTextures[i] != null){
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glEnable(_myTextures[i].target().glID);
			}
		}
	}
	
	public void disableTextures() {
		for(int i = 0; i < _myTextures.length;i++){
			if(_myTextures[i] != null){
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glDisable(_myTextures[i].target().glID);
			}
		}
	}
	
	
	
	/**
	 * Viewport specifies the affine transformation of x and y from normalized device coordinates to window coordinates.
	 * 
	 * Let <code>xnd</code> and <code>ynd</code> be normalized device coordinates. Then the window coordinates 
	 * <code>xw</code> and <code>yw</code> are computed as follows:
	 * 
	 * <pre>
	 * xw = (xnd + 1)(width / 2) + x
	 * yw = (xnd + 1)(height / 2) + y
	 * </pre>
	 * Viewport width and height are silently clamped to a range that depends on the implementation.
	 * When creative computing is started width and height are set to the dimensions of the application window.
	 * @param theX Specify the left corner of the viewport rectangle, in pixels. The initial value is 0.
	 * @param theY Specify the lower corner of the viewport rectangle, in pixels. The initial value is 0.
	 * @param theWidth Specify the width of the viewport. 
	 * @param theHeight Specify the height of the viewport. 
	 */
	public void viewport(final int theX, final int theY, final int theWidth, final int theHeight) {
		gl.glViewport(theX, theY, theWidth, theHeight);
	}

	// ////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////

	public void strokeWeight(final float weight){
		gl.glLineWidth(weight);
	}
	
	// ////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////

	public void smooth(){
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);
	}

	public void noSmooth(){
		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glDisable(GL.GL_BLEND);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// TEXTURE HANDLING
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////

	/// IntBuffer to go with the pixels[] array
	protected IntBuffer pixelBuffer;
	public int pixels[];
	
	public void loadPixels(){
		if ((pixels == null) || (pixels.length != width * height)){
			pixels = new int[width * height];
			pixelBuffer = CCBufferUtil.newIntBuffer(pixels.length);
		}

		gl.glReadPixels(0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
		pixelBuffer.get(pixels);
		pixelBuffer.rewind();

		// flip vertically (opengl stores images upside down),
		// and swap RGBA components to ARGB (big endian)
		int index = 0;
		int yindex = (height - 1) * width;
		for (int y = 0; y < height / 2; y++){
			if (BIG_ENDIAN){
				for (int x = 0; x < width; x++){
					int temp = pixels[index];
					// ignores alpha component, just sets it opaque
					pixels[index] = 0xff000000 | ((pixels[yindex] >> 8) & 0x00ffffff);
					pixels[yindex] = 0xff000000 | ((temp >> 8) & 0x00ffffff);

					index++;
					yindex++;
				}
			}else{ // LITTLE_ENDIAN, convert ABGR to ARGB
				for (int x = 0; x < width; x++){
					int temp = pixels[index];

					// identical to updatePixels because only two
					// components are being swapped
					pixels[index] = 0xff000000 | ((pixels[yindex] << 16) & 0xff0000) | (pixels[yindex] & 0xff00) | ((pixels[yindex] >> 16) & 0xff);

					pixels[yindex] = 0xff000000 | ((temp << 16) & 0xff0000) | (temp & 0xff00) | ((temp >> 16) & 0xff);

					index++;
					yindex++;
				}
			}
			yindex -= width * 2;
		}
	}
	
	 static final float EPSILON   = 0.0001f;
	

	//////////////////////////////////////////////////////////////

	IntBuffer getsetBuffer = CCBufferUtil.newIntBuffer(1);

	//int getset[] = new int[1];

	public int get(int x, int y){
		gl.glReadPixels(x, y, 1, 1, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, getsetBuffer);
		int getset = getsetBuffer.get(0);

		if (BIG_ENDIAN){
			return 0xff000000 | ((getset >> 8) & 0x00ffffff);

		}else{
			return 0xff000000 | ((getset << 16) & 0xff0000) | (getset & 0xff00) | ((getset >> 16) & 0xff);
		}
	}
	
	public static enum CCShapeMode{
		CORNER,CORNERS,RADIUS,CENTER;
	}

	public static final CCShapeMode CORNER = CCShapeMode.CORNER;
	public static final CCShapeMode CORNERS = CCShapeMode.CORNERS;
	public static final CCShapeMode CENTER_RADIUS = CCShapeMode.RADIUS;
	public static final CCShapeMode CENTER = CCShapeMode.CENTER; // former
																				// CENTER_DIAMETER
	protected CCShapeMode _myImageMode = CORNER;

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	//  COPYING OF SCREEN AND IMAGE PARTS
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	
	public void pixelUnpackStorage(final CCPixelStorageModes theStorageModes) {
		theStorageModes.unpackStorage();
	}
	
	public void copy(
		final int sx1, final int sy1, int sx2, int sy2, 
		final CCTexture des, 
		final int dx1, final int dy1, int dx2, int dy2
	){
		if (_myImageMode == CORNERS){
			sx2 = sx2 - sx1;
			sy2 = sy2 - sy1;
			dx2 = dx2 - dx1;
			dy2 = dy2 - dy1;
		}
		
		// Bind The Texture
		des.bind();

		gl.glCopyTexSubImage2D(des.target().glID, 0, dx1, dy1, sx1, sy1, sx2, sy2);
	}
	
	/**
	 * All possible blend factors
	 * @author info
	 *
	 */
	static public enum CCBlendFactor{
		/**
		 * (0,0,0,0)
		 */
		ZERO(GL.GL_ZERO),
		/**
		 * (1,1,1,1)
		 */
        ONE	(GL.GL_ONE),
        /**
         * (Rs,Gs,Bs,As)
         */
        SRC_COLOR(GL.GL_SRC_COLOR),
        /**
         * (1 - Rs, 1 - Gs, 1 - Bs, 1 - As)
         */
        ONE_MINUS_SRC_COLOR(GL.GL_ONE_MINUS_SRC_COLOR),
        /**
         * (Rd, Gd, Bd, Ad)
         */
        DST_COLOR(GL.GL_DST_COLOR),
        /**
         * (1 - Rd, 1 - Gd, 1 - Bd, 1 - Ad)
         */
        ONE_MINUS_DST_COLOR(GL.GL_ONE_MINUS_DST_COLOR),
        /**
         * (As, As, As, As)
         */
        SRC_ALPHA(GL.GL_SRC_ALPHA),
        /**
         * (1 - As, 1 - As, 1 - As, 1 - As)
         */
        ONE_MINUS_SRC_ALPHA(GL.GL_ONE_MINUS_SRC_ALPHA),
        /**
         * (Ad, Ad, Ad, Ad)
         */
        DST_ALPHA(GL.GL_DST_ALPHA),
        /**
         * (1 - Ad, 1 - Ad, 1 - Ad, 1 - Ad)
         */
        ONE_MINUS_DST_ALPHA(GL.GL_ONE_MINUS_DST_ALPHA),
        /**
         * Constant color is set by blendColor()
         * (Rbc, Gbc, Bbc, Abc)
         */
        CONSTANT_COLOR(GL2ES2.GL_CONSTANT_COLOR),
        /**
         * Constant color is set by blendColor()
         * (1 - Rbc, 1 - Gbc, 1 - Bbc, 1 - Abc)
         */
        ONE_MINUS_CONSTANT_COLOR(GL2ES2.GL_ONE_MINUS_CONSTANT_COLOR),
        /**
         * Constant color is set by blendColor()
         * (Abc, Abc, Abc, Abc)
         */
        CONSTANT_ALPHA(GL2ES2.GL_CONSTANT_ALPHA),
        /**
         * Constant color is set by blendColor()
         * (1 - Abc, 1 - Abc, 1 - Abc, 1 - Abc)
         */
        ONE_MINUS_CONSTANT_ALPHA(GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA),
        SRC_ALPHA_SATURATE(GL.GL_SRC_ALPHA_SATURATE);
        
		private final int glId;
		  
		private CCBlendFactor(final int theglID){
			glId = theglID;
		}
	}
	
	public static enum CCBlendEquation{
		ADD(GL.GL_FUNC_ADD), 
		SUBTRACT(GL.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL.GL_FUNC_REVERSE_SUBTRACT), 
        DARKEST(GL2GL3.GL_MIN), 
        LIGHTEST(GL2GL3.GL_MAX);
    
		private final int glId;
		  
		private CCBlendEquation(final int theglID){
			glId = theglID;
		}
	}
	
	/**
	 * blendColor may be used to calculate the source and destination
	 * blending factors. The color components are clamped to the range {0,1}
	 * before being stored. See beginBlend for a complete description of the
	 * blending operations. Initially the blend color is set to (0, 0, 0, 0).
	 * @param theColor
	 * @see #beginBlend()
	 */
	public void blendColor(final CCColor theColor) {
		gl.glBlendColor(theColor.r, theColor.g, theColor.b, theColor.a);
	}
	
	/**
	 * <p>
	 * In RGBA mode, pixels can be drawn using a function that blends
	 * the incoming (source) RGBA values with the RGBA values that are 
	 * already in the frame buffer (the destination values). Blending is 
	 * initially disabled. Use <code>blend()</code> and <code>noBlend()</code>
	 * to enable and disable blending.
	 * </p>
	 * <p>
	 * <code>blendMode</code> defines the operation of blending when it is enabled.
	 * sfactor specifies which method is used to scale the source color 
	 * components. dfactor specifies which method is used to scale the
	 * destination color components. The possible methods are described 
	 * in <code>CCBlendFactor</code> enumeration. Each method defines 
	 * four scale factors, one each for red, green, blue, and alpha.
	 * You can set different factors for the rgb and the alpha component.
	 * </p>
	 * <p>
	 * The blend equations determines how a new pixel (the ''source'' color)
	 * is combined with a pixel already in the framebuffer (the ''destination'' color).  
	 * The default equation is add, you can set this value to work equally on RGBA or
	 * set two different equations for RGB and alpha. The blend equations use the 
	 * specified source and destination blend factors
	 * </p>
	 * 
	 * @param theSrcFactor Specifies how the red, green, blue and alpha source blending factors are computed.
	 * @param theDstFactor Specifies how the red, green, blue and alpha destination blending factors are computed.
	 */
	public void blendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		
		gl.glBlendFunc(theSrcFactor.glId, theDstFactor.glId);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
	/**
	 * @param theSrcFactor Specifies how the red, green, blue and alpha source blending factors are computed.
	 * @param theDstFactor Specifies how the red, green, blue and alpha destination blending factors are computed.
	 * @param theEquation specifies how source and destination colors are combined
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, 
		final CCBlendFactor theDstFactor,
		final CCBlendEquation theEquation
	) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		
		gl.glBlendFunc(theSrcFactor.glId, theDstFactor.glId);
		gl.glBlendEquation(theEquation.glId);
	}
	
	/**
	 * @param theSrcFactor Specifies how the red, green, and blue blending factors are computed
	 * @param theDstFactor Specifies how the red, green, and blue destination blending factors are computed
	 * @param theSrcAlphaFactor Specified how the alpha source blending factor is computed
	 * @param theDstAlphaFactor Specified how the alpha destination blending factor is computed
	 * @param theColorEquation specifies the RGB blend equation, how the red, green and blue 
	 * 						  	components of the source and destination colors are combined
	 * @param theAlphaEquation specifies the alpha blend equation, how the alpha component of
	 * 							 the source and destination colors are combined
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, 
		final CCBlendFactor theSrcAlphaFactor, final CCBlendFactor theDstAlphaFactor,
		final CCBlendEquation theColorEquation, final CCBlendEquation theAlphaEquation
	) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		if(theDstAlphaFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination alpha factor");
		
		gl.glBlendFuncSeparate(theSrcFactor.glId, theDstFactor.glId, theSrcAlphaFactor.glId, theDstAlphaFactor.glId);
		gl.glBlendEquationSeparate(theColorEquation.glId, theAlphaEquation.glId);
	}
	
	/**
	 * @param theSrcFactor factor for source rgb
	 * @param theDstFactor factor for destination rgb
	 * @param theSrcAlphaFactor factor for source alpha
	 * @param theDstAlphaFactor factor for destination alpha
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, 
		final CCBlendFactor theSrcAlphaFactor, final CCBlendFactor theDstAlphaFactor
	) {
		gl.glBlendFuncSeparate(theSrcFactor.glId, theDstFactor.glId, theSrcAlphaFactor.glId, theDstAlphaFactor.glId);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
// blend mode keyword definitions

	  public final static CCBlendMode ADD        = CCBlendMode.ADD;
	  public final static CCBlendMode SUBTRACT   = CCBlendMode.SUBTRACT;
	  public final static CCBlendMode LIGHTEST   = CCBlendMode.LIGHTEST;
	  public final static CCBlendMode DARKEST    = CCBlendMode.DARKEST;
	  
	  /**
	   * Enumeration to collect useful blend settings
	   * @author info
	   *
	   */
	public static enum CCBlendMode {
		ALPHA(CCBlendFactor.ONE, CCBlendFactor.ONE_MINUS_SRC_ALPHA, CCBlendEquation.ADD), 
		BLEND(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE_MINUS_SRC_ALPHA, CCBlendEquation.ADD), 
		REPLACE(CCBlendFactor.ONE, CCBlendFactor.ZERO, CCBlendEquation.ADD), 
		ADD(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.ADD), 
		REVERSE_SUBTRACT(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.REVERSE_SUBTRACT), 
		SUBTRACT(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.SUBTRACT), 
		LIGHTEST(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.LIGHTEST), 
		DARKEST(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.DARKEST), 
		DARKEST_ALPHA(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.DARKEST);

		private final CCBlendFactor _mySrcFactor;
		private final CCBlendFactor _myDstFactor;
		private final CCBlendFactor _mySrcAlphaFactor;
		private final CCBlendFactor _myDstAlphaFactor;
		private final CCBlendEquation _myEquation;
		private final CCBlendEquation _myAlphaEquation;

		private CCBlendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, final CCBlendEquation theEquation) {
			_mySrcFactor = theSrcFactor;
			_myDstFactor = theDstFactor;
			_mySrcAlphaFactor = theSrcFactor;
			_myDstAlphaFactor = theDstFactor;
			_myEquation = theEquation;
			_myAlphaEquation = theEquation;
		}

		private CCBlendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, final CCBlendEquation theEquation, final CCBlendFactor theSrcAlphaFactor,
				final CCBlendFactor theDstAlphaFactor, final CCBlendEquation theAlphaEquation) {
			_mySrcFactor = theSrcFactor;
			_myDstFactor = theDstFactor;
			_mySrcAlphaFactor = theSrcAlphaFactor;
			_myDstAlphaFactor = theDstAlphaFactor;
			_myEquation = theEquation;
			_myAlphaEquation = theAlphaEquation;
		}
	}

	public void blendMode(final CCBlendMode theBlendMode){
		blendMode(
			theBlendMode._mySrcFactor, theBlendMode._myDstFactor, 
			theBlendMode._mySrcAlphaFactor, theBlendMode._myDstAlphaFactor,
			theBlendMode._myEquation, theBlendMode._myAlphaEquation
		);
	}

	public void endBlend(){
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
	public void blend(){
		blend(CCBlendMode.BLEND);
	}
	
	public void blend(final CCBlendMode theBlendMode) {
		gl.glEnable(GL.GL_BLEND);
		blendMode(theBlendMode);
	}
	
	public void noBlend(){
		endBlend();
		gl.glDisable(GL.GL_BLEND);
	}
}

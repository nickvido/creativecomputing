package cc.creativecomputing.graphics.texture;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import cc.creativecomputing.CCAppCapabilities;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;


public abstract class CCFrameBufferObject extends CCTexture2D{
	
	static int sMaxSamples = -1;

	/**
	 * Returns the maximum number of samples the graphics card is capable of using per pixel in MSAA for an Fbo
	 * @return maximum number of samples the graphics card is capable of using per pixel
	 */
	public static int getMaxSamples() {
		if( sMaxSamples < 0 ) {
			if(!CCAppCapabilities.GL_EXT_framebuffer_multisample || !CCAppCapabilities.GL_EXT_framebuffer_blit) {
				sMaxSamples = 0;
			}else {
				int[] myResult = new int[1];
				CCGraphics.currentGL().glGetIntegerv(GL2.GL_MAX_SAMPLES, myResult,0);
				sMaxSamples = myResult[0];
			}
		}
		
		return sMaxSamples;	
	}

	@SuppressWarnings("serial")
	public static class CCFrameBufferObjectException extends RuntimeException{

		public CCFrameBufferObjectException() {
			super();
		}

		public CCFrameBufferObjectException(String theMessage, Throwable theCause) {
			super(theMessage, theCause);
		}

		public CCFrameBufferObjectException(String theMessage) {
			super(theMessage);
		}

		public CCFrameBufferObjectException(Throwable theCause) {
			super(theCause);
		}
	
	}
	
	protected int[] _myFrameBuffers;
	/**
	 * ID of the frame buffer used for rendering this is different whether you
	 * use multisampling or not 
	 */
	private int _myRenderFrameBufferID = 0;
	
	private CCTexture2D _myDepthTexture;
	
	protected int[] _myDrawBuffers;
	private int _myBindIndex = 0;
	
	private int[] _myRenderBufferIDs;
	private boolean _myUseMultisampling;
	
	private CCFrameBufferObjectAttributes _myAttributes;
	protected int _myNumberOfAttachments;
	
	private int _myMaxAntialiasing;
	
	public CCFrameBufferObject(final CCTextureTarget theTarget, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theAttributes.numberOfColorBuffers(), theWidth, theHeight);
		
		_myAttributes = theAttributes;

		_myNumberOfAttachments = theAttributes.numberOfColorBuffers();
		
		_myUseMultisampling = theAttributes.samples() > 0;
		
		_myFrameBuffers = new int[2];
		
		GL2 gl = CCGraphics.currentGL();
		gl.glGenFramebuffers(2, _myFrameBuffers,0);

		_myDrawBuffers = new int[_myNumberOfAttachments];
		// if we don't need any variety of multisampling or it failed to initialize

		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0]);
	
		for(int i = 0; i < _myNumberOfAttachments;i++) {
			gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0 + i, _myTarget.glID, id(i), 0);
			_myDrawBuffers[i] = GL2.GL_COLOR_ATTACHMENT0 + i;
		}
		
		if( !(_myUseMultisampling) || !initMultisampling(gl)) { 
			_myUseMultisampling = false;
			init(gl);
		}

	}
	
	public CCFrameBufferObject(final CCTextureTarget theTarget, final int theWidth, final int theHeight){
		this(theTarget,new CCFrameBufferObjectAttributes(),theWidth, theHeight);
	}
	
	public CCFrameBufferObject(final int theNumberOfAttachments, final int theWidth, final int theHeight){
		this(CCTextureTarget.TEXTURE_2D, new CCFrameBufferObjectAttributes(theNumberOfAttachments), theWidth, theHeight);
	}
	
	public CCFrameBufferObject(final int theWidth, final int theHeight){
		this(CCTextureTarget.TEXTURE_2D, theWidth, theHeight);
	}

	/**
	 * Checks the current framebuffer status and throws an Exception if the fbo could not be built
	 * @param gl
	 */
	private void checkStatusException(GL2 gl) {
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		
		switch( status ) {
		case GL2.GL_FRAMEBUFFER_COMPLETE:
			return;
		case GL2.GL_FRAMEBUFFER_UNSUPPORTED:
			throw new CCFrameBufferObjectException("Unsupported framebuffer format");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: missing attachment");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: duplicate attachment");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: attached images must have same dimensions");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: attached images must have same format");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: missing draw buffer");
		case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
			throw new CCFrameBufferObjectException("Framebuffer incomplete: missing read buffer");
		default:
			throw new CCFrameBufferObjectException("Framebuffer invalid: unknown reason");
		}
	}
	
	/**
	 * Checks if the current framebuffer is completed
	 * @param gl
	 * @return
	 */
	private boolean checkStatus(GL2 gl) {
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		return status == GL2.GL_FRAMEBUFFER_COMPLETE;
	}
	
	private void init(GL2 gl) {
		
		// allocate and attach depth texture
		if(_myAttributes.hasDepthBuffer()) {
			CCTextureAttributes myDepthTextureAttributes = new CCTextureAttributes();
			myDepthTextureAttributes.filter(CCTextureFilter.LINEAR);
			myDepthTextureAttributes.wrap(CCTextureWrap.CLAMP_TO_EDGE);
			myDepthTextureAttributes.internalFormat(CCPixelInternalFormat.DEPTH_COMPONENT24);
			myDepthTextureAttributes.format(CCPixelFormat.DEPTH_COMPONENT);
			myDepthTextureAttributes.pixelType(CCPixelType.FLOAT);
			
			_myDepthTexture = new CCTexture2D(_myTarget, myDepthTextureAttributes, 1, _myWidth, _myHeight);
			gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, _myTarget.glID, _myDepthTexture.id(), 0 );
		}
		
		int stat = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if (stat != GL2.GL_FRAMEBUFFER_COMPLETE)
			CCLog.error("Framebuffer Object error!");
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}
	
	public boolean initMultisampling(GL2 gl) {
		_myRenderFrameBufferID = 1;
	
		_myMaxAntialiasing = getMaxSamples();
		                       
		int mySamples = CCMath.constrain(_myAttributes.samples(), 0, _myMaxAntialiasing);
		
		// create Render Buffer ids
		_myRenderBufferIDs = new int[_myNumberOfAttachments + 1];
		gl.glGenRenderbuffers(_myRenderBufferIDs.length, _myRenderBufferIDs,0);
		
		// create Multi sample depth buffer
		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, _myRenderBufferIDs[0]);
		gl.glRenderbufferStorageMultisample(
			GL2.GL_RENDERBUFFER, 
			mySamples, 
			GL2.GL_DEPTH_COMPONENT, 
			_myWidth, _myHeight
		);
		
		// create Multi sample colorbuffers
		for(int i = 0; i < _myNumberOfAttachments;i++) {
			gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, _myRenderBufferIDs[1 + i]);
			gl.glRenderbufferStorageMultisample(
				GL2.GL_RENDERBUFFER, 
				mySamples, 
				GL2.GL_RGBA8, 
				_myWidth, _myHeight
			);
		}
		
		// Attach them
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[1]);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, _myRenderBufferIDs[0]);
		
		for(int i = 0; i < _myNumberOfAttachments;i++) {
			gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0 + i, GL2.GL_RENDERBUFFER, _myRenderBufferIDs[1]);
		}
		
		// Final framebuffer color buffers
		textureFilter(CCTextureFilter.LINEAR);

		
		checkStatusException(gl);
		return checkStatus(gl);
	}
	
	private int[] mDepthRenderBufferId;
	private int[] mColorRenderBufferId;
	private int[] mResolveFramebufferId;
	
	public boolean initMultisample() {
		GL2 gl = CCGraphics.currentGL();
		mDepthRenderBufferId = new int[1];
		gl.glGenRenderbuffers(1, mDepthRenderBufferId,0);
		
		mColorRenderBufferId = new int[_myAttributes.numberOfColorBuffers()];
		gl.glGenRenderbuffers(_myAttributes.numberOfColorBuffers(), mColorRenderBufferId,0 );
		
		mResolveFramebufferId = new int[1];
		gl.glGenFramebuffers( 1, mResolveFramebufferId,0);
		
		// multisample, so we need to resolve from the FBO, bind the texture to the resolve FBO
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, mResolveFramebufferId[0]);
		
		for(int i = 0; i < _myAttributes.numberOfColorBuffers();i++) {
			gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0 + i, _myTarget.glID, id(i), 0);
		}
		
		// see if the resolve buffer is ok
		if(!checkStatus(gl))return false;
		
		if( _myAttributes.samples() > getMaxSamples() ) {
			_myAttributes.samples(getMaxSamples());
		}
		
		// setup the primary framebuffer
		for(int i = 0; i < _myAttributes.numberOfColorBuffers();i++) {
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0] );
			gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, mColorRenderBufferId[i]);
			
			
				// create a regular MSAA color buffer
				gl.glRenderbufferStorageMultisample(
					GL2.GL_RENDERBUFFER, 
					_myAttributes.samples(), 
					_myAttributes.internalFormat().glID, 
					_myWidth, _myHeight
				);
		
			// attach the multisampled color buffer
			gl.glFramebufferRenderbuffer(
				GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT0 + i, 
				GL2.GL_RENDERBUFFER, 
				mColorRenderBufferId[i]
			);
		}
		
		if(_myAttributes.hasDepthBuffer()) {
			gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, mDepthRenderBufferId[0]);
			// create the multisampled depth buffer (with or without coverage sampling)
			
				// create a regular (not coverage sampled) MSAA depth buffer
				gl.glRenderbufferStorageMultisample(
					GL2.GL_RENDERBUFFER, 
					_myAttributes.samples(), 
					_myAttributes.depthInternalFormat().glID, 
					_myWidth, _myHeight
				);
		
			// attach the depth buffer
			gl.glFramebufferRenderbuffer(
				GL2.GL_FRAMEBUFFER, 
				GL2.GL_DEPTH_ATTACHMENT, 
				GL2.GL_RENDERBUFFER, 
				mDepthRenderBufferId[0]
			);
		}
		
		// see if the primary framebuffer turned out ok
		return checkStatus(gl);
	}
	
	private void resolveTexture() {
		
		// if this FBO is multisampled, resolve it, so it can be displayed
		GL2 gl = CCGraphics.currentGL();
		
		if (mResolveFramebufferId != null) {
			int[] oldFb = new int[1];
			gl.glGetIntegerv(GL2.GL_FRAMEBUFFER_BINDING, oldFb,0);
			gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, _myFrameBuffers[0]);
			gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, mResolveFramebufferId[0]);
			gl.glBlitFramebuffer(0, 0, _myWidth, _myHeight, 0, 0, _myWidth, _myHeight, GL2.GL_COLOR_BUFFER_BIT, GL2.GL_NEAREST );
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, oldFb[0]);
		}
	
	}	
	
	public CCFrameBufferObjectAttributes attributes() {
		return _myAttributes;
	}
	
	public int numberOfAttachments() {
		return _myNumberOfAttachments;
	}
	
	private void updateMipmaps() {
		if(!_myGenerateMipmaps)return;
		
		GL2 gl = CCGraphics.currentGL();
		for(int i = 0; i < _myAttributes.numberOfColorBuffers();i++) {
			bind(i);
			gl.glGenerateMipmap(_myTarget.glID);
		}
	
	}
	
	@Override
	public void bind() {
		bind(_myBindIndex);
	}
	
	public void bindIndex(final int theBindIndex) {
		_myBindIndex = theBindIndex;
	}
	
	public CCTexture2D depthTexture() {
		return _myDepthTexture;
	}
	
	public void bindBuffer(){
		final GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0]);
	}
	
	public void unbindBuffer(){
		final GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}
	
	public void bindFBO(){
		final GL2 gl = CCGraphics.currentGL();
		// Directing rendering to the texture...
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[_myRenderFrameBufferID]);
		gl.glDrawBuffers(_myNumberOfAttachments,_myDrawBuffers,0);
	}
	
	public void bindFBO(final int theTexture) {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0 + theTexture);
	}
	
	public abstract void beginDraw();
	
	public void releaseFBO(){
		final GL2 gl = CCGraphics.currentGL();
		
		if(_myUseMultisampling) {
			gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, _myFrameBuffers[1]);
			gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, _myFrameBuffers[0]);
			gl.glBlitFramebuffer(0, 0, _myWidth, _myHeight, 0, 0, _myWidth, _myHeight, GL2.GL_COLOR_BUFFER_BIT, GL2.GL_NEAREST);
		}
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
		
		updateMipmaps();
	}

	public abstract void endDraw();
	
	@Override
	public void finalize(){
		super.finalize();
	}
}

package cc.creativecomputing.graphics;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import cc.creativecomputing.CCAppCapabilities;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.texture.CCFrameBufferObject;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;


public class CCRenderTexture extends CCFrameBufferObject{
	
	private CCCamera _myCamera;
	private CCGraphics _myGraphics;
	
	public CCRenderTexture(final CCGraphics theGraphics, final CCTextureTarget theTarget, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theWidth, theHeight);
		
		_myGraphics = theGraphics;
		_myCamera = new CCCamera(theGraphics,theWidth,theHeight);
	}
	
	public CCRenderTexture(final CCGraphics theGraphics, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, theAttributes, theWidth, theHeight);
	}
	
	public CCRenderTexture(CCGraphics theGraphics, final CCTextureTarget theTarget, final int theWidth, final int theHeight){
		this(theGraphics, theTarget,new CCFrameBufferObjectAttributes(),theWidth, theHeight);
	}
	
	public CCRenderTexture(CCGraphics theGraphics, final int theNumberOfAttachments, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, new CCFrameBufferObjectAttributes(theNumberOfAttachments), theWidth, theHeight);
	}
	
	public CCRenderTexture(CCGraphics theGraphics, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, theWidth, theHeight);
	}
	
	public void beginDraw() {
		bindFBO();
		_myGraphics.matrixMode(CCMatrixMode.PROJECTION);
		_myGraphics.pushMatrix();
		_myGraphics.matrixMode(CCMatrixMode.MODELVIEW);
		_myGraphics.pushMatrix();
		_myCamera.draw(_myGraphics);
	}

	public void endDraw() {
		_myGraphics.camera().draw(_myGraphics);
		_myGraphics.matrixMode(CCMatrixMode.PROJECTION);
		_myGraphics.popMatrix();
		_myGraphics.matrixMode(CCMatrixMode.MODELVIEW);
		_myGraphics.popMatrix();
		releaseFBO();
	}
	
	public CCCamera camera(){
		return _myCamera;
	}
	
	@Override
	public void finalize(){
		super.finalize();
	}
}

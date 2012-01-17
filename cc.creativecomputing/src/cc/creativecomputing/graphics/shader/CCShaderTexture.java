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
package cc.creativecomputing.graphics.shader;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCPBO;
import cc.creativecomputing.graphics.texture.CCFrameBufferObject;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCAABoundingRectangle;

/**
 * @author christianriekoff
 *
 */
public class CCShaderTexture extends CCFrameBufferObject{
	
	private static CCFrameBufferObjectAttributes attributes(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	) {
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
		myAttributes.enableDepthBuffer(false);
		myAttributes.filter(CCTextureFilter.NEAREST);
		myAttributes.numberOfColorBuffers(theNumberOfTextures);
		myAttributes.pixelType(CCPixelType.FLOAT);
		myAttributes.wrap(CCTextureWrap.CLAMP);
		
		boolean myIs16Bit;
		
		switch(theNumberOfBits){
		case 16:
			myIs16Bit = true;
			break;
		case 32:
			myIs16Bit = false;
			break;
		default:
			throw new CCShaderException("The given number of bits is not supported. You can only create shader textures with 16 or 32 bit resolution.");
		}
		
		boolean _myIsNvidia = CCGraphics.currentGL().glGetString(GL.GL_VENDOR).startsWith("NVIDIA");
		
		CCPixelFormat _myFormat;
		CCPixelInternalFormat _myInternalFormat;
		
		switch(theNumberOfChannels){
		case 1:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_R16_NV : CCPixelInternalFormat.FLOAT_R32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE;
			break;
		case 2:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_RG16_NV : CCPixelInternalFormat.FLOAT_RG32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE_ALPHA;
			break;
		case 3:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGB16F : CCPixelInternalFormat.RGB32F;
			_myFormat = CCPixelFormat.RGB;
			break;
		case 4:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGBA16F : CCPixelInternalFormat.RGBA32F;
			_myFormat = CCPixelFormat.RGBA;
			break;
		default:
			throw new CCShaderException("The given number of channels is not supported. You can only create shader textures with 1,2,3 or 4 channels.");
		
		}
		
		myAttributes.internalFormat(_myInternalFormat);
		myAttributes.format(_myFormat);
		
		return myAttributes;
	}
	
	private CCPBO[] _myPBO = new CCPBO[2];
	private int _myNumberOfChannels;
	private int _myNumberOfBits;
	
	public CCShaderTexture(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures, 
		final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		super(theTarget, attributes(theNumberOfBits,theNumberOfChannels,theNumberOfTextures), theWidth, theHeight);
		
		_myNumberOfChannels = theNumberOfChannels;
		_myNumberOfBits = theNumberOfBits;
		
		_myPBO[0] = new CCPBO(theNumberOfChannels * theWidth * theHeight * (theNumberOfBits == 16 ? 2 : 4));
		_myPBO[1] = new CCPBO(theNumberOfChannels * theWidth * theHeight * (theNumberOfBits == 16 ? 2 : 4));
		
//		clear();
	}
	
	public CCShaderTexture(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theNumberOfTextures, 
		final int theWidth,
		final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,theNumberOfTextures,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderTexture(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,theTarget);
	}
	
	public CCShaderTexture(final int theWidth, final int theHeight, final CCTextureTarget theTarget){
		this(32,3,theWidth,theHeight,theTarget);
	}
	
	public CCShaderTexture(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderTexture(final int theWidth, final int theHeight){
		this(32,3,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public int numberOfChannels() {
		return _myNumberOfChannels;
	}
	
	public int numberOfBits() {
		return _myNumberOfBits;
	}
	
	public void beginOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, _myWidth, _myHeight);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, _myWidth, 0, _myHeight,-1,1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.texture.CCFrameBufferObject#beginDraw()
	 */
	@Override
	public void beginDraw() {
		bindFBO();
		beginOrtho2D();
	}
	
	public void beginDraw(int theTexture) {
		bindFBO(theTexture);
		beginOrtho2D();
	}

	public void endOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glPopAttrib();
	}
	
	public void endDraw(){
		endOrtho2D();
		releaseFBO();
	}
	
	public void draw(){
		beginDraw();
	
		GL2 gl = CCGraphics.currentGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		drawQuad();
		
		endDraw();
	}
	
	public void drawQuad() {

		GL2 gl = CCGraphics.currentGL();
		switch (_myTarget) {
		case TEXTURE_2D:
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(0, 0);
			gl.glTexCoord2f(1f, 0);
			gl.glVertex2f(_myWidth, 0);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex2f(_myWidth, _myHeight);
			gl.glTexCoord2f(0, 1f);
			gl.glVertex2f(0, _myHeight);
			gl.glEnd();
			break;

		default:
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(0, 0);
			gl.glTexCoord2f(_myWidth, 0);
			gl.glVertex2f(_myWidth, 0);
			gl.glTexCoord2f(_myWidth, _myHeight);
			gl.glVertex2f(_myWidth, _myHeight);
			gl.glTexCoord2f(0, _myHeight);
			gl.glVertex2f(0, _myHeight);
			gl.glEnd();
			break;
		}
	}
	
	public void draw(CCAABoundingRectangle theRectangle) {
		beginDraw();
		GL2 gl = CCGraphics.currentGL();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(theRectangle.min().x, theRectangle.min().y);
		gl.glVertex2f(theRectangle.min().x, theRectangle.min().y);
		gl.glTexCoord2f(theRectangle.max().x, theRectangle.min().y);
		gl.glVertex2f(theRectangle.max().x, theRectangle.min().y);
		gl.glTexCoord2f(theRectangle.max().x, theRectangle.max().y);
		gl.glVertex2f(theRectangle.max().x, theRectangle.max().y);
		gl.glTexCoord2f(theRectangle.min().x, theRectangle.max().y);
		gl.glVertex2f(theRectangle.min().x, theRectangle.max().y);
		gl.glEnd();
		endDraw();
	}
	
	public void clear() {
		beginDraw();
		GL gl = CCGraphics.currentGL();
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		endDraw();
	}
	
	public FloatBuffer getData(){
		return getData(0, 0, 0, _myWidth, _myHeight);
	}
	
	public FloatBuffer getData(final int theX, final int theY, final int theWidth, final int theHeight){
		return getData(0, theX, theY, _myWidth, _myHeight);
	}
	
	public FloatBuffer getData(final int theTexture){
		FloatBuffer myResult = FloatBuffer.allocate(_myWidth * _myHeight * _myNumberOfChannels);
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theTexture]);
		gl.glReadPixels(0, 0, _myWidth, _myHeight,_myFormat.glID,GL.GL_FLOAT,myResult);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		return myResult;
	}
	
	public FloatBuffer getData(final int theTexture, final int theX, final int theY, final int theWidth, final int theHeight) {
		FloatBuffer myResult = FloatBuffer.allocate(theWidth * theHeight * _myNumberOfChannels);
		
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theTexture]);
		gl.glReadPixels(theX, theY, theWidth, theHeight,_myFormat.glID,GL.GL_FLOAT,myResult);
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		return myResult;
	}
	
	private int i = 0;
	
	public FloatBuffer getPBOData(final int theTexture) {
		return getPBOData(theTexture, 0, 0, _myWidth, _myHeight);
	}
	
	/**
	 * @param theTexture
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 * @return
	 */
	public FloatBuffer getPBOData(final int theTexture, final int theX, final int theY, final int theWidth, final int theHeight) {
		FloatBuffer myResult = FloatBuffer.allocate(theWidth * theHeight * _myNumberOfAttachments);
		
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theTexture]);
		
		_myPBO[i % 2].beginPack();
		gl.glReadPixels(theX, theY, theWidth, theHeight,_myFormat.glID,GL.GL_FLOAT,0);
		_myPBO[i % 2].endPack();
		
		myResult = _myPBO[(i + 1) % 2].mapReadBuffer().asFloatBuffer();
		_myPBO[(i + 1) % 2].unmapReadBuffer();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		i++;
		return myResult;
	}
	
	/**
	 * Read data from a floatbuffer
	 * @param theData
	 */
	public void loadData(final FloatBuffer theData){
		theData.rewind();
		GL gl = CCGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID,_myTextureIDs[0]);
		gl.glTexImage2D(_myTarget.glID,0,_myInternalFormat.glID,_myWidth,_myHeight,0,_myFormat.glID,GL.GL_FLOAT,theData);
		gl.glBindTexture(_myTarget.glID,0);
	}

}

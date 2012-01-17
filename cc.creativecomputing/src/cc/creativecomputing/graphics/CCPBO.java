package cc.creativecomputing.graphics;

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;

public class CCPBO {

	private int[] _myID = new int[1];
	
	public CCPBO(final int theDataSize){
		GL2 gl = CCGraphics.currentGL();
		gl.glGenBuffers(1, _myID,0);
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		gl.glBufferData(GL2.GL_PIXEL_PACK_BUFFER, theDataSize, null, GL2.GL_STREAM_READ);
        gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
	}
	
	public void beginUnpack(){
		GL2 gl = CCGraphics.currentGL();
        gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, _myID[0]);
	}
	
	/**
	 * it is good idea to release PBOs after use.
	 * So that all pixel operations behave normal ways.
	 */
	public void endUnpack(){
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);
	}
	
	public void beginPack(){
		GL2 gl = CCGraphics.currentGL();
        gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
	}
	
	/**
	 * it is good idea to release PBOs after use.
	 * So that all pixel operations behave normal ways.
	 */
	public void endPack(){
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
	}
	
	public ByteBuffer mapBuffer(){
		GL2 gl = CCGraphics.currentGL();
		return gl.glMapBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, GL2.GL_WRITE_ONLY);
	}
	
	public ByteBuffer mapReadBuffer() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		return gl.glMapBuffer(GL2.GL_PIXEL_PACK_BUFFER, GL2.GL_READ_ONLY);
	}
	
	public void unmapReadBuffer() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _myID[0]);
		gl.glUnmapBuffer(GL2.GL_PIXEL_PACK_BUFFER);
	}
	
	
	@Override
	public void finalize(){
		GL2 gl = CCGraphics.currentGL();
		gl.glDeleteBuffers(1, _myID,0);
	}
}

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
package cc.creativecomputing.graphics.texture;

import javax.media.opengl.GL;

import cc.creativecomputing.graphics.CCGraphics;

/**
 * @author christianriekoff
 *
 */
public class CCTextureCubeMap extends CCTexture{
	
	public static enum CCCubeMapSide{
		POSITIVE_X(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X),
		POSITIVE_Y(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
		POSITIVE_Z(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
		NEGATIVE_X(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
		NEGATIVE_Y(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
		NEGATIVE_Z(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
		
		public int glID;
		
		private CCCubeMapSide(final int theGLID) {
			glID = theGLID;
		}
	}
	
	private static final CCCubeMapSide cube[] = new CCCubeMapSide[]{
		CCCubeMapSide.POSITIVE_X, 
		CCCubeMapSide.NEGATIVE_X, 
		CCCubeMapSide.POSITIVE_Y, 
		CCCubeMapSide.NEGATIVE_Y, 
		CCCubeMapSide.POSITIVE_Z, 
		CCCubeMapSide.NEGATIVE_Z
	};

	/**
	 * @param theTarget
	 * @param theGenerateMipmaps
	 */
	public CCTextureCubeMap(CCTextureData theCubemapData) {
		super(CCTextureTarget.TEXTURE_CUBE_MAP);
		data(theCubemapData);
	}
	
	private void compressedData(final CCTextureData theData, final int theID) {
		CCGraphics.currentGL().glCompressedTexImage2D(
			cube[theID].glID, 0, theData.internalFormat().glID, 
			theData.width(), theData.height(), 0, 
			theData.buffer().capacity(), theData.buffer(theID)
		);
	}
	
	private void data(final CCTextureData theData, final int theID) {
		CCGraphics.currentGL().glTexImage2D(
			cube[theID].glID, 0, theData.internalFormat().glID, 
			theData.width(), theData.height(), 0, 
			theData.pixelFormat().glID, 
			theData.pixelType().glID, 
			theData.buffer(theID)
		);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.CCTextureNew#dataImplementation(cc.creativecomputing.texture_new.CCTextureDataNew)
	 */
	@Override
	public void dataImplementation(CCTextureData theData) {
		if(theData.mipmapData().length < 6) {
			throw new CCTextureException("Cubemap requires 6 images to be generated. Make sure you pass a data object that contains 6 images.");
		}
		// Load Cube Map images 
		if(theData.isDataCompressed()) {
			for(int i = 0; i < 6; i++){ 
				compressedData(theData, i);
			}
		}else {
			for(int i = 0; i < 6; i++){ 
				data(theData, i);
			}
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.CCTextureNew#updateData(cc.creativecomputing.texture_new.CCTextureDataNew)
	 */
	@Override
	public void updateData(CCTextureData theData) {
		// TODO Auto-generated method stub
		
	}

}
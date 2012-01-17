/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.cv.openni;

import java.nio.ByteBuffer;

import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIImageGenerator extends CCOpenNIMapGenerator<ImageGenerator>{
	
	public ImageGenerator create(Context theContext) {
		try {
			return ImageGenerator.create(theContext);
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	CCOpenNIImageGenerator(Context theContext) {
		super(theContext);
		
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelFormat.RGB, CCPixelType.UNSIGNED_BYTE);
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.mustFlipVertically(true);
	}
	
	private long _myLastTimeStamp = 0;
	
	public void update() {
		ByteBuffer myData = _myGenerator.getMetaData().getData().createByteBuffer();
		_myTextureData.buffer(myData);
		_myTexture.updateData(_myTextureData);
	}
}

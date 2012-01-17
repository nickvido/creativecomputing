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
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.IRGenerator;
import org.OpenNI.ImageGenerator;
import org.OpenNI.MapOutputMode;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIIRGenerator extends CCOpenNIMapGenerator<IRGenerator>{
	
	public IRGenerator create(Context theContext) {
		try {
			MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   // xRes, yRes, FPS
			IRGenerator myGenerator = IRGenerator.create(theContext);
			myGenerator.setMapOutputMode(mapMode); 
			return myGenerator;
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private ShortBuffer _myRawData;
	
	CCOpenNIIRGenerator(Context theContext) {
		super(theContext);
	      
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		_myTexture.mustFlipVertically(true);
	}
	
	private long _myLastTimeStamp = 0;
	
	private boolean _myNeedTextureUpdate = true;
	
	public void update() {
		_myNeedTextureUpdate = true;
		try {
			_myRawData = _myGenerator.getIRMap().createShortBuffer();
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	@Override
	public CCTexture2D texture() {
		if(_myNeedTextureUpdate) {
			_myNeedTextureUpdate = false;

			_myRawData.rewind();
			int minIR = _myRawData.get();
			int maxIR = minIR;

			while (_myRawData.remaining() > 0) {
				int irVal = _myRawData.get();
				if (irVal > maxIR)
					maxIR = irVal;
				if (irVal < minIR)
					minIR = irVal;
			}
		      
			FloatBuffer myFloatBuffer = (FloatBuffer)_myTextureData.buffer();
			myFloatBuffer.rewind();
			_myRawData.rewind();
			
			float ratio = maxIR - minIR;
			
			while(_myRawData.hasRemaining()) {
				int myValue = _myRawData.get();
				myFloatBuffer.put((myValue - minIR)/(float)maxIR);
			}
			myFloatBuffer.rewind();
			_myTexture.updateData(_myTextureData);
		}
		return _myTexture;
	}
}

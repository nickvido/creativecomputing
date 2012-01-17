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
package cc.creativecomputing.opencl;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat.ChannelOrder;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelStorageModes;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * @author christianriekoff
 *
 */
public class CCCLTextureData extends CCTextureData{

	private CLImage2d<Buffer> _myCLImage;
	private CCCLPixelFormat _myFormat;
	private CCCLPixelType _myType;
	
	CCCLTextureData(CLImage2d<Buffer> theImage2d, CCCLPixelFormat thePixelFormat, CCCLPixelType thePixelType){
		_myCLImage = theImage2d;
		_myFormat = thePixelFormat;
		_myType = thePixelType;
		
		_myWidth = theImage2d.width;
		_myHeight = theImage2d.height;
		_myBorder = 0;
		
		_myPixelInternalFormat = _myFormat.internalFormat();
		_myPixelFormat = _myFormat.format();
		_myPixelType = _myType.pixelType();
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = false;
		
		_myBuffer = new Buffer[] {theImage2d.getBuffer()};
		
		_myFlusher = null;
		_myPixelStorageModes.alignment(4);
		_myEstimatedMemorySize = estimatedMemorySize(_myBuffer[0]);
	}
	
	public CLImage2d<Buffer> clImage(){
		return _myCLImage;
	}
}

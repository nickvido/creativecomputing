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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.Plane3D;
import org.OpenNI.Point3D;
import org.OpenNI.SceneAnalyzer;
import org.OpenNI.StatusException;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.math.CCVector3f;

/**
 * A Scene Analyzer node is a Map Generator that performs scene analysis. 
 * @author christianriekoff
 *
 */
public class CCOpenNISceneAnalyzer extends CCOpenNIMapGenerator<SceneAnalyzer>{
	

	private CCVector3f _myPlanePoint = new CCVector3f();
	private CCVector3f _myPlaneNormal = new CCVector3f();

	private ShortBuffer _myRawData;

	/**
	 * @param theContext
	 */
	CCOpenNISceneAnalyzer(Context theContext) {
		super(theContext);
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		_myTexture.mustFlipVertically(true);
	}

	@Override
	SceneAnalyzer create(Context theContext) {
		try {
			return SceneAnalyzer.create(theContext);
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private boolean _myTextureNeedsUpdate = true;
	
	@Override
	void update() {
		_myTextureNeedsUpdate = true;
		_myRawData = _myGenerator.getMetaData().getData().createShortBuffer();
		
		FloatBuffer myFloatBuffer = (FloatBuffer)_myTextureData.buffer();
		myFloatBuffer.rewind();
		_myRawData.rewind();
		while(_myRawData.hasRemaining()) {
			int myValue = _myRawData.get();
			myFloatBuffer.put(myValue);
		}
		myFloatBuffer.rewind();
		_myTexture.updateData(_myTextureData);
		
		try {
			Plane3D myFloor = _myGenerator.getFloor();
			Point3D myPoint = myFloor.getPoint();
			Point3D myNormal = myFloor.getNormal();
			_myPlanePoint.set(myPoint.getX(), myPoint.getY(), myPoint.getZ());
			_myPlaneNormal.set(myNormal.getX(), myNormal.getY(), myNormal.getZ());
		} catch (StatusException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}
	
	/**
	 * A point on the floor plane.
	 * @return point on the floor plane
	 */
	public CCVector3f floorPoint() {
		return _myPlanePoint;
	}
	
	/**
	 * The normal of the floor plane.
	 * @return normal of the floor plane
	 */
	public CCVector3f floorNormal() {
		return _myPlaneNormal;
	}

	@Override
	
	/**
	 * Gets the current scene map as texture.
	 * @return
	 */
	public CCTexture2D texture() {
		return _myTexture;
	}
	
	/**
	 * Gets the current scene data
	 * @return the current scene data
	 */
	public ShortBuffer rawData() {
		return _myRawData;
	}
}

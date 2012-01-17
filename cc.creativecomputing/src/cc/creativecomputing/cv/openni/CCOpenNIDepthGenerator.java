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

import org.OpenNI.Capability;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.FieldOfView;
import org.OpenNI.GeneralException;
import org.OpenNI.IRGenerator;
import org.OpenNI.ImageGenerator;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;
import org.OpenNI.UserPositionCapability;
import org.OpenNI.WrapperUtils;


import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIDepthGenerator extends CCOpenNIMapGenerator<DepthGenerator>{
	
	private CCVector3f[] _myRealWorldData;
	private Point3D[] _myProjectiveDataP3;
	
	private ShortBuffer _myRawData;
	
	public DepthGenerator create(Context theContext) {
		try {
			return DepthGenerator.create(theContext);
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private UserPositionCapability _myUserPositionCapability;
	
	CCOpenNIDepthGenerator(Context theContext) {
		super(theContext);
		
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		_myTexture.mustFlipVertically(true);
		_myTexture.generateMipmaps(true);
		_myTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		try {
			_myUserPositionCapability = _myGenerator.getUserPositionCapability();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private long _myLastTimeStamp = 0;
	private boolean _myTextureNeedsUpdate = true;
	private boolean _myRealWorldNeedsUpdate = true;
	
	void update() {
		_myRealWorldNeedsUpdate = true;
		_myTextureNeedsUpdate = true;
		_myRawData = _myGenerator.getMetaData().getData().createShortBuffer();

		_myTextureNeedsUpdate = false;
		FloatBuffer myFloatBuffer = (FloatBuffer) _myTextureData.buffer();
		myFloatBuffer.rewind();
		_myRawData.rewind();
		float myMaxDepth = (float) maxDepth();
		while (_myRawData.hasRemaining()) {
			int myValue = _myRawData.get();
			myFloatBuffer.put(myValue / myMaxDepth);
		}
		myFloatBuffer.rewind();
		_myTexture.updateData(_myTextureData);
	}
	
	/**
	 * Converts a list of points from projective coordinates to real world coordinates.
	 * @param projectivePoints projective coordinates
	 * @return real world coordinates
	 */
	public CCVector3f[] convertProjectiveToRealWorld(CCVector3f[] projectivePoints){
		Point3D[] projective = new Point3D[projectivePoints.length];
		for(int i = 0; i < projective.length;i++) {
			projective[i] = new Point3D(
				projectivePoints[i].x, 
				projectivePoints[i].y, 
				projectivePoints[i].z
			);
		}
		Point3D[] realWorld;
		try {
			realWorld = _myGenerator.convertProjectiveToRealWorld(projective);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}

		CCVector3f[] realWorld3f = new CCVector3f[projectivePoints.length];
		for(int i = 0; i < projective.length;i++) {
			realWorld3f[i] = convert(realWorld[i]);
		}
		return realWorld3f;
	}
	
	/**
	 * Converts a point from projective coordinates to real world coordinates.
	 * @param projectivePoint projective coordinates
	 * @return real world coordinates
	 */
	public CCVector3f convertProjectiveToRealWorld(CCVector3f projectivePoint) {
		CCVector3f[] projectivePoints = new CCVector3f[1];
        projectivePoints[0] = projectivePoint;

        return convertProjectiveToRealWorld(projectivePoints)[0];
    }

	/**
	 * Converts a list of points from real world coordinates to projective coordinates.
	 * @param realWorldPoints real world coordinates
	 * @return projective coordinates
	 */
    public CCVector3f[] convertRealWorldToProjective(CCVector3f[] realWorldPoints){
    	Point3D[] realWorld = new Point3D[realWorldPoints.length];
		for(int i = 0; i < realWorld.length;i++) {
			realWorld[i] = new Point3D(
				realWorldPoints[i].x, 
				realWorldPoints[i].y, 
				realWorldPoints[i].z
			);
		}
		Point3D[] projective;
		try {
			projective = _myGenerator.convertProjectiveToRealWorld(realWorld);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}

		CCVector3f[] projective3f = new CCVector3f[projective.length];
		for(int i = 0; i < projective.length;i++) {
			projective3f[i] = convert(projective[i]);
		}
		return projective3f;
    }
    
    /**
	 * Converts a  point from real world coordinates to projective coordinates.
	 * @param realWorldPoint real world coordinates
	 * @return projective coordinates
	 */
    public CCVector3f convertRealWorldToProjective(CCVector3f realWorldPoint) {
    	CCVector3f[] realWorldPoints = new CCVector3f[1];
        realWorldPoints[0] = realWorldPoint;

        return convertRealWorldToProjective(realWorldPoints)[0];
    }
    
    public ShortBuffer depthMap() {
		return _myRawData;
	}
    
    private int _myStepSize = 1;

	public CCVector3f[] depthMapRealWorld(int theStepSize) {
		if(_myRealWorldData == null || _myStepSize != theStepSize) {
			_myStepSize = theStepSize;
			_myRealWorldData = new CCVector3f[_myGenerator.getDataSize() / _myStepSize / _myStepSize];
			_myProjectiveDataP3 = new Point3D[_myGenerator.getDataSize() / _myStepSize / _myStepSize];
			for(int i = 0; i < _myRealWorldData.length;i++) {
				_myRealWorldData[i] = new CCVector3f();
				_myProjectiveDataP3[i] = new Point3D();
			}
			_myRealWorldNeedsUpdate = true;
		}
		if(_myRealWorldNeedsUpdate) {
			_myRealWorldNeedsUpdate = false;
			_myRawData.rewind();
		    int i = 0;
		    for(int y = 0; y < _myHeight; y+= theStepSize){
		        for(int x = 0; x < _myWidth; x+= theStepSize){
		        	int id = y * _myWidth + x;
		        	_myProjectiveDataP3[i].setPoint(x,y,_myRawData.get(id));
		            i++;
		        }
		    }

		    // convert all point into realworld coord
		    try {

				Point3D[] realWorld = _myGenerator.convertProjectiveToRealWorld(_myProjectiveDataP3);
				for(i = 0; i < realWorld.length;i++) {
					_myRealWorldData[i].set(
						realWorld[i].getX(), 
						realWorld[i].getY(), 
						realWorld[i].getZ()
					);
				}
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
		return _myRealWorldData;
	}
	
	/**
	 * Gets the maximum depth the device can produce.
	 * @return
	 */
	public int maxDepth() {
		return _myGenerator.getDeviceMaxDepth();
	}
	
	/**
	 * Gets the Field-Of-View of the depth generator, in radians.
	 * @return
	 */
	public CCVector2f fieldOfView(){
		try {
			FieldOfView myFieldOfView = _myGenerator.getFieldOfView();
			return new CCVector2f(myFieldOfView.getHFOV(), myFieldOfView.getVFOV());
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
		
	}
	
	@Override
	/**
	 * Gets the current depth-map as texture.
	 * @return
	 */
	public CCTexture2D texture() {
		if(_myTextureNeedsUpdate) {
			
		}
		return _myTexture;
	}
}

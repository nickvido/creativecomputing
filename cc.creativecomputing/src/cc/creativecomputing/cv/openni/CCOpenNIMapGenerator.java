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

import org.OpenNI.Context;
import org.OpenNI.MapGenerator;
import org.OpenNI.MapOutputMode;
import org.OpenNI.StatusException;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * @author christianriekoff
 *
 */
abstract class CCOpenNIMapGenerator<MapGeneratorType extends MapGenerator> extends CCOpenNIGenerator<MapGeneratorType>{

	protected MapOutputMode _myMapOutputMode;
	
	protected int _myWidth;
	protected int _myHeight;
	protected int _myFPS;
	
	protected CCTextureData _myTextureData;
	protected CCTexture2D _myTexture;
	
	/**
	 * @param theGenerator
	 */
	CCOpenNIMapGenerator(Context theContext) {
		super(theContext);
		
		try {
			_myMapOutputMode = _myGenerator.getMapOutputMode();
//			_myMapOutputMode.setXRes(320);
//			_myMapOutputMode.setYRes(240);
//			_myGenerator.setMapOutputMode(_myMapOutputMode);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
		_myWidth = _myMapOutputMode.getXRes();
		_myHeight = _myMapOutputMode.getYRes();
		_myFPS = _myMapOutputMode.getFPS();
	}
	
	public CCTexture2D texture() {
		return _myTexture;
	}

	public int width() {
		return _myWidth;
	}
	
	public int height() {
		return _myHeight;
	}
	
	public int fps() {
		return _myFPS;
	}
}

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
package cc.creativecomputing.graphics.texture.video;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;

/**
 * @author christianriekoff
 *
 */
public class CCVideoTexture<VideoType extends CCVideoData> extends CCTexture2D implements CCVideoTextureDataListener{
	
	private VideoType _myMovie;
	
	public CCVideoTexture(VideoType theData, CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		video(theData);
	}
	
	public CCVideoTexture(VideoType theData){
		super(theData);
		video(theData);
	}
	
	public void video(VideoType theData){
		if(_myMovie != null)_myMovie.removeListener(this);
		_myMovie = theData;
		_myMovie.addListener(this);
	}
	
	public VideoType video(){
		return _myMovie;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onInit(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onInit(CCVideoData theData) {
		data(theData);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onUpdate(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onUpdate(CCVideoData theData) {
		updateData(theData);
	}

}

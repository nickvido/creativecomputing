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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCAbstractApp;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * This class is representing dynamic texture data the content of this object
 * might be fed from a movie or capture device and change. You can add listeners
 * to react on changes of the data.
 * @author christian riekoff
 *
 */
public abstract class CCVideoData extends CCTextureData implements CCUpdateListener, CCPostListener{
	
	/**
	 * indicates a needed update of the data although the movie is not running
	 * this might happen on change of the position
	 */
	protected boolean _myForceUpdate = false;
	
	/**
	 * indicates the initialization of the first frame on data update
	 */
	protected boolean _myIsFirstFrame;
	
	/**
	 * Keep the listeners for update events
	 */
	protected CCListenerManager<CCVideoTextureDataListener> _myListener = new CCListenerManager<CCVideoTextureDataListener>(CCVideoTextureDataListener.class);

	/**
	 * Creates a new instance, without setting any parameters.
	 * @param theApp
	 */
	public CCVideoData(final CCAbstractApp theApp) {
		super();
		_myPixelStorageModes.alignment(1);
		theApp.addUpdateListener(this);
		theApp.addPostListener(this);
	}
	
	/**
	 * Adds a listener to react on update events.
	 * @param theListener the listener 
	 */
	public void addListener(final CCVideoTextureDataListener theListener) {
		_myListener.add(theListener);
	}
	
	/**
	 * Removes a listener to react on update events.
	 * @param theListener the listener 
	 */
	public void removeListener(final CCVideoTextureDataListener theListener) {
		_myListener.remove(theListener);
	}
	
}

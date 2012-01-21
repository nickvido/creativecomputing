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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCQuicktimeMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCMovieLoopDemo extends CCApp {
	
	private CCGStreamerMovie _myMovie;
	private CCVideoTexture _myTexture;

	@Override
	public void setup() {
		frameRate(20);
		
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120116_spline2_fine2_1356x136_jpg.mov"));
		_myMovie = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/station.mov"));
		_myMovie.loop();
		_myMovie.time(20);
		
		_myTexture = new CCVideoTexture(_myMovie);
		g.clearColor(1f);
		g.clear();
	}

	@Override
	public void draw() {
		g.color(255,30);
//		System.out.println(_myTexture.width() + ":"+_myTexture.height());
		g.image(_myTexture,mouseX - width/2, height/2 - mouseY);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMovieLoopDemo.class);
		myManager.settings().size(320 * 2, 212 * 2);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


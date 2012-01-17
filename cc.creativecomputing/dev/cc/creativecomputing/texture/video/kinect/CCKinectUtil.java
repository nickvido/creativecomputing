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
package cc.creativecomputing.texture.video.kinect;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
	// These functions come from: http://graphics.stanford.edu/~mdfisher/Kinect.html
 */
public class CCKinectUtil {
	
	// We'll use a lookup table so that we don't have to repeat the math over and over
	private static float[] depthLookUp = new float[2048];
	
	static {
		// Lookup table for all possible depth values (0 - 2047)
		for (int i = 0; i < depthLookUp.length; i++) {
//			if (depthValue < 2047)	{
//				return (float)(1.0 / ((double)(depthValue) * -0.0030711016 + 3.3309495161));
//			}
//			return 0.0f;
		
			float k1 = 1.1863f; 
			float k2 = 2842.5f; 
			float k3 = 0.1236f;
        
			depthLookUp[i] = k3 * CCMath.tan(i/k2 + k1);
		}
	}
	
	/**
	 * Converts the given raw depth to a depth value in meters
	 * @param depthValue
	 * @return
	 */
	public static float rawDepthToMeters(int depthValue) {
		return depthLookUp[CCMath.constrain(depthValue, 0, CCKinect.DEPTH_RANGE)];
	}
	
	private static final double fx_d = 1.0 / 5.9421434211923247e+02;
	private static final double fy_d = 1.0 / 5.9104053696870778e+02;
	private static final double cx_d = 3.3930780975300314e+02;
	private static final double cy_d = 2.4273913761751615e+02;
	
	/**
	 * DepthToWorld maps from the depth camera's (x, y, depth) into world coordinates.
	 * @param x
	 * @param y
	 * @param depthValue
	 * @return world coordinates
	 */
	public static CCVector3f depthToWorld(double x, double y, int depthValue) {
		CCVector3f result = new CCVector3f();
		y = CCKinect.DEVICE_HEIGHT - y;
		double depth =  rawDepthToMeters(depthValue);
		result.x = (float)((x - cx_d) * depth * fx_d);
		result.y = (float)((y - cy_d) * depth * fy_d);
		result.z = (float)(depth);
		return result;
	}
	
	private static final double fx_rgb = 5.2921508098293293e+02;
	private static final double fy_rgb = 5.2556393630057437e+02;
	private static final double cx_rgb = 3.2894272028759258e+02;
	private static final double cy_rgb = 2.6748068171871557e+02;
	
	private static final CCVector3f translation = new CCVector3f(1.9985242312092553e-02f, -7.4423738761617583e-04f, -1.0916736334336222e-02f);
	
	private static final CCMatrix4f finalMatrix = new CCMatrix4f(
		new CCVector3f( 9.9984628826577793e-01f, 1.2635359098409581e-03f, -1.7487233004436643e-02f),
		new CCVector3f(-1.4779096108364480e-03f, 9.9992385683542895e-01f, -1.2251380107679535e-02f),
		new CCVector3f( 1.7470421412464927e-02f, 1.2275341476520762e-02f,  9.9977202419716948e-01f)
	).transpose().translate(translation);
	
	
	public static CCVector2i worldToColor(CCVector3f theWorldCoords){
//		CCVector3f transformedPos = finalMatrix.transform(theWorldCoords);
//	    float invZ = 1.0f / transformedPos.z;
//
//	    CCVector2i result = new CCVector2i();
//	    result.x = CCMath.constrain(CCMath.round((transformedPos.x * fx_rgb * invZ) + cx_rgb), 0, 639) - 14;
//	    result.y = CCMath.constrain(CCMath.round((transformedPos.y * fy_rgb * invZ) + cy_rgb), 0, 479) - 16;
//	    return result;
		
		CCVector3f v = theWorldCoords.clone();
		v.x = (v.x/9.9984628826577793e-01f) - 1.9985242312092553e-02f;
		v.y = (v.y/9.9984628826577793e-01f);
		v.z = (v.z/9.9984628826577793e-01f) - 1.9985242312092553e-02f;
		float invZ = 1.0f / v.z;
		
		CCVector2i result = new CCVector2i();
	    result.x = CCMath.constrain(CCMath.round((v.x * fx_rgb * invZ) + cx_rgb), 0, 639);
	    result.y = CCMath.constrain(CCMath.round((v.y * fy_rgb * invZ) + cy_rgb), 0, 479);
	    return result;
	}
	
	public static int depth2rgb(short depth){
		int r,g,b;

		float v = depth / 2047f;
		v = CCMath.pow(v, 3)* 6;
		v = v * 6 * 256;

		int pval = CCMath.round(v);
		int lb = pval & 0xff;
		switch (pval>>8) {
		case 0:
			b = 255;
			g = 255-lb;
			r = 255-lb;
			break;
		case 1:
			b = 255;
			g = lb;
			r = 0;
			break;
		case 2:
			b = 255-lb;
			g = 255;
			r = 0;
			break;
		case 3:
			b = 0;
			g = 255;
			r = lb;
			break;
		case 4:
			b = 0;
			g = 255-lb;
			r = 255;
			break;
		case 5:
			b = 0;
			g = 0;
			r = 255-lb;
			break;
		default:
			r = 0;
			g = 0;
			b = 0;
			break;
		}

		int pixel = (0xFF) << 24 | (b & 0xFF) << 16 | (g & 0xFF) << 8 | (r & 0xFF) << 0;

		return pixel;
	}

	public static int depth2intensity(short depth){
		int d = Math.round((1 - (depth / 2047f)) * 255f);
		int pixel = (0xFF) << 24 | (d & 0xFF) << 16 | (d & 0xFF) << 8 | (d & 0xFF) << 0;
		return pixel;
	}
}

package cc.creativecomputing.demo.graphics.shader.noise;

import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUFragmentPerlinNoiseTest extends CCApp {
	
	// problem size
	int texSize = width;
	
	int[] inputTexID = new int[1];
	
	CGparameter inputParam;
	CGparameter _myNoiseScaleParameter;
	CGparameter _myNoiseOffsetParameter;
	
	CCShaderTexture _myInputTexture;
	CCShaderTexture _myShaderTexture;
	CCCGShader _myShader;
	
	public void setup() {
		g.noBlend();
		_myInputTexture = new CCShaderTexture(32,3,texSize,texSize);
		_myShaderTexture = new CCShaderTexture(32,3,texSize,texSize);
		_myShader = new CCCGShader(null,"demo/gpu/util/testnoise.fp");
		_myShader.load();
		inputParam = _myShader.fragmentParameter("input");
		_myNoiseScaleParameter = _myShader.fragmentParameter("noiseScale");
		_myNoiseOffsetParameter = _myShader.fragmentParameter("noiseOffset");
		
		CCGPUNoise.attachFragmentNoise(_myShader);
		
		//
	    // create data vectors
	    //
		FloatBuffer inputData = FloatBuffer.allocate(texSize*texSize*3);
	    for (int x=-texSize/2; x < texSize/2; x++){
	    	for (int y=-texSize/2; y<texSize/2; y++){
	    		inputData.put(x * 0.01f);
	    		inputData.put(y * 0.01f);
	    		inputData.put(0);
	    	}
	    }
	    
	    inputData.rewind();
	    _myInputTexture.loadData(inputData);
	}
	
	float time = 0;
	CCVector2f _myOffset = new CCVector2f();
	CCVector2f _myOldOffset = new CCVector2f();
	CCVector2f _myNewOffset = new CCVector2f(10,10);
	
	float blend = 0;
	float max = 2;
	
	public void update(final float theDeltaTime){
		time += theDeltaTime * 1f;
		if(time > max){
			time -= max;
			_myOldOffset = _myNewOffset;
			_myNewOffset = CCVecMath.random(-10, 10, -10, 10);
		}
		blend = (CCMath.cos((time / max)*CCMath.PI + CCMath.PI)+1)/2;
		_myOffset = CCVecMath.blend(blend, _myOldOffset, _myNewOffset);
		System.out.println(_myOffset);
	}

	public void draw() {
		g.clear();
		_myShader.start();
		_myShader.parameter(_myNoiseScaleParameter, 1 - CCMath.abs((blend - 0.5f) * 2));
		_myShader.parameter(_myNoiseOffsetParameter, _myOffset);
		_myShader.texture(inputParam, _myInputTexture.id());
		_myShaderTexture.draw();
		_myShader.end();
		g.image(_myShaderTexture, -width/2,-height/2);
		System.out.println(frameRate);
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUFragmentPerlinNoiseTest.class);
		myManager.settings().size(1000, 1000);
		myManager.start();
	}
}

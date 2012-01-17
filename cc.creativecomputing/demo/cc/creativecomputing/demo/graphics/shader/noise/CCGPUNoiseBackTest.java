package cc.creativecomputing.demo.graphics.shader.noise;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlClass;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.math.util.CCArcball;

import com.jogamp.opengl.cg.CGparameter;

/**
 * This test demonstrates the basic use of the noise shader extension
 * @author info
 *
 */
public class CCGPUNoiseBackTest extends CCApp {
	
	@CCControlClass(name = "settings")
	public static class NoiseSettings{
		@CCControl(name = "noise scale", min = 0, max = 0.01f)
		public static float noiseScale;

		@CCControl(name = "noise pow", min = 0.1f, max = 4f)
		public static float noisePow;
	}
	
	private CCCGShader _myNoiseShader;
	
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseZParameter;
	private CGparameter _myNoisePowParameter;
	
	private float _myNoiseZ = 0;
	
	private CCArcball _myArcball;
	private CCVBOMesh _myMesh;
	
	public void setup() {
		g.clearColor(0);
		g.noBlend();
		
		addControls(CCGPUNoiseBackTest.class);
		
		_myNoiseShader = new CCCGShader("demo/gpu/util/noiseback.vp","demo/gpu/util/noiseback.fp");
		_myNoiseShader.load();
		
		_myNoiseScaleParameter = _myNoiseShader.vertexParameter("noiseScale");
		_myNoiseZParameter = _myNoiseShader.vertexParameter("noiseZ");
		_myNoisePowParameter = _myNoiseShader.vertexParameter("noisePow");
		CCGPUNoise.attachVertexNoise(_myNoiseShader);
		
		_myArcball = new CCArcball(this);
		_myMesh = new CCVBOMesh(CCGraphics.QUADS,400 * 400 * 4);
		
		for(float x = -400; x < 400; x +=4){
			for(float y = -400; y < 400; y +=4){
				_myMesh.addVertex(x, y, 0);
				_myMesh.addVertex(x+3,y,0);
				_myMesh.addVertex(x+3,y+3,0);
				_myMesh.addVertex(x,y+3,0);
			}
		}
		
		frameRate(30);
	}
	
	public void update(final float theDeltaTime){
		_myNoiseZ += theDeltaTime * 0.1f;
	}

	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myNoiseShader.start();
		_myNoiseShader.parameter(_myNoiseScaleParameter, NoiseSettings.noiseScale);
		_myNoiseShader.parameter(_myNoisePowParameter, NoiseSettings.noisePow);
		_myNoiseShader.parameter(_myNoiseZParameter, _myNoiseZ);
		_myMesh.draw(g);
		_myNoiseShader.end();
		System.out.println(frameRate);
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUNoiseBackTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

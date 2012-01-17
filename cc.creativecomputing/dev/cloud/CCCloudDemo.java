package cloud;

import com.jogamp.opengl.cg.CGparameter;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCCloudDemo extends CCApp {
	
	private class CCCloudParticle{
		private CCVector2f _myPosition;
		private float _mySpeed = CCMath.random();
		
		CCCloudParticle(float theX, float theY){
			_myPosition = new CCVector2f(theX, theY);
		}
		
		public void update(float theTime) {
			_myPosition.x += CCMath.blend(_cSpeedMin, _cSpeedMax, _mySpeed) * theTime;
			if(_myPosition.x > width / 2 + _myBlurRadius)_myPosition.x -= width + 2 * _myBlurRadius;
		}
		
		public float x() {
			return _myPosition.x;
		}
		
		public float y() {
			return _myPosition.y;
		}
	}
	
	@CCControl(name = "blur")
	private boolean _cBlur = true;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1f;
	
	@CCControl(name = "speed min", min = 0, max = 200)
	private float _cSpeedMin = 1f;
	
	@CCControl(name = "speed max", min = 0, max = 200)
	private float _cSpeedMax = 1f;
	
	@CCControl(name = "noise scale", min = 0, max = 200)
	private float _cNoiseScale = 0;
	
	private float _myNoiseX = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 2)
	private float _cNoiseSpeed = 1f;
	
	@CCControl(name = "noise amp1", min = 0, max = 1)
	private float _cNoiseAmp1 = 0;

	@CCControl(name = "noise amp2", min = 0, max = 1)
	private float _cNoiseAmp2 = 0;

	@CCControl(name = "particles", min = 0, max = 700)
	private int cParticles = 0;
	
	private int _myBlurRadius = 50;
	
	private CCCloudParticle[] _myParticles = new CCCloudParticle[700];
	private CCRenderTexture _myRenderTexture;
	
	private CCTexture2D _myBlurredTexture;
	private CCCGShader _myCloudShader;
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseAmp1Parameter;
	private CGparameter _myNoiseAmp2Parameter;
	private CGparameter _myNoiseOffsetParameter;

	@Override
	public void setup() {
		// CREATE CLOUD OF PARTICLES

		_myParticles = new CCCloudParticle[700];
		for (int i = 0; i < _myParticles.length; i++) {
//			do {
				_myParticles[i] = new CCCloudParticle(
					CCMath.random(-width/2,width/2),
					CCMath.random(-height/2,height/2)
				);
//			} while (_myParticles[i].lengthSquared() > width/2);
		}
		
		_myRenderTexture = new CCRenderTexture(g, width, height);
		_myBlurredTexture = new CCTexture2D();
		createBlurTexture(50);
		frameRate(20);
		
		_myCloudShader = new CCCGShader(
			CCIOUtil.classPath(this, "ccclouddemoshader.vp"),
			CCIOUtil.classPath(this, "ccclouddemoshader.fp")
		);
		_myNoiseScaleParameter = _myCloudShader.fragmentParameter("noiseScale");
		_myNoiseOffsetParameter = _myCloudShader.fragmentParameter("noiseOffset");
		_myNoiseAmp1Parameter = _myCloudShader.fragmentParameter("noiseAmp1");
		_myNoiseAmp2Parameter = _myCloudShader.fragmentParameter("noiseAmp2");
		_myCloudShader.load();
		CCGPUNoise.attachFragmentNoise(_myCloudShader);
		
		addControls("app", "app", this);
	}
	
	@CCControl(name = "blur radius", min = 1, max = 100)
	private void createBlurTexture(int theBlurRadius){
		_myBlurRadius = theBlurRadius;
		CCTextureData myData = new CCTextureData(theBlurRadius * 2 + 1, theBlurRadius * 2 + 1);
		for (int u = -theBlurRadius; u <= theBlurRadius; u++)
			for (int v = -theBlurRadius; v <= theBlurRadius; v++) {
				float un = u / ((float)theBlurRadius);
				float vn = v / ((float)theBlurRadius);
				float myValue = CCMath.max(0, 1 - un * un - vn * vn);
				CCColor myColor = new CCColor(myValue, myValue);
				myData.setPixel(u + theBlurRadius, v + theBlurRadius, myColor);
			}
		_myBlurredTexture.data(myData);
	}

	@Override
	public void update(final float theDeltaTime) {
		for(CCCloudParticle myParticle:_myParticles){
			myParticle.update(theDeltaTime);
		}
		_myNoiseX += _cNoiseSpeed * theDeltaTime;
	}

	@Override
	public void draw() {
		_myRenderTexture.beginDraw();
		g.clear();
		
		
		if(_cBlur) {
			g.noDepthTest();
			g.blend(CCBlendMode.ADD);
			g.color(1f, _cAlpha);
			g.texture(_myBlurredTexture);
			g.beginShape(CCDrawMode.QUADS);
			for(int i = 0; i < cParticles;i++){
				CCCloudParticle myParticle = _myParticles[i];
				g.textureCoords(0f, 0f);
				g.vertex(myParticle.x() - _myBlurRadius, myParticle.y() - _myBlurRadius);
				g.textureCoords(1f, 0f);
				g.vertex(myParticle.x() + _myBlurRadius, myParticle.y() - _myBlurRadius);
				g.textureCoords(1f, 1f);
				g.vertex(myParticle.x() + _myBlurRadius, myParticle.y() + _myBlurRadius);
				g.textureCoords(0f, 1f);
				g.vertex(myParticle.x() - _myBlurRadius, myParticle.y() + _myBlurRadius);
			}
			g.endShape();
			g.noTexture();
		}else {
			g.beginShape(CCDrawMode.POINTS);
			for(CCCloudParticle myParticle:_myParticles){
				g.vertex(myParticle._myPosition);
			}
			g.endShape();
		}
		_myRenderTexture.endDraw();
		
		g.blend();
		g.clear();
		g.color(1f);
		
		_myCloudShader.start();
		_myCloudShader.parameter(_myNoiseScaleParameter, _cNoiseScale);
		_myCloudShader.parameter(_myNoiseAmp1Parameter, _cNoiseAmp1);
		_myCloudShader.parameter(_myNoiseAmp2Parameter, _cNoiseAmp2);
		_myCloudShader.parameter(_myNoiseOffsetParameter, 0,0,_myNoiseX);
		g.texture(_myRenderTexture);
		g.beginShape(CCDrawMode.QUADS);
			g.textureCoords(0f, 0f);
			g.vertex(-width/2, -height/2);
			g.textureCoords(1f, 0f);
			g.vertex( width/2, -height/2);
			g.textureCoords(1f, 1f);
			g.vertex( width/2,  height/2);
			g.textureCoords(0f, 1f);
			g.vertex(-width/2,  height/2);
		g.endShape();
		g.noTexture();
		_myCloudShader.end();
		
		g.image(_myBlurredTexture,0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCloudDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

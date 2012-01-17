package cc.creativecomputing.demo.graphics.shader.imaging;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.CCGPUGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCBlurTest extends CCApp {
	
	private CCTexture2D _myTexture;
	private CCGPUGaussianBlur _myConvolutionFilter;

	public void setup() {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"));
		_myConvolutionFilter = new CCGPUGaussianBlur(g,5);
		_myConvolutionFilter.texture(_myTexture);
	}

	public void draw() {
		g.clear();
		_myConvolutionFilter.start();
		g.image(_myTexture, -width/2, -height/2);
		_myConvolutionFilter.end();

		g.image(_myTexture, 0, -height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlurTest.class);
		myManager.settings().size(1000, 500);
		myManager.start();
	}
}

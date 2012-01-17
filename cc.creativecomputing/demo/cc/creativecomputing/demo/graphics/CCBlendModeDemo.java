package cc.creativecomputing.demo.graphics;

 

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendFactor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCBlendModeDemo extends CCApp {
	
	private class BlendFactorSwitcher{
		private CCBlendFactor[] _myFactors = CCBlendFactor.values();
		private int _myIndex = 0;
		
		void next() {
			_myIndex = (_myIndex + 1 >= _myFactors.length) ? 0 : _myIndex + 1;
		}

		void prev() {
			_myIndex = (_myIndex - 1 < 0) ? _myFactors.length - 1 : _myIndex - 1;
		}

		String currentBlendFactorName() {
			return _myFactors[_myIndex].name();
		}
	}

	BlendFactorSwitcher _mySourceFactorSwitcher = new BlendFactorSwitcher();
	BlendFactorSwitcher _myDestinationFactorSwitcher = new BlendFactorSwitcher();

	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@CCControl(name = "source factor")
	private CCBlendFactor _mySourceFactor = CCBlendFactor.ZERO;
	
	@CCControl(name = "destination factor")
	private CCBlendFactor _myDestinationFactor = CCBlendFactor.ZERO;

	public void setup() {
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/01.jpg"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/02.jpg"));
		
		addControls("test", "blend", this);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.blend();
		g.blendMode(_mySourceFactor, _myDestinationFactor);

		g.image(_myTexture1, -width / 2, -height / 2);
		g.image(_myTexture2, -width / 2, -height / 2);
		
		g.noBlend();
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyChar()) {
		case 'e':
			_mySourceFactorSwitcher.next();
			System.out.println(
				"BLEND MODE : " + 
				_mySourceFactorSwitcher.currentBlendFactorName() + " , " + 
				_myDestinationFactorSwitcher.currentBlendFactorName());
			break;
		case 'd':
			_mySourceFactorSwitcher.prev();
			System.out.println(
				"BLEND MODE : " + 
				_mySourceFactorSwitcher.currentBlendFactorName() + " , " + 
				_myDestinationFactorSwitcher.currentBlendFactorName());
			break;
		case 'r':
			_myDestinationFactorSwitcher.next();
			System.out.println(
				"BLEND MODE : " + 
				_mySourceFactorSwitcher.currentBlendFactorName() + " , " + 
				_myDestinationFactorSwitcher.currentBlendFactorName());
			break;
		case 'f':
			_myDestinationFactorSwitcher.prev();
			System.out.println(
				"BLEND MODE : " + 
				_mySourceFactorSwitcher.currentBlendFactorName() + " , " + 
				_myDestinationFactorSwitcher.currentBlendFactorName());
			break;
		}

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlendModeDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

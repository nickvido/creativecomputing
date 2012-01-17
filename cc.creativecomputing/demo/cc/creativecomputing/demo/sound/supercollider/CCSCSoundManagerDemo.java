package cc.creativecomputing.demo.sound.supercollider;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.supercollider.CCSoundLibrary;
import cc.creativecomputing.sound.supercollider.SCSoundManager;

public class CCSCSoundManagerDemo extends CCApp {
	
	@CCControl(name = "volume", min=0f, max=1f)
	private static float _cVolume = 0f;
	
	private float _myPlayTime = 0;

	public CCSCSoundManagerDemo() {
		
		CCSoundLibrary myLib = new CCSoundLibrary();
		myLib.setSound("test0", CCIOUtil.dataPath("demo/sounds/one.wav"));
		myLib.setSound("test1", CCIOUtil.dataPath("demo/sounds/two.wav"));
		
		SCSoundManager.getInstance().setLibrary(myLib);
		addControls("audio", CCSCSoundManagerDemo.class);
	}
	
	public void update(float theDeltaTime) {
		if (_myPlayTime > 2) {
			_myPlayTime = 0;
			int myRandomNumber = CCMath.floor(CCMath.random(0,2));
			System.out.println("playing sound: test" + myRandomNumber);
			SCSoundManager.getInstance().play("test" + myRandomNumber, _cVolume, false);
		}
		_myPlayTime += theDeltaTime;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSCSoundManagerDemo.class);
		myManager.start();
	}

}

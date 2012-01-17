package cc.creativecomputing.sound.supercollider;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.io.CCIOUtil;


public class SCSoundManager {

    private static SCSoundManager _myInstance = null;
	
	CCSuperColliderClient _mySCClient = null;
	CCSoundLibrary _mySoundLibrary = null;
	Map<String, Integer> _myBuffIDMap = null;
	
	private SCSoundManager() {
		_mySCClient = new CCSuperColliderClient();
		_myBuffIDMap = new HashMap<String, Integer>();
		_mySCClient.dumpOSC(false);
		loadSynthDefs(null);
	}
	
	public CCSuperColliderClient client() {
		return _mySCClient;
	}
	
	public void client(CCSuperColliderClient theClient) {
		_mySCClient = theClient;
	}
	
	public void loadSynthDefs(String theFolder) {
		String myPath = theFolder;
		if (myPath == null) {
			myPath = CCIOUtil.dataPath("supercollider");
		}
		_mySCClient.createGroup(1, 1, 0);
        _mySCClient.loadSynthDefDirectory(CCIOUtil.dataPath("supercollider"));
		//_mySCClient.loadSynthDef(myPath + "/SoundPlayerMono.scsyndef");
		//_mySCClient.loadSynthDef(myPath + "/SoundPlayerStereo.scsyndef");
	}

    public static SCSoundManager getInstance() {
        if (_myInstance == null) {
            _myInstance = new SCSoundManager();
        }
        return _myInstance;
    }
    
    public int play(String theSoundID) {
    	return play(theSoundID, false);
    }
    
    public int play(String theSoundID, boolean theLoopFlag) {
    	return play(theSoundID, 1.0f, theLoopFlag);
    }

	public int play(String theSoundID, float theVolume, boolean theLoopFlag) {
		if (_myBuffIDMap.containsKey(theSoundID)) {
			CCSCBuffer myBuffer = _mySCClient.getBuffer(_myBuffIDMap.get(theSoundID));
			if (myBuffer != null) { // buffer loaded!
				Map<String, Object> myParams = new HashMap<String, Object>();
				myParams.put("bufnum", (Integer)myBuffer.id);
				myParams.put("loopFlag", theLoopFlag ? 1 : 0);
				myParams.put("volume", (Float)theVolume);
				String mySynthDefName = myBuffer.numChannels == 1 ? "SoundPlayerMono" : "SoundPlayerStereo";
				int myID = _mySCClient.createSynth(mySynthDefName, myParams);
				return myID;
			}
		}
        return -1;
	}

    public void stop(int theInstanceID) {
        _mySCClient.stopSynth(theInstanceID);
    }

	public void setLibrary(CCSoundLibrary theLibrary) {
		_mySoundLibrary = theLibrary;
		for (String theKey : _mySoundLibrary.keySet()) {
			CCSound mySound = _mySoundLibrary.get(theKey);
			int myBuffID = _mySCClient.loadSoundFile(mySound.path);
			_myBuffIDMap.put(mySound.id, myBuffID);
		}
	}
	
	public void stop() {
		_mySCClient.stop();
	}

	public void volume(int theInstanceId, float theVolume) {
		_mySCClient.setFloatParam(theInstanceId, "vol", theVolume);
	}

}

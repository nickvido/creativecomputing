package cc.creativecomputing.sound.supercollider;

import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.CCIOUtil;

import cc.creativecomputing.protocol.osc.CCOSCBundle;
import cc.creativecomputing.protocol.osc.CCOSCChannel;
import cc.creativecomputing.protocol.osc.CCOSCChannelAttributes;
import cc.creativecomputing.protocol.osc.CCOSCIn;
import cc.creativecomputing.protocol.osc.CCOSCListener;
import cc.creativecomputing.protocol.osc.CCOSCMessage;
import cc.creativecomputing.protocol.osc.CCOSCOut;
import cc.creativecomputing.protocol.osc.CCOSCProtocol;
import cc.creativecomputing.sound.supercollider.CCSCNode.NodeType;
import cc.creativecomputing.util.logging.CCLog;

public class CCSuperColliderClient implements CCOSCListener {

	CCOSCOut _myOscOut;
	CCOSCIn  _myOscIn;

	int _myNextBufID = 0;
	int _myNextSynthID = 1000;
	Map<Integer, CCSCBuffer> _myBufferMap;
	Map<Integer, CCSCNode>   _myNodeMap;
	
	boolean isWaitingForNodeID = false;

    Deque<String> _mySynthDefs = new ArrayDeque<String>();

	public CCSuperColliderClient(String theServerAddress, int theServerPort) 
	{
		_myBufferMap = new HashMap<Integer, CCSCBuffer>();
		_myNodeMap = new HashMap<Integer, CCSCNode>();
		_myOscOut = CCOSCChannel.createOut(new CCOSCChannelAttributes(CCOSCProtocol.UDP),theServerAddress, theServerPort);
		CCLog.info("localport: " + _myOscOut.localAddress());
		_myOscIn = CCOSCChannel.createIn(new CCOSCChannelAttributes(CCOSCProtocol.UDP));
		_myOscIn.addOSCListener(this);
		_myOscIn.startListening();
        
		setNextNodeID();
        notify(true);
	}
	
	public CCSuperColliderClient(int theServerPort) {
		this("localhost", theServerPort);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public CCSuperColliderClient() {
		this(57110);
	}

    public void notify(boolean theFlag) {
        int myNotify = theFlag ? 1 : 0;
        CCOSCMessage myMessage = new CCOSCMessage("/notify");
        myMessage.add(myNotify);
        _myOscOut.send(myMessage);
    }
    
    public void dumpOSC(boolean theFlag) {
    	int myNotify = theFlag ? 1 : 0;
    	CCOSCMessage myMessage = new CCOSCMessage("/dumpOSC");
    	myMessage.add(theFlag ? 1 : 0);
    	_myOscOut.send(myMessage);
    }

    public void loadSynthDef(String theDefFile) {
    	CCLog.info("CCSuperColliderClient: \"loading synth definition: " + theDefFile + "\"");
        byte[] myBytes;
        myBytes = CCIOUtil.loadBytes(theDefFile);
		CCOSCMessage myMessage = new CCOSCMessage("/d_recv");
		myMessage.add(myBytes);
	    _myOscOut.send(myMessage);
    }

    public void loadSynthDefDirectory(String thePath) {
        CCOSCMessage myMessage = new CCOSCMessage("/d_loadDir");
        myMessage.add(thePath);
        _myOscOut.send(myMessage);
    }

	public int loadSoundFile(String thePath) {
		int myBuffID = _myNextBufID++;
		CCOSCMessage myMessage = new CCOSCMessage("/b_allocRead");
		myMessage.add(myBuffID);
		myMessage.add(thePath);
		_myOscOut.send(myMessage);
		return myBuffID;
	}
	
	public void queryBuffer(int theBufferID) {
		CCOSCMessage myMessage = new CCOSCMessage("/b_query");
		myMessage.add(theBufferID);
		_myOscOut.send(myMessage);
	}
	
	public void querySynth(int theSynthID) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_query");
		myMessage.add(theSynthID);
		_myOscOut.send(myMessage);
	}
	
	public void setNextNodeID() {
		_myNextSynthID++;
		if (_myNextSynthID == Integer.MAX_VALUE) {
			_myNextSynthID = 999;
		}
	}
	
	public void queryStatus() {
		CCOSCMessage myMessage = new CCOSCMessage("/status");
		_myOscOut.send(myMessage);
	}
	
	public void setFloatParam(int theSynthID, String theParamName, float theValue) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_set");
		myMessage.add(theSynthID);
		myMessage.add(theParamName);
		myMessage.add(theValue);
		_myOscOut.send(myMessage);
	}
	
	public void setParam(int theSynthID, String theParamName, int theValue) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_set");
		myMessage.add(theSynthID);
		myMessage.add(theParamName);
		myMessage.add(theValue);
		_myOscOut.send(myMessage);
	}
	
	public void setParam(int theSynthID, String theParamName, float theValue) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_set");
		myMessage.add(theSynthID);
		myMessage.add(theParamName);
		myMessage.add(theValue);
		_myOscOut.send(myMessage);
	}
	
	public void run(int theSynthID, boolean theFlag) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_run");
		myMessage.add(theSynthID);
		myMessage.add(theFlag ? 1 : 0);
		_myOscOut.send(myMessage);
	}
	
	public void createGroup(int theGroupID, int theAddAction, int theTargetID) {
		CCOSCMessage myMessage = new CCOSCMessage("/g_new");
		myMessage.add(theGroupID);
		myMessage.add(theAddAction);
		myMessage.add(theTargetID);
		_myOscOut.send(myMessage);
	}
	
	public int createSynth(String theSynthDefName) {
		return createSynth(theSynthDefName, new HashMap<String, Object>());
	}
	
	public int createSynth(String theSynthDefName, Map<String, Object> theParams) {
		CCOSCMessage myMessage = new CCOSCMessage("/s_new");
		myMessage.add(theSynthDefName);
		int mySynthID = _myNextSynthID;
		myMessage.add(mySynthID);
		myMessage.add(1);
		myMessage.add(1);
		
		for (String myCommand : theParams.keySet()) {
			myMessage.add(myCommand);
			myMessage.add(theParams.get(myCommand));
		}
		_myOscOut.send(myMessage);
		setNextNodeID();
		return mySynthID;
	}
	
	public CCSCBuffer getBuffer(int theBufferID) {
		return _myBufferMap.get(theBufferID);
	}
	
	public void stopSynth(int theSynthId) {
		CCOSCMessage myMessage = new CCOSCMessage("/n_free");
		myMessage.add(theSynthId);
		_myOscOut.send(myMessage);
		_myNodeMap.remove(theSynthId);
	}
	
	public void stop() {
		for (CCSCNode mySynth : _myNodeMap.values()) {
			stopSynth(mySynth.id);
		}
	}

	@Override
	public void messageReceived(CCOSCMessage message, SocketAddress sender, long time) {
		
		String myCommand = message.address();
		List<Object> myArgs = message.arguments();
		
		if (myCommand.equals("/b_info") && message.numberOfArguments() == 4) {
			CCSCBuffer myBuf = new CCSCBuffer();
			myBuf.id = (Integer) myArgs.get(0);
			myBuf.numFrames = (Integer) myArgs.get(1);
			myBuf.numChannels = (Integer) myArgs.get(2);
			myBuf.sampleRate = (Float) myArgs.get(3);
			_myBufferMap.put(myBuf.id, myBuf);
		}
		else if (myCommand.equals("/n_go") || myCommand.equals("/n_info")) {
			CCSCNode myNode = new CCSCNode();
			myNode.id = (Integer) myArgs.get(0);
			myNode.parentId = (Integer) myArgs.get(1);
			myNode.previousId = (Integer) myArgs.get(2);
			myNode.nextId = (Integer) myArgs.get(3);
			myNode.type = ((Integer) myArgs.get(4)) == 0 ? NodeType.SYNTH : NodeType.GROUP;
			if (myNode.type == NodeType.GROUP) {
				myNode.headId = (Integer) myArgs.get(5);
				myNode.tailId = (Integer) myArgs.get(6);
			}
			_myNodeMap.put(myNode.id, myNode);
		} else if (myCommand.equals("/n_end")) {
			Integer myID = (Integer) myArgs.get(0);
			_myNodeMap.remove(myID);
		} else if (myCommand.equals("/fail")) {
			if (isWaitingForNodeID && myArgs.get(0).equals("/n_query")) {
				isWaitingForNodeID = false;
			}
		} else if (myCommand.equals("/status.reply")) {
            CCLog.info("num ugens: " + myArgs.get(1));
            CCLog.info("num synths: " + myArgs.get(2));
            CCLog.info("num groups: " + myArgs.get(3));
            CCLog.info("num synth defs: " + myArgs.get(4));
        } else if (myCommand.equals("/done")) {
        	String myMessage = (String) myArgs.get(0);
        	if (myMessage.equals("/b_allocRead")) {
        		Integer myBufID = (Integer) myArgs.get(1);
        		queryBuffer(myBufID);
        	} 
        }

	}

}

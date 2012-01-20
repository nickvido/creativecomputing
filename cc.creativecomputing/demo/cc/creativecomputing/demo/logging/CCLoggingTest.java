package cc.creativecomputing.demo.logging;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.util.logging.CCLog;

public class CCLoggingTest extends CCApp {
	
	public void setup() {		
		
		CCLog.info("Vendor:   " + g.vendor());
		CCLog.info("Renderer: " + g.renderer());
		CCLog.info("Version:  " + g.version());
		CCLog.warn("log incoming");
		CCLog.error("ALL log incoming");
		CCLog.info("plopp"); 
		List<String> list = new ArrayList<String>();
		
		while(true)
		{
			list.add("speicher voll?!");
		}	
	}
	
	public static void main(String[] args) {
		CCLog.info("calling setup..");		
		CCApplicationManager myManager = new CCApplicationManager(CCLoggingTest.class);
		myManager.settings().size(400, 400);
 		CCLog.info("starting application");
		myManager.start();
	}
}
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
package cc.creativecomputing.watchdog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

/**
 * @author info
 * 
 */
public class CCWatchDog extends Thread{
	
	private int _myTime;
	private int _myStartTime;
	private int _myRestartTime;
	private int _myWaitTime;
	private boolean _myStartedProcess;
	
	private String _myClassName;
	
	private List<String> _myLibraries = new ArrayList<String>();
	private List<String> _myVirtualMachineOptions = new ArrayList<String>();
	
	private ProcessBuilder _myProcessBuilder;
	private Process _myProcess;
	
	public CCWatchDog() {
		super();
		loadSettings("process.xml");
		
		List<String> myArgumentList = new ArrayList<String>();
		myArgumentList.add("java");
		myArgumentList.add("-cp");
		myArgumentList.addAll(_myLibraries);
		myArgumentList.addAll(_myVirtualMachineOptions);
		myArgumentList.add(_myClassName);
		
		_myProcessBuilder = new ProcessBuilder(myArgumentList);
		_myStartedProcess = false;
		_myWaitTime = _myStartTime;
	}
	
	private void loadSettings(final String theFile) {
		CCXMLElement myProcessXML = CCXMLIO.createXMLElement(theFile);
		
		CCXMLElement myStartTimeXML = myProcessXML.child("starttime");
		if(myStartTimeXML != null)_myStartTime = myStartTimeXML.intContent(0);

		CCXMLElement myRestartTimeXML = myProcessXML.child("restarttime");
		if(myRestartTimeXML != null)_myRestartTime = myRestartTimeXML.intContent(0);
		
		CCXMLElement myClassNameXML = myProcessXML.child("class");
		if(myClassNameXML == null)throw new RuntimeException("You have to define a class to start inside the process.xml!");
		_myClassName = myClassNameXML.content();
		
		CCXMLElement myClassPathXML = myProcessXML.child("classpath");
		if(myClassPathXML == null)throw new RuntimeException("You have to define a classpath inside the process.xml!");
		
		for(CCXMLElement myLibXML:myClassPathXML) {
			_myLibraries.add(myLibXML.content());
		}
		
		CCXMLElement myVMParametersXML = myProcessXML.child("vm_options");
		if(myVMParametersXML != null) {
			for(CCXMLElement myVMParameterXML:myVMParametersXML) {
				_myVirtualMachineOptions.add(myVMParameterXML.content());
			}
		}
	}
	
	public void run() {
		
		
		try {

			while (true) {
				System.out.println(_myTime);
				long myTime = System.currentTimeMillis();
				if(!_myStartedProcess && _myTime >= _myWaitTime * 1000) {
					_myProcess = _myProcessBuilder.start();
					_myStartedProcess = true;
				}
				
				if(_myStartedProcess) {
					try {
						System.out.println(_myProcess.exitValue());
						System.out.println("RESTART");
						_myWaitTime = _myRestartTime;
						_myStartedProcess = false;
						_myTime = 0;
					} catch (IllegalThreadStateException e) {
						// ignore this process is still running which is exactly what we want
					}

					Scanner scanner = new Scanner(_myProcess.getInputStream());
					while (scanner.hasNextLine())
						System.out.println(scanner.nextLine());
					
					scanner = new Scanner(_myProcess.getErrorStream());
					while (scanner.hasNextLine())
						System.out.println(scanner.nextLine());

				}
				Thread.sleep(10);
				

				_myTime += System.currentTimeMillis() - myTime;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CCWatchDog myWatchDog = new CCWatchDog();
		myWatchDog.start();
		
		while(true) {
			System.out.println("YEAH");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

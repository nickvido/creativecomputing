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
package cc.creativecomputing.watchdog.test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author info
 * 
 */
public class CCTestClass {
	static class Task extends TimerTask {
		@Override
		public void run() {
			System.out.println("Make my day.");
		}
	}
	
	static class ExceptionTask extends TimerTask {
		@Override
		public void run() {
			throw new RuntimeException("FUCKER");
		}
	}

	public static void main(String[] args) {
		System.out.println("YO");
		System.out.println("YO");
		Timer timer = new Timer(); 
		 
	    // nach zwei Sekunden geht’s los 
	    timer.schedule( new Task(), 2000,3000 ); 
	 
	    // nach einer Sekunde geht’s los und dann alle fünf Sekunden 
//	    timer.schedule( new ExceptionTask(), 5000 ); 
//		System.out.println("YO");
	}
}

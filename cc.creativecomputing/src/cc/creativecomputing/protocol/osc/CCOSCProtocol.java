/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.protocol.osc;

/**
 * @author christianriekoff
 *
 */
public enum CCOSCProtocol {
	/**
	 * Protocol type : user datagram protocol. <A
	 * HREF="http://en.wikipedia.org/wiki/User_Datagram_Protocol">en.wikipedia.org/wiki/User_Datagram_Protocol</A> for
	 * explanation
	 */
	UDP, 
	
	/**
	 * Protocol type : transmission control protocol. <A
	 * HREF="http://en.wikipedia.org/wiki/Transmission_Control_Protocol"
	 * >en.wikipedia.org/wiki/Transmission_Control_Protocol</A> for explanation
	 */
	TCP
	

	// /**
	// * Protocol type :
	// * (currently not supported)
	// */
	// public static final String PIPE = "pipe";
	// /**
	// * Protocol type :
	// * (currently not supported)
	// */
	// public static final String FILE = "file";
}

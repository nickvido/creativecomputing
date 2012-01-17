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
package cc.creativecomputing.net;

import java.util.List;
import java.util.Map;

import cc.creativecomputing.net.CCHttpRequest.CCHttpContentType;
import cc.creativecomputing.net.CCHttpServer.CCHttpServerListener;

/**
 * @author christianriekoff
 *
 */
public class CCCommandHttpServer extends CCHttpServer implements CCHttpServerListener{
	
	private static String HTML_START = 
		"<html>" +
		"<head>" +
		"<title>creative computing command server feedback</title>" +
		"</head>" +
		"<body>";
		; 
	private static String HTML_END = 
		"</body>" +
		"</html>";
	
	public static class CCHttpCommandParameter{
		private String _myName;
		private String _myValue;
	}

	public abstract static class CCHttpCommand{
		private String _myCommand;
		
		public CCHttpCommand(String theCommand) {
			_myCommand = theCommand;
		}
		
		public abstract void onCall();
	}
	
	public CCCommandHttpServer(CCHttpServer theServer) {
		theServer.addListener(this);
	}
	
	public CCCommandHttpServer(int thePort) {
		CCHttpServer myServer = new CCHttpServer(thePort);
		myServer.addListener(this);
	}
	
	public CCCommandHttpServer() {
		this(8080);
	}
	
	private void appendLink(StringBuilder theBuilder, String theLink, String theText) {
		theBuilder.append("<a href=\"");
		theBuilder.append(theLink);
		theBuilder.append("\">");
		theBuilder.append(theText);
		theBuilder.append("</a>");
	}
	
	private void appendHeadline(StringBuilder theBuilder, int theLevel, String theText) {
		theBuilder.append("<h");
		theBuilder.append(theLevel);
		theBuilder.append(">");
		theBuilder.append(theText);
		theBuilder.append("</h");
		theBuilder.append(theLevel);
		theBuilder.append(">");
	}
	
	private String buildHelp() {
		StringBuilder myStringBuilder = new StringBuilder();
		myStringBuilder.append(HTML_START);
		appendHeadline(myStringBuilder, 1, "creativecomputing command server help");
		myStringBuilder.append(HTML_END);
		return myStringBuilder.toString();
	}

	@Override
	public void onRequest(CCHttpRequest theRequest) {
		Map<String, List<String>> myParameter = theRequest.queryParameters();
		
		if(!myParameter.containsKey("command")) {
			theRequest.respond(buildHelp(), CCHttpContentType.HTML);
		}
		
	}
}

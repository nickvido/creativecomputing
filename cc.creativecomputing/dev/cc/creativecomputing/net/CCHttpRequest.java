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

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author christianriekoff
 *
 */
public class CCHttpRequest {
	
	public static enum CCHttpContentType{
		TEXT("text/plane"),
		HTML("html");
		
		private String _myType;
		
		private CCHttpContentType(String theType) {
			_myType = theType;
		}
	}

	private HttpRequest _myRequest;
	
	private MessageEvent _myMessageEvent;
	
	private Map<String, List<String>> _myQueryParameters;
	
	private String _myContent;
	
	CCHttpRequest(MessageEvent theMessageEvent, String theContent){
		_myMessageEvent = theMessageEvent;
		_myRequest = (HttpRequest) _myMessageEvent.getMessage();
		
		QueryStringDecoder myQueryStringDecoder = new QueryStringDecoder(_myRequest.getUri());
		_myQueryParameters = myQueryStringDecoder.getParameters();
		
		_myContent = theContent;
	}
	
	public String content() {
		return _myContent;
	}
	
	public Map<String, List<String>> queryParameters(){
		return _myQueryParameters;
	}
	
	public void respond(String theResponse, CCHttpContentType theContentType) {
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpHeaders.isKeepAlive(_myRequest);

		// Build the response object.
		HttpResponse myResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		myResponse.setContent(ChannelBuffers.copiedBuffer(theResponse, CharsetUtil.UTF_8));
		myResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, theContentType._myType +"; charset=UTF-8");

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			myResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, myResponse.getContent().readableBytes());
		}

		// // Encode the cookie.
		// String cookieString = request.getHeader(HttpHeaders.Names.COOKIE);
		// if (cookieString != null) {
		// CookieDecoder cookieDecoder = new CookieDecoder();
		// Set<Cookie> cookies = cookieDecoder.decode(cookieString);
		// if (!cookies.isEmpty()) {
		// // Reset the cookies if necessary.
		// CookieEncoder cookieEncoder = new CookieEncoder(true);
		// for (Cookie cookie : cookies) {
		// cookieEncoder.addCookie(cookie);
		// }
		// response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
		// }
		// }

		// Write the response.
		ChannelFuture future = _myMessageEvent.getChannel().write(myResponse);

		// Close the non-keep-alive connection after the write operation is done.
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}

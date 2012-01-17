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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import cc.creativecomputing.events.CCListenerManager;

/**
 * @author christianriekoff
 * 
 */
public class CCHttpServer extends SimpleChannelUpstreamHandler implements ChannelPipelineFactory {
	
	public static interface CCHttpServerListener{
		public void onRequest(CCHttpRequest theRequest);
	}

	private HttpRequest _myRequest;
	private boolean _myIsReadingChunks;

	/** Buffer that stores the response content */
	private final StringBuilder _myStringBuilder = new StringBuilder();
	
	private CCListenerManager<CCHttpServerListener> _myListenerManager;

	public CCHttpServer() {
		this(8080);
	}
	
	public CCHttpServer(int thePort) {
		ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()
			)
		);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(this);

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(thePort));
		
		_myListenerManager = new CCListenerManager<CCHttpServerListener>(CCHttpServerListener.class);
	}
	
	public void addListener(CCHttpServerListener theListener) {
		_myListenerManager.add(theListener);
	}
	
	public void removeListener(CCHttpServerListener theListener) {
		_myListenerManager.remove(theListener);
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline myPipeline = Channels.pipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));

		myPipeline.addLast("decoder", new HttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		myPipeline.addLast("encoder", new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content compression.
		myPipeline.addLast("deflater", new HttpContentCompressor());
		myPipeline.addLast("handler", this);
		return myPipeline;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (!_myIsReadingChunks) {
			HttpRequest request = this._myRequest = (HttpRequest) e.getMessage();

			if (HttpHeaders.is100ContinueExpected(request)) {
				send100Continue(e);
			}

			_myStringBuilder.setLength(0);

			if (request.isChunked()) {
				_myIsReadingChunks = true;
			} else {
				ChannelBuffer myContentBuffer = request.getContent();
				String myContent = "";
				if (myContentBuffer.readable()) {
					myContent = myContentBuffer.toString(CharsetUtil.UTF_8);
				}
				_myListenerManager.proxy().onRequest(new CCHttpRequest(e, myContent));
			}
		} else {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				_myIsReadingChunks = false;

//				HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
//				if (!trailer.getHeaderNames().isEmpty()) {
//					_myStringBuilder.append("\r\n");
//					for (String name : trailer.getHeaderNames()) {
//						for (String value : trailer.getHeaders(name)) {
//							_myStringBuilder.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
//						}
//					}
//					_myStringBuilder.append("\r\n");
//				}

				_myListenerManager.proxy().onRequest(new CCHttpRequest(e, _myStringBuilder.toString()));
			} else {
				_myStringBuilder.append(chunk.getContent().toString(CharsetUtil.UTF_8));
			}
		}
	}

	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		e.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}

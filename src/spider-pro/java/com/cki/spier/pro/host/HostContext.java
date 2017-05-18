package com.cki.spier.pro.host;

import java.net.InetSocketAddress;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.ExecutionContext;
import com.cki.spider.pro.SpiderConfig;
import com.cki.spider.pro.future.SpiderFuture;

@SuppressWarnings("unchecked")
public class HostContext {

	private static final Logger logger = LoggerFactory.getLogger(HostContext.class);

	private String id;

	private String host;

	private int port;

	protected final Deque<ExecutionContext> queue;

	private final ExecutionDispatcher contextHolder;

	private Map<Handler, Long> handlerMap;

	private int maxConn;

	public HostContext(String id, String host, int port, ExecutionDispatcher contextHolder) {

		this.id = id;
		this.host = host;
		this.port = port;
		this.queue = new LinkedList<ExecutionContext>();
		this.contextHolder = contextHolder;
		this.handlerMap = new HashMap<Handler, Long>();
		this.maxConn = 1;
	}

	public synchronized void add(ExecutionContext e) {
		this.queue.addLast(e);
	}

	public synchronized void execute() {

		if (this.queue.size() == 0) {
			return;
		}

		synchronized (handlerMap) {

			int handlerSize = handlerMap.size();

			if (handlerSize >= this.maxConn) {

				Iterator<Handler> iterator = this.handlerMap.keySet().iterator();

				while (iterator.hasNext()) {

					Handler conn = iterator.next();

					if (conn.isError()) {
						iterator.remove();
					}

				}

			}

			if (handlerMap.size() >= this.maxConn) {
				return;
			}

		}

		final Handler handler = new Handler(this);

		synchronized (handlerMap) {
			handlerMap.put(handler, System.currentTimeMillis());
		}

		ExecutionContext context = queue.pollFirst();
		logger.info(" connect(handler, context):{}", context.getFetchUrl());
		try {
			connect(handler, context);
		} catch (Throwable e) {
			context.markFailure(e);
			logger.error("connect error.", e);
		}

	}

	public synchronized boolean isDead() {
		return this.queue.isEmpty();
	}

	private void connect(final Handler handler, final ExecutionContext context) {

		ClientBootstrap bootstrap = new ClientBootstrap(context.getChannelFactory());

		long connectTimeoutMillis = context.getFetchUrl().getConnectionTimeoutInMillis();

		if (connectTimeoutMillis == -1L) {
			connectTimeoutMillis = context.getFetcherConfig().getConnectionTimeoutInMillis();
		}

		bootstrap.setOption("connectTimeoutMillis", connectTimeoutMillis);

		ChannelPipeline pipeline = context.getChannelPipelineFactory().getPipeline(context.isSSL(), handler);

		bootstrap.setPipeline(pipeline);

		logger.info("context.markStart(handler):{}", context.getFetchUrl());
		context.markStart(handler);

		bootstrap.connect(new InetSocketAddress(this.host, this.port)).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				if (future.isSuccess()) {
					handler.execute(context, future.getChannel());
				} else {
					logger.info("future.is not Success. url:{}", context.getFetchUrl());
				}
			}
		});

	}

	public void connectionClose(Handler handler) {

		synchronized (handlerMap) {

			handlerMap.remove(handler);

		}

		contextHolder.fireEvent(this);
	}

	private void reback(ExecutionContext context) {
		this.queue.addFirst(context);
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		return host + ":" + port;
	}

	public static class Handler extends SimpleChannelUpstreamHandler {

		private ExecutionContext executionContext;

		private volatile Channel channel;

		private HostContext hostContext;

		private Throwable cause;

		public Handler(HostContext hostContext) {
			this.hostContext = hostContext;
		}

		private void releaseLimit() {

			if (SpiderConfig.limit.availablePermits() < SpiderConfig.maxConnectionCount) {
				SpiderConfig.limit.release();
			}
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			boolean isRelease = true;
			try{
				HttpResponse response = (HttpResponse) e.getMessage();
	
				this.executionContext.setResponse(response);
	
				if (this.executionContext.getResponseBodyCollector().willProcessResponse(response)) {
					ChannelBuffer content = response.getContent();
	
					if (content.readable()) {
						this.executionContext.getResponseBodyCollector().addLastData(content);
					}
	
					if(this.executionContext.markSuccess2()==0){
						isRelease=false;
					}
	
					this.executionContext = null;
	
				} else {
					this.executionContext.markFailure(SpiderFuture.CANCELLED);
				}
	
				channel.close();
	
				hostContext.connectionClose(this);
			}catch(Exception ex){
				logger.error("occured an error:{}",ex);
				throw ex;
			}finally{
				if(isRelease){
					releaseLimit();
				}else{
					logger.info("redirect url. no release limit.");
				}
			}

		}

		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

			if (this.channel == null) {
				this.channel = e.getChannel();
			}
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			releaseLimit();
			this.hostContext.connectionClose(this);

			if (this.executionContext != null) {

				if (this.channel != null) {

					this.executionContext.markFailure(SpiderFuture.CONNECTION_LOST);

				} else {
					this.executionContext.markFailure(SpiderFuture.CANNOT_CONNECT);
				}
			}

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			releaseLimit();
			cause = e.getCause();

			if (this.executionContext != null) {
				this.executionContext.markFailure(e.getCause());
			}

			e.getChannel().close();

			hostContext.connectionClose(this);

		}

		public void execute(ExecutionContext context) {

			this.executionContext = context;

			try {
				this.channel.write(context.getRequest());
			} catch (Exception e) {

				this.cause = e.getCause();

				this.channel.close();

				this.hostContext.reback(context);

				this.executionContext = null;

				logger.error("write error.{}", e);
			}
		}

		public void execute(ExecutionContext context, Channel channel) {

			this.channel = channel;

			execute(context);
		}

		public void close() {
			releaseLimit();
			if (this.channel != null) {
				this.channel.close();
			}

			if (this.executionContext != null) {
				this.executionContext.markFailure(SpiderFuture.TIMED_OUT);
			}

			this.hostContext.connectionClose(this);

		}

		public boolean isError() {
			return this.cause != null;
		}

	}

}

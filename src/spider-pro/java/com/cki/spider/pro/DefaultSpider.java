package com.cki.spider.pro;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.cookie.CookieStore;
import com.cki.spider.pro.filter.SpiderFilter;
import com.cki.spider.pro.filter.SpiderFilters;
import com.cki.spider.pro.netty.ChannelPipelineFactory;
import com.cki.spider.pro.timeout.SimpleTimeoutManager;
import com.cki.spider.pro.timeout.TimeoutManager;
import com.cki.spider.pro.util.NamedThreadFactory;
import com.cki.spier.pro.host.ExecutionDispatcher;
import com.cki.spier.pro.host.HostContext;

public class DefaultSpider implements Spider<SpiderData> {
	private static final Logger logger = LoggerFactory.getLogger(DefaultSpider.class);
	private SpiderConfig config;

	private ExecutionDispatcher dispatcher;

	private volatile boolean terminate = false;

	private ChannelFactory channelFactory;

	private ChannelPipelineFactory channelPipelineFactory;

	private TimeoutManager timeoutManager;

	private Executor listenerExecutor;

	private Executor internalListenerExecutor;

	private CookieStore cookieStore;

	public DefaultSpider(SpiderConfig spiderConfig, Executor listenerExecutor) {

		this.config = spiderConfig;
		this.listenerExecutor = listenerExecutor;
	}

	public DefaultSpider(SpiderConfig spiderConfig, int listenerExecutorThreads) {

		this.config = spiderConfig;
		this.listenerExecutor = Executors.newFixedThreadPool(listenerExecutorThreads, new NamedThreadFactory("ListenerExecutor"));
		;
	}

	@Override
	public void init() {

		if (this.config == null) {
			throw new RuntimeException("config must not be null.");
		}

		if (this.listenerExecutor == null) {
			this.internalListenerExecutor = Executors.newFixedThreadPool(25, new NamedThreadFactory("ListenerExecutor"));
			this.listenerExecutor = this.internalListenerExecutor;
		}

		Executor bossPool = Executors.newCachedThreadPool();

		Executor workerPool = Executors.newFixedThreadPool(this.config.getMaxIoWorkerThreads());

		this.channelFactory = new NioClientSocketChannelFactory(bossPool, workerPool);

		this.channelPipelineFactory = new ChannelPipelineFactory(this.config);

		this.timeoutManager = new SimpleTimeoutManager(1000);

		this.timeoutManager.init();

		this.dispatcher = new ExecutionDispatcher(this.config);

		this.dispatcher.init();

	}

	@Override
	public Future<SpiderData> fetch(SpiderUrl fetchUrl, SpiderUrlListener listener) {

		return fetch(fetchUrl, listener, SpiderFilters.ACCEPT_ALL_FILTER);
	}

	@Override
	public Future<SpiderData> fetch(SpiderUrl fetchUrl, SpiderUrlListener listener, SpiderFilter filter) {

		if (this.terminate) {
			throw new IllegalStateException("spider already terminated.");
		}
		
		logger.info(" 111111begin fetch url:{}",fetchUrl.getUrl());
		try {
			SpiderConfig.limit.acquire();
		} catch (InterruptedException e1) {
			throw new IllegalStateException("spider already interrupted.");
		}
		logger.info(" 22222begin fetch url:{}",fetchUrl.getUrl());
		
		int executionTimeout = fetchUrl.getMaxExecutionTimeout();

		if (executionTimeout == -1) {
			executionTimeout = config.getMaxExecutionTimeout();
		}

		ExecutionContext<SpiderData> context = new ExecutionContext<SpiderData>(fetchUrl, listener, filter, executionTimeout, channelFactory, channelPipelineFactory, timeoutManager, config,
				listenerExecutor, dispatcher, cookieStore);

		try {
			context.dispath();
			return context.getFuture();
		} catch (InterruptedException e) {
			terminate = false;
			throw new IllegalStateException("spider already terminated.", e);
		}
	}

	@Override
	public String fetchHtml(SpiderUrl fetchUrl) {

		Future<SpiderData> future = fetch(fetchUrl, null, SpiderFilters.HTML_FILTER);

		try {
			return future.get().getBody();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			e.printStackTrace();
		}

		return null;

	}

	@Override
	public void terminate() {

		if (this.terminate) {
			return;
		}

		this.terminate = true;

		this.dispatcher.terminate();

		this.channelFactory.releaseExternalResources();

		this.timeoutManager.terminate();

		if (this.internalListenerExecutor != null) {
			ExecutorUtil.terminate(this.internalListenerExecutor);
		}

	}

	public SpiderConfig getConfig() {
		return config;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

}

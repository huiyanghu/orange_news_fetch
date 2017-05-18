package com.cki.spier.pro.host;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.jboss.netty.util.internal.ExecutorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.ExecutionContext;
import com.cki.spider.pro.SpiderProxy;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderConfig;
import com.cki.spider.pro.host.event.ContextEvent;
import com.cki.spider.pro.host.event.HostContextEvent;
import com.cki.spider.pro.host.event.RequestContextEvent;
import com.cki.spider.pro.util.NamedThreadFactory;

@SuppressWarnings("unchecked")
public class ExecutionDispatcher {

	public SpiderConfig getFetcherConfig() {
		return spiderConfig;
	}

	public void setFetcherConfig(SpiderConfig spiderConfig) {
		this.spiderConfig = spiderConfig;
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ConcurrentHashMap<String, HostContext> contextMap;

	private Executor loopExecutor;

	private LinkedBlockingDeque<ContextEvent> eventQueue;

	private SpiderConfig spiderConfig;

	public ExecutionDispatcher(SpiderConfig spiderConfig) {

		this.spiderConfig = spiderConfig;

		this.contextMap = new ConcurrentHashMap<String, HostContext>();
		this.loopExecutor = Executors.newFixedThreadPool(spiderConfig.getDispatcherThreadCount(), new NamedThreadFactory("HostContextLoopExecutor"));
		this.eventQueue = new LinkedBlockingDeque<ContextEvent>();

	}

	public void init() {

		for (int i = 0; i < spiderConfig.getDispatcherThreadCount(); i++) {

			this.loopExecutor.execute(new Runnable() {

				@Override
				public void run() {

					while (true) {

						if (Thread.currentThread().isInterrupted()) {
							return;
						}

						try {
							ContextEvent event = eventQueue.take();

							if (event instanceof HostContextEvent) {

								HostContext hostContext = ((HostContextEvent) event).getHostContext();

								if (hostContext.isDead()) {
									if (contextMap.remove(hostContext.getId()) != null) {
										logger.info("remove context:{}", hostContext);
									}
								} else {
									hostContext.execute();
								}

							} else if (event instanceof RequestContextEvent) {

								ExecutionContext e = ((RequestContextEvent) event).getExecutionContext();

								HostContext hostContext = get(e);

								hostContext.add(e);

								hostContext.execute();

							}

						} catch (InterruptedException e) {
							logger.error("Interrupted.", e);
							return;
						}
					}

				}
			});

		}

	}

	public void dispatch(ExecutionContext e) {

		try {
			eventQueue.put(new RequestContextEvent(e));
		} catch (InterruptedException t) {
			return;
		}

	}

	public void terminate() {
		ExecutorUtil.terminate(this.loopExecutor);
	}

	private HostContext get(ExecutionContext e) {

		SpiderUrl fetchUrl = e.getFetchUrl();

		SpiderProxy proxy = fetchUrl.getFetchProxy();

		String host = null;

		int port = -1;

		if (proxy != null && proxy.getHost() != null && proxy.getPort() > 0) {

			host = proxy.getHost();
			port = proxy.getPort();
		} else {
			HostPortAndUri hostPortAndUri = e.getHostPortAndUri();

			host = hostPortAndUri.getHost();
			port = hostPortAndUri.getPort();
		}

		String hostId = hostId(host, port);

		HostContext hostContext = this.contextMap.get(hostId);

		if (hostContext != null) {

			return hostContext;
		}

		hostContext = new HostContext(hostId, host, port, this);

		HostContext oldContext = this.contextMap.putIfAbsent(hostId, hostContext);

		if (oldContext != null) {
			return oldContext;
		} else {
			return hostContext;
		}

	}

	public void fireEvent(HostContext context) {

		try {
			eventQueue.put(new HostContextEvent(context));
		} catch (InterruptedException e) {
			return;
		}
	}

	protected String hostId(String host, int port) {
		return new StringBuilder().append(host).append(":").append(port).toString();
	}
}

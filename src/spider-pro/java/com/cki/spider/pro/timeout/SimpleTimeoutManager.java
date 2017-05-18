package com.cki.spider.pro.timeout;

import java.lang.ref.WeakReference;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.util.internal.ExecutorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.ExecutionContext;
import com.cki.spider.pro.util.NamedThreadFactory;

@SuppressWarnings("unchecked")
public class SimpleTimeoutManager implements TimeoutManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final long tickDuration;

	private final Executor timeoutChecker;

	private ConcurrentHashMap<ExecutionContext, Object> map;

	public SimpleTimeoutManager(long tick) {

		this.tickDuration = tick;
		this.map = new ConcurrentHashMap<ExecutionContext, Object>();
		this.timeoutChecker = Executors.newSingleThreadExecutor(new NamedThreadFactory("timeoutChecker"));
	}

	@Override
	public boolean init() {

		this.timeoutChecker.execute(new Runnable() {

			@Override
			public void run() {

				logger.info("start timeout manager.");

				while (true) {

					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					Iterator<ExecutionContext> iterator = map.keySet().iterator();

					long start = System.currentTimeMillis();

					while (iterator.hasNext()) {

						ExecutionContext e = iterator.next();

						if (e != null) {
							if (e.timeout()) {
								e.markTimeout();
								iterator.remove();
							}
						} else {
							iterator.remove();
						}

					}

					long elapse = System.currentTimeMillis() - start;

					if (tickDuration > elapse) {
						try {
							Thread.sleep(tickDuration - elapse);
						} catch (InterruptedException e) {
							return;
						}
					}

				}

			}
		});

		return true;
	}

	@Override
	public void terminate() {

		ExecutorUtil.terminate(this.timeoutChecker);
	}

	@Override
	public void manageRequestTimeout(ExecutionContext context) {

		this.map.put(context, Boolean.TRUE);

	}

	@Override
	public void removeTimeoutManage(ExecutionContext context) {
		this.map.remove(context);
	}
}

package com.cki.spider.pro.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.controller.filter.SimilarUrlDetector;
import com.cki.spider.pro.filter.SpiderFilter;
import com.cki.spider.pro.util.HtmlUtil;

@SuppressWarnings("unchecked")
public class SpiderTaskContext {

	private static final Logger logger = LoggerFactory.getLogger(SpiderTaskContext.class);

	private final SpiderTask fetchTask;

	private final SpiderTaskFilter fetchTaskFilter;

	private final SpiderTaskListener spiderTaskListener;

	private TaskFuture<TaskStatistics> taskFuture;

	private volatile SpiderUrlListener fetchUrlListener;

	private volatile SpiderFilter fetchUrlFilter;

	private final SpiderUrlQueue fetchUrlQueue;

	private SimilarUrlDetector similarUrlDetector;

	private LinkResolver linkExtractor;

	private Boolean executed;

	private final AtomicLong urlCounter;

	public SpiderTaskContext(SpiderTask task, SpiderTaskFilter filter, SpiderTaskListener listener, SimilarUrlDetector detector, LinkResolver linkExtractor, SpiderUrlQueue fetchUrlQueue) {

		this.fetchTask = task;
		this.fetchTaskFilter = filter;
		this.spiderTaskListener = listener;
		this.taskFuture = new TaskFuture<TaskStatistics>();
		this.fetchUrlQueue = fetchUrlQueue;
		this.executed = false;
		this.urlCounter = new AtomicLong(1L);
		this.similarUrlDetector = detector;
		this.linkExtractor = linkExtractor;
	}

	public void execute() {

		if (this.executed) {
			return;
		}

		synchronized (this.executed) {
			if (this.executed) {
				return;
			}

			InternalSpiderUrl seed = new InternalSpiderUrl(this.fetchTask.getSeed(), this);

			this.fetchUrlQueue.putSeed(seed);

		}
	}

	public SpiderUrlListener getFetchUrlListener() {

		if (this.fetchUrlListener != null) {
			return this.fetchUrlListener;
		}

		synchronized (this) {
			if (this.fetchUrlListener != null) {
				return this.fetchUrlListener;
			}

			this.fetchUrlListener = new SpiderUrlListener() {

				@Override
				public void postFetch(SpiderUrl url, SpiderData data) {

					urlCounter.decrementAndGet();

					if (spiderTaskListener != null) {

						spiderTaskListener.postFetch(fetchTask, url, data);
					}

					List<InternalSpiderUrl> urls = resolveLinksAndFilter(data);

					if (urls != null && !urls.isEmpty()) {

						logger.info("extract {} links from {}", urls.size(), url.getUrl());

						for (InternalSpiderUrl fu : urls) {
							fetchUrlQueue.put(fu);
							urlCounter.incrementAndGet();
						}
					}

					if (urlCounter.get() <= 0L) {
						spiderTaskListener.onTaskComplete(fetchTask);
					}
				}

				@Override
				public void preFetch(SpiderUrl url) {

					if (spiderTaskListener != null) {
						spiderTaskListener.preFetch(fetchTask, url);
					}
				}

				@Override
				public void refusedByFilter(SpiderUrl fetchUrl, SpiderData data) {

					urlCounter.decrementAndGet();

				}
			};

			return this.fetchUrlListener;

		}

	}

	public SpiderFilter getFetchUrlFilter() {

		if (this.fetchUrlFilter != null) {
			return this.fetchUrlFilter;
		}

		synchronized (this) {

			if (this.fetchUrlFilter != null) {
				return this.fetchUrlFilter;
			}

			this.fetchUrlFilter = new SpiderFilter() {

				@Override
				public boolean accept(SpiderUrl url, SpiderData spiderData) {

					return fetchTaskFilter.accept(fetchTask, url, spiderData);
				}

			};

			return this.fetchUrlFilter;

		}
	}

	public TaskFuture<TaskStatistics> getFuture() {
		return this.taskFuture;
	}

	protected List<InternalSpiderUrl> resolveLinksAndFilter(SpiderData data) {

		InternalSpiderUrl fetchUrl = (InternalSpiderUrl) data.getFetchUrl();

		if (fetchUrl.getDepth() >= this.fetchTask.getFetchDepth()) {
			return Collections.emptyList();
		}

		if (data == null || data.getBody() == null) {
			return Collections.emptyList();
		}

		int depth = fetchUrl.getDepth();

		String seedDomain = HtmlUtil.getDomain(fetchUrl.getUrl());

		if (seedDomain.startsWith("www")) {
			seedDomain = seedDomain.substring(3, seedDomain.length());
		}

		Set<String> allLinks = linkExtractor.resolve(data, seedDomain);

		if (allLinks == null || allLinks.isEmpty()) {
			return Collections.emptyList();
		}

		List<InternalSpiderUrl> urls = new ArrayList<InternalSpiderUrl>();

		for (String l : allLinks) {

			String link = StringEscapeUtils.unescapeHtml(l);

			if (!similarUrlDetector.feed(link)) {
				continue;
			}

			InternalSpiderUrl url = new InternalSpiderUrl(fetchUrl, this, depth + 1);

			url.setUrl(link);

			urls.add(url);

		}

		return urls;
	}

}

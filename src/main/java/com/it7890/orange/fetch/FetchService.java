package com.it7890.orange.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import com.it7890.orange.config.TpConfig;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import com.cki.spider.pro.SpiderUrlListener;
import com.cki.spider.pro.util.NamedThreadFactory;

@Component
public class FetchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Spider<SpiderData> spider;
	@Autowired
	private TpConfig tpConfig;
	
//	@Resource
//	private RedisQueueUtil redisQueueUtil;
//	@Resource
//	private SiteUrlConfigRepository siteUrlConfigRepository;
	
	ExecutorService searchExecutor;

	Semaphore limit;
	AtomicLong atomicSum;
	
	public FetchService() {
		this.searchExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("searchExecutor"));
		atomicSum = new AtomicLong(0);
		logger.debug("FetchServiceFetchServiceFetchServiceFetchServiceFetchServiceFetchServiceFetchServiceFetchServiceFetchServiceFetchService>>>>>>>>>>>>>");
	}

	@PostConstruct
	public void init() {
		this.limit = new Semaphore(tpConfig.getSpiderConn());

		/**
		 * =============================
		 * =============================
		 * 测试数据 start
		 * =============================
		 * =============================
		 */
		GrabListRule grabListRulInfo = new GrabListRule();
		grabListRulInfo.setObjectId("0001");
		grabListRulInfo.setRuleName("测试列表规则");
		grabListRulInfo.setNodeId("test_node_1");
		grabListRulInfo.setChannelId("test_channel_1");
		grabListRulInfo.setCountryCode("tw");
		grabListRulInfo.setLangId("zh1");
		grabListRulInfo.setTopicId("测试话题1");
		grabListRulInfo.setConstant("utf-8");
		grabListRulInfo.setGrabTime(600);
		grabListRulInfo.setSiteUrl("www.esquire.tw/category/food-drink/"); //源url
		grabListRulInfo.setCssPath(""); //目标区域规则
		grabListRulInfo.setFindPre(""); //目标文章url规则

		List<GrabListRule> grabListRules = new ArrayList();
		grabListRules.add(grabListRulInfo);
		/**
		 * =============================
		 * =============================
		 * 测试数据 end
		 * =============================
		 * =============================
		 */

		this.searchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					if(grabListRules.size() > 0) {
						GrabListRule grabListRulInfo = grabListRules.get(0);
						grabListRules.remove(0);

						if(StringUtil.isNotEmpty(grabListRulInfo.getSiteUrl())) {
							logger.debug("抓取数据源 - {}", grabListRulInfo.getSiteUrl());
							urlSearch(grabListRulInfo);
						}
					}
				}
			}
		});		
	}

	protected void urlSearch(final GrabListRule grabListRulInfo) {
		logger.debug(" check new url search");
		try {
			limit.acquire();
		} catch (InterruptedException e1) {
			logger.error("interrupted.return", e1);
			return;
		}
		SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(grabListRulInfo.getSiteUrl());

		fetchUrl.setConnectionTimeoutInMillis(50 * 1000);
		fetchUrl.setMaxExecutionTimeout(100 * 1000);
		// fetchUrl.setAttachment(pmUrl);

		spider.fetch(fetchUrl, new SpiderUrlListener() {
			@Override
			public void postFetch(SpiderUrl furl, SpiderData fd) {
				limit.release();
				logger.debug(" fetch-->postFetch,furl:{}", furl.getUrl());

				if (fd == null || fd.getCause() != null || fd.getStatusCode() != 200) {
					if (fd == null) {
						logger.warn(" fetch-->fd is null.furl:{},proxy:{}", furl.getUrl(), furl.getFetchProxy());
					} else {
						logger.warn(" fetch-->proxy failed,proxy:{},status:{},cause:{}", new Object[] { furl.getFetchProxy(), fd.getStatusCode(), fd.getCause() });
					}
					atomicSum.incrementAndGet();
					return;
				}
				logger.debug("fetch result: {}, {}", new Object[] {furl, fd});
				String body = fd.getBody();

				logger.debug("========================================================================>>");
				logger.debug("========================================================================>>");
				logger.debug("========================================================================>>");

				Document doc = Jsoup.parse(body);
//				Elements contentElements = doc.select("div#main.clearfix");
				Elements contentElements = doc.select("div[role=main]");
				logger.debug("content element size: {}", contentElements.size());

				if (contentElements.size() > 0) {
					Elements articleElements = contentElements.get(0).select("article[role=article]");
					for(Element articleElement : articleElements) {
						logger.info("A href =======>>>>{}", articleElement.select("a[href~=/[\\s|\\S]*"));
					}
				}
				
				atomicSum.incrementAndGet();
				// proxyManager.stat(furl.getFetchProxy(), true);
			}

			@Override
			public void preFetch(SpiderUrl furl) {

			}

			@Override
			public void refusedByFilter(SpiderUrl furl, SpiderData fd) {

			}
		});
	}
}

package com.it7890.orange.fetch;

import com.cki.fetch.util.SpiderUrlUtil;
import com.cki.spider.pro.Spider;
import com.cki.spider.pro.SpiderData;
import com.cki.spider.pro.SpiderUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
public class FetchTestService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Spider<SpiderData> spider;


	@PostConstruct
	public void init() {
		urlSearch("https://blog.wilddog.com/");
	}

	private void urlSearch(String siteUrl) {
		logger.debug(" check new url search");

		SpiderUrl fetchUrl = SpiderUrlUtil.buildSpiderUrl(siteUrl);

		fetchUrl.setConnectionTimeoutInMillis(50 * 1000);
		fetchUrl.setMaxExecutionTimeout(100 * 1000);
		// fetchUrl.setAttachment(pmUrl);
		logger.info("body:::::   {}", spider.fetchHtml(fetchUrl));
	}
}

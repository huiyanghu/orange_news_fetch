package com.it7890.orange.fetch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.SpiderData;

public class DefaultAnalysis implements BaseAnalysis {

	private final Logger logger = LoggerFactory.getLogger(DefaultAnalysis.class);
	
	@Override
	public void analysis(String body, Long siteUrlConfigId) {
		logger.error("########################: " + siteUrlConfigId + "不存在或出错了!!!" );
	}
	
	@Override
	public void analysis(SpiderData spiderData, Long siteUrlConfigId) {
		logger.error("########################: " + siteUrlConfigId + "不存在或出错了!!!" );
	}

	@Override
	public void analysis(SpiderData spiderData, Long siteUrlConfigId, String classifyCode) {
		logger.error("########################: " + siteUrlConfigId + "不存在或出错了!!!" );
	}
}

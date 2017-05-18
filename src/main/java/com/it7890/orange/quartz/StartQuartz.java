package com.it7890.orange.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class StartQuartz {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	@Resource
//	private SiteUrlConfigRepository siteUrlConfigRepository;
//
//	@PostConstruct
//	public void init() {
//		List<SiteUrlConfig> list = siteUrlConfigRepository.findSiteUrlConfigList(1);
//
//		for(SiteUrlConfig siteUrlConfig : list) {
//			if(siteUrlConfig != null) {
//				JobDetail jobDetail = QuartzManager.getJob(siteUrlConfig.getSiteUrl());
//				if(jobDetail == null) {
//					logger.debug("添加定时任务，任务名-->{}", siteUrlConfig.getSiteUrl());
//
//					QuartzManager.addJob(siteUrlConfig.getSiteUrl(), FetchSiteService.class, siteUrlConfig.getScheduled(), siteUrlConfig.getId());
//				}
//			}
//		}
//	}
}

package com.it7890.orange.fetch;

import com.cki.spider.pro.SpiderData;

/**
 * 解析抓取结果类
 */
public interface BaseAnalysis {

	/**
	 * 解析抓取内容
	 * @param body 抓取内容
	 * @param siteUrlConfigId 抓取源id
	 */
	public void analysis(String body, Long siteUrlConfigId);
	
	/**
	 * 解析抓取内容
	 * @param spiderData 抓取对象
	 * @param siteUrlConfigId 抓取源id
	 */
	public void analysis(SpiderData spiderData, Long siteUrlConfigId);
	
	/**
	 * 解析抓取内容
	 * @param spiderData 抓取对象
	 * @param siteUrlConfigId 抓取源id
	 * @param classifyCode 抓取源分类code
	 */
	public void analysis(SpiderData spiderData, Long siteUrlConfigId, String classifyCode);
}
